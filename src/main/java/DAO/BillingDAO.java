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
      //method for updating bill/billdetails/payment
    public Integer insertWithPaymentAndDetails(Billing bill, List<BillDetails> items, User user, String employeeId, String paymentType) {
        if(!hasPermission(user, "add") || bill == null || items == null || items.isEmpty()) return null;

        Connection conn = null;
        try {
            conn = DBConnect.getDBConnection();
            conn.setAutoCommit(false); //Setting autoCommit = false allows to execute multiple statements as a single transaction.

            // check current stock
            for(BillDetails bd : items){ //item is a list of the billdetails objects ,bd is a single object of it
                if(bd.getStockId() != null){ //Only check stock for items that are linked to the inventory (have a valid StockId).

                    String stockCheckSql = "SELECT Quantity FROM ClothingStock WHERE StockId=?"; //reading availivle qty from stock table
                    try (PreparedStatement psCheck = conn.prepareStatement(stockCheckSql)) {
                        psCheck.setInt(1, bd.getStockId());
                        ResultSet rs = psCheck.executeQuery();
                        if(rs.next()){
                            int availableQty = rs.getInt("Quantity"); //availible qty = actual qty read from clothing stock tble
                            if(bd.getQuantity() > availableQty){                //if needed qty for bill > actual qty
                                throw new RuntimeException("Insufficient stock for Stock ID: " + bd.getStockId() +
                                        " (requested: " + bd.getQuantity() + ", available: " + availableQty + ")");
                            }
                        } else {
                            throw new RuntimeException("Stock ID not found: " + bd.getStockId());
                        }
                    }
                }
            }

            PaymentDAO paymentDAO = new PaymentDAO();        //create paymentDAO object for access methods in paymentDAO
            BigDecimal totalAmount = items.stream() //take the list of items and turn it into a stream | allow functional style operations without loops
                    .map(BillDetails::getTotalAmount) // get total amount of billdetails of one bill
                    .reduce(BigDecimal.ZERO, BigDecimal::add); //sum all total amounts starting from 0

            Payment payment = new Payment(paymentType, totalAmount, new java.util.Date(), employeeId, bill.getCustomerId());
            if(!paymentDAO.insert(payment, user)){ //calls insert method in paymentDAO
                conn.rollback(); //if inserting payment is not success, then rollback connection for undo all previous insertion(if exits)
                return null;
            }
            Integer paymentId = payment.getPaymentId(); //if payment success, then get paymentID of that payment

            bill.setPaymentId(paymentId);   //set that paymentId in bill object
            bill.setAmount(totalAmount);
            bill.setBillDate(new java.util.Date());
            bill.setBillStatus("Paid");


            //inserting that date into billing table
            String sqlBill = "INSERT INTO Billing (BillDate, Amount, BillDescription, BillStatus, PaymentId, CustomerId) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement psBill = conn.prepareStatement(sqlBill, Statement.RETURN_GENERATED_KEYS)) {
                psBill.setDate(1, new java.sql.Date(bill.getBillDate().getTime()));
                psBill.setBigDecimal(2, bill.getAmount());
                psBill.setString(3, bill.getBillDescription());
                psBill.setString(4, bill.getBillStatus());
                psBill.setInt(5, bill.getPaymentId());
                psBill.setString(6, bill.getCustomerId());

                if(psBill.executeUpdate() == 0){
                    conn.rollback(); //if inserting fails. rollback connection
                    return null;
                }
                try (ResultSet rs = psBill.getGeneratedKeys()) {
                    if(rs.next()) bill.setBillId(rs.getInt(1));  //get auto generated billID and pass it to bill object
                    else {
                        conn.rollback();
                        return null;
                    }
                }
            }
            //insert data into bill details
            String sqlDetail = "INSERT INTO BillDetails (BillId, StockId, Quantity, TotalAmount) VALUES (?,?,?,?)";
            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                for(BillDetails item : items){ //list <billDetails> is the list of billDetails for one bill ( this is a loop over that list)
                    psDetail.setInt(1, bill.getBillId());
                    if(item.getStockId() != null) psDetail.setInt(2, item.getStockId());  //handle nullable stockId
                    else psDetail.setNull(2, Types.INTEGER);

                    psDetail.setInt(3, item.getQuantity());
                    psDetail.setBigDecimal(4, item.getTotalAmount());

                    psDetail.addBatch(); //Instead of executing immediately, this Stores the current insert in memory till all items are added
                }
                psDetail.executeBatch(); //send all inserts at once
            }

            //  UPDATE STOCK
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

            conn.commit();  //permanatly saves bill/billDetails/payment/stock
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
     //method for return specific bill details (use in billing panel in generating bill PDF)
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


    //methods to read all records from billing
    public List<Billing> getAll(){
        List<Billing> list = new ArrayList<>();
        String sql = "SELECT BillId, BillDate, Amount, BillDescription, BillStatus, PaymentId, CustomerId FROM Billing ORDER BY BillId DESC,BillDate";
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

    // REFUND RELATED METHODS

    // Update BillStatus (Paid, Partially Refunded, Refunded)
    public boolean updateBillStatus(int billId, String status){
        String sql = "UPDATE Billing SET BillStatus=? WHERE BillId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, status);
            ps.setInt(2, billId);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error updating BillStatus: " + e.getMessage());
        }
        return false;
    }
}
