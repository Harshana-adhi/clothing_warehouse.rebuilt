package GUI;

import javax.swing.*;
import java.awt.*;

public class StaffDashboardPanel extends JPanel {

    public StaffDashboardPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Staff Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // changed to 3x2
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 30, 100));

        JButton btnBilling = createButton("Sales & Billing");
        btnBilling.addActionListener(e -> mainFrame.switchPanel("Billing"));

        JButton btnStockManagement = createButton("Stock Management");
        btnStockManagement.addActionListener(e -> mainFrame.switchPanel("StockManagement"));

        JButton btnViewCustomers = createButton("View Customers");
        btnViewCustomers.addActionListener(e -> mainFrame.switchPanel("Customer"));

        JButton btnViewSuppliers = createButton("View Suppliers");
        btnViewSuppliers.addActionListener(e -> mainFrame.switchPanel("Supplier"));

        JButton btnRefund = createButton("Refund"); // NEW BUTTON
        btnRefund.addActionListener(e -> mainFrame.switchPanel("Refund")); // connect to Refund panel

        // Add buttons in order
        buttonPanel.add(btnBilling);
        buttonPanel.add(btnStockManagement);
        buttonPanel.add(btnViewCustomers);
        buttonPanel.add(btnViewSuppliers);
        buttonPanel.add(btnRefund);
        buttonPanel.add(new JLabel("")); // filler for grid balance

        // Back Button
        JButton btnBack = createBackButton("Back to Login");
        btnBack.addActionListener(e -> mainFrame.showLoginPanel());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(245, 245, 245));
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
        btn.setBackground(new Color(156, 39, 176));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(142, 36, 170)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(156, 39, 176)); }
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
