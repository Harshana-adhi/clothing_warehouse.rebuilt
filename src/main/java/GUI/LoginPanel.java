package GUI;

import DAO.UserDAO;
import Models.User;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignup;

    public LoginPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        // --- Left Panel: Login Form ---
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(new Color(30, 30, 30));
        leftPanel.setPreferredSize(new Dimension(450, 0)); // fixed width

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(50, 30, 300, 40);
        leftPanel.add(title);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUsername.setBounds(50, 100, 120, 30);
        leftPanel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 135, 300, 35);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setBackground(new Color(50, 50, 50));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPassword.setBounds(50, 190, 120, 30);
        leftPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 225, 300, 35);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBackground(new Color(50, 50, 50));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(50, 285, 300, 40);
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBorder(null);
        btnLogin.addActionListener(e -> handleLogin(mainFrame));
        leftPanel.add(btnLogin);

        btnSignup = new JButton("Don't have an account? Sign Up");
        btnSignup.setBounds(50, 340, 300, 35);
        btnSignup.setFocusPainted(false);
        btnSignup.setBackground(new Color(40, 40, 40));
        btnSignup.setForeground(Color.CYAN);
        btnSignup.setBorder(null);
        btnSignup.addActionListener(e -> mainFrame.showSignupPanel());
        leftPanel.add(btnSignup);

        // --- Right Panel: Image ---
        ImageIcon icon = new ImageIcon("src/main/resources/loginscreenimage.jpg"); // put your image path here
        Image img = icon.getImage();
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this); // scale image
            }
        };

        // --- Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);        // fixed divider
        splitPane.setEnabled(false);              // disable dragging
        splitPane.setDividerSize(0);              // hide divider
        splitPane.setOneTouchExpandable(false);   // no expandable buttons
        add(splitPane, BorderLayout.CENTER);
    }

    private void handleLogin(MainFrame frame) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        } else {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername());
            frame.loadDashboardByRole(user.getRole(), user); // pass full User object
        }
    }
}
