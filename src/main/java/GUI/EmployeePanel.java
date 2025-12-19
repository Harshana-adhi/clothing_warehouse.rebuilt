package GUI;

import DAO.EmployeeDAO;
import Models.Employee;
import Models.User;
import utils.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EmployeePanel extends JPanel {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private User currentUser;

    private JTextField txtId, txtName, txtPhone, txtSalary, txtPosition, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh, btnSearch;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

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
    private final Font buttonFontLarge = new Font("Segoe UI", Font.BOLD, 16);

    public EmployeePanel(User user) {
        this.currentUser = user;

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
                        "Employee Details",
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

        JLabel lblId = new JLabel("Employee ID:"); lblId.setFont(labelFont);
        txtId = new JTextField();
        txtId.setFont(inputFont);
        txtId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblName = new JLabel("Name:"); lblName.setFont(labelFont);
        txtName = new JTextField();
        txtName.setFont(inputFont);
        txtName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblPhone = new JLabel("Phone:"); lblPhone.setFont(labelFont);
        txtPhone = new JTextField();
        txtPhone.setFont(inputFont);
        txtPhone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblSalary = new JLabel("Salary:"); lblSalary.setFont(labelFont);
        txtSalary = new JTextField();
        txtSalary.setFont(inputFont);
        txtSalary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblPosition = new JLabel("Position:"); lblPosition.setFont(labelFont);
        txtPosition = new JTextField();
        txtPosition.setFont(inputFont);
        txtPosition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblId, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblName, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblPhone, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblSalary, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtSalary, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(lblPosition, gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(txtPosition, gbc);

        // BUTTON PANEL STYLING
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBackground(formBackground);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnBack = new JButton("Back");
        btnBack.setBackground(backButtonColor);
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(buttonFontLarge);
        btnBack.setPreferredSize(new Dimension(100, 45));
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
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(panelBackground);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        searchPanel.setBackground(panelBackground);
        txtSearch = new JTextField(25);
        txtSearch.setFont(inputFont);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        btnSearch = createButton("Search");
        JLabel lblSearch = new JLabel("Search:"); lblSearch.setFont(labelFont);
        searchPanel.add(lblSearch); searchPanel.add(txtSearch); searchPanel.add(btnSearch);

        tableModel = new DefaultTableModel(new String[]{"Employee ID", "Name", "Phone", "Salary", "Position"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(inputFont);
        employeeTable.setRowHeight(35);
        employeeTable.setSelectionBackground(tableSelection);
        employeeTable.setGridColor(new Color(230, 230, 230));
        employeeTable.setShowVerticalLines(false);

        JTableHeader header = employeeTable.getTableHeader();
        header.setFont(headerFont);
        header.setBackground(tableHeaderBackground);
        header.setForeground(tableHeaderForeground);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                if (!isSelected) c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : formBackground);
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(employeeTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        loadEmployees();

        employeeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = employeeTable.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtPhone.setText(tableModel.getValueAt(row, 2).toString());
                    txtSalary.setText(tableModel.getValueAt(row, 3).toString());
                    txtPosition.setText(tableModel.getValueAt(row, 4).toString());
                    txtId.setEditable(false);
                }
            }
        });

        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadEmployees());
        btnSearch.addActionListener(e -> searchEmployee());

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
                btnDelete.setEnabled(false);
                break;
            case "staff":
            default:
                btnAdd.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
        }
    }

    private JButton createButton(String text) {
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

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAll();
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getEmpName(),
                    emp.getTellNo(),
                    emp.getSalary(),
                    emp.getEmpPosition()
            });
        }
    }

    private void addEmployee() {
        if (!btnAdd.isEnabled()) return;

        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String salaryStr = txtSalary.getText().trim();
        String position = txtPosition.getText().trim();

        //  START VALIDATION INTEGRATION (ADD)

        // 1. Check for required fields
        if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || salaryStr.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Employee ID format
        if (!ValidationUtil.isValidEmployeeID(id)) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID format. Must be E followed by 3 or more digits (e.g., E001).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtId.requestFocus();
            return;
        }

        // 3. Name format
        if (!ValidationUtil.isValidName(name)) {
            JOptionPane.showMessageDialog(this, "Invalid Name format. Name must contain only letters and spaces.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return;
        }

        // 4. Phone format
        if (!ValidationUtil.isValidSriLankanMobile(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid Phone number format. Use 10 digits (07x...) or (+947x...).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return;
        }

        // 5. Salary validation
        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary <= 0) {
                JOptionPane.showMessageDialog(this, "Salary must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtSalary.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid numerical value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtSalary.requestFocus();
            return;
        }
        //  END VALIDATION INTEGRATION (ADD)

        // Business Logic Check (Original logic retained)
        if ("Manager".equalsIgnoreCase(position) && ("Manager".equalsIgnoreCase(currentUser.getRole()) || "Staff".equalsIgnoreCase(currentUser.getRole()))) {
            JOptionPane.showMessageDialog(this, "Only Admin can add new Managers.", "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp = new Employee(id, name, phone, salary, position);

        if (employeeDAO.insert(emp, currentUser)) {
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            loadEmployees();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add employee. ID may already exist or DB error.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (!btnUpdate.isEnabled()) return;

        String id = txtId.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Select an employee to update.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String salaryStr = txtSalary.getText().trim();
        String position = txtPosition.getText().trim();

        // --- START VALIDATION INTEGRATION (UPDATE) ---
        // 1. Check for required fields
        if (name.isEmpty() || phone.isEmpty() || salaryStr.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (except ID) are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Name format
        if (!ValidationUtil.isValidName(name)) {
            JOptionPane.showMessageDialog(this, "Invalid Name format. Name must contain only letters and spaces.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return;
        }

        // 3. Phone format
        if (!ValidationUtil.isValidSriLankanMobile(phone)) {
            JOptionPane.showMessageDialog(this, "Invalid Phone number format. Use 10 digits (07x...) or (+947x...).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return;
        }

        // 4. Salary validation
        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary <= 0) {
                JOptionPane.showMessageDialog(this, "Salary must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtSalary.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid numerical value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtSalary.requestFocus();
            return;
        }
        //  END VALIDATION INTEGRATION (UPDATE)

        Employee emp = new Employee(id, name, phone, salary, position);

        if (employeeDAO.update(emp, currentUser)) {
            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            loadEmployees();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update employee. Check permissions or DB error.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        if (!btnDelete.isEnabled()) return;
        String id = txtId.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Select an employee to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (employeeDAO.delete(id, currentUser)) {
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            loadEmployees();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete employee. Check permissions or linked user accounts.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtPhone.setText(""); txtSalary.setText(""); txtPosition.setText("");
        txtId.setEditable(true);
    }

    private void searchEmployee() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        if (keyword.isEmpty()) { loadEmployees(); return; }
        List<Employee> employees = employeeDAO.searchByIdOrName(keyword);
        for (Employee emp : employees) {
            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(), emp.getEmpName(), emp.getTellNo(), emp.getSalary(), emp.getEmpPosition()
            });
        }
    }
}