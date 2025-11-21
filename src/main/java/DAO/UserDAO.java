package DAO;

import DBConnection.DBConnect;
import Models.User;

import java.sql.*;

public class UserDAO {

    // REGISTER USER
    public boolean signup(User user) {
        if (user == null || user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getRole().isEmpty()) {
            System.out.println("Invalid user data.");
            return false;
        }

        if (usernameExists(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            return false;
        }

        String sql = "INSERT INTO Users (Username, Password, Role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // TODO: Hash password in production
            pstmt.setString(3, user.getRole());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Signup Error: " + e.getMessage());
            return false;
        }
    }

    // LOGIN USER
    public User login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return null;

        String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // TODO: Compare hashed password if using hash

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role")
                );
            }

        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }

        return null;
    }

    // CHECK IF USERNAME EXISTS
    public boolean usernameExists(String username) {
        String sql = "SELECT UserId FROM Users WHERE Username = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Check User Error: " + e.getMessage());
            return false;
        }
    }
}
