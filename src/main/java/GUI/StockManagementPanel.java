package GUI;

import DAO.ClothingItemDAO;
import DAO.ClothingStockDAO;
import DAO.SupplierDAO;
import Models.ClothingItem;
import Models.ClothingStock;
import Models.Supplier;
import Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class StockManagementPanel extends JPanel {

    private ClothingItemDAO itemDAO = new ClothingItemDAO();
    private ClothingStockDAO stockDAO = new ClothingStockDAO();
    private User currentUser;

    // Form Components
    private JTextField txtClothId, txtMaterial, txtCategory, txtPrice, txtSupplierId, txtSearch;

    // Buttons
    private JButton btnAddItem, btnUpdateItem, btnRestock, btnDeleteItem, btnClear, btnRefresh, btnSearch, btnViewSupplier;

    private JTable stockTable;
    private DefaultTableModel tableModel;

    // Styling
    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonHover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);

    public StockManagementPanel(User user) {
        this.currentUser = user;

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
        // Removed Color field
        txtMaterial = createLabeledField(formPanel, gbc, "Material:", 1);
        txtCategory = createLabeledField(formPanel, gbc, "Category:", 2);
        txtPrice = createLabeledField(formPanel, gbc, "Price:", 3);
        txtSupplierId = createLabeledField(formPanel, gbc, "Supplier ID:", 4);

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
        JPanel buttonPanel = new JPanel(new GridLayout(4,2,15,15));
        buttonPanel.setBackground(formBackground);

        JButton btnBack = createBackButton("Back");
        btnAddItem = createButton("Add New Item");
        btnUpdateItem = createButton("Update Details");
        btnRestock = createButton("Restock Qty");
        btnDeleteItem = createButton("Delete Item");
        btnClear = createButton("Clear");
        btnRefresh = createButton("Refresh");
        btnViewSupplier = createButton("View Supplier");

        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
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
            String clothId = tableModel.getValueAt(row,0).toString();
            txtClothId.setText(clothId);
            // Removed setting txtColor
            txtMaterial.setText(tableModel.getValueAt(row,2).toString());
            txtCategory.setText(tableModel.getValueAt(row,3).toString());
            txtPrice.setText(tableModel.getValueAt(row,6).toString());

            try {
                txtSupplierId.setText(itemDAO.getSupplierIdByClothId(clothId));
            } catch(Exception e) {
                txtSupplierId.setText("");
            }

            txtClothId.setEditable(false);
        }
    }

    private void addNewItem(){
        if(!btnAddItem.isEnabled()) return;

        String id = txtClothId.getText().trim();
        String material = txtMaterial.getText().trim();
        String category = txtCategory.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String supId = txtSupplierId.getText().trim();

        if(id.isEmpty() || priceStr.isEmpty() || material.isEmpty()){
            JOptionPane.showMessageDialog(this,"Item ID, Material, and Price are required!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            ClothingItem item = new ClothingItem(id, material, category, price, supId);

            if(itemDAO.insert(item, currentUser)){
                JOptionPane.showMessageDialog(this,"Item added successfully! Use Restock to add stock.");
                clearForm();
                txtClothId.setText(id);
            } else {
                JOptionPane.showMessageDialog(this,"Failed to add item. ID may exist or DB error.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItemDetails(){
        if(!btnUpdateItem.isEnabled()) return;

        String id = txtClothId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select an item to update."); return; }

        try {
            BigDecimal price = new BigDecimal(txtPrice.getText().trim());
            ClothingItem item = new ClothingItem(id, txtMaterial.getText().trim(),
                    txtCategory.getText().trim(), price, txtSupplierId.getText().trim());

            if(itemDAO.update(item, currentUser)){
                JOptionPane.showMessageDialog(this,"Item details updated!");
                loadStockData(); clearForm();
            } else {
                JOptionPane.showMessageDialog(this,"Failed to update item details.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem(){
        if(!btnDeleteItem.isEnabled()) return;
        String id = txtClothId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select an item to delete."); return; }

        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure to delete item " + id + "?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm!=JOptionPane.YES_OPTION) return;

        if(itemDAO.delete(id, currentUser)){
            JOptionPane.showMessageDialog(this,"Item deleted successfully!");
            loadStockData(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this,"Failed to delete item.");
        }
    }

    private void showRestockDialog() {
        if(!btnRestock.isEnabled()) return;

        JTextField idField = new JTextField(txtClothId.getText().trim());
        JTextField colorField = new JTextField(10);
        JTextField sizeField = new JTextField(10);
        JTextField qtyField = new JTextField(10);

        if(!idField.getText().isEmpty()) idField.setEditable(false);

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
                int qty = Integer.parseInt(qtyField.getText().trim());

                if(clothId.isEmpty() || size.isEmpty() || qty <= 0){
                    JOptionPane.showMessageDialog(this,"All fields valid and quantity > 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ClothingStock stock = new ClothingStock(clothId, color, size, qty);
                if(stockDAO.restockItem(stock, currentUser)){
                    JOptionPane.showMessageDialog(this, qty + " units added to stock for " + clothId);
                    loadStockData();
                } else {
                    JOptionPane.showMessageDialog(this,"Restock failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm(){
        txtClothId.setText(""); txtMaterial.setText("");
        txtCategory.setText(""); txtPrice.setText(""); txtSupplierId.setText("");
        txtClothId.setEditable(true);
    }

    private void searchStock(){
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);

        if(keyword.isEmpty()){
            loadStockData(); return;
        }

        try {
            List<ClothingItem> items = itemDAO.search(keyword);
            for(ClothingItem item : items){
                List<String[]> stockList = stockDAO.getStockByClothId(item.getClothId());
                if(stockList.isEmpty()){
                    tableModel.addRow(new String[]{item.getClothId(), "-", item.getMaterial(), item.getCategory(), "-", "0", item.getPrice().toString()});
                } else {
                    for(String[] stockRow : stockList){
                        tableModel.addRow(new String[]{item.getClothId(),
                                stockRow[0]==null?"-":stockRow[0],
                                item.getMaterial(),
                                item.getCategory(),
                                stockRow[1],
                                stockRow[2],
                                item.getPrice().toString()});
                    }
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

        String supplierId = txtSupplierId.getText().trim();
        if(supplierId.isEmpty()){
            JOptionPane.showMessageDialog(this,"No supplier assigned for this item.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        SupplierDAO supplierDAO = new SupplierDAO();
        supplierDAO.getSupplierById(supplierId).ifPresentOrElse(
                supplier -> {
                    String message = "Supplier ID: "+supplier.getSupplierId()+"\n"+
                            "Name: "+supplier.getSupName()+"\n"+
                            "Contact: "+supplier.getTellNo();
                    JOptionPane.showMessageDialog(this, message, "Supplier Details", JOptionPane.INFORMATION_MESSAGE);
                },
                () -> JOptionPane.showMessageDialog(this,"Supplier not found.","Error",JOptionPane.ERROR_MESSAGE)
        );
    }
}
