package GUI;

import DAO.CustomerDAO;
import Models.Customer;
import Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private CustomerDAO customerDAO = new CustomerDAO();
    private User currentUser;

    private JTextField txtId, txtName, txtAddress, txtPhone, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh, btnSearch;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    // --- STYLING CONSTANTS (Copied from EmployeePanel) ---
    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136);
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonhover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Color tableHeaderBackground = new Color(50, 50, 50);
    private final Color tableHeaderForeground = Color.WHITE;
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font buttonFontLarge = new Font("Segoe UI", Font.BOLD, 16); // Larger font for buttons

    public CustomerPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(15, 15)); // Increased spacing
        setBackground(panelBackground);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Increased padding

        // --- Left Panel: Form + Buttons ---
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15)); // Increased spacing
        leftPanel.setBackground(panelBackground);

        // FORM PANEL STYLING
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(formBackground);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Light border
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "Customer Details",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        labelFont.deriveFont(Font.BOLD, 16),
                        buttonColor
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Increased inner padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 10;

        // Input Field Stylings
        JLabel lblId = new JLabel("Customer ID:"); lblId.setFont(labelFont);
        txtId = new JTextField();
        txtId.setFont(inputFont);
        txtId.setBorder(createInputBorder());

        JLabel lblName = new JLabel("Name:"); lblName.setFont(labelFont);
        txtName = new JTextField();
        txtName.setFont(inputFont);
        txtName.setBorder(createInputBorder());

        JLabel lblAddress = new JLabel("Address:"); lblAddress.setFont(labelFont);
        txtAddress = new JTextField();
        txtAddress.setFont(inputFont);
        txtAddress.setBorder(createInputBorder());

        JLabel lblPhone = new JLabel("Phone:"); lblPhone.setFont(labelFont);
        txtPhone = new JTextField();
        txtPhone.setFont(inputFont);
        txtPhone.setBorder(createInputBorder());

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblId, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblName, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblAddress, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtAddress, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblPhone, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtPhone, gbc);

        // BUTTON PANEL STYLING
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20)); // Increased gaps
        buttonPanel.setBackground(formBackground);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0)); // Top margin

        // --- Back Button ---
        JButton btnBack = new JButton("Back");
        btnBack.setBackground(backButtonColor);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(buttonFontLarge); // Use larger font
        btnBack.setPreferredSize(new Dimension(100, 45)); // Set preferred size
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(backButtonColor, 2));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnBack.setBackground(backButtonhover); }
            @Override
            public void mouseExited(MouseEvent e) { btnBack.setBackground(backButtonColor); }
        });
        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            if(topFrame instanceof MainFrame) {
                ((MainFrame) topFrame).switchPanel("Dashboard");
            }
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

        // --- Right Panel: Search + Table ---
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15)); // Increased spacing
        rightPanel.setBackground(panelBackground);

        // SEARCH PANEL STYLING
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Increased gaps
        searchPanel.setBackground(panelBackground);
        txtSearch = new JTextField(25);
        txtSearch.setFont(inputFont);
        txtSearch.setBorder(createInputBorder());
        btnSearch = createButton("Search");
        JLabel lblSearch = new JLabel("Search:"); lblSearch.setFont(labelFont);
        searchPanel.add(lblSearch); searchPanel.add(txtSearch); searchPanel.add(btnSearch);

        // TABLE STYLING
        tableModel = new DefaultTableModel(new String[]{"Customer ID", "Name", "Address", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // All cells are set to be uneditable (view-only)
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setFont(inputFont);
        customerTable.setRowHeight(35);
        customerTable.setSelectionBackground(tableSelection);
        customerTable.setGridColor(new Color(230, 230, 230)); // Lighter grid lines
        customerTable.setShowVerticalLines(false); // Hide vertical lines

        // Table Header Styling
        JTableHeader header = customerTable.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(tableHeaderBackground);
        header.setForeground(tableHeaderForeground);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        // Center align header text
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Table Cell Renderer for alternating row colors
        customerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding to cells
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : formBackground); // Subtle alternating row colors
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(customerTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Border around table

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        loadCustomers();

        // Row selection
        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = customerTable.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                    txtPhone.setText(tableModel.getValueAt(row, 3).toString());
                    txtId.setEditable(false);
                }
            }
        });

        // Button Actions
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadCustomers());
        btnSearch.addActionListener(e -> searchCustomer());

        // Apply role permissions
        applyRolePermissions();
    }

    private void applyRolePermissions() {
        if (currentUser == null) return;

        String role = currentUser.getRole().toLowerCase();
        switch (role) {
            case "admin":
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                break;
            case "manager":
                btnAdd.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                break;
            case "staff":
                btnAdd.setEnabled(true);    // Staff can add
                btnUpdate.setEnabled(true); // Staff can update
                btnDelete.setEnabled(false); // Staff cannot delete
                break;
            default:
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
        }
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(buttonColor); btn.setForeground(Color.WHITE);
        btn.setFont(buttonFontLarge); // Use larger font
        btn.setPreferredSize(new Dimension(100, 45)); // Set preferred size for larger button
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(buttonColor, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(buttonHover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(buttonColor); }
        });
        return btn;
    }

    // Helper method to create input field border
    private javax.swing.border.Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAll();
        for (Customer c : customers) {
            tableModel.addRow(c.toValuesArray());
        }
    }

    private void addCustomer() {
        if (!btnAdd.isEnabled()) return;
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String phone = txtPhone.getText().trim();

        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        Customer c = new Customer(id, name, address, phone);
        if (customerDAO.insert(c, currentUser)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            loadCustomers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer. Check ID uniqueness or permission.");
        }
    }

    private void updateCustomer() {
        if (!btnUpdate.isEnabled()) return;
        String id = txtId.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Select a customer to update."); return; }

        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String phone = txtPhone.getText().trim();

        Customer c = new Customer(id, name, address, phone);
        if (customerDAO.update(c, currentUser)) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            loadCustomers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update customer. Check permissions.");
        }
    }

    private void deleteCustomer() {
        if (!btnDelete.isEnabled()) return;
        String id = txtId.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Select a customer to delete."); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (customerDAO.delete(id, currentUser)) {
            JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            loadCustomers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete customer. Check permissions.");
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtAddress.setText(""); txtPhone.setText("");
        txtId.setEditable(true);
    }

    private void searchCustomer() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        if (keyword.isEmpty()) { loadCustomers(); return; }
        List<Customer> customers = customerDAO.searchByIdOrName(keyword);
        for (Customer c : customers) {
            tableModel.addRow(c.toValuesArray());
        }
    }
}