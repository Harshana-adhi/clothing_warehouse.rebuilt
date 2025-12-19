package DAO;

import DBConnection.DBConnect;
import Models.Payment;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

public class PaymentDAO {

    // Check permissions based on logged-in user role
    private boolean hasPermission(User user, String operation){
        if(user == null) return false;
        String role = user.getRole().toLowerCase();
        switch(operation.toLowerCase()){
            case "add":
            case "view":
                return role.equals("admin") || role.equals("manager") || role.equals("staff");
            case "update":
                return role.equals("admin") || role.equals("manager") || role.equals("staff");
            case "delete":
                return role.equals("admin") || role.equals("manager");
            default:
                return false;
        }
    }

    // Insert a new payment
    public boolean insert(Payment payment, User user){
        if(!hasPermission(user, "add") || payment == null) return false;

        String sql = "INSERT INTO Payment (PayType, Amount, PayDate, EmployeeId, CustomerId) VALUES (?,?,?,?,?)";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){ //tells the database we want to retrieve PKs

            ps.setString(1, payment.getPayType());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setDate(3, new java.sql.Date(payment.getPayDate().getTime()));
            ps.setString(4, payment.getEmployeeId());
            ps.setString(5, payment.getCustomerId());

            int affected = ps.executeUpdate();
            if(affected > 0){
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()){
                        payment.setPaymentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch(SQLException e){
            System.out.println("Error inserting Payment: " + e.getMessage());
        }
        return false;
    }

   /*
    // Create payment and return generated PaymentId
    public Integer createPayment(String payType, BigDecimal amount, User user, String customerId){
        if(user == null || !hasPermission(user, "add")) return null;

        Payment payment = new Payment(payType, amount, new Date(), user.getEmployeeId(), customerId);
        boolean success = insert(payment, user);
        return success ? payment.getPaymentId() : null;
    }

    */

    /*
    // Update payment (only admin/manager)
    public boolean update(Payment payment, User user){
        if(!hasPermission(user, "update") || payment == null) return false;

        String sql = "UPDATE Payment SET PayType=?, Amount=?, PayDate=?, EmployeeId=?, CustomerId=? WHERE PaymentId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, payment.getPayType());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setDate(3, new java.sql.Date(payment.getPayDate().getTime()));
            ps.setString(4, payment.getEmployeeId());
            ps.setString(5, payment.getCustomerId());
            ps.setInt(6, payment.getPaymentId());

            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error updating Payment: " + e.getMessage());
        }
        return false;
    }

     */

    /*
    // Delete payment (only admin/manager)
    public boolean delete(int paymentId, User user){
        if(!hasPermission(user, "delete")) return false;

        String sql = "DELETE FROM Payment WHERE PaymentId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, paymentId);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error deleting Payment: " + e.getMessage());
        }
        return false;
    }


     */


    // Get payment by ID
   /* public Payment getById(int paymentId, User user){
        if(!hasPermission(user, "view")) return null;

        String sql = "SELECT PaymentId, PayType, Amount, PayDate, EmployeeId, CustomerId FROM Payment WHERE PaymentId=? ORDER BY PaymentId DESC";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, paymentId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new Payment(
                            rs.getInt("PaymentId"),
                            rs.getString("PayType"),
                            rs.getBigDecimal("Amount"),
                            rs.getDate("PayDate"),
                            rs.getString("EmployeeId"),
                            rs.getString("CustomerId")
                    );
                }
            }
        } catch(SQLException e){
            System.out.println("Error fetching Payment: " + e.getMessage());
        }
        return null;
    }

    */

    // Get all payments
    public List<Payment> getAll(User user){
        List<Payment> list = new ArrayList<>();
        if(!hasPermission(user, "view")) return list;

        String sql = "SELECT PaymentId, PayType, Amount, PayDate, EmployeeId, CustomerId FROM Payment ORDER BY PaymentId DESC,PayDate ";
        try(Connection conn = DBConnect.getDBConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                list.add(new Payment(
                        rs.getInt("PaymentId"),
                        rs.getString("PayType"),
                        rs.getBigDecimal("Amount"),
                        rs.getDate("PayDate"),
                        rs.getString("EmployeeId"),
                        rs.getString("CustomerId")
                ));
            }

        } catch(SQLException e){
            System.out.println("Error fetching all Payments: " + e.getMessage());
        }
        return list;
    }

    // Get Payment Method by BillId
   /* public String getPaymentMethodByBillId(int billId){
        String sql = "SELECT p.PayType FROM Payment p " +
                "JOIN Billing b ON p.PaymentId = b.PaymentId " +
                "WHERE b.BillId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getString("PayType");
        } catch(SQLException e){
            System.out.println("Error fetching payment method: "+e.getMessage());
        }
        return null;
    }

    */

    // NEW METHODS FOR PARTIAL REFUND

    // Get PaymentId by BillId
    public Integer getPaymentIdByBillId(int billId){
        String sql = "SELECT PaymentId FROM Billing WHERE BillId=?"; //
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, billId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return rs.getInt("PaymentId");
            }
        } catch(SQLException e){
            System.out.println("Error fetching PaymentId by BillId: " + e.getMessage());
        }
        return null;
    }

    // Adjust payment amount (reduce amount for partial refund)
    public boolean adjustPaymentAmount(int paymentId, BigDecimal refundAmount, User user){
        if(!hasPermission(user, "update")) return false;
        String sql = "UPDATE Payment SET Amount = Amount - ? WHERE PaymentId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setBigDecimal(1, refundAmount); //new payment amount = amount - refund amount
            ps.setInt(2, paymentId);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error adjusting payment amount: " + e.getMessage());
        }
        return false;
    }
}
