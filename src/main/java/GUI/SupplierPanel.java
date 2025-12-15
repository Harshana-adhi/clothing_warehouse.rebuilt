package GUI;

import DAO.SupplierDAO;
import Models.Supplier;
import Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer; // Added for table styling
import javax.swing.table.JTableHeader; // Added for table styling
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SupplierPanel extends JPanel {

    private SupplierDAO supplierDAO = new SupplierDAO();
    private User currentUser;

    private JTextField txtId, txtName, txtPhone, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh, btnSearch;
    private JTable supplierTable;
    private DefaultTableModel tableModel;

    // Styling (Original Styling + New Table Styling Constants)
    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonhover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);

    // --- New Table Styling Constants ---
    private final Color tableHeaderBackground = new Color(50, 50, 50);
    private final Color tableHeaderForeground = Color.WHITE;
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font tableFontSmall = new Font("Segoe UI", Font.PLAIN, 14); // Smaller font for table cells

    public SupplierPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(10, 10));
        setBackground(panelBackground);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Panel
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(panelBackground);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(formBackground);
        formPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 10;

        JLabel lblId = new JLabel("Supplier ID:"); lblId.setFont(labelFont);
        txtId = new JTextField(); txtId.setFont(inputFont);

        JLabel lblName = new JLabel("Name:"); lblName.setFont(labelFont);
        txtName = new JTextField(); txtName.setFont(inputFont);

        JLabel lblPhone = new JLabel("Phone:"); lblPhone.setFont(labelFont);
        txtPhone = new JTextField(); txtPhone.setFont(inputFont);

        gbc.gridx=0; gbc.gridy=0; formPanel.add(lblId, gbc);
        gbc.gridx=1; gbc.gridy=0; formPanel.add(txtId, gbc);
        gbc.gridx=0; gbc.gridy=1; formPanel.add(lblName, gbc);
        gbc.gridx=1; gbc.gridy=1; formPanel.add(txtName, gbc);
        gbc.gridx=0; gbc.gridy=2; formPanel.add(lblPhone, gbc);
        gbc.gridx=1; gbc.gridy=2; formPanel.add(txtPhone, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(3,2,15,15));
        buttonPanel.setBackground(formBackground);

        JButton btnBack = new JButton("Back");
        btnBack.setBackground(backButtonColor);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(labelFont);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){ btnBack.setBackground(backButtonhover); }
            @Override
            public void mouseExited(MouseEvent e){ btnBack.setBackground(backButtonColor); }
        });
        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            if(topFrame instanceof MainFrame) ((MainFrame)topFrame).switchPanel("Dashboard");
        });
        buttonPanel.add(btnBack);

        btnAdd = createButton("Add");
        btnUpdate = createButton("Update");
        btnDelete = createButton("Delete");
        btnClear = createButton("Clear");
        btnRefresh = createButton("Refresh");

        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Right Panel: Search + Table (MODIFIED)
        JPanel rightPanel = new JPanel(new BorderLayout(10,10));
        rightPanel.setBackground(panelBackground);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(panelBackground);
        txtSearch = new JTextField(25); txtSearch.setFont(inputFont);
        btnSearch = createButton("Search");
        searchPanel.add(new JLabel("Search:")); searchPanel.add(txtSearch); searchPanel.add(btnSearch);

        // Define table model as read-only
        tableModel = new DefaultTableModel(new String[]{"Supplier ID","Name","Phone"},0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ensures all cells are view-only
            }
        };

        supplierTable = new JTable(tableModel);
        supplierTable.setFont(tableFontSmall); // Use smaller font for cells (14)
        supplierTable.setRowHeight(35);
        supplierTable.setSelectionBackground(tableSelection);

        // Apply advanced table styling
        supplierTable.setGridColor(new Color(230, 230, 230));
        supplierTable.setShowVerticalLines(false);

        JTableHeader header = supplierTable.getTableHeader();
        header.setFont(headerFont); // Use header font (16 bold)
        header.setBackground(tableHeaderBackground);
        header.setForeground(tableHeaderForeground);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        // Center align header text (requires DefaultTableCellRenderer import)
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Table Cell Renderer for alternating row colors and padding
        supplierTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JScrollPane tableScroll = new JScrollPane(supplierTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Add border

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        loadSuppliers();

        supplierTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                int row = supplierTable.getSelectedRow();
                if(row >= 0){
                    txtId.setText(tableModel.getValueAt(row,0).toString());
                    txtName.setText(tableModel.getValueAt(row,1).toString());
                    txtPhone.setText(tableModel.getValueAt(row,2).toString());
                    txtId.setEditable(false);
                }
            }
        });

        btnAdd.addActionListener(e -> addSupplier());
        btnUpdate.addActionListener(e -> updateSupplier());
        btnDelete.addActionListener(e -> deleteSupplier());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadSuppliers());
        btnSearch.addActionListener(e -> searchSupplier());

        applyRolePermissions();
    }

    private void applyRolePermissions(){
        if(currentUser==null) return;
        String role = currentUser.getRole().toLowerCase();
        switch(role){
            case "admin":
            case "manager":
                btnAdd.setEnabled(true); btnUpdate.setEnabled(true); btnDelete.setEnabled(true);
                break;
            case "staff":
                btnAdd.setEnabled(false); btnUpdate.setEnabled(false); btnDelete.setEnabled(false);
                break;
            default:
                btnAdd.setEnabled(false); btnUpdate.setEnabled(false); btnDelete.setEnabled(false);
        }
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

    private void loadSuppliers(){
        tableModel.setRowCount(0);
        List<Supplier> list = supplierDAO.getAll();
        for(Supplier s : list){
            tableModel.addRow(s.toValuesArray());
        }
    }

    private void addSupplier(){
        if(!btnAdd.isEnabled()) return;
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        if(id.isEmpty() || name.isEmpty() || phone.isEmpty()){
            JOptionPane.showMessageDialog(this,"All fields are required!");
            return;
        }
        Supplier s = new Supplier(id,name,phone);
        if(supplierDAO.insert(s,currentUser)){
            JOptionPane.showMessageDialog(this,"Supplier added successfully!");
            loadSuppliers(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this,"Failed to add supplier. Check ID uniqueness or permission.");
        }
    }

    private void updateSupplier(){
        if(!btnUpdate.isEnabled()) return;
        String id = txtId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select a supplier to update."); return; }
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        Supplier s = new Supplier(id,name,phone);
        if(supplierDAO.update(s,currentUser)){
            JOptionPane.showMessageDialog(this,"Supplier updated successfully!");
            loadSuppliers(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this,"Failed to update supplier. Check permission.");
        }
    }

    private void deleteSupplier(){
        if(!btnDelete.isEnabled()) return;
        String id = txtId.getText().trim();
        if(id.isEmpty()){ JOptionPane.showMessageDialog(this,"Select a supplier to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure to delete this supplier?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm!=JOptionPane.YES_OPTION) return;
        if(supplierDAO.delete(id,currentUser)){
            JOptionPane.showMessageDialog(this,"Supplier deleted successfully!");
            loadSuppliers(); clearForm();
        } else {
            JOptionPane.showMessageDialog(this,"Failed to delete supplier. Check permission.");
        }
    }

    private void clearForm(){
        txtId.setText(""); txtName.setText(""); txtPhone.setText("");
        txtId.setEditable(true);
    }

    private void searchSupplier(){
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        if(keyword.isEmpty()){ loadSuppliers(); return; }
        List<Supplier> list = supplierDAO.searchByIdOrName(keyword);
        for(Supplier s : list){
            tableModel.addRow(s.toValuesArray());
        }
    }
}