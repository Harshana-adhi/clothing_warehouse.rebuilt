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
import java.math.BigDecimal;

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

    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonhover = new Color(200, 100, 136);
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color buttonHover = new Color(0, 137, 123);

    private final Color formBackground = Color.WHITE;
    private final Color tableSelection = new Color(200, 230, 201);
    private final Color tableHeaderBackground = new Color(50, 50, 50);
    private final Color tableHeaderForeground = Color.WHITE;
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font buttonFontLarge = new Font("Segoe UI", Font.BOLD, 16);

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
        billTableModel = new DefaultTableModel(
                new String[]{"Bill ID", "Customer", "Date", "Amount", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBills = new JTable(billTableModel);
        styleTable(tblBills);

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

        refundDetailsTableModel = new DefaultTableModel(
                new String[]{"RefundDetailId", "RefundId", "StockId", "Qty", "Amount", "Method", "Reason", "Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRefundDetails = new JTable(refundDetailsTableModel);
        styleTable(tblRefundDetails);

        JSplitPane tablesSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitTop, new JScrollPane(tblRefundDetails));
        tablesSplit.setResizeWeight(0.7);
        tablesSplit.setOneTouchExpandable(true);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(tablesSplit, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);

        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Refund Options",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                labelFont,
                buttonColor
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        int y = 0;

        leftPanel.add(new JLabel("Refund Reason:"), gbc);
        gbc.gridy = ++y;
        txtRefundReason = new JTextField();
        txtRefundReason.setFont(inputFont);
        leftPanel.add(txtRefundReason, gbc);

        btnSelectQty = new JButton("Select Qty to Refund");
        btnProcessRefund = new JButton("Process Refund");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back");

        stylePrimaryButton(btnSelectQty);
        stylePrimaryButton(btnProcessRefund);
        stylePrimaryButton(btnRefresh);
        styleBackButton(btnBack);

        gbc.gridy = ++y; leftPanel.add(btnSelectQty, gbc);
        gbc.gridy = ++y; leftPanel.add(btnProcessRefund, gbc);
        gbc.gridy = ++y; leftPanel.add(btnRefresh, gbc);
        gbc.gridy = ++y; leftPanel.add(btnBack, gbc);

        add(leftPanel, BorderLayout.WEST);

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
        loadRefundDetails();
    }

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
        table.setFont(inputFont);
        table.setRowHeight(35);
        table.setSelectionBackground(tableSelection);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(tableHeaderBackground);
        header.setForeground(tableHeaderForeground);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                if (!isSelected) { c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : formBackground); }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }

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
            Integer stockIdObj = bd.getStockId();
            if (stockIdObj == null) continue;
            int stockId = stockIdObj;

            Object[] info = stockDAO.getStockInfoByStockId(stockId);
            String category = info.length > 4 && info[4] != null ? info[4].toString() : "-";

            billItemsTableModel.addRow(new Object[]{
                    stockId, info[0], category, info[1], info[2], bd.getQuantity()
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

        tblBillItems.setValueAt(qty, selectedRow, 5);
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

        boolean anyRefund = false;
        for (int i = 0; i < tblBillItems.getRowCount(); i++) {
            int refundQty = (int) tblBillItems.getValueAt(i, 5);
            if (refundQty > 0) { anyRefund = true; break; }
        }

        if(!anyRefund){
            JOptionPane.showMessageDialog(this,"No quantity selected for refund.");
            return;
        }

        Refund refund = new Refund();
        refund.setBillId(billId);
        refund.setCustomerId(tblBills.getValueAt(billRow, 1).toString());
        refund.setRefundDate(new java.util.Date());
        refund.setRefundMethod("Cash");
        refund.setReason(refundReason);

        double totalRefundAmount = 0;
        for (int i = 0; i < tblBillItems.getRowCount(); i++) {
            int stockId = (int) tblBillItems.getValueAt(i, 0);
            int refundQty = (int) tblBillItems.getValueAt(i, 5);
            if (refundQty <= 0) continue;

            BillDetails bd = null;
            for(BillDetails b: currentBillItems){
                Integer sid = b.getStockId();
                if(sid != null && sid == stockId){ bd = b; break; }
            }
            if(bd == null) continue;

            double amountPerUnit = bd.getTotalAmount().doubleValue() / bd.getQuantity();
            totalRefundAmount += refundQty * amountPerUnit;
        }
        refund.setAmount(totalRefundAmount);

        int refundId = refundDAO.insert(refund);
        if(refundId <= 0){
            JOptionPane.showMessageDialog(this,"Failed to create refund record.");
            return;
        }

        for (int i = 0; i < tblBillItems.getRowCount(); i++) {
            int stockId = (int) tblBillItems.getValueAt(i, 0);
            int refundQty = (int) tblBillItems.getValueAt(i, 5);
            if(refundQty <= 0) continue;

            BillDetails bd = null;
            for(BillDetails b: currentBillItems){
                Integer sid = b.getStockId();
                if(sid != null && sid == stockId){ bd = b; break; }
            }
            if(bd == null) continue;

            double amountPerUnit = bd.getTotalAmount().doubleValue() / bd.getQuantity();
            double refundAmount = refundQty * amountPerUnit;

            refundDAO.insertRefundDetail(refundId, stockId, refundQty, refundAmount);

            stockDAO.increaseStockQuantity(stockId, refundQty);

            bd.setQuantity(bd.getQuantity() - refundQty);
            billDetailsDAO.updateBillDetailQuantity(bd);
        }

        // ✅ Adjust payment amount after refund
        PaymentDAO paymentDAO = new PaymentDAO();
        Integer paymentId = paymentDAO.getPaymentIdByBillId(billId);
        if(paymentId != null){
            paymentDAO.adjustPaymentAmount(paymentId, BigDecimal.valueOf(totalRefundAmount), currentUser);
        }

        boolean allRefunded = currentBillItems.stream()
                .allMatch(bd -> bd.getQuantity() == 0);
        String newStatus = allRefunded ? "Refunded" : "Partially Refunded";
        billingDAO.updateBillStatus(billId, newStatus);

        JOptionPane.showMessageDialog(this,"Refund processed successfully.");

        txtRefundReason.setText("");
        loadSelectedBillItems();
        loadRefundDetails();
        refreshBillTable();
    }
}
