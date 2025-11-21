package DAO;

import DBConnection.DBConnect;
import Models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // 1. Insert new Customer
    public boolean insert(Customer cust) {
        String sql = "INSERT INTO Customer (CustomerId, CusName, Address, TelNo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cust.toValuesArray()[0]); // CustomerId
            pstmt.setString(2, cust.toValuesArray()[1]); // CusName
            pstmt.setString(3, cust.toValuesArray()[2]); // Address
            pstmt.setString(4, cust.toValuesArray()[3]); // TelNo

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting Customer: " + e.getMessage());
            return false;
        }
    }

    // 2. Update Customer
    public boolean update(Customer cust) {
        String sql = "UPDATE Customer SET CusName = ?, Address = ?, TelNo = ? WHERE CustomerId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cust.toValuesArray()[1]); // CusName
            pstmt.setString(2, cust.toValuesArray()[2]); // Address
            pstmt.setString(3, cust.toValuesArray()[3]); // TelNo
            pstmt.setString(4, cust.toValuesArray()[0]); // CustomerId

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating Customer: " + e.getMessage());
            return false;
        }
    }

    // 3. Delete Customer by CustomerId
    public boolean delete(String customerId) {
        String sql = "DELETE FROM Customer WHERE CustomerId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting Customer: " + e.getMessage());
            return false;
        }
    }

    // 4. Get Customer by ID
    public Customer getById(String customerId) {
        String sql = "SELECT * FROM Customer WHERE CustomerId = ?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getString("CustomerId"),
                        rs.getString("CusName"),
                        rs.getString("Address"),
                        rs.getString("TelNo")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching Customer: " + e.getMessage());
        }
        return null;
    }

    // 5. Get all Customers
    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer";

        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer cust = new Customer(
                        rs.getString("CustomerId"),
                        rs.getString("CusName"),
                        rs.getString("Address"),
                        rs.getString("TelNo")
                );
                list.add(cust);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all Customers: " + e.getMessage());
        }

        return list;
    }
}

