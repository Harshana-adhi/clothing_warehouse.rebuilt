package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboardPanel extends JPanel {

    private final Color buttonColor = new Color(0, 150, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color dashboardBackBg = new Color(0, 180, 140);
    private final Color dashboardBackHover = new Color(0, 160, 125);
    private final Color panelBackground = new Color(245, 245, 245);

    public AdminDashboardPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(panelBackground);

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        buttonPanel.setBackground(panelBackground);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 200, 30, 200));

        // --- Buttons ---
        JButton btnManageEmployees = createButton("Manage Employees");
        btnManageEmployees.addActionListener(e -> mainFrame.switchPanel("Employee"));

        JButton btnManageUsers = createButton("Manage Users");
        btnManageUsers.addActionListener(e -> mainFrame.switchPanel("User"));

        JButton btnStockManagement = createButton("Stock Management");
        btnStockManagement.addActionListener(e -> mainFrame.switchPanel("StockManagement"));

        JButton btnSuppliers = createButton("Suppliers");
        btnSuppliers.addActionListener(e -> mainFrame.switchPanel("Supplier"));

        JButton btnSalesBilling = createButton("Sales & Billing");
        btnSalesBilling.addActionListener(e -> mainFrame.switchPanel("Billing"));

        JButton btnRefund = createButton("Refund");
        btnRefund.addActionListener(e -> mainFrame.switchPanel("Refund"));

        JButton btnManageCustomers = createButton("Manage Customers");
        btnManageCustomers.addActionListener(e -> mainFrame.switchPanel("Customer"));

        // Add buttons in 4x2 grid order
        buttonPanel.add(btnManageEmployees);
        buttonPanel.add(btnManageUsers); // ✅ Manage Users button stays
        buttonPanel.add(btnStockManagement);
        buttonPanel.add(btnSuppliers);
        buttonPanel.add(btnSalesBilling);
        buttonPanel.add(btnRefund);
        buttonPanel.add(btnManageCustomers);
        buttonPanel.add(new JLabel("")); // keep grid balanced

        JButton btnBack = createBackButton("Back to Login");
        btnBack.addActionListener(e -> mainFrame.showLoginPanel());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(panelBackground);
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));
        backPanel.add(btnBack);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(backPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(buttonColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(buttonHover); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(buttonColor); }
        });

        return btn;
    }

    private JButton createBackButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(dashboardBackBg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(dashboardBackHover); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(dashboardBackBg); }
        });

        return btn;
    }
}
