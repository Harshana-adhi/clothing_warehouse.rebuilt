package DAO;

import DBConnection.DBConnect;
import Models.User;
import DAO.EmployeeDAO;
import Models.Employee;
import utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ================= EXISTING CODE (Signup/Login/etc.) =================

    // REGISTER USER with employee rules
    public boolean signup(User user) {
        if (user == null || user.getUsername().isEmpty() || user.getPassword().isEmpty() || user.getRole().isEmpty()) {
            System.out.println("Invalid user data.");
            return false;
        }

        if (usernameExists(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            return false;
        }

        // --- Employee-related rules ---
        EmployeeDAO empDao = new EmployeeDAO();

        if ("Admin".equalsIgnoreCase(user.getRole())) {
            if (adminExists()) {
                System.out.println("Admin account already exists!");
                return false;
            }
            user.setEmployeeId(null);
        } else {
            if (user.getEmployeeId() == null || user.getEmployeeId().isEmpty()) {
                System.out.println("Employee ID required for Manager or Staff account.");
                return false;
            }

            Employee emp = empDao.getById(user.getEmployeeId());
            if (emp == null) {
                System.out.println("No employee found with ID: " + user.getEmployeeId());
                return false;
            }

            if ("Manager".equalsIgnoreCase(user.getRole())) {
                if (!"Manager".equalsIgnoreCase(emp.getEmpPosition())) {
                    System.out.println("Only employees with position 'Manager' can create Manager account.");
                    return false;
                }
                if (managerExists(user.getEmployeeId())) {
                    System.out.println("This employee already has a manager account.");
                    return false;
                }
            } else {
                if ("Manager".equalsIgnoreCase(emp.getEmpPosition())) {
                    System.out.println("Managers cannot create staff accounts.");
                    return false;
                }
                if (employeeHasAccount(user.getEmployeeId())) {
                    System.out.println("This employee already has an account.");
                    return false;
                }
            }
        }

        String sql = "INSERT INTO Users (Username, Password, Role, EmployeeId) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // HASH PASSWORD BEFORE SAVING
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmployeeId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Signup Error: " + e.getMessage());
            return false;
        }
    }

    // LOGIN USER (with hashed password)
    public User login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return null;

        String sql = "SELECT * FROM Users WHERE Username = ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                String storedHash = rs.getString("Password");

                // compare hashed password
                if (!PasswordUtil.checkPassword(password, storedHash)) {
                    return null; // WRONG PASSWORD
                }

                // password valid → return User object
                return new User(
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        storedHash,
                        rs.getString("Role"),
                        rs.getString("EmployeeId")
                );
            }

        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }

        return null;
    }

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

    private boolean adminExists() {
        String sql = "SELECT UserId FROM Users WHERE Role='Admin'";
        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next();
        } catch (SQLException e) {
            System.out.println("Check Admin Error: " + e.getMessage());
            return false;
        }
    }

    private boolean managerExists(String empId) {
        String sql = "SELECT UserId FROM Users WHERE Role='Manager' AND EmployeeId=?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Check Manager Error: " + e.getMessage());
            return false;
        }
    }

    private boolean employeeHasAccount(String empId) {
        String sql = "SELECT UserId FROM Users WHERE EmployeeId=?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Check Employee Account Error: " + e.getMessage());
            return false;
        }
    }

    // ================= NEW METHODS (Role-based Management) =================

    // Role permission check
    private boolean hasPermission(User currentUser, String operation, String targetRole) {
        if (currentUser == null) return false;
        String role = currentUser.getRole();
        switch (operation.toUpperCase()) {
            case "ADD":
            case "UPDATE":
            case "DELETE":
                if ("Admin".equalsIgnoreCase(role)) return true;
                if ("Manager".equalsIgnoreCase(role)) return "Staff".equalsIgnoreCase(targetRole);
                return false;
            case "VIEW":
                return "Admin".equalsIgnoreCase(role) || "Manager".equalsIgnoreCase(role);
            default:
                return false;
        }
    }

    // CREATE user (Admin/Manager)
    public boolean createUser(User newUser, User currentUser) {
        if (!hasPermission(currentUser, "ADD", newUser.getRole())) return false;

        String sql = "INSERT INTO Users (Username, Password, Role, EmployeeId) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // hash password
            String hashedPassword = PasswordUtil.hashPassword(newUser.getPassword());

            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, newUser.getRole());
            ps.setString(4, newUser.getEmployeeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Create User Error: " + e.getMessage());
            return false;
        }
    }

    // UPDATE user (Admin/Manager)
    public boolean updateUser(User userToUpdate, User currentUser) {
        if (!hasPermission(currentUser, "UPDATE", userToUpdate.getRole())) return false;

        String sql = "UPDATE Users SET Username=?, Password=?, Role=?, EmployeeId=? WHERE UserId=?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // hash password
            String hashedPassword = PasswordUtil.hashPassword(userToUpdate.getPassword());

            ps.setString(1, userToUpdate.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, userToUpdate.getRole());
            ps.setString(4, userToUpdate.getEmployeeId());
            ps.setInt(5, userToUpdate.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update User Error: " + e.getMessage());
            return false;
        }
    }


    // DELETE user (Admin/Manager)
    public boolean deleteUser(int userId, String targetRole, User currentUser) {
        // Prevent deleting Admin unless currentUser is Admin
        if ("Admin".equalsIgnoreCase(targetRole) && !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            System.out.println("Only Admin can delete Admin account!");
            return false;
        }

        if (!hasPermission(currentUser, "DELETE", targetRole)) return false;

        String sql = "DELETE FROM Users WHERE UserId=?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Delete User Error: " + e.getMessage());
            return false;
        }
    }


    // GET user by ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE UserId=?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("EmployeeId")
                );
            }

        } catch (SQLException e) {
            System.out.println("Get User Error: " + e.getMessage());
        }
        return null;
    }

    // GET all users (Admin sees all, Manager sees Staff only)
    public List<User> getAllUsers(User currentUser) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        try (Connection conn = DBConnect.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String role = rs.getString("Role");

                if (!hasPermission(currentUser, "VIEW", role)) continue;

                list.add(new User(
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        role,
                        rs.getString("EmployeeId")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Get All Users Error: " + e.getMessage());
        }

        return list;
    }

    // CHECK if username exists (for CRUD)
    public boolean userExists(String username) {
        String sql = "SELECT UserId FROM Users WHERE Username=?";
        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Check User Exists Error: " + e.getMessage());
            return false;
        }
    }

    // ADDED METHOD TO SUPPORT SEARCH FUNCTIONALITY
    public List<User> searchUsers(String keyword, User currentUser) {
        List<User> list = new ArrayList<>();
        // Search by Username OR EmployeeId
        String sql = "SELECT * FROM Users WHERE Username LIKE ? OR EmployeeId LIKE ?";

        try (Connection conn = DBConnect.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String role = rs.getString("Role");

                // Apply existing role-based view permission check
                if (!hasPermission(currentUser, "VIEW", role)) continue;

                list.add(new User(
                        rs.getInt("UserId"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        role,
                        rs.getString("EmployeeId")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Search Users Error: " + e.getMessage());
        }

        return list;
    }
}