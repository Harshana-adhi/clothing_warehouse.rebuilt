package DAO;

import DBConnection.DBConnect;
import Models.RefundDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefundDetailDAO {

    // Insert a refund detail record
    public boolean insert(RefundDetail detail) {
        String sql = "INSERT INTO RefundDetails (RefundId, StockId, Quantity, Amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detail.getRefundId());
            ps.setInt(2, detail.getStockId());
            ps.setInt(3, detail.getQuantity());
            ps.setDouble(4, detail.getAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting RefundDetail: " + e.getMessage());
            return false;
        }
    }

    // Fetch all refund details for a specific refund
    public List<RefundDetail> getByRefundId(int refundId) {
        List<RefundDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM RefundDetails WHERE RefundId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, refundId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RefundDetail detail = new RefundDetail(
                        rs.getInt("RefundDetailId"),
                        rs.getInt("RefundId"),
                        rs.getInt("StockId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("Amount")
                );
                list.add(detail);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching RefundDetails: " + e.getMessage());
        }

        return list;
    }

    // Check already refunded quantity for a stock item
    public int getTotalRefundedQuantity(int refundId, int stockId) {
        int total = 0;
        String sql = "SELECT IFNULL(SUM(Quantity),0) AS TotalQty FROM RefundDetails WHERE RefundId = ? AND StockId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, refundId);
            ps.setInt(2, stockId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt("TotalQty");
            }

        } catch (SQLException e) {
            System.out.println("Error checking refunded quantity: " + e.getMessage());
        }

        return total;
    }
}
