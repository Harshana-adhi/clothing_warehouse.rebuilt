package DAO;

import DBConnection.DBConnect;
import Models.Billing;
import Models.BillDetails;
import Models.Payment;
import Models.User;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class BillingDAO {

    private boolean hasPermission(User user, String operation){
        if(user == null) return false;
        String role = user.getRole().toLowerCase();
        switch(operation.toLowerCase()){
            case "add":
            case "view":
                return role.equals("admin") || role.equals("manager") || role.equals("staff");
            case "update":
            case "delete":
                return role.equals("admin") || role.equals("manager");
            default:
                return false;
        }
    }

    public Integer insertWithPaymentAndDetails(Billing bill, List<BillDetails> items, User user, String employeeId, String paymentType) {
        if(!hasPermission(user, "add") || bill == null || items == null || items.isEmpty()) return null;

        Connection conn = null;
        try {
            conn = DBConnect.getDBConnection();
            conn.setAutoCommit(false);

            // ==================== CHECK STOCK ====================
            for(BillDetails bd : items){
                if(bd.getStockId() != null){
                    String stockCheckSql = "SELECT Quantity FROM ClothingStock WHERE StockId=?";
                    try (PreparedStatement psCheck = conn.prepareStatement(stockCheckSql)) {
                        psCheck.setInt(1, bd.getStockId());
                        ResultSet rs = psCheck.executeQuery();
                        if(rs.next()){
                            int availableQty = rs.getInt("Quantity");
                            if(bd.getQuantity() > availableQty){
                                throw new RuntimeException("Insufficient stock for Stock ID: " + bd.getStockId() +
                                        " (requested: " + bd.getQuantity() + ", available: " + availableQty + ")");
                            }
                        } else {
                            throw new RuntimeException("Stock ID not found: " + bd.getStockId());
                        }
                    }
                }
            }

            PaymentDAO paymentDAO = new PaymentDAO();
            BigDecimal totalAmount = items.stream()
                    .map(BillDetails::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Payment payment = new Payment(paymentType, totalAmount, new java.util.Date(), employeeId, bill.getCustomerId());
            if(!paymentDAO.insert(payment, user)){
                conn.rollback();
                return null;
            }
            Integer paymentId = payment.getPaymentId();

            bill.setPaymentId(paymentId);
            bill.setAmount(totalAmount);
            bill.setBillDate(new java.util.Date());
            bill.setBillStatus("Paid");

            String sqlBill = "INSERT INTO Billing (BillDate, Amount, BillDescription, BillStatus, PaymentId, CustomerId) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement psBill = conn.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS)) {
                psBill.setDate(1, new java.sql.Date(bill.getBillDate().getTime()));
                psBill.setBigDecimal(2, bill.getAmount());
                psBill.setString(3, bill.getBillDescription());
                psBill.setString(4, bill.getBillStatus());
                psBill.setInt(5, bill.getPaymentId());
                psBill.setString(6, bill.getCustomerId());

                if(psBill.executeUpdate() == 0){
                    conn.rollback();
                    return null;
                }
                try (ResultSet rs = psBill.getGeneratedKeys()) {
                    if(rs.next()) bill.setBillId(rs.getInt(1));
                    else {
                        conn.rollback();
                        return null;
                    }
                }
            }

            String sqlDetail = "INSERT INTO BillDetails (BillId, StockId, Quantity, TotalAmount) VALUES (?,?,?,?)";
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for(BillDetails item : items){
                    psDetail.setInt(1, bill.getBillId());
                    if(item.getStockId() != null) psDetail.setInt(2, item.getStockId());
                    else psDetail.setNull(2, Types.INTEGER);

                    psDetail.setInt(3, item.getQuantity());
                    psDetail.setBigDecimal(4, item.getTotalAmount());

                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            // ==================== UPDATE STOCK ====================
            String updateStockSql = "UPDATE ClothingStock SET Quantity = Quantity - ? WHERE StockId=?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateStockSql)) {
                for(BillDetails bd : items){
                    if(bd.getStockId() != null){
                        psUpdate.setInt(1, bd.getQuantity());
                        psUpdate.setInt(2, bd.getStockId());
                        psUpdate.addBatch();
                    }
                }
                psUpdate.executeBatch();
            }

            conn.commit();
            return bill.getBillId();

        } catch(Exception e){
            try { if(conn != null) conn.rollback(); } catch(SQLException ex){}
            System.out.println("Error in insertWithPaymentAndDetails: "+e.getMessage());
        } finally {
            try { if(conn != null) conn.setAutoCommit(true); conn.close(); } catch(SQLException e){}
        }
        return null;
    }

    public boolean update(Billing bill, User user){
        if(!hasPermission(user, "update") || bill == null) return false;

        String sql = "UPDATE Billing SET BillDate=?, Amount=?, BillDescription=?, BillStatus=?, PaymentId=?, CustomerId=? WHERE BillId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setDate(1, new java.sql.Date(bill.getBillDate().getTime()));
            ps.setBigDecimal(2, bill.getAmount());
            ps.setString(3, bill.getBillDescription());
            ps.setString(4, bill.getBillStatus());
            if(bill.getPaymentId() != null) ps.setInt(5, bill.getPaymentId());
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, bill.getCustomerId());
            ps.setInt(7, bill.getBillId());

            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error updating Billing: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int billId, User user){
        if(!hasPermission(user, "delete")) return false;

        String sql = "DELETE FROM Billing WHERE BillId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, billId);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error deleting Billing: " + e.getMessage());
        }
        return false;
    }

    public Billing getById(int billId){
        String sql = "SELECT BillId, BillDate, Amount, BillDescription, BillStatus, PaymentId, CustomerId FROM Billing WHERE BillId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, billId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new Billing(
                            rs.getInt("BillId"),
                            rs.getDate("BillDate"),
                            rs.getBigDecimal("Amount"),
                            rs.getString("BillDescription"),
                            rs.getString("BillStatus"),
                            rs.getObject("PaymentId") != null ? rs.getInt("PaymentId") : null,
                            rs.getString("CustomerId")
                    );
                }
            }
        } catch(SQLException e){
            System.out.println("Error fetching Billing: " + e.getMessage());
        }
        return null;
    }

    public List<Billing> getAll(){
        List<Billing> list = new ArrayList<>();
        String sql = "SELECT BillId, BillDate, Amount, BillDescription, BillStatus, PaymentId, CustomerId FROM Billing ORDER BY BillDate DESC";
        try(Connection conn = DBConnect.getDBConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                list.add(new Billing(
                        rs.getInt("BillId"),
                        rs.getDate("BillDate"),
                        rs.getBigDecimal("Amount"),
                        rs.getString("BillDescription"),
                        rs.getString("BillStatus"),
                        rs.getObject("PaymentId") != null ? rs.getInt("PaymentId") : null,
                        rs.getString("CustomerId")
                ));
            }
        } catch(SQLException e){
            System.out.println("Error fetching all Billing: " + e.getMessage());
        }
        return list;
    }
}
