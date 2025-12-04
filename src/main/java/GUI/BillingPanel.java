package GUI;

import DAO.*;
import Models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class BillingPanel extends JPanel {

    private User currentUser;

    private JComboBox<String> cmbCustomer, cmbItem;
    private JTextField txtQuantity, txtEmployeeId;
    // New button added
    private JButton btnAddItem, btnSaveBill, btnRefreshItems, btnBack, btnClearItems;

    private JTable tblItems;
    private DefaultTableModel tableModel;

    private JTable tblBills;
    private DefaultTableModel billTableModel;

    private JTable tblBillDetails;
    private DefaultTableModel billDetailsTableModel;

    private List<Object[]> stockList;
    private List<Customer> customerList;
    private List<BillDetails> billItems;

    private BillingDAO billingDAO;
    private BillDetailsDAO billDetailsDAO;
    private ClothingStockDAO stockDAO;

    private static final int LEFT_PANEL_NOMINAL_WIDTH = 350;

    public BillingPanel(User user) {
        this.currentUser = user;
        this.billingDAO = new BillingDAO();
        this.billDetailsDAO = new BillDetailsDAO();
        this.stockDAO = new ClothingStockDAO();
        this.billItems = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // ================= LEFT FORM PANEL (WEST component for JSplitPane) =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new TitledBorder("Create Bill"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        cmbCustomer = new JComboBox<>();
        loadCustomers();

        txtEmployeeId = new JTextField();
        cmbItem = new JComboBox<>();
        loadItems();

        txtQuantity = new JTextField();

        int componentHeight = 35;
        Dimension inputDim = new Dimension(LEFT_PANEL_NOMINAL_WIDTH - 20, componentHeight);

        cmbCustomer.setPreferredSize(inputDim);
        txtEmployeeId.setPreferredSize(inputDim);
        cmbItem.setPreferredSize(inputDim);
        txtQuantity.setPreferredSize(inputDim);

        Dimension buttonDim = new Dimension(LEFT_PANEL_NOMINAL_WIDTH, componentHeight + 5);

        btnAddItem = new JButton("Add Item");
        // New button initialization
        btnClearItems = new JButton("Clear Items");
        btnSaveBill = new JButton("Save Bill");
        btnRefreshItems = new JButton("Refresh Items");
        btnBack = new JButton("⬅ Back");

        btnAddItem.setPreferredSize(buttonDim);
        btnClearItems.setPreferredSize(buttonDim); // Set size for new button
        btnSaveBill.setPreferredSize(buttonDim);
        btnRefreshItems.setPreferredSize(buttonDim);
        btnBack.setPreferredSize(buttonDim);

        styleGreenButton(btnAddItem);
        styleRedButton(btnClearItems); // Style the clear button red for visibility
        styleGreenButton(btnSaveBill);
        styleGreenButton(btnRefreshItems);
        styleGreenButton(btnBack);

        int y = 0;

        // Customer
        gbc.gridx = 0; gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Customer"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbCustomer, gbc);

        // Employee ID
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Employee ID"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtEmployeeId, gbc);

        // Item
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Item"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(cmbItem, gbc);

        // Quantity
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; formPanel.add(new JLabel("Quantity"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(txtQuantity, gbc);


        // Buttons
        gbc.gridy = y++; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(btnAddItem, gbc);

        // New button placement
        gbc.gridy = y++;
        formPanel.add(btnClearItems, gbc);

        gbc.gridy = y++;
        formPanel.add(btnSaveBill, gbc);

        gbc.gridy = y++;
        formPanel.add(btnRefreshItems, gbc);

        gbc.gridy = y++;
        formPanel.add(btnBack, gbc);

        // ================= RIGHT PANEL (EAST component for JSplitPane) =================

        // --- 1. Current Bill Items Table (New Top Table on the right) ---
        tableModel = new DefaultTableModel(
                new String[]{"StockId", "Cloth", "Category", "Color", "Size", "Qty", "Unit Price", "Total"}, 0);
        tblItems = new JTable(tableModel);
        JScrollPane currentBillScroll = new JScrollPane(tblItems);

        // --- 2. Past Bills Table (tblBills) ---
        billTableModel = new DefaultTableModel(
                new String[]{"Bill ID", "Customer", "Date", "Amount"}, 0);
        tblBills = new JTable(billTableModel);
        JScrollPane pastBillScroll = new JScrollPane(tblBills);

        // --- Separator Label (Between 2nd and 3rd table) ---
        JLabel billItemsLabel = new JLabel("Items in the selected bill", SwingConstants.CENTER);
        billItemsLabel.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        billItemsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        billItemsLabel.setMinimumSize(new Dimension(1, 30));
        billItemsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // --- 3. Bill Details Table (tblBillDetails) ---
        billDetailsTableModel = new DefaultTableModel(
                new String[]{"StockId", "Cloth", "Category", "Color", "Size", "Qty", "Total"}, 0);
        tblBillDetails = new JTable(billDetailsTableModel);
        JScrollPane billDetailsScroll = new JScrollPane(tblBillDetails);

        // Container for Label + Details (3rd component)
        JPanel detailsContainer = new JPanel(new BorderLayout());
        detailsContainer.add(billItemsLabel, BorderLayout.NORTH);
        detailsContainer.add(billDetailsScroll, BorderLayout.CENTER);

        // 2. Split between 2nd Table and 3rd Component
        JSplitPane billsAndDetailsSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                pastBillScroll,       // 2nd Table (Past Bills)
                detailsContainer      // Label + 3rd Table (Details)
        );
        billsAndDetailsSplit.setResizeWeight(0.5);
        billsAndDetailsSplit.setOneTouchExpandable(true);

        // Add a title above the past bills section
        JPanel pastBillsTitle = new JPanel(new BorderLayout());
        pastBillsTitle.add(new JLabel("Past Bills", SwingConstants.CENTER), BorderLayout.NORTH);
        pastBillsTitle.add(billsAndDetailsSplit, BorderLayout.CENTER);

        // 1. Main Split (1st Table vs. Everything Else)
        JSplitPane mainVerticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                currentBillScroll, // 1st Table (Current Bill Items)
                pastBillsTitle     // 2nd Table + Label + 3rd Table
        );
        mainVerticalSplit.setResizeWeight(0.33);
        mainVerticalSplit.setOneTouchExpandable(true);

        // Wrap the final right panel stack in a titled container for clarity
        JPanel rightPanelWrapper = new JPanel(new BorderLayout());
        rightPanelWrapper.setBorder(new TitledBorder("Transaction History & Current Bill"));
        rightPanelWrapper.add(mainVerticalSplit, BorderLayout.CENTER);

        // ================= MAIN LAYOUT (HORIZONTAL SPLIT) =================

        JSplitPane mainHorizontalSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                formPanel,
                rightPanelWrapper
        );
        mainHorizontalSplit.setResizeWeight(0.35);
        mainHorizontalSplit.setOneTouchExpandable(true);

        add(mainHorizontalSplit, BorderLayout.CENTER);

        // ================== EVENT LISTENERS ==================
        btnAddItem.addActionListener(e -> addItem());
        btnClearItems.addActionListener(e -> clearCurrentBillItems()); // New listener
        btnSaveBill.addActionListener(e -> saveBill());
        btnRefreshItems.addActionListener(e -> loadItems());

        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof MainFrame) {
                ((MainFrame) topFrame).switchPanel("Dashboard");
            }
        });

        tblBills.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedBillDetails();
        });

        refreshBillTable();
    }

    private void styleGreenButton(JButton btn){
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void styleRedButton(JButton btn){
        btn.setBackground(new Color(220, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void loadItems() {
        stockList = stockDAO.getAllStockForBilling();
        cmbItem.removeAllItems();
        for (Object[] s : stockList) {
            Object categoryObj = s.length > 6 ? s[6] : "-";
            String category = categoryObj != null ? categoryObj.toString() : "-";

            cmbItem.addItem("Stock#" + s[0] + " | " + s[1] + " | " + category +
                    " | " + s[2] + " | Size: " + s[3] + " | Rs." + s[5]);
        }
    }

    private void loadCustomers() {
        customerList = new CustomerDAO().getAll();
        cmbCustomer.removeAllItems();
        for (Customer c : customerList) {
            cmbCustomer.addItem(c.getCustomerId() + " - " + c.getCusName());
        }
    }

    private void addItem() {
        int sel = cmbItem.getSelectedIndex();
        if (sel < 0) return;

        Object[] stock = stockList.get(sel);
        int stockId = (int) stock[0];
        String clothId = (String) stock[1];
        String category = stock.length > 6 ? stock[6].toString() : "-";
        String color = (String) stock[2];
        String size = (String) stock[3];
        BigDecimal price = (BigDecimal) stock[5];

        int qty;
        try {
            qty = Integer.parseInt(txtQuantity.getText());
            if (qty <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
            return;
        }

        BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

        tableModel.addRow(new Object[]{stockId, clothId, category, color, size, qty, price, total});

        BillDetails bd = new BillDetails();
        bd.setStockId(stockId);
        bd.setQuantity(qty);
        bd.setTotalAmount(total);
        billItems.add(bd);

        txtQuantity.setText("");
    }

    // New method to clear the current bill items
    private void clearCurrentBillItems() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "The current bill is already empty.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all items from the current bill?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            billItems.clear();
            JOptionPane.showMessageDialog(this, "Current bill items cleared.");
        }
    }

    private void saveBill() {
        int cSel = cmbCustomer.getSelectedIndex();
        if (cSel < 0) return;

        String customerId = customerList.get(cSel).getCustomerId();
        String employeeId = txtEmployeeId.getText().trim();
        if (employeeId.isEmpty()) return;

        Billing bill = new Billing();
        bill.setBillDate(java.sql.Date.valueOf(LocalDate.now()));
        bill.setBillDescription("Bill for " + customerId);
        bill.setCustomerId(customerId);

        Integer billId = billingDAO.insertWithPaymentAndDetails(
                bill, billItems, currentUser, employeeId);

        if (billId == null) return;

        generateProfessionalPDF(billId, customerId, employeeId);

        tableModel.setRowCount(0);
        billItems.clear();
        txtEmployeeId.setText("");
        refreshBillTable();
        loadItems(); // refresh items after saving
    }

    private void generateProfessionalPDF(int billId, String customerId, String employeeId) {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("Bill_" + billId + ".pdf"));
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

            doc.add(new Paragraph("CLOTHING WAREHOUSE BILL", titleFont));
            doc.add(new Paragraph("Bill ID: " + billId));
            doc.add(new Paragraph("Customer: " + customerId));
            doc.add(new Paragraph("Employee: " + employeeId));
            doc.add(new Paragraph("Date: " + LocalDate.now()));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            String[] headers = {"Stock", "Cloth", "Category", "Color", "Size", "Qty", "Total"};
            for (String h : headers) table.addCell(h);

            BigDecimal grandTotal = BigDecimal.ZERO;

            for (BillDetails bd : billItems) {
                Object[] info = stockDAO.getStockInfoByStockId(bd.getStockId());
                String category = info.length > 4 && info[4] != null ? info[4].toString() : "-";

                table.addCell(String.valueOf(bd.getStockId()));
                table.addCell(info[0].toString());
                table.addCell(category);
                table.addCell(info[1].toString());
                table.addCell(info[2].toString());
                table.addCell(String.valueOf(bd.getQuantity()));
                table.addCell(bd.getTotalAmount().toString());

                grandTotal = grandTotal.add(bd.getTotalAmount());
            }

            doc.add(table);
            doc.add(new Paragraph("\nGRAND TOTAL: Rs. " + grandTotal));

            doc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshBillTable() {
        billTableModel.setRowCount(0);
        for (Billing b : billingDAO.getAll()) {
            billTableModel.addRow(new Object[]{
                    " " + b.getBillId(),
                    " " + b.getCustomerId(),
                    " " + b.getBillDate(),
                    " " + b.getAmount()
            });
        }
    }

    private void loadSelectedBillDetails() {
        int row = tblBills.getSelectedRow();
        if (row < 0) return;

        int billId = Integer.parseInt(tblBills.getValueAt(row, 0).toString().trim());
        List<BillDetails> list = billDetailsDAO.getByBillId(billId);
        billDetailsTableModel.setRowCount(0);

        for (BillDetails bd : list) {
            Object[] info = stockDAO.getStockInfoByStockId(bd.getStockId());
            String category = info.length > 4 && info[4] != null ? info[4].toString() : "-";

            billDetailsTableModel.addRow(new Object[]{
                    bd.getStockId(), info[0], category, info[1], info[2], bd.getQuantity(), bd.getTotalAmount()
            });
        }
    }
}