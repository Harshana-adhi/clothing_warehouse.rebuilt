package GUI;

import Models.User;
import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private User loggedInUser; // Store current user

    private final Color backgroundColor = new Color(245, 245, 245);

    public MainFrame() {
        setTitle("Clothing Warehouse Management System");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(backgroundColor);

        // Main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(backgroundColor);
        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Welcome! Please login.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(230, 230, 230));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);

        // Show login first
        showLoginPanel();

        setVisible(true);
    }

    public void showLoginPanel() {
        mainPanel.removeAll();
        mainPanel.add(new LoginPanel(this), "Login");
        cardLayout.show(mainPanel, "Login");
        statusLabel.setText("Please login to continue.");
        revalidate();
        repaint();
    }

    public void showSignupPanel() {
        mainPanel.removeAll();
        mainPanel.add(new SignupPanel(this), "Signup");
        cardLayout.show(mainPanel, "Signup");
        statusLabel.setText("Create a new account.");
        revalidate();
        repaint();
    }

    // Add this import at the top


    public void loadDashboardByRole(String role, User user) {
        loggedInUser = user; // store current user
        mainPanel.removeAll();

        switch (role.toLowerCase()) {

            // ---------------------------------------------------------
            // ADMIN
            // ---------------------------------------------------------
            case "admin":
                AdminDashboardPanel adminDashboard = new AdminDashboardPanel(this);
                EmployeePanel adminEmployeePanel = new EmployeePanel(loggedInUser);
                CustomerPanel adminCustomerPanel = new CustomerPanel(loggedInUser);
                SupplierPanel adminSupplierPanel = new SupplierPanel(loggedInUser);
                StockManagementPanel adminStockPanel = new StockManagementPanel(loggedInUser); // NEW

                mainPanel.add(adminDashboard, "Dashboard");
                mainPanel.add(adminEmployeePanel, "Employee");
                mainPanel.add(adminCustomerPanel, "Customer");
                mainPanel.add(adminSupplierPanel, "Supplier");
                mainPanel.add(adminStockPanel, "StockManagement"); // NEW

                statusLabel.setText("Welcome Admin!");
                break;

            // ---------------------------------------------------------
            // MANAGER
            // ---------------------------------------------------------
            case "manager":
                ManagerDashboardPanel managerDashboard = new ManagerDashboardPanel(this);
                EmployeePanel managerEmployeePanel = new EmployeePanel(loggedInUser);
                CustomerPanel managerCustomerPanel = new CustomerPanel(loggedInUser);
                SupplierPanel managerSupplierPanel = new SupplierPanel(loggedInUser);
                StockManagementPanel managerStockPanel = new StockManagementPanel(loggedInUser); // NEW

                mainPanel.add(managerDashboard, "Dashboard");
                mainPanel.add(managerEmployeePanel, "Employee");
                mainPanel.add(managerCustomerPanel, "Customer");
                mainPanel.add(managerSupplierPanel, "Supplier");
                mainPanel.add(managerStockPanel, "StockManagement"); // NEW

                statusLabel.setText("Welcome Manager!");
                break;

            // ---------------------------------------------------------
            // STAFF
            // ---------------------------------------------------------
            case "staff":
                StaffDashboardPanel staffDashboard = new StaffDashboardPanel(this);
                CustomerPanel staffCustomerPanel = new CustomerPanel(loggedInUser);
                SupplierPanel staffSupplierPanel = new SupplierPanel(loggedInUser);
                StockManagementPanel staffStockPanel = new StockManagementPanel(loggedInUser); // NEW

                mainPanel.add(staffDashboard, "Dashboard");
                mainPanel.add(staffCustomerPanel, "Customer");
                mainPanel.add(staffSupplierPanel, "Supplier");
                mainPanel.add(staffStockPanel, "StockManagement"); // NEW

                statusLabel.setText("Welcome Staff!");
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown role: " + role);
                showLoginPanel();
                return;
        }

        cardLayout.show(mainPanel, "Dashboard");
        revalidate();
        repaint();
    }


    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        statusLabel.setText("You are in " + panelName + " module");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
