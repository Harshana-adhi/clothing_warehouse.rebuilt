package DAO;

import DBConnection.DBConnect;
import Models.BillDetails;
import Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDetailsDAO {

    private boolean hasPermission(User user, String operation){
        if(user == null) return false;
        String role = user.getRole().toLowerCase();
        switch(operation.toLowerCase()){
            case "add":
            case "view":
                return true; // Staff can add/view
            case "update":
            case "delete":
                return role.equals("admin") || role.equals("manager");
            default:
                return false;
        }
    }

    // Insert a BillDetail row
    public boolean insert(BillDetails detail, User user){
        if(!hasPermission(user, "add") || detail == null) return false;

        String sql = "INSERT INTO BillDetails (BillId, StockId, Quantity, TotalAmount) VALUES (?,?,?,?)";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            ps.setInt(1, detail.getBillId());
            if(detail.getStockId() != null) ps.setInt(2, detail.getStockId());
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getTotalAmount());

            int affected = ps.executeUpdate();
            if(affected > 0){
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()) detail.setBillDetailId(rs.getInt(1));
                }
                return true;
            }

        } catch(SQLException e){
            System.out.println("Error inserting BillDetails: " + e.getMessage());
        }
        return false;
    }

    // Get all bill details by BillId
    public List<BillDetails> getByBillId(int billId){
        List<BillDetails> list = new ArrayList<>();
        String sql = "SELECT BillDetailId, BillId, StockId, Quantity, TotalAmount FROM BillDetails WHERE BillId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, billId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(new BillDetails(
                            rs.getInt("BillDetailId"),
                            rs.getInt("BillId"),
                            rs.getObject("StockId") != null ? rs.getInt("StockId") : null,
                            rs.getInt("Quantity"),
                            rs.getBigDecimal("TotalAmount")
                    ));
                }
            }
        } catch(SQLException e){
            System.out.println("Error fetching BillDetails: " + e.getMessage());
        }
        return list;
    }

    // Delete bill detail (admin/manager)
    public boolean delete(int billDetailId, User user){
        if(!hasPermission(user, "delete")) return false;

        String sql = "DELETE FROM BillDetails WHERE BillDetailId=?";
        try(Connection conn = DBConnect.getDBConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, billDetailId);
            return ps.executeUpdate() > 0;
        } catch(SQLException e){
            System.out.println("Error deleting BillDetails: " + e.getMessage());
        }
        return false;
    }
}
