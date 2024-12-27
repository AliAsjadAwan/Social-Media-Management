package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;

public class LoginPage extends JFrame {
    private static UserHashTable userHashTable;  // Assuming UserHashTable is already populated

    public LoginPage(UserHashTable userHashTable) {
        LoginPage.userHashTable = userHashTable; // Initialize the UserHashTable

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                userHashTable.writeUsersToFile("Backend/users.txt");
                userHashTable.writeConnectionsToFile("Backend/connections.txt");
                userHashTable.writePendingRequestsToFile("Backend/pending_Requests.txt");
                userHashTable.writePostsToFile("Backend/posts.txt");
            }
        });

        JFrame frame = new JFrame("Login Page");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Welcome to User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = new JTextField();
        configureTextField(usernameField);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField();
        configureTextField(passwordField);

        // Login Button
        JButton loginButton = getjButton(usernameField, passwordField, frame);

        // Register Button
        JButton registerButton = getjButton(frame);

        // Add Components
        panel.add(Box.createVerticalStrut(40));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(25));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(registerButton);

        // Frame Setup
        frame.add(panel);
        frame.setSize(350, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton getjButton(JFrame frame) {
        JButton registerButton = new RoundedButton("Register", new Color(30, 100, 200), Color.WHITE);
        registerButton.setPreferredSize(new Dimension(240, 30)); // Set size to 240x30
        registerButton.setMinimumSize(new Dimension(240, 30));    // Ensure minimum size is 240x30
        registerButton.setMaximumSize(new Dimension(240, 30));    // Also set maximum size to 240x30 to prevent resizing
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(_ -> {
            /* Navigate to signup page (implement the signup form as needed) */
            frame.dispose();
            new SignupPage(userHashTable);
        });

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(30, 150, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(30, 100, 200));
            }
        });
        return registerButton;
    }

    private JButton getjButton(JTextField usernameField, JPasswordField passwordField, JFrame frame) {
        JButton loginButton = new RoundedButton("Login", new Color(50, 150, 50), Color.WHITE);
        loginButton.setPreferredSize(new Dimension(240, 30));  // Set size to 240x30
        loginButton.setMinimumSize(new Dimension(240, 30));     // Ensure minimum size is 240x30
        loginButton.setMaximumSize(new Dimension(240, 30));     // Also set maximum size to 240x30 to prevent resizing
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = userHashTable.login(username, password); // Calling login method from UserHashTable
            if (user != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.dispose();
                new DashboardPage(user, userHashTable); // Pass the logged-in user to the DashboardPage
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(50, 200, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(50, 150, 50));
            }
        });
        return loginButton;
    }


    // Configure Text Fields for Proper Rendering
    public static void configureTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(240, 30)); // Increased size for better visibility
        field.setBackground(Color.WHITE);            // White background
        field.setForeground(Color.BLACK);            // Black text
        field.setCaretColor(Color.BLACK);            // Black cursor
        field.setOpaque(true);                       // Ensure proper rendering
        field.setBorder(new RoundedBorder(15));      // Add rounded corners with smaller radius
    }

    public static void main(String[] args) {
        // Example UserHashTable initialization (replace with your actual data)
        UserHashTable userHashTable = new UserHashTable(10);
        // Populate the hashtable with users
        SwingUtilities.invokeLater(() -> new LoginPage(userHashTable));
    }

    // Custom Rounded Button Class
    public static class RoundedButton extends JButton {
        private Color backgroundColor;
        private final Color textColor;

        public RoundedButton(String text, Color backgroundColor, Color textColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(textColor);
        }

        @Override
        public void setBackground(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            repaint(); // Repaint the button to reflect the new color
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
            g2.dispose();
        }
    }

    // Custom Rounded Border for Fields
    public static class RoundedBorder implements Border {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 10, 5, 10); // Reduced inset values to avoid text clipping
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY); // Border color
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
