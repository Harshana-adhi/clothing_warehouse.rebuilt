package DAO;

import DBConnection.DBConnect;
import Models.ClothingStock;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

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

    // ================== STOCK UPDATE METHODS (NEW) ==================

    public int getAvailableQuantity(Connection conn, int stockId) throws SQLException {
        String sql = "SELECT Quantity FROM ClothingStock WHERE StockId=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("Quantity");
        }
        return 0;
    }

    public boolean reduceStockQuantity(Connection conn, int stockId, int qty) throws SQLException {
        String sql = "UPDATE ClothingStock SET Quantity = Quantity - ? WHERE StockId=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, stockId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================== EXISTING CODE (UNCHANGED) ==================

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

    public List<Object[]> getAllStockForBilling() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT cs.StockId, cs.ClothId, cs.Color, cs.Size, cs.Quantity, ci.RetailPrice, ci.Category " +
                "FROM ClothingStock cs JOIN ClothingItem ci ON cs.ClothId = ci.ClothId";

        try (Connection conn = DBConnect.getDBConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("StockId"),
                        rs.getString("ClothId"),
                        rs.getString("Color"),
                        rs.getString("Size"),
                        rs.getInt("Quantity"),
                        rs.getBigDecimal("RetailPrice"),
                        rs.getString("Category")
                });
            }
        } catch (SQLException e) {
            System.out.println("Billing Stock Load Error: " + e.getMessage());
        }
        return list;
    }

    public Object[] getStockInfoByStockId(int stockId) {
        String sql = "SELECT cs.ClothId, cs.Color, cs.Size, ci.RetailPrice, ci.Category " +
                "FROM ClothingStock cs JOIN ClothingItem ci ON cs.ClothId = ci.ClothId WHERE cs.StockId=?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stockId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                        rs.getString("ClothId"),
                        rs.getString("Color"),
                        rs.getString("Size"),
                        rs.getBigDecimal("RetailPrice"),
                        rs.getString("Category")
                };
            }
        } catch (SQLException e) {
            System.out.println("StockInfo Load Error: " + e.getMessage());
        }
        return null;
    }
}
