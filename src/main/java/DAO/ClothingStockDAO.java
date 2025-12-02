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

    // Add or update stock (now includes Color)
    public boolean restockItem(ClothingStock stock, User user) {
        if (!hasPermission(user, "restock") || stock == null || stock.getClothId() == null || stock.getClothId().isEmpty())
            return false;

        String checkSql = "SELECT Quantity FROM ClothingStock WHERE ClothId=? AND Color=? AND Size=?";
        String insertSql = "INSERT INTO ClothingStock (ClothId, Color, Size, Quantity) VALUES (?,?,?,?)";
        String updateSql = "UPDATE ClothingStock SET Quantity = Quantity + ? WHERE ClothId=? AND Color=? AND Size=?";

        try (Connection conn = DBConnect.getDBConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, stock.getClothId());
                psCheck.setString(2, stock.getColor());
                psCheck.setString(3, stock.getSize());
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setInt(1, stock.getQuantity());
                        psUpdate.setString(2, stock.getClothId());
                        psUpdate.setString(3, stock.getColor());
                        psUpdate.setString(4, stock.getSize());
                        return psUpdate.executeUpdate() > 0;
                    }
                } else {
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setString(1, stock.getClothId());
                        psInsert.setString(2, stock.getColor());
                        psInsert.setString(3, stock.getSize());
                        psInsert.setInt(4, stock.getQuantity());
                        return psInsert.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error restocking item: " + e.getMessage());
            return false;
        }
    }

    // Get stock details for display (all items). Join now uses ClothingStock.color (stock level color).
    public List<String[]> getAllStockDetails() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT cs.ClothId, cs.Color, ci.Material, ci.category, cs.Size, cs.Quantity, ci.Price " +
                "FROM ClothingStock cs INNER JOIN ClothingItem ci ON cs.ClothId = ci.ClothId " +
                "ORDER BY cs.ClothId, cs.Color, cs.Size";
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

    // Return stock rows for a given clothId: each row => [Color, Size, Quantity]
    public List<String[]> getStockByClothId(String clothId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT Color, Size, Quantity FROM ClothingStock WHERE ClothId = ? ORDER BY Color, Size";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clothId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("Color"),
                            rs.getString("Size"),
                            String.valueOf(rs.getInt("Quantity"))
                    });
                }
            }
        }
        return list;
    }

    // Optionally: delete stock by clothId/color/size if needed (not used in current panel)
    public boolean deleteStockRow(String clothId, String color, String size, User user) {
        if (user == null) return false;
        String role = user.getRole().toLowerCase();
        if (!(role.equals("admin") || role.equals("manager"))) return false;

        String sql = "DELETE FROM ClothingStock WHERE ClothId = ? AND Color = ? AND Size = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clothId);
            ps.setString(2, color);
            ps.setString(3, size);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting stock row: " + e.getMessage());
            return false;
        }
    }
}
