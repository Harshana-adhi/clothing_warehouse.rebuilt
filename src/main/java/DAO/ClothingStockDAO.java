package DAO;

import DBConnection.DBConnect;
import Models.ClothingStock;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingStockDAO {

    private boolean hasPermission(User user, String operation) {
        if (user == null) return false;
        String role = user.getRole().toLowerCase();
        switch (operation.toLowerCase()) {
            case "restock":
            case "view":
                return role.equals("admin") || role.equals("manager") || role.equals("staff");
            default:
                return false;
        }
    }

    // Add or update stock
    public boolean restockItem(ClothingStock stock, User user) {
        if (!hasPermission(user, "restock") || stock == null || stock.getClothId().isEmpty()) return false;

        String checkSql = "SELECT Quantity FROM ClothingStock WHERE ClothId=? AND Size=?";
        String insertSql = "INSERT INTO ClothingStock (ClothId, Size, Quantity) VALUES (?,?,?)";
        String updateSql = "UPDATE ClothingStock SET Quantity = Quantity + ? WHERE ClothId=? AND Size=?";

        try (Connection conn = DBConnect.getDBConnection()) {
            // Check if stock exists
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, stock.getClothId());
                psCheck.setString(2, stock.getSize());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    // Update existing
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setInt(1, stock.getQuantity());
                        psUpdate.setString(2, stock.getClothId());
                        psUpdate.setString(3, stock.getSize());
                        return psUpdate.executeUpdate() > 0;
                    }
                } else {
                    // Insert new
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setString(1, stock.getClothId());
                        psInsert.setString(2, stock.getSize());
                        psInsert.setInt(3, stock.getQuantity());
                        return psInsert.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error restocking item: " + e.getMessage());
            return false;
        }
    }

    // Get stock details for display (all items)
    public List<String[]> getAllStockDetails() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT cs.ClothId, ci.Color, ci.Material, ci.category, cs.Size, cs.Quantity, ci.Price " +
                "FROM ClothingStock cs INNER JOIN ClothingItem ci ON cs.ClothId = ci.ClothId " +
                "ORDER BY cs.ClothId, cs.Size";
        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("ClothId"),
                        rs.getString("Color"),
                        rs.getString("Material"),
                        rs.getString("category"),
                        rs.getString("Size"),
                        String.valueOf(rs.getInt("Quantity")),
                        rs.getBigDecimal("Price").toString()
                });
            }
        }
        return list;
    }

    // --- NEW METHOD: get stock by ClothId for search ---
    public List<String[]> getStockByClothId(String clothId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT Size, Quantity FROM ClothingStock WHERE ClothId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clothId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("Size"),
                            String.valueOf(rs.getInt("Quantity"))
                    });
                }
            }
        }
        return list;
    }
}
