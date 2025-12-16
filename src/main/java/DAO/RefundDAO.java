package DAO;

import DBConnection.DBConnect;
import Models.Refund;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefundDAO {

    // ================= EXISTING METHODS =================

    // Insert a new refund and return generated RefundId
    public int insert(Refund refund) {
        int generatedId = -1;
        String sql = "INSERT INTO Refund (RefundDate, Amount, Reason, RefundMethod, CustomerId, BillId) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, new java.sql.Date(refund.getRefundDate().getTime()));
            ps.setDouble(2, refund.getAmount());
            ps.setString(3, refund.getReason());
            ps.setString(4, refund.getRefundMethod());
            ps.setString(5, refund.getCustomerId());
            ps.setInt(6, refund.getBillId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error inserting Refund: " + e.getMessage());
        }

        return generatedId;
    }

    // Fetch all refunds for a given bill
    public List<Refund> getByBillId(int billId) {
        List<Refund> list = new ArrayList<>();
        String sql = "SELECT * FROM Refund WHERE BillId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Refund refund = new Refund(
                            rs.getInt("RefundId"),
                            rs.getDate("RefundDate"),
                            rs.getDouble("Amount"),
                            rs.getString("Reason"),
                            rs.getString("RefundMethod"),
                            rs.getString("CustomerId"),
                            rs.getInt("BillId")
                    );
                    list.add(refund);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Refunds: " + e.getMessage());
        }

        return list;
    }

    // Get total refunded amount for a bill
    public double getTotalRefundedAmount(int billId) {
        double total = 0;
        String sql = "SELECT IFNULL(SUM(Amount), 0) AS TotalRefunded FROM Refund WHERE BillId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("TotalRefunded");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total refund: " + e.getMessage());
        }

        return total;
    }

    // ================= NEW METHODS FOR REFUND DETAILS =================

    // Insert a refund item into RefundDetails
    public boolean insertRefundDetail(int refundId, int stockId, int quantity, double amount) {
        String sql = "INSERT INTO RefundDetails (RefundId, StockId, Quantity, Amount) VALUES (?,?,?,?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, refundId);
            ps.setInt(2, stockId);
            ps.setInt(3, quantity);
            ps.setDouble(4, amount);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting RefundDetail: " + e.getMessage());
            return false;
        }
    }

    // Fetch all refund items for a given BillId (for RefundPanel table)
    public List<Object[]> getRefundDetailsByBillId(int billId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT rd.RefundDetailId, r.RefundId, rd.StockId, rd.Quantity, rd.Amount, " +
                "r.RefundMethod, r.Reason, r.RefundDate " +
                "FROM RefundDetails rd " +
                "JOIN Refund r ON rd.RefundId = r.RefundId " +
                "WHERE r.BillId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getInt("RefundDetailId"),
                            rs.getInt("RefundId"),
                            rs.getInt("StockId"),
                            rs.getInt("Quantity"),
                            rs.getDouble("Amount"),
                            rs.getString("RefundMethod"),
                            rs.getString("Reason"),
                            rs.getDate("RefundDate")
                    });
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching RefundDetails: " + e.getMessage());
        }

        return list;
    }

    // Fetch all refund items (for showing in RefundPanel without selecting a bill)
    public List<Object[]> getAllRefundDetails() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT rd.RefundDetailId, r.RefundId, rd.StockId, rd.Quantity, rd.Amount, " +
                "r.RefundMethod, r.Reason, r.RefundDate " +
                "FROM RefundDetails rd " +
                "JOIN Refund r ON rd.RefundId = r.RefundId";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("RefundDetailId"),
                        rs.getInt("RefundId"),
                        rs.getInt("StockId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("Amount"),
                        rs.getString("RefundMethod"),
                        rs.getString("Reason"),
                        rs.getDate("RefundDate")
                });
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all RefundDetails: " + e.getMessage());
        }

        return list;
    }

    // Get total refunded quantity for a stock in a bill
    public int getRefundedQuantity(int billId, int stockId) {
        int total = 0;
        String sql = "SELECT IFNULL(SUM(rd.Quantity),0) AS TotalQty " +
                "FROM RefundDetails rd " +
                "JOIN Refund r ON rd.RefundId = r.RefundId " +
                "WHERE r.BillId=? AND rd.StockId=?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            ps.setInt(2, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt("TotalQty");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching refunded quantity: " + e.getMessage());
        }

        return total;
    }

}
