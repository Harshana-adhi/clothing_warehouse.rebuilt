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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 200, 30, 200));

        JButton btnBilling = createButton("Billing");
        btnBilling.addActionListener(e -> mainFrame.switchPanel("Billing"));

        JButton btnViewStock = createButton("View Stock");
        btnViewStock.addActionListener(e -> mainFrame.switchPanel("WarehouseStock")); // Example

        buttonPanel.add(btnBilling);
        buttonPanel.add(btnViewStock);

        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(156, 39, 176));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(142, 36, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(156, 39, 176));
            }
        });

        return btn;
    }
}
