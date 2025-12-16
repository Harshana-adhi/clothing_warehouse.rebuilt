package GUI;

import DAO.UserDAO;
import Models.User;
import DAO.EmployeeDAO;
import Models.Employee;
import utils.ValidationUtil;

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

public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> roleComboBox;
    private JTextField txtEmployeeId;
    private JTextField txtSearch; // Added search field

    private JButton addButton, updateButton, deleteButton, clearButton, btnRefresh, btnSearch, btnBack;

    private UserDAO userDAO;
    private EmployeeDAO empDAO;
    private User currentUser; // Logged in user

    // --- STYLING CONSTANTS COPIED FROM EmployeePanel ---
    private final Color panelBackground = new Color(245, 245, 245);
    private final Color formBackground = Color.WHITE;
    private final Color buttonColor = new Color(0, 150, 136); // Primary button color
    private final Color backButtonColor = new Color(200, 150, 136);
    private final Color backButtonhover = new Color(200, 100, 136);
    private final Color buttonHover = new Color(0, 137, 123);
    private final Color tableSelection = new Color(200, 230, 201);
    private final Color tableHeaderBackground = new Color(50, 50, 50);
    private final Color tableHeaderForeground = Color.WHITE;
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font buttonFontLarge = new Font("Segoe UI", Font.BOLD, 16);
    // ---------------------------------------------------

    public UserManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        userDAO = new UserDAO();
        empDAO = new EmployeeDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(panelBackground);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Left Panel: Form + Buttons ---
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setBackground(panelBackground);

        // FORM PANEL STYLING
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(formBackground);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        "User Account Details",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        labelFont.deriveFont(Font.BOLD, 16),
                        buttonColor
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.ipady = 10;

        // Username
        JLabel lblUsername = new JLabel("Username:"); lblUsername.setFont(labelFont);
        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBorder(createInputBorder());

        // Password
        JLabel lblPassword = new JLabel("Password:"); lblPassword.setFont(labelFont);
        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBorder(createInputBorder());

        // Role
        JLabel lblRole = new JLabel("Role:"); lblRole.setFont(labelFont);
        roleComboBox = new JComboBox<>(new String[]{"Manager","Staff"}); // Admin removed as per original
        roleComboBox.setFont(inputFont);
        ((JLabel)roleComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Employee ID
        JLabel lblEmployeeId = new JLabel("Employee ID:"); lblEmployeeId.setFont(labelFont);
        txtEmployeeId = new JTextField();
        txtEmployeeId.setFont(inputFont);
        txtEmployeeId.setBorder(createInputBorder());

        // Placing components in GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblUsername, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblRole, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(roleComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblEmployeeId, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtEmployeeId, gbc);


        // BUTTON PANEL STYLING
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBackground(formBackground);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Back Button
        btnBack = createBackButton("Back");
        btnBack.addActionListener(e -> {
            Container topFrame = SwingUtilities.getWindowAncestor(this);
            if(topFrame instanceof MainFrame) {
                // Assuming MainFrame exists and has switchPanel method
                try {
                    ((MainFrame) topFrame).switchPanel("Dashboard");
                } catch (Exception ex) {
                    System.err.println("MainFrame or switchPanel method not found: " + ex.getMessage());
                }
            }
        });

        // Primary Buttons (Using createPrimaryButton, which matches EmployeePanel's createButton)
        addButton = createPrimaryButton("Add");
        updateButton = createPrimaryButton("Update");
        deleteButton = createPrimaryButton("Delete");
        clearButton = createPrimaryButton("Clear");
        btnRefresh = createPrimaryButton("Refresh");

        buttonPanel.add(btnBack);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(btnRefresh);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Right Panel: Search + Table ---
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(panelBackground);

        // Search Bar Container (Using GridBagLayout for robust spacing, as fixed earlier)
        JPanel searchContainer = new JPanel(new GridBagLayout());
        searchContainer.setBackground(panelBackground);
        searchContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Extra vertical spacing

        GridBagConstraints searchGBC = new GridBagConstraints();
        searchGBC.insets = new Insets(0, 5, 0, 5); // Padding between search elements
        searchGBC.anchor = GridBagConstraints.EAST; // Anchor elements to the right

        txtSearch = new JTextField(25);
        txtSearch.setFont(inputFont);
        txtSearch.setBorder(createInputBorder());

        // --- FIX: Using createPrimaryButton for the search button (100x45) ---
        btnSearch = createPrimaryButton("Search");
        // -----------------------------------------------------------------------

        JLabel lblSearch = new JLabel("Search (Username/Emp ID):"); lblSearch.setFont(labelFont);

        // 1. Label
        searchGBC.gridx = 0; searchGBC.gridy = 0; searchGBC.weightx = 0.0;
        searchContainer.add(lblSearch, searchGBC);

        // 2. Text Field
        searchGBC.gridx = 1; searchGBC.gridy = 0; searchGBC.weightx = 1.0;
        searchGBC.fill = GridBagConstraints.HORIZONTAL;
        searchContainer.add(txtSearch, searchGBC);

        // 3. Button
        searchGBC.gridx = 2; searchGBC.gridy = 0; searchGBC.weightx = 0.0;
        searchGBC.fill = GridBagConstraints.NONE;
        searchContainer.add(btnSearch, searchGBC);


        // Table Model Setup (Retaining EmployeePanel style)
        tableModel = new DefaultTableModel(new Object[]{"User ID","Username","Role","Employee ID"},0) {
            public boolean isCellEditable(int row, int column){ return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(inputFont);
        userTable.setRowHeight(35);
        userTable.setSelectionBackground(tableSelection);
        userTable.setGridColor(new Color(230, 230, 230));
        userTable.setShowVerticalLines(false);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        // Table Header Styling (Retaining EmployeePanel style)
        JTableHeader header = userTable.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(tableHeaderBackground);
        header.setForeground(tableHeaderForeground);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Table Cell Renderer Styling (Retaining EmployeePanel style)
        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Added horizontal padding
                setHorizontalAlignment(SwingConstants.CENTER); // Center alignment retained
                if (!isSelected) c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : formBackground);
                return c;
            }
        });


        JScrollPane tableScroll = new JScrollPane(userTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Use the new container in BorderLayout.NORTH
        rightPanel.add(searchContainer, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // Split Pane (Retaining EmployeePanel style)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450); // Setting initial size similar to employee panel
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);


        loadUsers();
        attachListeners();
        applyRolePermissions();
    }

    // Helper method for input field border styling (Matches EmployeePanel)
    private Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    // Helper method for primary buttons (Matches EmployeePanel's createButton)
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(buttonColor); btn.setForeground(Color.WHITE);
        btn.setFont(buttonFontLarge);
        btn.setPreferredSize(new Dimension(100, 45));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(buttonColor, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(buttonHover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(buttonColor); }
        });
        return btn;
    }

    // Helper method for the Back button (Matches EmployeePanel's style)
    private JButton createBackButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(backButtonColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(buttonFontLarge);
        btn.setPreferredSize(new Dimension(100, 45));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(backButtonColor, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(backButtonhover); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(backButtonColor); }
        });
        return btn;
    }


    private void applyRolePermissions() {
        // User management is typically restricted to Admin/Manager roles in this type of application
        if (currentUser == null) return;
        String role = currentUser.getRole().toLowerCase();

        // Staff can only view and refresh
        if (role.equals("staff")) {
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
        // Manager can add and update, but often cannot delete users or perform admin-level tasks
        else if (role.equals("manager")) {
            addButton.setEnabled(true);
            updateButton.setEnabled(true);
            deleteButton.setEnabled(false); // Common restriction for Managers
        }
        // Admin has full access (Default)
        else if (role.equals("admin")) {
            addButton.setEnabled(true);
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }


    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers(currentUser);
        for(User u : users) {
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    safeToString(u.getUsername()),
                    safeToString(u.getRole()),
                    safeToString(u.getEmployeeId())
            });
        }
    }

    private void attachListeners() {
        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        clearButton.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadUsers());
        btnSearch.addActionListener(e -> searchUser());


        userTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = userTable.getSelectedRow();
                if(row>=0) {
                    txtUsername.setText(safeToString(tableModel.getValueAt(row,1)));
                    txtPassword.setText("");
                    roleComboBox.setSelectedItem(safeToString(tableModel.getValueAt(row,2)));
                    txtEmployeeId.setText(safeToString(tableModel.getValueAt(row,3)));
                    txtUsername.setEditable(false); // Username/ID cannot be changed after selection
                }
            }
        });
    }

    private String safeToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private void addUser() {
        if (!addButton.isEnabled()) return;
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = roleComboBox.getSelectedItem().toString();
        String empId = txtEmployeeId.getText().trim();

        if(username.isEmpty() || password.isEmpty() || empId.isEmpty()) {
            JOptionPane.showMessageDialog(this,"All fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!ValidationUtil.isValidUsername(username)) {
            JOptionPane.showMessageDialog(this,"Invalid username format! (Letters/Numbers, min 3)", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if(!ValidationUtil.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this,"Password must be min 6 chars, contain letters and numbers!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }
        if(!ValidationUtil.isValidEmployeeID(empId)) {
            JOptionPane.showMessageDialog(this,"Invalid Employee ID format. Must be E followed by 3 or more digits (e.g., E001).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtEmployeeId.requestFocus();
            return;
        }

        Employee emp = empDAO.getById(empId);
        if(emp==null) { JOptionPane.showMessageDialog(this,"Employee not found with this ID!", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // --- Position validation: Manager vs Staff (Logic retained) ---
        if("Manager".equalsIgnoreCase(role) && !"Manager".equalsIgnoreCase(emp.getEmpPosition())) {
            JOptionPane.showMessageDialog(this,"Cannot assign Manager role to an employee whose position is not Manager!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if("Staff".equalsIgnoreCase(role) && "Manager".equalsIgnoreCase(emp.getEmpPosition())) {
            JOptionPane.showMessageDialog(this,"Cannot assign Staff role to an employee whose position is Manager!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User(username,password,role,empId);
        boolean success = userDAO.createUser(newUser,currentUser);
        if(success) {
            JOptionPane.showMessageDialog(this,"User added successfully!");
            loadUsers();
            clearForm();
        }
        else JOptionPane.showMessageDialog(this,"Failed to add user. Check permissions or duplicate username.", "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateUser() {
        if (!updateButton.isEnabled()) return;
        int row = userTable.getSelectedRow();
        if(row<0) { JOptionPane.showMessageDialog(this,"Select a user to update", "Validation Error", JOptionPane.WARNING_MESSAGE); return;}

        // Get the existing ID from the table selection
        int userId;
        try {
            userId = (int)tableModel.getValueAt(row,0);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(this,"Error: Could not retrieve User ID. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = roleComboBox.getSelectedItem().toString();
        String empId = txtEmployeeId.getText().trim();

        // Check if password field was left empty - if so, assume no change (Only on update)
        if (password.isEmpty()) {
            // Retrieve current password hash/data from DAO to pass to update method without changing it.
            User existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
                JOptionPane.showMessageDialog(this, "User not found for update.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            password = existingUser.getPassword(); // Assuming the DAO returns a User object with the stored (hashed) password
        } else {
            // If password is not empty, validate it
            if(!ValidationUtil.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this,"New Password must be min 6 chars, contain letters and numbers!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtPassword.requestFocus();
                return;
            }
        }

        if(username.isEmpty() || empId.isEmpty()) { // Password check handled above
            JOptionPane.showMessageDialog(this,"Username and Employee ID are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!ValidationUtil.isValidUsername(username)) {
            JOptionPane.showMessageDialog(this,"Invalid username format! (Letters/Numbers, min 3)", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if(!ValidationUtil.isValidEmployeeID(empId)) {
            JOptionPane.showMessageDialog(this,"Invalid Employee ID format. Must be E followed by 3 or more digits (e.g., E001).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtEmployeeId.requestFocus();
            return;
        }

        Employee emp = empDAO.getById(empId);
        if(emp==null) { JOptionPane.showMessageDialog(this,"Employee not found with this ID!", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        // --- Position validation: Manager vs Staff (Logic retained) ---
        if("Manager".equalsIgnoreCase(role) && !"Manager".equalsIgnoreCase(emp.getEmpPosition())) {
            JOptionPane.showMessageDialog(this,"Cannot assign Manager role to an employee whose position is not Manager!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if("Staff".equalsIgnoreCase(role) && "Manager".equalsIgnoreCase(emp.getEmpPosition())) {
            JOptionPane.showMessageDialog(this,"Cannot assign Staff role to an employee whose position is Manager!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User userToUpdate = new User(userId,username,password,role,empId);
        boolean success = userDAO.updateUser(userToUpdate,currentUser);
        if(success) {
            JOptionPane.showMessageDialog(this,"User updated successfully!");
            loadUsers();
            clearForm();
        }
        else JOptionPane.showMessageDialog(this,"Failed to update user. Check permissions or duplicate username.", "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteUser() {
        if (!deleteButton.isEnabled()) return;
        int row = userTable.getSelectedRow();
        if(row<0) { JOptionPane.showMessageDialog(this,"Select a user to delete", "Validation Error", JOptionPane.WARNING_MESSAGE); return;}

        int userId = (int)tableModel.getValueAt(row,0);
        String role = safeToString(tableModel.getValueAt(row,2));

        // Logic check retained: Admin deletion protection
        if("Admin".equalsIgnoreCase(role) && !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,"Only Admin can delete an Admin account!", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete this user?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm!=JOptionPane.YES_OPTION) return;

        boolean success = userDAO.deleteUser(userId,role,currentUser);
        if(success) {
            JOptionPane.showMessageDialog(this,"User deleted successfully!");
            loadUsers();
            clearForm();
        }
        else JOptionPane.showMessageDialog(this,"Cannot delete user. Check permissions or linked data.", "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmployeeId.setText("");
        txtUsername.setEditable(true); // Re-enable editing for new entry (FIXED)
        if(roleComboBox.getItemCount()>0) roleComboBox.setSelectedIndex(0);
        userTable.clearSelection();
    }

    private void searchUser() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        if (keyword.isEmpty()) {
            loadUsers();
            return;
        }

        // Calls the new method in UserDAO
        List<User> users = userDAO.searchUsers(keyword, currentUser);

        for(User u : users) {
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    safeToString(u.getUsername()),
                    safeToString(u.getRole()),
                    safeToString(u.getEmployeeId())
            });
        }
    }
}