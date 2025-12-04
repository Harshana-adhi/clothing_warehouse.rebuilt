package GUI;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboardPanel extends JPanel {

    public ManagerDashboardPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 200, 30, 200));

        JButton btnManageEmployees = createButton("Manage Employees");
        btnManageEmployees.addActionListener(e -> mainFrame.switchPanel("Employee"));

        JButton btnStockManagement = createButton("Stock Management");
        btnStockManagement.addActionListener(e -> mainFrame.switchPanel("StockManagement"));

        JButton btnSalesBilling = createButton("Sales & Billing");
        btnSalesBilling.addActionListener(e -> mainFrame.switchPanel("Billing")); // ✅ Fixed

        JButton btnReports = createButton("Reports");

        JButton btnManageCustomers = createButton("Manage Customers");
        btnManageCustomers.addActionListener(e -> mainFrame.switchPanel("Customer"));

        JButton btnSuppliers = createButton("Supplier Management");
        btnSuppliers.addActionListener(e -> mainFrame.switchPanel("Supplier"));

        // ADD buttons in order
        buttonPanel.add(btnManageEmployees);
        buttonPanel.add(btnStockManagement);
        buttonPanel.add(btnSalesBilling);
        buttonPanel.add(btnReports);
        buttonPanel.add(btnManageCustomers);
        buttonPanel.add(btnSuppliers);

        // --- Back Button ---
        JButton btnBack = createBackButton("Back to Login");
        btnBack.addActionListener(e -> mainFrame.showLoginPanel());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(245, 245, 245));
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        backPanel.add(btnBack);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(backPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(25, 118, 210)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(33, 150, 243)); }
        });

        return btn;
    }

    private JButton createBackButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 180, 140));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(0, 160, 125)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(0, 180, 140)); }
        });

        return btn;
    }
}
