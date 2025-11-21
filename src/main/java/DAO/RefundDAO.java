package DAO;

import DBConnection.DBConnect;
import Models.Refund;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefundDAO {

    // 1. Insert new Refund
    public boolean insert(Refund refund) {
        String sql = "INSERT INTO Refund (RefundDate, Amount, Reason, RefundMethod, CustomerId, BillId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String[] values = refund.toValuesArray();
            pstmt.setDate(1, Date.valueOf(values[0]));          // RefundDate
            pstmt.setDouble(2, Double.parseDouble(values[1]));  // Amount
            pstmt.setString(3, values[2]);                      // Reason
            pstmt.setString(4, values[3]);                      // RefundMethod
            pstmt.setString(5, values[4]);                      // CustomerId
            pstmt.setInt(6, Integer.parseInt(values[5]));       // BillId

            int rows = pstmt.executeUpdate();
            // Optional: get generated RefundId
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                // Optional: store in Refund object if needed
            }

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Refund: " + e.getMessage());
            return false;
        }
    }

    // 2. Update Refund by RefundId
    public boolean update(Refund refund, int refundId) {
        String sql = "UPDATE Refund SET RefundDate = ?, Amount = ?, Reason = ?, RefundMethod = ?, CustomerId = ?, BillId = ? WHERE RefundId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] values = refund.toValuesArray();
            pstmt.setDate(1, Date.valueOf(values[0]));          // RefundDate
            pstmt.setDouble(2, Double.parseDouble(values[1]));  // Amount
            pstmt.setString(3, values[2]);                      // Reason
            pstmt.setString(4, values[3]);                      // RefundMethod
            pstmt.setString(5, values[4]);                      // CustomerId
            pstmt.setInt(6, Integer.parseInt(values[5]));       // BillId
            pstmt.setInt(7, refundId);                          // RefundId

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Refund: " + e.getMessage());
            return false;
        }
    }

    // 3. Delete Refund by RefundId
    public boolean delete(int refundId) {
        String sql = "DELETE FROM Refund WHERE RefundId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, refundId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting Refund: " + e.getMessage());
            return false;
        }
    }

    // 4. Get Refund by ID
    public Refund getById(int refundId) {
        String sql = "SELECT * FROM Refund WHERE RefundId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, refundId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Refund(
                        rs.getDate("RefundDate").toString(),
                        rs.getDouble("Amount"),
                        rs.getString("Reason"),
                        rs.getString("RefundMethod"),
                        rs.getString("CustomerId"),
                        String.valueOf(rs.getInt("BillId"))
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Refund: " + e.getMessage());
        }
        return null;
    }

    // 5. Get all Refunds
    public List<Refund> getAll() {
        List<Refund> list = new ArrayList<>();
        String sql = "SELECT * FROM Refund";

        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Refund refund = new Refund(
                        rs.getDate("RefundDate").toString(),
                        rs.getDouble("Amount"),
                        rs.getString("Reason"),
                        rs.getString("RefundMethod"),
                        rs.getString("CustomerId"),
                        String.valueOf(rs.getInt("BillId"))
                );
                list.add(refund);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all Refunds: " + e.getMessage());
        }

        return list;
    }

    // 6. Get Refunds by CustomerId
    public List<Refund> getByCustomerId(String customerId) {
        List<Refund> list = new ArrayList<>();
        String sql = "SELECT * FROM Refund WHERE CustomerId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Refund refund = new Refund(
                        rs.getDate("RefundDate").toString(),
                        rs.getDouble("Amount"),
                        rs.getString("Reason"),
                        rs.getString("RefundMethod"),
                        rs.getString("CustomerId"),
                        String.valueOf(rs.getInt("BillId"))
                );
                list.add(refund);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Refunds by CustomerId: " + e.getMessage());
        }

        return list;
    }
}
