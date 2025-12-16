package GUI;

import DAO.ClothingItemDAO;
import DAO.ClothingStockDAO;
import DAO.SupplierDAO;
import Models.ClothingItem;
import Models.ClothingStock;
import Models.Supplier;
import Models.User;
import utils.ValidationUtil; // <<-- IMPORTED VALIDATION UTIL

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class StockManagementPanel extends JPanel {

    private ClothingItemDAO itemDAO = new ClothingItemDAO();
    private ClothingStockDAO stockDAO = new ClothingStockDAO();
    private User currentUser;

    // Form Components
    private JTextField txtClothId, txtMaterial, txtCategory, txtCostPrice, txtRetailPrice, txtSearch;
    private JComboBox<String> cmbSupplier;
    private List<Supplier> supplierList; // store all suppliers for ID mapping

    // Buttons
    private JButton btnAddItem, btnUpdateItem, btnRestock, btnDeleteItem, btnClear, btnRefresh, btnSearch, btnViewSupplier;

    private JTable stockTable;
    private DefaultTableModel tableModel;

    // Styling (Original Styling + New Table Styling Constants)
    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonHover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);

    // --- New Table Styling Constants ---
    private final Color tableHeaderBackground = new Color(50, 50, 50);
    private final Color tableHeaderForeground = Color.WHITE;
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 16);

    public StockManagementPanel(User user) {
        this.currentUser = user;

        // Original layout and padding
        setLayout(new BorderLayout(10, 10));
        setBackground(panelBackground);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Panel: Form + Buttons
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(panelBackground);
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Right Panel: Search + Table
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        loadStockData();

        // Event Listeners
        stockTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { handleTableClick(); }
        });

        btnAddItem.addActionListener(e -> addNewItem());
        btnUpdateItem.addActionListener(e -> updateItemDetails());
        btnRestock.addActionListener(e -> showRestockDialog());
        btnDeleteItem.addActionListener(e -> deleteItem());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadStockData());
        btnSearch.addActionListener(e -> searchStock());
        btnViewSupplier.addActionListener(e -> viewSupplierDetails());

        applyRolePermissions();
    }

    // ------------------- COMPONENTS -------------------

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(formBackground);
        formPanel.setBorder(BorderFactory.createTitledBorder("Clothing Item Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 10;

        txtClothId = createLabeledField(formPanel, gbc, "Item ID:", 0);
        txtMaterial = createLabeledField(formPanel, gbc, "Material:", 1);
        txtCategory = createLabeledField(formPanel, gbc, "Category:", 2);
        txtCostPrice = createLabeledField(formPanel, gbc, "Cost Price:", 3);
        txtRetailPrice = createLabeledField(formPanel, gbc, "Retail Price:", 4);

        // Supplier ComboBox
        cmbSupplier = new JComboBox<>();
        cmbSupplier.setFont(inputFont); // Keep original font size
        loadSuppliers(); // populate combo box
        JLabel lblSupplier = new JLabel("Supplier:");
        lblSupplier.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(lblSupplier, gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(cmbSupplier, gbc);

        return formPanel;
    }

    private void loadSuppliers() {
        // Assume SupplierDAO has a getAllSuppliers() method
        supplierList = new SupplierDAO().getAllSuppliers();
        cmbSupplier.removeAllItems();
        for (Supplier s : supplierList) {
            cmbSupplier.addItem(s.getSupplierId() + " - " + s.getSupName());
        }
    }

    private JTextField createLabeledField(JPanel parent, GridBagConstraints gbc, String labelText, int y) {
        JLabel lbl = new JLabel(labelText); lbl.setFont(labelFont);
        JTextField txtField = new JTextField(); txtField.setFont(inputFont);

        gbc.gridx=0; gbc.gridy=y; parent.add(lbl, gbc);
        gbc.gridx=1; gbc.gridy=y; parent.add(txtField, gbc);
        return txtField;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(4,2,15,15));
        buttonPanel.setBackground(formBackground);

        JButton btnBack = createBackButton("Back");
        btnAddItem = createButton("Add New Item");
        btnUpdateItem = createButton("Update Details");
        btnRestock = createButton("Restock Qty");
        btnDeleteItem = createButton("Delete Item");
        btnClear = createButton("Clear");
        btnRefresh = createButton("Refresh");
        btnViewSupplier = createButton("View Supplier Details");

        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            // Assuming MainFrame exists
            if(topFrame instanceof MainFrame) ((MainFrame)topFrame).switchPanel("Dashboard");
        });

        buttonPanel.add(btnBack);
        buttonPanel.add(btnAddItem);
        buttonPanel.add(btnUpdateItem);
        buttonPanel.add(btnRestock);
        buttonPanel.add(btnDeleteItem);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnViewSupplier);

        return buttonPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10,10));
        rightPanel.setBackground(panelBackground);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(panelBackground);
        txtSearch = new JTextField(25); txtSearch.setFont(inputFont);
        btnSearch = createButton("Search");
        searchPanel.add(new JLabel("Search (ID/Category):")); searchPanel.add(txtSearch); searchPanel.add(btnSearch);

        // ------------------ UPDATED TABLE COLUMNS ------------------
        String[] columns = {"Item ID", "Color", "Material", "Category", "Size", "Qty", "Cost Price", "Retail Price"};

        // MODIFICATION: Make table view-only
        tableModel = new DefaultTableModel(columns,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensures all cells are view-only
            }
        };

        stockTable = new JTable(tableModel);
        stockTable.setFont(inputFont); // Keep original font size (16)
        stockTable.setRowHeight(35);
        stockTable.setSelectionBackground(tableSelection);

        // --- Table Styling (New additions) ---
        stockTable.setGridColor(new Color(230, 230, 230));
        stockTable.setShowVerticalLines(false);

        JTableHeader header = stockTable.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(tableHeaderBackground);
        header.setForeground(tableHeaderForeground);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Table Cell Renderer for alternating row colors and padding
        stockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : formBackground); // Subtle alternating row colors
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(stockTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Add border

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);
        return rightPanel;
    }

    private JButton createButton(String text){
        JButton btn = new JButton(text);
        btn.setBackground(buttonColor); btn.setForeground(Color.WHITE);
        btn.setFont(labelFont); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(buttonHover);}
            public void mouseExited(MouseEvent e){ btn.setBackground(buttonColor);}
        });
        return btn;
    }

    private JButton createBackButton(String text){
        JButton btn = new JButton(text);
        btn.setBackground(backButtonColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(labelFont);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(backButtonHover); }
            public void mouseExited(MouseEvent e){ btn.setBackground(backButtonColor); }
        });
        return btn;
    }

    // ------------------- VALIDATION UTILITY -------------------

    // Helper function to validate price inputs
    private boolean validatePrice(String priceStr, String fieldName) {
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, fieldName + " is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, fieldName + " cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, fieldName + " must be a valid numerical value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // ------------------- ROLE PERMISSIONS -------------------

    private void applyRolePermissions(){
        if(currentUser==null) return;
        String role = currentUser.getRole().toLowerCase();

        btnViewSupplier.setEnabled(true); // All roles can view supplier

        if (role.equals("staff")) {
            btnAddItem.setEnabled(true);
            btnUpdateItem.setEnabled(false);
            btnRestock.setEnabled(true);
            btnDeleteItem.setEnabled(false);
        } else if (role.equals("admin") || role.equals("manager")) {
            btnAddItem.setEnabled(true); btnUpdateItem.setEnabled(true);
            btnRestock.setEnabled(true); btnDeleteItem.setEnabled(true);
        } else {
            btnAddItem.setEnabled(false); btnUpdateItem.setEnabled(false);
            btnRestock.setEnabled(false); btnDeleteItem.setEnabled(false);
        }
    }

    // ------------------- DATA HANDLING -------------------

    private void loadStockData(){
        tableModel.setRowCount(0);
        try {
            List<Object[]> stockList = stockDAO.getAllStockForBilling(); // Updated DAO method
            for(Object[] row : stockList){
                // Retrieve item details for material and cost price which are stored separately
                ClothingItem item = itemDAO.getItemById((String)row[1]);
                String material = item != null ? item.getMaterial() : "-";
                String costPrice = item != null ? item.getCostPrice().toString() : "-";

                tableModel.addRow(new String[]{
                        row[1].toString(),     // ClothId / Item ID
                        row[2] != null ? row[2].toString() : "-", // Color
                        material,                              // Material (from Item)
                        row[6] != null ? row[6].toString() : "-", // Category
                        row[3] != null ? row[3].toString() : "-", // Size
                        row[4] != null ? row[4].toString() : "0", // Quantity
                        costPrice,                                 // Cost Price (from Item)
                        row[5] != null ? row[5].toString() : "-"  // Retail Price
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTableClick(){
        int row = stockTable.getSelectedRow();
        if(row >= 0){
            txtClothId.setText(tableModel.getValueAt(row,0).toString());
            txtMaterial.setText(tableModel.getValueAt(row,2).toString());
            txtCategory.setText(tableModel.getValueAt(row,3).toString());
            txtCostPrice.setText(tableModel.getValueAt(row,6).toString());
            txtRetailPrice.setText(tableModel.getValueAt(row,7).toString());

            // Set supplier combo box
            String supplierId = itemDAO.getSupplierIdByClothId(txtClothId.getText());
            if(supplierId.isEmpty()){
                cmbSupplier.setSelectedIndex(-1);
            } else {
                for (int i = 0; i < supplierList.size(); i++) {
                    if (supplierList.get(i).getSupplierId().equals(supplierId)) {
                        cmbSupplier.setSelectedIndex(i);
                        break;
                    }
                }
            }

            txtClothId.setEditable(false);
        }
    }

    private void addNewItem(){
        if(!btnAddItem.isEnabled()) return;

        String id = txtClothId.getText().trim();
        String material = txtMaterial.getText().trim();
        String category = txtCategory.getText().trim();
        String costStr = txtCostPrice.getText().trim();
        String retailStr = txtRetailPrice.getText().trim();

        String supId = "";
        int sel = cmbSupplier.getSelectedIndex();
        if(sel >= 0) supId = supplierList.get(sel).getSupplierId();

        // --- START VALIDATION INTEGRATION (ADD) ---
        if(id.isEmpty()){
            JOptionPane.showMessageDialog(this,"Item ID is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtClothId.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidItemID(id)) {
            JOptionPane.showMessageDialog(this, "Invalid Item ID format. Must be I followed by 3 or more digits (e.g., I001).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtClothId.requestFocus();
            return;
        }

        if (material.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Material (Name) is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtMaterial.requestFocus();
            return;
        }

        // Material is treated as a descriptive name, using isValidName strictly might be too restrictive for materials (e.g., Nylon/Spandex Blend). We will use a basic emptiness check.
        // If you intended Material to be strictly A-Z only, use ValidationUtil.isValidName(material). For now, we only check emptiness.

        if (!validatePrice(costStr, "Cost Price")) {
            txtCostPrice.requestFocus();
            return;
        }

        if (!validatePrice(retailStr, "Retail Price")) {
            txtRetailPrice.requestFocus();
            return;
        }
        // --- END VALIDATION INTEGRATION (ADD) ---

        try {
            BigDecimal cost = new BigDecimal(costStr);
            BigDecimal retail = new BigDecimal(retailStr);
            ClothingItem item = new ClothingItem(id, material, category, cost, retail, supId);

            if(itemDAO.insert(item, currentUser)){
                JOptionPane.showMessageDialog(this,"Item added successfully! Use Restock to add stock.");
                clearForm();
                txtClothId.setText(id);
            } else {
                JOptionPane.showMessageDialog(this,"Failed to add item. ID may exist or DB error.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during item addition: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItemDetails(){
        if(!btnUpdateItem.isEnabled()) return;

        String id = txtClothId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select an item to update.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        String material = txtMaterial.getText().trim();
        String costStr = txtCostPrice.getText().trim();
        String retailStr = txtRetailPrice.getText().trim();

        String supId = "";
        int sel = cmbSupplier.getSelectedIndex();
        if(sel >= 0) supId = supplierList.get(sel).getSupplierId();

        // --- START VALIDATION INTEGRATION (UPDATE) ---
        if (material.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Material (Name) is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtMaterial.requestFocus();
            return;
        }

        if (!validatePrice(costStr, "Cost Price")) {
            txtCostPrice.requestFocus();
            return;
        }

        if (!validatePrice(retailStr, "Retail Price")) {
            txtRetailPrice.requestFocus();
            return;
        }
        // --- END VALIDATION INTEGRATION (UPDATE) ---

        try {
            BigDecimal cost = new BigDecimal(costStr);
            BigDecimal retail = new BigDecimal(retailStr);
            ClothingItem item = new ClothingItem(id, material,
                    txtCategory.getText().trim(), cost, retail, supId);

            if(itemDAO.update(item, currentUser)){
                JOptionPane.showMessageDialog(this,"Item details updated!");
                loadStockData(); clearForm();
            } else {
                JOptionPane.showMessageDialog(this,"Failed to update item details.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem(){
        if(!btnDeleteItem.isEnabled()) return;
        String id = txtClothId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select an item to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure to delete item " + id + "? (This also removes all associated stock)","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm!=JOptionPane.YES_OPTION) return;

        if(itemDAO.delete(id, currentUser)){
            JOptionPane.showMessageDialog(this,"Item deleted successfully!");
            loadStockData(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this,"Failed to delete item. It may have existing dependencies or you lack permission.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRestockDialog() {
        if(!btnRestock.isEnabled()) return;

        JTextField idField = new JTextField(txtClothId.getText().trim());
        JTextField colorField = new JTextField(10);
        JTextField sizeField = new JTextField(10);
        JTextField qtyField = new JTextField(10);

        // --- START VALIDATION INTEGRATION (RESTOCK DIALOG PRE-CHECK) ---
        if(!idField.getText().isEmpty()) {
            if (!ValidationUtil.isValidItemID(idField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Selected Item ID is invalid. Please clear form and re-select or input a valid ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            idField.setEditable(false);
        }
        // --- END VALIDATION INTEGRATION (RESTOCK DIALOG PRE-CHECK) ---

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Item ID:")); panel.add(idField);
        panel.add(new JLabel("Color:")); panel.add(colorField);
        panel.add(new JLabel("Size:")); panel.add(sizeField);
        panel.add(new JLabel("Quantity:")); panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Restock Inventory",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION){
            try {
                String clothId = idField.getText().trim();
                String color = colorField.getText().trim();
                String size = sizeField.getText().trim();
                String qtyStr = qtyField.getText().trim();

                // --- START VALIDATION INTEGRATION (RESTOCK DIALOG POST-CHECK) ---
                if(clothId.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Item ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!ValidationUtil.isValidItemID(clothId)) {
                    JOptionPane.showMessageDialog(this, "Item ID is invalid (Format: I001+).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (size.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Size cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (qtyStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int qty;
                try {
                    qty = Integer.parseInt(qtyStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a whole number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (qty <= 0){
                    JOptionPane.showMessageDialog(this,"Quantity must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // --- END VALIDATION INTEGRATION (RESTOCK DIALOG POST-CHECK) ---

                ClothingStock stock = new ClothingStock(clothId, color, size, qty);
                if(stockDAO.restockItem(stock, currentUser)){
                    JOptionPane.showMessageDialog(this, qty + " units added to stock for " + clothId);
                    loadStockData();
                } else {
                    JOptionPane.showMessageDialog(this,"Restock failed. Item ID may not exist or DB error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this,"An unexpected error occurred: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm(){
        txtClothId.setText(""); txtMaterial.setText("");
        txtCategory.setText(""); txtCostPrice.setText(""); txtRetailPrice.setText("");
        cmbSupplier.setSelectedIndex(-1);
        txtClothId.setEditable(true);
    }

    private void searchStock(){
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);

        if(keyword.isEmpty()){
            loadStockData(); return;
        }

        try {
            List<Object[]> stockList = stockDAO.getAllStockForBilling(); // Updated DAO method
            for(Object[] row : stockList){
                String clothId = row[1].toString();
                String category = row[6] != null ? row[6].toString() : "-";
                ClothingItem item = itemDAO.getItemById(clothId);
                String material = item != null ? item.getMaterial() : "-";
                String costPrice = item != null ? item.getCostPrice().toString() : "-";
                String retailPrice = item != null ? item.getRetailPrice().toString() : "-";

                // Simple keyword check on ID or Category
                if(clothId.toLowerCase().contains(keyword.toLowerCase()) || category.toLowerCase().contains(keyword.toLowerCase())){
                    tableModel.addRow(new String[]{
                            clothId,
                            row[2] != null ? row[2].toString() : "-", // Color
                            material,
                            category,
                            row[3] != null ? row[3].toString() : "-", // Size
                            row[4] != null ? row[4].toString() : "0", // Quantity
                            costPrice,
                            retailPrice
                    });
                }
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Search error: " + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------- VIEW SUPPLIER -------------------

    private void viewSupplierDetails(){
        int row = stockTable.getSelectedRow();
        if(row < 0){
            JOptionPane.showMessageDialog(this, "Select a stock item to view supplier details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sel = cmbSupplier.getSelectedIndex();
        if(sel < 0){
            JOptionPane.showMessageDialog(this,"No supplier assigned to this item.");
            return;
        }

        Supplier supplier = supplierList.get(sel);
        String message = "Supplier ID: "+supplier.getSupplierId()+"\n"+
                "Name: "+supplier.getSupName()+"\n"+
                "Contact: "+supplier.getTellNo();
        JOptionPane.showMessageDialog(this, message, "Supplier Details", JOptionPane.INFORMATION_MESSAGE);
    }
}