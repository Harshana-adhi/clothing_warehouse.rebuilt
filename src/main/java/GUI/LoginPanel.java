package GUI;

import DAO.UserDAO;
import Models.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignup;

    // Stylish Color Palette
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color INPUT_DARK = new Color(45, 45, 45);
    private static final Color INPUT_TEXT = Color.WHITE;
    private static final Color ACCENT_BLUE = new Color(0, 150, 255);
    private static final Color ACCENT_HOVER = new Color(0, 180, 255);
    private static final Color ACCENT_LINK = new Color(0, 200, 255);

    // Default Border
    private final Border defaultBorder;

    public LoginPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        // Define default border for input fields
        defaultBorder = BorderFactory.createCompoundBorder(
                new LineBorder(INPUT_DARK.brighter(), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Left Panel: Login Form ---
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(BG_DARK);
        leftPanel.setPreferredSize(new Dimension(450, 0)); // fixed width

        // 1. Divided Welcome Message
        JLabel welcomeLine1 = new JLabel("Welcome to");
        welcomeLine1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeLine1.setForeground(ACCENT_BLUE);
        welcomeLine1.setBounds(50, 40, 400, 40);
        leftPanel.add(welcomeLine1);

        JLabel welcomeLine2 = new JLabel("Clothing Warehouse");
        welcomeLine2.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeLine2.setForeground(ACCENT_BLUE);
        welcomeLine2.setBounds(50, 75, 400, 40); // Positioned slightly below Line 1
        leftPanel.add(welcomeLine2);

        // Subtitle/Login prompt (Increased font size)
        JLabel subtitle = new JLabel("Login to your account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 22)); // Increased size to 22
        subtitle.setForeground(Color.WHITE);
        subtitle.setBounds(50, 135, 350, 30); // Adjusted Y position to follow new header
        leftPanel.add(subtitle);

        // --- Username ---
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.LIGHT_GRAY);
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUsername.setBounds(50, 185, 120, 25); // Adjusted Y position
        leftPanel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 215, 350, 40); // Adjusted Y position
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setBackground(INPUT_DARK);
        txtUsername.setForeground(INPUT_TEXT);
        txtUsername.setCaretColor(ACCENT_LINK); // Blinking cursor color
        txtUsername.setBorder(defaultBorder);
        leftPanel.add(txtUsername);

        // --- Password ---
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.LIGHT_GRAY);
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblPassword.setBounds(50, 275, 120, 25); // Adjusted Y position
        leftPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 305, 350, 40); // Adjusted Y position
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBackground(INPUT_DARK);
        txtPassword.setForeground(INPUT_TEXT);
        txtPassword.setCaretColor(ACCENT_LINK); // Blinking cursor color
        txtPassword.setBorder(defaultBorder);
        leftPanel.add(txtPassword);

        // --- Login Button ---
        btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(50, 375, 350, 45); // Adjusted Y position
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(ACCENT_BLUE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE.darker(), 1));

        // Hover effect
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(ACCENT_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(ACCENT_BLUE);
            }
        });

        btnLogin.addActionListener(e -> handleLogin(mainFrame));
        leftPanel.add(btnLogin);

        // --- Signup Link ---
        btnSignup = new JButton("Don't have an account? Sign Up");
        btnSignup.setBounds(50, 435, 350, 35); // Adjusted Y position
        btnSignup.setFocusPainted(false);
        btnSignup.setContentAreaFilled(false);
        btnSignup.setForeground(ACCENT_LINK);
        btnSignup.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnSignup.setBorder(null);

        // Underline effect on hover
        btnSignup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSignup.setText("<html><u>Don't have an account? Sign Up</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnSignup.setText("Don't have an account? Sign Up");
            }
        });

        btnSignup.addActionListener(e -> mainFrame.showSignupPanel());
        leftPanel.add(btnSignup);


        // --- Right Panel: Image ---
        ImageIcon icon = new ImageIcon("src/main/resources/loginscreenimage.jpg");
        Image img = icon.getImage();

        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(60, 60, 60));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    g.drawString("Image Not Found: loginscreenimage.jpg", 10, 20);
                }
            }
        };

        // --- Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.setOneTouchExpandable(false);
        add(splitPane, BorderLayout.CENTER);
    }

    private void handleLogin(MainFrame frame) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getUsername(), "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            frame.loadDashboardByRole(user.getRole(), user);
        }
    }
}