package DAO;

import DBConnection.DBConnect;
import Models.ClothingItem;
import Models.User; // Assuming Models.User exists
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClothingItemDAO {

    // --- RBAC Check: Staff cannot delete, but can add/update item details ---
    private boolean hasPermission(User user, String operation) {
        if (user == null) return false;
        String role = user.getRole().toLowerCase();
        switch (operation.toLowerCase()) {
            case "delete":
                return role.equals("admin") || role.equals("manager");
            case "add":
            case "update":
            case "view":
            case "search":
                // NOTE: Staff can update/add ITEM definitions (per prompt, staff only restricted on DELETE)
                return true;
            default:
                return false;
        }
    }

    public boolean insert(ClothingItem item, User user) {
        if (!hasPermission(user, "add")) return false;
        if (item == null || item.getClothId().isEmpty()) return false;

        String sql = "INSERT INTO ClothingItem (ClothId, Color, Material, category, Price, SupplierId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getClothId());
            ps.setString(2, item.getColor());
            ps.setString(3, item.getMaterial());
            ps.setString(4, item.getCategory());
            ps.setBigDecimal(5, item.getPrice());
            ps.setString(6, item.getSupplierId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error inserting ClothingItem: " + e.getMessage());
            return false;
        }
    }

    public boolean update(ClothingItem item, User user) {
        if (!hasPermission(user, "update")) return false;
        if (item == null || item.getClothId().isEmpty()) return false;

        String sql = "UPDATE ClothingItem SET Color = ?, Material = ?, category = ?, Price = ?, SupplierId = ? WHERE ClothId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getColor());
            ps.setString(2, item.getMaterial());
            ps.setString(3, item.getCategory());
            ps.setBigDecimal(4, item.getPrice());
            ps.setString(5, item.getSupplierId());
            ps.setString(6, item.getClothId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating ClothingItem: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String clothId, User user) {
        // Staff cannot delete
        if (!hasPermission(user, "delete")) return false;
        if (clothId.isEmpty()) return false;

        String sql = "DELETE FROM ClothingItem WHERE ClothId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clothId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting ClothingItem: " + e.getMessage());
            return false;
        }
    }

    // Method to get a single item (useful for pre-populating forms)
    public ClothingItem getItemById(String clothId) {
        String sql = "SELECT * FROM ClothingItem WHERE ClothId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clothId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ClothingItem(
                            rs.getString("ClothId"),
                            rs.getString("Color"),
                            rs.getString("Material"),
                            rs.getString("category"),
                            rs.getBigDecimal("Price"),
                            rs.getString("SupplierId")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching item: " + e.getMessage());
        }
        return null;
    }
}