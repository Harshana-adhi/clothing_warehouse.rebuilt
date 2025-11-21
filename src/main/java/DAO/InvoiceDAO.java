package DAO;

import DBConnection.DBConnect;
import Models.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // 1. Insert new Invoice
    public boolean insert(Invoice inv) {
        String sql = "INSERT INTO Invoice (issueDate, BillId) VALUES (?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String[] values = inv.toValuesArray();
            pstmt.setDate(1, Date.valueOf(values[0])); // issueDate
            pstmt.setInt(2, Integer.parseInt(values[1])); // BillId

            int rows = pstmt.executeUpdate();
            // Optional: get generated InvoiceId
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                // set ID in model if needed
            }

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Invoice: " + e.getMessage());
            return false;
        }
    }

    // 2. Update Invoice
    public boolean update(Invoice inv, int invoiceId) {
        String sql = "UPDATE Invoice SET issueDate = ?, BillId = ? WHERE InvoiceId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] values = inv.toValuesArray();
            pstmt.setDate(1, Date.valueOf(values[0])); // issueDate
            pstmt.setInt(2, Integer.parseInt(values[1])); // BillId
            pstmt.setInt(3, invoiceId); // InvoiceId

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Invoice: " + e.getMessage());
            return false;
        }
    }

    // 3. Delete Invoice by InvoiceId
    public boolean delete(int invoiceId) {
        String sql = "DELETE FROM Invoice WHERE InvoiceId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting Invoice: " + e.getMessage());
            return false;
        }
    }

    // 4. Get Invoice by ID
    public Invoice getById(int invoiceId) {
        String sql = "SELECT * FROM Invoice WHERE InvoiceId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Invoice(
                        rs.getDate("issueDate").toString(),
                        String.valueOf(rs.getInt("BillId"))
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Invoice: " + e.getMessage());
        }
        return null;
    }

    // 5. Get all Invoices
    public List<Invoice> getAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM Invoice";

        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getDate("issueDate").toString(),
                        String.valueOf(rs.getInt("BillId"))
                );
                list.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all Invoices: " + e.getMessage());
        }

        return list;
    }

    // 6. Get Invoices by BillId
    public List<Invoice> getByBillId(int billId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM Invoice WHERE BillId = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getDate("issueDate").toString(),
                        String.valueOf(rs.getInt("BillId"))
                );
                list.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Invoices by BillId: " + e.getMessage());
        }

        return list;
    }
}
