package GUI;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {

    public AdminDashboardPanel(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 200, 30, 200));

        JButton btnManageEmployees = createButton("Manage Employees");
        btnManageEmployees.addActionListener(e -> mainFrame.switchPanel("Employee"));

        JButton btnManageUsers = createButton("Manage Users");
        btnManageUsers.addActionListener(e -> mainFrame.switchPanel("User")); // if UserPanel exists

        JButton btnWarehouseStock = createButton("Warehouse Stock");
        JButton btnPurchasesSuppliers = createButton("Purchases / Suppliers");
        JButton btnSalesBilling = createButton("Sales & Billing");
        JButton btnReports = createButton("Reports");

        buttonPanel.add(btnManageEmployees);
        buttonPanel.add(btnManageUsers);
        buttonPanel.add(btnWarehouseStock);
        buttonPanel.add(btnPurchasesSuppliers);
        buttonPanel.add(btnSalesBilling);
        buttonPanel.add(btnReports);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 137, 123));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 150, 136));
            }
        });

        return btn;
    }
}
