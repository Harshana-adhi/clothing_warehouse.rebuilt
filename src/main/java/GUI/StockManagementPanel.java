package GUI;

import DAO.ClothingItemDAO;
import DAO.ClothingStockDAO;
import Models.ClothingItem;
import Models.ClothingStock;
import Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class StockManagementPanel extends JPanel {

    private ClothingItemDAO itemDAO = new ClothingItemDAO();
    private ClothingStockDAO stockDAO = new ClothingStockDAO();
    private User currentUser;

    // Form Components
    private JTextField txtClothId, txtColor, txtMaterial, txtCategory, txtPrice, txtSupplierId, txtSearch;

    // Buttons
    private JButton btnAddItem, btnUpdateItem, btnRestock, btnDeleteItem, btnClear, btnRefresh, btnSearch;
    private JTable stockTable;
    private DefaultTableModel tableModel;

    // --- Styling Variables (Copied from SupplierPanel) ---
    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonhover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);
    // --------------------------------------------------------

    public StockManagementPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 10));
        setBackground(panelBackground);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Panel (Form and Buttons)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(panelBackground);

        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Right Panel (Search and Table)
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        loadStockData();

        // --- Event Listeners ---
        stockTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){ handleTableClick(); }
        });

        btnAddItem.addActionListener(e -> addNewItem());
        btnUpdateItem.addActionListener(e -> updateItemDetails());
        btnRestock.addActionListener(e -> showRestockDialog());
        btnDeleteItem.addActionListener(e -> deleteItem());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadStockData());
        btnSearch.addActionListener(e -> searchStock()); // Needs implementation

        applyRolePermissions();
    }

    // --- Component Initialization Methods ---

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
        txtColor = createLabeledField(formPanel, gbc, "Color:", 1);
        txtMaterial = createLabeledField(formPanel, gbc, "Material:", 2);
        txtCategory = createLabeledField(formPanel, gbc, "Category:", 3);
        txtPrice = createLabeledField(formPanel, gbc, "Price:", 4);
        txtSupplierId = createLabeledField(formPanel, gbc, "Supplier ID:", 5);

        return formPanel;
    }

    private JTextField createLabeledField(JPanel parent, GridBagConstraints gbc, String labelText, int y) {
        JLabel lbl = new JLabel(labelText); lbl.setFont(labelFont);
        JTextField txtField = new JTextField(); txtField.setFont(inputFont);

        gbc.gridx=0; gbc.gridy=y; parent.add(lbl, gbc);
        gbc.gridx=1; gbc.gridy=y; parent.add(txtField, gbc);
        return txtField;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(3,2,15,15));
        buttonPanel.setBackground(formBackground);

        JButton btnBack = createBackButton("Back");

        btnAddItem = createButton("Add New Item");
        btnUpdateItem = createButton("Update Details");
        btnRestock = createButton("Restock Qty");
        btnDeleteItem = createButton("Delete Item");
        btnClear = createButton("Clear");
        btnRefresh = createButton("Refresh");

        // Back button listener placeholder
        btnBack.addActionListener(e -> {
            // Container topFrame = SwingUtilities.getWindowAncestor(this);
            // if(topFrame instanceof MainFrame) ((MainFrame)topFrame).switchPanel("Dashboard");
        });

        buttonPanel.add(btnBack);
        buttonPanel.add(btnAddItem); buttonPanel.add(btnUpdateItem);
        buttonPanel.add(btnRestock); buttonPanel.add(btnDeleteItem);
        buttonPanel.add(btnClear); buttonPanel.add(btnRefresh);

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

        // Columns for the joined Stock View (must match the DAO columns order)
        String[] columns = {"Item ID", "Color", "Material", "Category", "Size", "Quantity", "Price"};
        tableModel = new DefaultTableModel(columns,0);
        stockTable = new JTable(tableModel);
        stockTable.setFont(inputFont);
        stockTable.setRowHeight(35);
        stockTable.setSelectionBackground(tableSelection);
        JScrollPane tableScroll = new JScrollPane(stockTable);

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);
        return rightPanel;
    }

    // --- Utility and Styling Methods (Copied from SupplierPanel) ---

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
            @Override
            public void mouseEntered(MouseEvent e){ btn.setBackground(backButtonhover); }
            @Override
            public void mouseExited(MouseEvent e){ btn.setBackground(backButtonColor); }
        });
        return btn;
    }

    // ----------------------------------------------------
    // ROLE-BASED ACCESS CONTROL (RBAC)
    // ----------------------------------------------------
    private void applyRolePermissions(){
        if(currentUser==null) return;
        String role = currentUser.getRole().toLowerCase();

        // Staff cannot delete (btnDeleteItem) or update item details (btnUpdateItem)
        // that affect price/description, but can add and restock quantity.
        if (role.equals("staff")) {
            btnAddItem.setEnabled(true);
            btnUpdateItem.setEnabled(false); // Staff cannot change Price, Color, etc.
            btnRestock.setEnabled(true);
            btnDeleteItem.setEnabled(false); // Staff cannot delete item definition
        } else if (role.equals("admin") || role.equals("manager")) {
            // Admin/Manager have full CRUD access
            btnAddItem.setEnabled(true); btnUpdateItem.setEnabled(true);
            btnRestock.setEnabled(true); btnDeleteItem.setEnabled(true);
        } else {
            // Default/Unknown roles have no access
            btnAddItem.setEnabled(false); btnUpdateItem.setEnabled(false);
            btnRestock.setEnabled(false); btnDeleteItem.setEnabled(false);
        }
    }

    // ----------------------------------------------------
    // DATA HANDLING AND ACTIONS
    // ----------------------------------------------------

    private void loadStockData(){
        tableModel.setRowCount(0);
        try {
            List<String[]> list = stockDAO.getAllStockDetails();
            for(String[] row : list){
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTableClick(){
        int row = stockTable.getSelectedRow();
        if(row >= 0){
            // Extract values from the selected row (Columns 0 to 5)
            txtClothId.setText(tableModel.getValueAt(row,0).toString());
            txtColor.setText(tableModel.getValueAt(row,1).toString());
            txtMaterial.setText(tableModel.getValueAt(row,2).toString());
            txtCategory.setText(tableModel.getValueAt(row,3).toString());
            txtPrice.setText(tableModel.getValueAt(row,6).toString());
            // txtSupplierId requires another lookup or join if not in the main table view

            txtClothId.setEditable(false);
        }
    }

    private void addNewItem(){
        if(!btnAddItem.isEnabled()) return;

        // Basic Validation
        String id = txtClothId.getText().trim();
        String color = txtColor.getText().trim();
        String material = txtMaterial.getText().trim();
        String category = txtCategory.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String supId = txtSupplierId.getText().trim();

        if(id.isEmpty() || color.isEmpty() || priceStr.isEmpty()){
            JOptionPane.showMessageDialog(this,"Item ID, Color, and Price are required!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            ClothingItem item = new ClothingItem(id, color, material, category, price, supId);

            if(itemDAO.insert(item, currentUser)){
                JOptionPane.showMessageDialog(this,"Item definition added successfully! Now add initial stock.");
                clearForm();
                txtClothId.setText(id); // Set ID back so user can immediately restock it
                // Optionally call showRestockDialog(id);
            } else {
                JOptionPane.showMessageDialog(this,"Failed to add item (ID already exists or DB error).");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItemDetails(){
        if(!btnUpdateItem.isEnabled()) return;

        String id = txtClothId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select an item to update details."); return; }

        try {
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            ClothingItem item = new ClothingItem(id, txtColor.getText().trim(), txtMaterial.getText().trim(),
                    txtCategory.getText().trim(), price, txtSupplierId.getText().trim());

            if(itemDAO.update(item, currentUser)){
                JOptionPane.showMessageDialog(this,"Item details updated successfully!");
                loadStockData(); clearForm();
            } else {
                JOptionPane.showMessageDialog(this,"Failed to update item details. Check permission or data.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem(){
        if(!btnDeleteItem.isEnabled()) return;
        String id = txtClothId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select an item to delete."); return; }

        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure to delete item " + id + "? (All stock will be deleted)","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm!=JOptionPane.YES_OPTION) return;

        try {
            if(itemDAO.delete(id, currentUser)){
                JOptionPane.showMessageDialog(this,"Item deleted successfully!");
                loadStockData(); clearForm();
            } else {
                JOptionPane.showMessageDialog(this,"Failed to delete item. Check permission.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRestockDialog() {
        if(!btnRestock.isEnabled()) return;

        // Use a JComboBox to list available sizes for the selected item, or allow free text input.
        JTextField idField = new JTextField(txtClothId.getText().trim());
        JTextField sizeField = new JTextField(10);
        JTextField qtyField = new JTextField(10);

        if (!idField.getText().isEmpty()) { idField.setEditable(false); } // Lock ID if selected

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Item ID:")); panel.add(idField);
        panel.add(new JLabel("Size (e.g., S, M, L):")); panel.add(sizeField);
        panel.add(new JLabel("Quantity to Add:")); panel.add(qtyField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Restock Inventory",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String clothId = idField.getText().trim();
                String size = sizeField.getText().trim();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (clothId.isEmpty() || size.isEmpty() || qty <= 0) {
                    JOptionPane.showMessageDialog(this, "All fields must be valid and quantity > 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ClothingStock stock = new ClothingStock(clothId, size, qty);
                if (stockDAO.restockItem(stock, currentUser)) {
                    JOptionPane.showMessageDialog(this, qty + " units of size " + size + " added to stock for " + clothId);
                    loadStockData();
                } else {
                    JOptionPane.showMessageDialog(this, "Restock failed. Item ID might not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Restock Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm(){
        txtClothId.setText(""); txtColor.setText(""); txtMaterial.setText("");
        txtCategory.setText(""); txtPrice.setText(""); txtSupplierId.setText("");
        txtClothId.setEditable(true);
    }

    private void searchStock(){
        // Implement search logic here, using a modified DAO method to search the joined table.
        String keyword = txtSearch.getText().trim();
        if(keyword.isEmpty()){ loadStockData(); return; }
        // Placeholder: Implement a stockDAO.searchByKeyword(keyword) method.
        JOptionPane.showMessageDialog(this, "Search by keyword '" + keyword + "' functionality needs implementation in DAO.");
    }
}