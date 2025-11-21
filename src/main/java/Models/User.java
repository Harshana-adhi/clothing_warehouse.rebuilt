package Models;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String employeeId; // NEW: Employee ID

    // Default constructor
    public User() {}

    // Constructor for fetching from DB
    public User(int userId, String username, String password, String role, String employeeId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
    }

    // Constructor for creating new user
    public User(String username, String password, String role, String employeeId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
    }

    // Getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
