package DAO;

import DBConnection.DBConnect;
import Models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    // 1. Insert new Supplier
    public boolean insert(Supplier sup) {
        String sql = "INSERT INTO Supplier (SupplierId, SupName, TellNo) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sup.toValuesArray()[0]); // SupplierId
            pstmt.setString(2, sup.toValuesArray()[1]); // SupName
            pstmt.setString(3, sup.toValuesArray()[2]); // TellNo

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Supplier: " + e.getMessage());
            return false;
        }
    }

    // 2. Update Supplier
    public boolean update(Supplier sup) {
        String sql = "UPDATE Supplier SET SupName = ?, TellNo = ? WHERE SupplierId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sup.toValuesArray()[1]); // SupName
            pstmt.setString(2, sup.toValuesArray()[2]); // TellNo
            pstmt.setString(3, sup.toValuesArray()[0]); // SupplierId

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Supplier: " + e.getMessage());
            return false;
        }
    }

    // 3. Delete Supplier by SupplierId
    public boolean delete(String supplierId) {
        String sql = "DELETE FROM Supplier WHERE SupplierId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplierId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting Supplier: " + e.getMessage());
            return false;
        }
    }

    // 4. Get Supplier by ID
    public Supplier getById(String supplierId) {
        String sql = "SELECT * FROM Supplier WHERE SupplierId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Supplier(
                        rs.getString("SupplierId"),
                        rs.getString("SupName"),
                        rs.getString("TellNo")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Supplier: " + e.getMessage());
        }
        return null;
    }

    // 5. Get all Suppliers
    public List<Supplier> getAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier";

        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier sup = new Supplier(
                        rs.getString("SupplierId"),
                        rs.getString("SupName"),
                        rs.getString("TellNo")
                );
                list.add(sup);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all Suppliers: " + e.getMessage());
        }

        return list;
    }
}
