package DAO;

import DBConnection.DBConnect;
import Models.ClothingItem;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingItemDAO {

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
                return true; // Staff can add/update/view/search
            default:
                return false;
        }
    }

    public boolean insert(ClothingItem item, User user) {
        if (!hasPermission(user, "add") || item == null || item.getClothId().isEmpty()) return false;
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
        if (!hasPermission(user, "update") || item == null || item.getClothId().isEmpty()) return false;
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
        if (!hasPermission(user, "delete") || clothId.isEmpty()) return false;
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

    public List<ClothingItem> getAll() {
        List<ClothingItem> list = new ArrayList<>();
        String sql = "SELECT * FROM ClothingItem";
        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ClothingItem(
                        rs.getString("ClothId"),
                        rs.getString("Color"),
                        rs.getString("Material"),
                        rs.getString("category"),
                        rs.getBigDecimal("Price"),
                        rs.getString("SupplierId")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all items: " + e.getMessage());
        }
        return list;
    }

    // --- SEARCH by ClothId, Color, Material, or Category ---
    public List<ClothingItem> search(String keyword) {
        List<ClothingItem> list = new ArrayList<>();
        String sql = "SELECT * FROM ClothingItem WHERE ClothId LIKE ? OR Color LIKE ? OR Material LIKE ? OR category LIKE ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern); ps.setString(2, pattern);
            ps.setString(3, pattern); ps.setString(4, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClothingItem(
                            rs.getString("ClothId"),
                            rs.getString("Color"),
                            rs.getString("Material"),
                            rs.getString("category"),
                            rs.getBigDecimal("Price"),
                            rs.getString("SupplierId")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching ClothingItems: " + e.getMessage());
        }
        return list;
    }
}
