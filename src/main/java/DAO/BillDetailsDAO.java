package DAO;

import DBConnection.DBConnect;
import Models.BillDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDetailsDAO {

    // 1. Insert new BillDetails
    public boolean insert(BillDetails bd) {
        String sql = "INSERT INTO BillDetails (BillId, ClothId, Quantity, TotalAmount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String[] values = bd.toValuesArray();
            pstmt.setInt(1, Integer.parseInt(values[0])); // BillId
            pstmt.setString(2, values[1]); // ClothId
            pstmt.setInt(3, Integer.parseInt(values[2])); // Quantity
            pstmt.setDouble(4, Double.parseDouble(values[3])); // TotalAmount

            int rows = pstmt.executeUpdate();

            // Optional: get generated BillDetailId
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                // you can set ID in model if needed
            }

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting BillDetails: " + e.getMessage());
            return false;
        }
    }

    // 2. Update BillDetails
    public boolean update(BillDetails bd, int billDetailId) {
        String sql = "UPDATE BillDetails SET BillId = ?, ClothId = ?, Quantity = ?, TotalAmount = ? WHERE BillDetailId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] values = bd.toValuesArray();
            pstmt.setInt(1, Integer.parseInt(values[0])); // BillId
            pstmt.setString(2, values[1]); // ClothId
            pstmt.setInt(3, Integer.parseInt(values[2])); // Quantity
            pstmt.setDouble(4, Double.parseDouble(values[3])); // TotalAmount
            pstmt.setInt(5, billDetailId); // BillDetailId

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating BillDetails: " + e.getMessage());
            return false;
        }
    }

    // 3. Delete BillDetails by BillDetailId
    public boolean delete(int billDetailId) {
        String sql = "DELETE FROM BillDetails WHERE BillDetailId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billDetailId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting BillDetails: " + e.getMessage());
            return false;
        }
    }

    // 4. Get BillDetails by BillDetailId
    public BillDetails getById(int billDetailId) {
        String sql = "SELECT * FROM BillDetails WHERE BillDetailId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billDetailId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new BillDetails(
                        String.valueOf(rs.getInt("BillId")),
                        rs.getString("ClothId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalAmount")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching BillDetails: " + e.getMessage());
        }
        return null;
    }

    // 5. Get all BillDetails for a specific BillId
    public List<BillDetails> getByBillId(int billId) {
        List<BillDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM BillDetails WHERE BillId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BillDetails bd = new BillDetails(
                        String.valueOf(rs.getInt("BillId")),
                        rs.getString("ClothId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalAmount")
                );
                list.add(bd);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching BillDetails list: " + e.getMessage());
        }

        return list;
    }

    // 6. Get all BillDetails
    public List<BillDetails> getAll() {
        List<BillDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM BillDetails";

        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BillDetails bd = new BillDetails(
                        String.valueOf(rs.getInt("BillId")),
                        rs.getString("ClothId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalAmount")
                );
                list.add(bd);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all BillDetails: " + e.getMessage());
        }

        return list;
    }
}
