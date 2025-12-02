package DAO;

import DBConnection.DBConnect;
import Models.ClothingStock;
import Models.User; // Assuming Models.User exists
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingStockDAO {

    // --- RBAC Check: All roles can restock/view stock levels ---
    private boolean hasPermission(User user, String operation) {
        if (user == null) return false;
        return true;
    }

    /**
     * Inserts new stock or updates existing stock quantity by adding to it.
     */
    public boolean restockItem(ClothingStock stock, User user) {
        if (!hasPermission(user, "restock")) return false;
        if (stock == null || stock.getClothId().isEmpty() || stock.getSize().isEmpty() || stock.getQuantity() <= 0) return false;

        // Uses the ON DUPLICATE KEY UPDATE syntax for atomic restock
        String sql = "INSERT INTO ClothingStock (ClothId, Size, Quantity) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE Quantity = Quantity + VALUES(Quantity)";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stock.getClothId());
            ps.setString(2, stock.getSize());
            ps.setInt(3, stock.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error restocking item: " + e.getMessage());
            return false;
        }
    }

    /**
     * Decreases the stock level (used primarily during Billing/Sale transaction).
     * Requires an existing open Connection for transaction support.
     */
    public boolean reduceStock(String clothId, String size, int quantitySold, Connection conn) throws SQLException {
        // Permission check is usually done at the transaction start.

        // This query also ensures Quantity is sufficient before updating.
        String sql = "UPDATE ClothingStock SET Quantity = Quantity - ? WHERE ClothId = ? AND Size = ? AND Quantity >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantitySold);
            ps.setString(2, clothId);
            ps.setString(3, size);
            ps.setInt(4, quantitySold);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Fetches all stock items combined with item details for the JTable view.
     */
    public List<String[]> getAllStockDetails() {
        List<String[]> list = new ArrayList<>();
        // Query joins ClothingItem (CI) and ClothingStock (CS) to get all necessary display columns
        String sql = "SELECT CI.ClothId, CI.Color, CI.Material, CI.category, CS.Size, CS.Quantity, CI.Price " +
                "FROM ClothingStock CS JOIN ClothingItem CI ON CS.ClothId = CI.ClothId ORDER BY CI.ClothId, CS.Size";
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
        } catch (SQLException e) {
            System.out.println("Error fetching stock details: " + e.getMessage());
        }
        return list;
    }
}