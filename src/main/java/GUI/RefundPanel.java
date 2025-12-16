package GUI;

import DAO.*;
import Models.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RefundPanel extends JPanel {

    private User currentUser;
    private JTable tblBills, tblBillItems, tblRefundDetails;
    private DefaultTableModel billTableModel, billItemsTableModel, refundDetailsTableModel;
    private JButton btnSelectQty, btnProcessRefund, btnRefresh, btnBack;
    private JTextField txtRefundReason;

    private BillingDAO billingDAO;
    private BillDetailsDAO billDetailsDAO;
    private ClothingStockDAO stockDAO;
    private RefundDAO refundDAO;

    private List<Billing> billList;
    private List<BillDetails> currentBillItems;

    // --- STYLING CONSTANTS COPIED FROM EmployeePanel ---
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonhover = new Color(200, 100, 136);
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color buttonHover = new Color(0, 137, 123);

    // NEW CONSTANTS FOR TABLE STYLING
    private final Color formBackground = Color.WHITE; // Used for odd rows
    private final Color tableSelection = new Color(200, 230, 201);
    private final Color tableHeaderBackground = new Color(50, 50, 50);
    private final Color tableHeaderForeground = Color.WHITE;
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16); // Used for table cells
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14); // Used for TitledBorder
    private final Font buttonFontLarge = new Font("Segoe UI", Font.BOLD, 16);
    // ---------------------------------------------------


    public RefundPanel(User user) {
        this.currentUser = user;
        this.billingDAO = new BillingDAO();
        this.billDetailsDAO = new BillDetailsDAO();
        this.stockDAO = new ClothingStockDAO();
        this.refundDAO = new RefundDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // ==================== TABLES ====================
        // BILL TABLE
        billTableModel = new DefaultTableModel(
                new String[]{"Bill ID", "Customer", "Date", "Amount", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBills = new JTable(billTableModel);
        styleTable(tblBills);

        // BILL ITEMS TABLE
        billItemsTableModel = new DefaultTableModel(
                new String[]{"StockId", "Cloth", "Category", "Color", "Size", "Qty Sold"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBillItems = new JTable(billItemsTableModel);
        styleTable(tblBillItems);

        JSplitPane splitTop = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblBills), new JScrollPane(tblBillItems));
        splitTop.setResizeWeight(0.4);
        splitTop.setOneTouchExpandable(true);

        // REFUND DETAILS TABLE
        refundDetailsTableModel = new DefaultTableModel(
                new String[]{"RefundDetailId", "RefundId", "StockId", "Qty", "Amount", "Method", "Reason", "Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRefundDetails = new JTable(refundDetailsTableModel);
        styleTable(tblRefundDetails);

        JSplitPane tablesSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitTop, new JScrollPane(tblRefundDetails));
        tablesSplit.setResizeWeight(0.7);
        tablesSplit.setOneTouchExpandable(true);

        // ==================== RIGHT PANEL FOR TABLES ====================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(tablesSplit, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        // ==================== LEFT PANEL FOR BUTTONS ====================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);

        // Use custom color for TitledBorder
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Refund Options",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                labelFont, // Use labelFont for title
                buttonColor
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        int y = 0;

        // Refund reason
        leftPanel.add(new JLabel("Refund Reason:"), gbc);
        gbc.gridy = ++y;
        txtRefundReason = new JTextField();
        txtRefundReason.setFont(inputFont); // Apply input font
        leftPanel.add(txtRefundReason, gbc);

        // Buttons
        btnSelectQty = new JButton("Select Qty to Refund");
        btnProcessRefund = new JButton("Process Refund");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back");

        // Apply styles
        stylePrimaryButton(btnSelectQty);
        stylePrimaryButton(btnProcessRefund);
        stylePrimaryButton(btnRefresh);
        styleBackButton(btnBack);

        gbc.gridy = ++y; leftPanel.add(btnSelectQty, gbc);
        gbc.gridy = ++y; leftPanel.add(btnProcessRefund, gbc);
        gbc.gridy = ++y; leftPanel.add(btnRefresh, gbc);
        gbc.gridy = ++y; leftPanel.add(btnBack, gbc);

        add(leftPanel, BorderLayout.WEST);

        // ==================== EVENTS ====================
        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof MainFrame) ((MainFrame) topFrame).switchPanel("Dashboard");
        });

        btnRefresh.addActionListener(e -> {
            refreshBillTable();
            loadSelectedBillItems();
            loadRefundDetails();
        });

        tblBills.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBillItems();
                loadRefundDetails();
            }
        });

        btnSelectQty.addActionListener(e -> openRefundQuantityDialog());
        btnProcessRefund.addActionListener(e -> processRefund());

        refreshBillTable();
        loadRefundDetails(); // load all refunds by default
    }

    // ==================== STYLING METHODS ====================

    // Primary Button Style
    private void stylePrimaryButton(JButton btn){
        btn.setBackground(buttonColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(buttonColor, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(buttonHover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(buttonColor); }
        });
    }

    // Back Button Style
    private void styleBackButton(JButton btn){
        btn.setBackground(backButtonColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(buttonFontLarge);
        btn.setBorder(BorderFactory.createLineBorder(backButtonColor, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(backButtonhover); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(backButtonColor); }
        });
    }

    private void styleTable(JTable table){
        // 1. Table properties
        table.setFont(inputFont);
        table.setRowHeight(35); // Set row height to 35
        table.setSelectionBackground(tableSelection);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0)); // Remove vertical intercell spacing

        // 2. Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont); // Set header font to size 16 bold
        header.setBackground(tableHeaderBackground); // Dark gray background
        header.setForeground(tableHeaderForeground); // White foreground
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // 3. Cell rendering (Zebra stripes and padding)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Set padding similar to EmployeePanel
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                // Zebra stripes (White or very light gray)
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : formBackground);
                }

                // Original horizontal alignment logic retained from RefundPanel's styleTable
                setHorizontalAlignment(JLabel.CENTER);

                return c;
            }
        });
    }

    // ==================== TABLE DATA METHODS ====================
    private void refreshBillTable() {
        billTableModel.setRowCount(0);
        billList = billingDAO.getAll();
        for (Billing b : billList) {
            billTableModel.addRow(new Object[]{
                    b.getBillId(), b.getCustomerId(), b.getBillDate(), b.getAmount(), b.getBillStatus()
            });
        }
    }

    private void loadSelectedBillItems() {
        int row = tblBills.getSelectedRow();
        if (row < 0) {
            billItemsTableModel.setRowCount(0);
            return;
        }

        int billId = Integer.parseInt(tblBills.getValueAt(row, 0).toString());
        currentBillItems = billDetailsDAO.getByBillId(billId);
        billItemsTableModel.setRowCount(0);

        for (BillDetails bd : currentBillItems) {
            Object[] info = stockDAO.getStockInfoByStockId(bd.getStockId());
            String category = info.length > 4 && info[4] != null ? info[4].toString() : "-";

            billItemsTableModel.addRow(new Object[]{
                    bd.getStockId(), info[0], category, info[1], info[2], bd.getQuantity()
            });
        }
    }

    private void loadRefundDetails() {
        refundDetailsTableModel.setRowCount(0);

        int row = tblBills.getSelectedRow();
        List<Object[]> refundList;

        if (row >= 0) {
            int billId = Integer.parseInt(tblBills.getValueAt(row, 0).toString());
            refundList = refundDAO.getRefundDetailsByBillId(billId);
        } else {
            refundList = refundDAO.getAllRefundDetails();
        }

        for (Object[] r : refundList) {
            refundDetailsTableModel.addRow(r);
        }
    }

    // ==================== REFUND LOGIC ====================
    private void openRefundQuantityDialog() {
        int selectedRow = tblBillItems.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return;
        }

        int stockId = (int) tblBillItems.getValueAt(selectedRow, 0);
        int soldQty = (int) tblBillItems.getValueAt(selectedRow, 5);

        String input = JOptionPane.showInputDialog(this,
                "Enter quantity to refund (Max " + soldQty + "):", "Refund Quantity",
                JOptionPane.PLAIN_MESSAGE);

        if (input == null) return;

        int qty;
        try { qty = Integer.parseInt(input); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this,"Invalid number"); return; }

        if (qty < 1 || qty > soldQty) {
            JOptionPane.showMessageDialog(this,"Quantity must be between 1 and " + soldQty);
            return;
        }

        tblBillItems.setValueAt(qty, selectedRow, 5); // overwrite soldQty temporarily
    }

    private void processRefund() {
        int billRow = tblBills.getSelectedRow();
        if (billRow < 0) return;

        int billId = Integer.parseInt(tblBills.getValueAt(billRow, 0).toString());
        String refundReason = txtRefundReason.getText().trim();
        if(refundReason.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter refund reason.");
            return;
        }

        // First, check if any quantity is selected for refund
        boolean anyRefund = false;
        for (int i = 0; i < tblBillItems.getRowCount(); i++) {
            int refundQty = (int) tblBillItems.getValueAt(i, 5);
            if (refundQty > 0) {
                anyRefund = true;
                break;
            }
        }

        if(!anyRefund){
            JOptionPane.showMessageDialog(this,"No quantity selected for refund.");
            return;
        }

        //  Insert a single Refund record for this refund action
        Refund refund = new Refund();
        refund.setBillId(billId);
        refund.setCustomerId(tblBills.getValueAt(billRow, 1).toString());
        refund.setRefundDate(new java.util.Date());
        refund.setRefundMethod("Cash");
        refund.setReason(refundReason);

        // Total amount will be calculated as sum of all refunded items
        double totalRefundAmount = 0;
        for (int i = 0; i < tblBillItems.getRowCount(); i++) {
            int stockId = (int) tblBillItems.getValueAt(i, 0);
            int refundQty = (int) tblBillItems.getValueAt(i, 5);
            if (refundQty <= 0) continue;

            BillDetails bd = null;
            for(BillDetails b: currentBillItems){
                if(b.getStockId() == stockId){ bd = b; break; }
            }
            if(bd == null) continue;

            double amountPerUnit = bd.getTotalAmount().doubleValue() / bd.getQuantity();
            totalRefundAmount += refundQty * amountPerUnit;
        }
        refund.setAmount(totalRefundAmount);

        // Insert Refund table once
        int refundId = refundDAO.insert(refund);
        if(refundId <= 0){
            JOptionPane.showMessageDialog(this,"Failed to create refund record.");
            return;
        }

        // 2⃣ Insert all refunded items into RefundDetails
        for (int i = 0; i < tblBillItems.getRowCount(); i++) {
            int stockId = (int) tblBillItems.getValueAt(i, 0);
            int refundQty = (int) tblBillItems.getValueAt(i, 5);
            if(refundQty <= 0) continue;

            BillDetails bd = null;
            for(BillDetails b: currentBillItems){
                if(b.getStockId() == stockId){ bd = b; break; }
            }
            if(bd == null) continue;

            double amountPerUnit = bd.getTotalAmount().doubleValue() / bd.getQuantity();
            double refundAmount = refundQty * amountPerUnit;

            refundDAO.insertRefundDetail(refundId, stockId, refundQty, refundAmount);

            // Update stock quantity
            stockDAO.increaseStockQuantity(stockId, refundQty);

            // Update bill details quantity
            bd.setQuantity(bd.getQuantity() - refundQty);
            billDetailsDAO.updateBillDetailQuantity(bd);
        }
        // 3️ Update bill status
        boolean allRefunded = currentBillItems.stream().allMatch(bd -> bd.getQuantity() == 0);
        String newStatus = allRefunded ? "Refunded" : "Partially Refunded";
        billingDAO.updateBillStatus(billId, newStatus);

        JOptionPane.showMessageDialog(this,"Refund processed successfully.");

        txtRefundReason.setText("");
        loadSelectedBillItems();
        loadRefundDetails();
        refreshBillTable();
    }
}