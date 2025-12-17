package GUI;

import DAO.*;
import Models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.Border;
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

    private JComboBox<String> cmbCustomer, cmbItem, cmbPaymentType;
    private JTextField txtQuantity, txtEmployeeId, txtBillDescription;
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
        txtBillDescription = new JTextField();
        cmbPaymentType = new JComboBox<>(new String[]{"Cash", "Card", "Online"});
        cmbItem = new JComboBox<>();
        loadItems();
        txtQuantity = new JTextField();

        int componentHeight = 35;
        Dimension inputDim = new Dimension(LEFT_PANEL_NOMINAL_WIDTH - 20, componentHeight);

        cmbCustomer.setPreferredSize(inputDim);
        txtEmployeeId.setPreferredSize(inputDim);
        txtBillDescription.setPreferredSize(inputDim);
        cmbPaymentType.setPreferredSize(inputDim);
        cmbItem.setPreferredSize(inputDim);
        txtQuantity.setPreferredSize(inputDim);

        Dimension buttonDim = new Dimension(LEFT_PANEL_NOMINAL_WIDTH, componentHeight + 5);

        btnAddItem = new JButton("Add Item");
        btnClearItems = new JButton("Clear Items");
        btnSaveBill = new JButton("Save Bill");
        btnRefreshItems = new JButton("Refresh Items");
        btnBack = new JButton("⬅ Back");

        btnAddItem.setPreferredSize(buttonDim);
        btnClearItems.setPreferredSize(buttonDim);
        btnSaveBill.setPreferredSize(buttonDim);
        btnRefreshItems.setPreferredSize(buttonDim);
        btnBack.setPreferredSize(buttonDim);

        styleGreenButton(btnAddItem);
        styleRedButton(btnClearItems);
        styleGreenButton(btnSaveBill);
        styleGreenButton(btnRefreshItems);
        styleGreenButton(btnBack);

        int y = 0;

        // Customer
        gbc.gridx = 0; gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Customer"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbCustomer, gbc);

        // Employee ID
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Employee ID"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtEmployeeId, gbc);

        // Payment Type
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Payment Type"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbPaymentType, gbc);

        // Bill Description
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Bill Description (optional)"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtBillDescription, gbc);

        // Item
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Item"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbItem, gbc);

        // Quantity
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Quantity"), gbc);
        gbc.gridy = y++; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtQuantity, gbc);

        // Buttons
        gbc.gridy = y++; gbc.fill = GridBagConstraints.HORIZONTAL; formPanel.add(btnAddItem, gbc);
        gbc.gridy = y++; formPanel.add(btnClearItems, gbc);
        gbc.gridy = y++; formPanel.add(btnSaveBill, gbc);
        gbc.gridy = y++; formPanel.add(btnRefreshItems, gbc);
        gbc.gridy = y++; formPanel.add(btnBack, gbc);

        // Tables
        tableModel = new DefaultTableModel(
                new String[]{"StockId", "Cloth", "Category", "Color", "Size", "Qty", "Unit Price", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblItems = new JTable(tableModel);
        JScrollPane currentBillScroll = new JScrollPane(tblItems);

        billTableModel = new DefaultTableModel(
                new String[]{"Bill ID", "Customer", "Date", "Amount", "Description", "Payment ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBills = new JTable(billTableModel);
        JScrollPane pastBillScroll = new JScrollPane(tblBills);

        JLabel billItemsLabel = new JLabel("Items in the selected bill", SwingConstants.CENTER);
        billItemsLabel.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        billItemsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        billItemsLabel.setMinimumSize(new Dimension(1, 30));
        billItemsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        billDetailsTableModel = new DefaultTableModel(
                new String[]{"StockId", "Cloth", "Category", "Color", "Size", "Qty", "Unit Price", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBillDetails = new JTable(billDetailsTableModel);
        JScrollPane billDetailsScroll = new JScrollPane(tblBillDetails);

        DefaultTableCellRenderer paddingRenderer = new DefaultTableCellRenderer() {
            private final Border cellPadding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(cellPadding);
                return this;
            }
        };
        applyCustomRenderer(tblItems, paddingRenderer);
        applyCustomRenderer(tblBills, paddingRenderer);
        applyCustomRenderer(tblBillDetails, paddingRenderer);
        tblItems.setRowHeight(tblItems.getRowHeight() + 8);
        tblBills.setRowHeight(tblBills.getRowHeight() + 8);
        tblBillDetails.setRowHeight(tblBillDetails.getRowHeight() + 8);
        tblItems.getTableHeader().setFont(tblItems.getTableHeader().getFont().deriveFont(Font.BOLD));
        tblBills.getTableHeader().setFont(tblBills.getTableHeader().getFont().deriveFont(Font.BOLD));
        tblBillDetails.getTableHeader().setFont(tblBillDetails.getTableHeader().getFont().deriveFont(Font.BOLD));

        JPanel detailsContainer = new JPanel(new BorderLayout());
        detailsContainer.add(billItemsLabel, BorderLayout.NORTH);
        detailsContainer.add(billDetailsScroll, BorderLayout.CENTER);

        JSplitPane billsAndDetailsSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, pastBillScroll, detailsContainer);
        billsAndDetailsSplit.setResizeWeight(0.5);
        billsAndDetailsSplit.setOneTouchExpandable(true);

        JPanel pastBillsTitle = new JPanel(new BorderLayout());
        pastBillsTitle.add(new JLabel("Past Bills", SwingConstants.CENTER), BorderLayout.NORTH);
        pastBillsTitle.add(billsAndDetailsSplit, BorderLayout.CENTER);

        JSplitPane mainVerticalSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, currentBillScroll, pastBillsTitle);
        mainVerticalSplit.setResizeWeight(0.33);
        mainVerticalSplit.setOneTouchExpandable(true);

        JPanel rightPanelWrapper = new JPanel(new BorderLayout());
        rightPanelWrapper.setBorder(new TitledBorder(" Current Bill"));
        rightPanelWrapper.add(mainVerticalSplit, BorderLayout.CENTER);

        JSplitPane mainHorizontalSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, formPanel, rightPanelWrapper);
        mainHorizontalSplit.setResizeWeight(0.35);
        mainHorizontalSplit.setOneTouchExpandable(true);

        add(mainHorizontalSplit, BorderLayout.CENTER);

        btnAddItem.addActionListener(e -> addItem());
        btnClearItems.addActionListener(e -> clearCurrentBillItems());
        btnSaveBill.addActionListener(e -> saveBillWithQuantityCheck());

        btnRefreshItems.addActionListener(e -> {
            loadItems();       // Refresh items combobox
            loadCustomers();   // Refresh customers combobox
        });

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

    private void applyCustomRenderer(JTable table, DefaultTableCellRenderer renderer) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void refreshBillTable() {
        billTableModel.setRowCount(0);
        for (Billing b : billingDAO.getAll()) {
            billTableModel.addRow(new Object[]{
                    " " + b.getBillId(),
                    " " + b.getCustomerId(),
                    " " + b.getBillDate(),
                    " " + b.getAmount(),
                    b.getBillDescription() != null ? b.getBillDescription() : "",
                    b.getPaymentId() != null ? b.getPaymentId() : ""
            });
        }
    }

    private void generateProfessionalPDF(int billId, String customerId, String customerName, String employeeId, String description, String paymentType) {
        try {
            Billing bill = billingDAO.getById(billId); // Get payment ID
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("Bill_" + billId + ".pdf"));
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);

            doc.add(new Paragraph("CLOTHING WAREHOUSE BILL", titleFont));
            doc.add(new Paragraph("Bill ID: " + billId));
            doc.add(new Paragraph("Customer ID: " + customerId));
            doc.add(new Paragraph("Customer Name: " + customerName));
            doc.add(new Paragraph("Employee: " + employeeId));
            doc.add(new Paragraph("Payment Type: " + paymentType));
            doc.add(new Paragraph("Payment ID: " + (bill.getPaymentId() != null ? bill.getPaymentId() : "")));
            if(description != null) doc.add(new Paragraph("Description: " + description));
            doc.add(new Paragraph("Date: " + LocalDate.now()));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            String[] headers = {"Stock", "Item ID", "Category", "Color", "Size", "Qty", "Unit Price", "Total"};
            for (String h : headers) table.addCell(h);

            BigDecimal grandTotal = BigDecimal.ZERO;

            for (BillDetails bd : billItems) {
                Object[] info = stockDAO.getStockInfoByStockId(bd.getStockId());
                String category = info.length > 4 && info[4] != null ? info[4].toString() : "-";
                BigDecimal unitPrice = (BigDecimal) info[3];

                table.addCell(String.valueOf(bd.getStockId()));
                table.addCell(info[0].toString());
                table.addCell(category);
                table.addCell(info[1].toString());
                table.addCell(info[2].toString());
                table.addCell(String.valueOf(bd.getQuantity()));
                table.addCell(unitPrice.toString());
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

    private void loadItems() {
        stockList = stockDAO.getAllStockForBilling();
        cmbItem.removeAllItems();
        for (Object[] s : stockList) {
            Object categoryObj = s.length > 6 ? s[6] : "-";
            String category = categoryObj != null ? categoryObj.toString() : "-";

            cmbItem.addItem("Stock " + s[0] + " | " + s[1] + " | " + category +
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
        int availableQty = (int) stock[4];

        int qty;
        try {
            qty = Integer.parseInt(txtQuantity.getText());
            if (qty <= 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity");
            return;
        }

        // ==== FIXED: Check cumulative quantity already in billItems ====
        int alreadyAddedQty = billItems.stream()
                .filter(b -> b.getStockId() == stockId)
                .mapToInt(BillDetails::getQuantity)
                .sum();

        if (qty + alreadyAddedQty > availableQty) {
            JOptionPane.showMessageDialog(this,
                    "Selected quantity not available | Available quantity: " + availableQty +
                            " | Already added: " + alreadyAddedQty,
                    "Stock Warning", JOptionPane.WARNING_MESSAGE);
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

    private void saveBillWithQuantityCheck() {
        for (BillDetails bd : billItems) {
            Object[] stockInfo = stockDAO.getStockInfoByStockId(bd.getStockId());
            if(stockInfo != null && stockInfo.length > 5){
                int availableQty = ((Integer) stockInfo[5]);
                if(bd.getQuantity() > availableQty){
                    JOptionPane.showMessageDialog(this,
                            "Quantity for Stock ID " + bd.getStockId() + " exceeds available stock (" + availableQty + ").",
                            "Stock Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        saveBill();
    }

    private void saveBill() {
        int cSel = cmbCustomer.getSelectedIndex();
        if (cSel < 0) return;

        Customer selectedCustomer = customerList.get(cSel);
        String customerId = selectedCustomer.getCustomerId();
        String customerName = selectedCustomer.getCusName();

        String employeeId = txtEmployeeId.getText().trim();
        if (employeeId.isEmpty()) return;

        String paymentType = (String) cmbPaymentType.getSelectedItem();
        String desc = txtBillDescription.getText().trim();

        Billing bill = new Billing();
        bill.setBillDate(java.sql.Date.valueOf(LocalDate.now()));
        bill.setCustomerId(customerId);
        bill.setBillDescription(desc.isEmpty() ? null : desc);

        Integer billId = billingDAO.insertWithPaymentAndDetails(
                bill, billItems, currentUser, employeeId, paymentType);

        if (billId == null) return;

        generateProfessionalPDF(billId, customerId, customerName, employeeId, desc, paymentType);

        tableModel.setRowCount(0);
        billItems.clear();
        txtEmployeeId.setText("");
        txtBillDescription.setText("");
        cmbPaymentType.setSelectedIndex(0);
        refreshBillTable();
        loadItems();
    }

    private void loadSelectedBillDetails() {
        int sel = tblBills.getSelectedRow();
        if (sel < 0) return;

        int billId = Integer.parseInt(tblBills.getValueAt(sel, 0).toString().trim());
        billDetailsTableModel.setRowCount(0);
        List<BillDetails> details = billDetailsDAO.getByBillId(billId);
        for (BillDetails bd : details) {
            Object[] stock = stockDAO.getStockInfoByStockId(bd.getStockId());
            String category = stock.length > 4 && stock[4] != null ? stock[4].toString() : "-";

            billDetailsTableModel.addRow(new Object[]{
                    bd.getStockId(),
                    stock[0],
                    category,
                    stock[1],
                    stock[2],
                    bd.getQuantity(),
                    stock[3],
                    bd.getTotalAmount()
            });
        }
    }

    private void styleGreenButton(JButton button) {
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void styleRedButton(JButton button) {
        button.setBackground(new Color(255, 70, 100));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }
}
