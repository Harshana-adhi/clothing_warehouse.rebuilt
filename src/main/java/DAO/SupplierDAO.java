package DAO;

import DBConnection.DBConnect;
import Models.Supplier;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    // Role-based access
    private boolean hasPermission(User user, String operation) {
        if (user == null) return false;
        String role = user.getRole().toLowerCase();
        switch (operation.toLowerCase()) {
            case "add":
            case "update":
            case "delete":
                return role.equals("admin") || role.equals("manager");
            case "view":
            case "search":
                return true; // All roles can view/search
            default:
                return false;
        }
    }

    public boolean insert(Supplier sup, User user) {
        if (!hasPermission(user, "add")) return false;
        if (sup == null || sup.getSupplierId().isEmpty() || exists(sup.getSupplierId())) return false;

        String sql = "INSERT INTO Supplier (SupplierId, SupName, TellNo) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sup.getSupplierId());
            ps.setString(2, sup.getSupName());
            ps.setString(3, sup.getTellNo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting Supplier: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Supplier sup, User user) {
        if (!hasPermission(user, "update")) return false;
        if (sup == null || sup.getSupplierId().isEmpty()) return false;

        String sql = "UPDATE Supplier SET SupName = ?, TellNo = ? WHERE SupplierId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sup.getSupName());
            ps.setString(2, sup.getTellNo());
            ps.setString(3, sup.getSupplierId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating Supplier: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String supplierId, User user) {
        if (!hasPermission(user, "delete")) return false;
        if (supplierId.isEmpty()) return false;

        String sql = "DELETE FROM Supplier WHERE SupplierId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting Supplier: " + e.getMessage());
            return false;
        }
    }

    public List<Supplier> getAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier";
        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Supplier(
                        rs.getString("SupplierId"),
                        rs.getString("SupName"),
                        rs.getString("TellNo")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Suppliers: " + e.getMessage());
        }
        return list;
    }

    public List<Supplier> searchByIdOrName(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier WHERE SupplierId LIKE ? OR SupName LIKE ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Supplier(
                        rs.getString("SupplierId"),
                        rs.getString("SupName"),
                        rs.getString("TellNo")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error searching Suppliers: " + e.getMessage());
        }
        return list;
    }

    public boolean exists(String supplierId) {
        String sql = "SELECT COUNT(*) FROM Supplier WHERE SupplierId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking Supplier ID: " + e.getMessage());
        }
        return false;
    }
}
