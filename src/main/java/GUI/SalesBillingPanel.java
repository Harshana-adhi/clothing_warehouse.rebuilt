package GUI;

import javax.swing.*;
import java.awt.*;
import Models.User;

public class SalesBillingPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cards;

    private PaymentPanel paymentPanel;
    private BillingPanel billingPanel;

    private JButton btnPayments, btnBilling;
    private User currentUser;
    private MainFrame mainFrame;

    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color buttonHover = new Color(0, 137, 123);

    public SalesBillingPanel(MainFrame frame, User user) {
        this.mainFrame = frame;
        this.currentUser = user;
        setLayout(new BorderLayout(10,10));

        // --- Top Buttons Panel ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(245, 245, 245));

        btnPayments = createStyledButton("Payments");
        btnBilling = createStyledButton("Billing");

        topPanel.add(btnPayments);
        topPanel.add(btnBilling);
        add(topPanel, BorderLayout.NORTH);

        // --- Card Layout Panel ---
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        paymentPanel = new PaymentPanel(currentUser);
        billingPanel = new BillingPanel(currentUser);

        cards.add(paymentPanel, "Payment");
        cards.add(billingPanel, "Billing");
        add(cards, BorderLayout.CENTER);

        // --- Button Actions ---
        btnPayments.addActionListener(e -> cardLayout.show(cards, "Payment"));
        btnBilling.addActionListener(e -> cardLayout.show(cards, "Billing"));
    }

    // --- Styled Buttons ---
    private JButton createStyledButton(String text){
        JButton btn = new JButton(text);
        btn.setFont(buttonFont);
        btn.setBackground(buttonColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){ btn.setBackground(buttonHover); }
            public void mouseExited(java.awt.event.MouseEvent e){ btn.setBackground(buttonColor); }
        });
        return btn;
    }
}
