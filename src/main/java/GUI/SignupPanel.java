package GUI;

import DAO.UserDAO;
import Models.User;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSignup, btnBack;

    public SignupPanel(MainFrame mainFrame) {

        setLayout(null);
        setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(100, 20, 300, 40);
        add(title);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUsername.setBounds(50, 100, 120, 30);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 135, 300, 35);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setBackground(new Color(50, 50, 50));
        txtUsername.setForeground(Color.WHITE);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPassword.setBounds(50, 190, 120, 30);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 225, 300, 35);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBackground(new Color(50, 50, 50));
        txtPassword.setForeground(Color.WHITE);
        add(txtPassword);

        JLabel lblRole = new JLabel("Select Role:");
        lblRole.setForeground(Color.WHITE);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblRole.setBounds(50, 280, 120, 30);
        add(lblRole);

        cmbRole = new JComboBox<>(new String[]{"Admin", "Manager", "Staff"});
        cmbRole.setBounds(50, 315, 300, 35);
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cmbRole);

        btnSignup = new JButton("Create Account");
        btnSignup.setBounds(50, 370, 300, 40);
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSignup.setBackground(new Color(0, 120, 215));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.addActionListener(e -> handleSignup());
        add(btnSignup);

        btnBack = new JButton("Back to Login");
        btnBack.setBounds(50, 420, 300, 35);
        btnBack.setForeground(Color.CYAN);
        btnBack.setBackground(new Color(40, 40, 40));
        btnBack.setBorder(null);
        btnBack.addActionListener(e -> mainFrame.showLoginPanel());
        add(btnBack);
    }

    private void handleSignup() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cmbRole.getSelectedItem().toString();

        UserDAO dao = new UserDAO();

        if (dao.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists!");
            return;
        }

        boolean added = dao.signup(new User(username, password, role));

        if (added) {
            JOptionPane.showMessageDialog(this, "Account created successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Error creating account!");
        }
    }
}
