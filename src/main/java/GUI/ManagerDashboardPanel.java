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

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 200, 30, 200));

        JButton btnManageEmployees = createButton("Manage Employees");
        btnManageEmployees.addActionListener(e -> mainFrame.switchPanel("Employee"));

        JButton btnWarehouseStock = createButton("Warehouse Stock");
        JButton btnSalesBilling = createButton("Sales & Billing");
        JButton btnReports = createButton("Reports");

        buttonPanel.add(btnManageEmployees);
        buttonPanel.add(btnWarehouseStock);
        buttonPanel.add(btnSalesBilling);
        buttonPanel.add(btnReports);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(25, 118, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 150, 243));
            }
        });

        return btn;
    }
}
