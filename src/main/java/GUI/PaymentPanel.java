package GUI;

import DAO.PaymentDAO;
import Models.Payment;
import Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PaymentPanel extends JPanel {

    private PaymentDAO paymentDAO = new PaymentDAO();
    private User currentUser;

    private JTextField txtPaymentId, txtCustomerId, txtEmployeeId, txtPayType, txtFromDate, txtToDate;
    private JButton btnBack, btnSearch, btnClear, btnRefresh;
    private JTable paymentTable;
    private DefaultTableModel tableModel;

    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonHover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);

    public PaymentPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 10));
        setBackground(panelBackground);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Left Panel: Form + Buttons ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(panelBackground);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(formBackground);
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Filters"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 10;

        // --- Form Fields ---
        JLabel lblPaymentId = new JLabel("Payment ID:"); lblPaymentId.setFont(labelFont);
        txtPaymentId = new JTextField(); txtPaymentId.setFont(inputFont);

        JLabel lblCustomerId = new JLabel("Customer ID:"); lblCustomerId.setFont(labelFont);
        txtCustomerId = new JTextField(); txtCustomerId.setFont(inputFont);

        JLabel lblEmployeeId = new JLabel("Employee ID:"); lblEmployeeId.setFont(labelFont);
        txtEmployeeId = new JTextField(); txtEmployeeId.setFont(inputFont);

        JLabel lblPayType = new JLabel("Pay Type:"); lblPayType.setFont(labelFont);
        txtPayType = new JTextField(); txtPayType.setFont(inputFont);

        JLabel lblFromDate = new JLabel("From Date (yyyy-mm-dd):"); lblFromDate.setFont(labelFont);
        txtFromDate = new JTextField(); txtFromDate.setFont(inputFont);

        JLabel lblToDate = new JLabel("To Date (yyyy-mm-dd):"); lblToDate.setFont(labelFont);
        txtToDate = new JTextField(); txtToDate.setFont(inputFont);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblPaymentId, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtPaymentId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblCustomerId, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtCustomerId, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblEmployeeId, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtEmployeeId, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblPayType, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtPayType, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(lblFromDate, gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(txtFromDate, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(lblToDate, gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(txtToDate, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(formBackground);

        btnBack = createBackButton("Back");
        btnSearch = createStyledButton("Search");
        btnClear = createStyledButton("Clear");
        btnRefresh = createStyledButton("Refresh");

        buttonPanel.add(btnBack); buttonPanel.add(btnSearch);
        buttonPanel.add(btnClear); buttonPanel.add(btnRefresh);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Right Panel: Table ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(panelBackground);

        tableModel = new DefaultTableModel(new String[]{
                "Payment ID", "Pay Type", "Amount", "Pay Date", "Employee ID", "Customer ID"}, 0);
        paymentTable = new JTable(tableModel);
        paymentTable.setFont(inputFont);
        paymentTable.setRowHeight(35);
        paymentTable.setSelectionBackground(tableSelection);
        JScrollPane tableScroll = new JScrollPane(paymentTable);

        rightPanel.add(tableScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        // --- Load all payments ---
        loadPayments();

        // --- Button Actions ---
        btnSearch.addActionListener(e -> searchPayments());
        btnClear.addActionListener(e -> clearFilters());
        btnRefresh.addActionListener(e -> loadPayments());
        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            if(topFrame instanceof MainFrame){
                ((MainFrame) topFrame).switchPanel("Dashboard");
            }
        });
    }

    private JButton createStyledButton(String text){
        JButton btn = new JButton(text);
        btn.setBackground(buttonColor); btn.setForeground(Color.WHITE);
        btn.setFont(labelFont); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(buttonHover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(buttonColor); }
        });
        return btn;
    }

    private JButton createBackButton(String text){
        JButton btn = new JButton(text);
        btn.setBackground(backButtonColor); btn.setForeground(Color.WHITE);
        btn.setFont(labelFont); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e){ btn.setBackground(backButtonHover); }
            public void mouseExited(java.awt.event.MouseEvent e){ btn.setBackground(backButtonColor); }
        });
        return btn;
    }

    private void loadPayments(){
        tableModel.setRowCount(0);
        List<Payment> payments = paymentDAO.getAll(currentUser);
        for(Payment p : payments){
            tableModel.addRow(new Object[]{
                    p.getPaymentId(),
                    p.getPayType(),
                    p.getAmount(),
                    p.getPayDate(),
                    p.getEmployeeId(),
                    p.getCustomerId()
            });
        }
    }

    private void searchPayments(){
        String paymentIdText = txtPaymentId.getText().trim();
        String custId = txtCustomerId.getText().trim().toLowerCase();
        String empId = txtEmployeeId.getText().trim().toLowerCase();
        String payType = txtPayType.getText().trim().toLowerCase();
        String fromDate = txtFromDate.getText().trim();
        String toDate = txtToDate.getText().trim();

        tableModel.setRowCount(0);
        List<Payment> payments = paymentDAO.getAll(currentUser);

        for(Payment p : payments){
            boolean matches = true;

            if(!paymentIdText.isEmpty()){
                try{
                    int filterId = Integer.parseInt(paymentIdText);
                    if(p.getPaymentId() != filterId) matches = false;
                } catch(NumberFormatException e){ matches = false; }
            }

            if(!custId.isEmpty() && !p.getCustomerId().toLowerCase().contains(custId)) matches = false;
            if(!empId.isEmpty() && !p.getEmployeeId().toLowerCase().contains(empId)) matches = false;
            if(!payType.isEmpty() && !p.getPayType().toLowerCase().contains(payType)) matches = false;

            if(!fromDate.isEmpty() && p.getPayDate().toString().compareTo(fromDate) < 0) matches = false;
            if(!toDate.isEmpty() && p.getPayDate().toString().compareTo(toDate) > 0) matches = false;

            if(matches){
                tableModel.addRow(new Object[]{
                        p.getPaymentId(),
                        p.getPayType(),
                        p.getAmount(),
                        p.getPayDate(),
                        p.getEmployeeId(),
                        p.getCustomerId()
                });
            }
        }
    }

    private void clearFilters(){
        txtPaymentId.setText("");
        txtCustomerId.setText("");
        txtEmployeeId.setText("");
        txtPayType.setText("");
        txtFromDate.setText("");
        txtToDate.setText("");
        loadPayments();
    }
}
