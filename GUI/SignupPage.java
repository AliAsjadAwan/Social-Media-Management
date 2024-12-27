package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.border.Border;
import com.toedter.calendar.JDateChooser;

public class SignupPage {

    private final JFrame frame;
    private final UserHashTable userhashTable;

    public SignupPage(UserHashTable userHashTable) {
        this.userhashTable = userHashTable;
        frame = new JFrame("Signup Page");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = new JTextField();
        JLabel usernameErrorLabel = new JLabel();
        usernameErrorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        configureTextField(usernameField);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField();
        JLabel passwordErrorLabel = new JLabel();
        passwordErrorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        configureTextField(passwordField);

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField emailField = new JTextField();
        JLabel emailErrorLabel = new JLabel();
        emailErrorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        configureTextField(emailField);

        // Date of Birth (Calendar) Field
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dobLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JDateChooser dobChooser = new JDateChooser();
        configureDateField(dobChooser);

        // Bio Field (Optional)
        JLabel bioLabel = new JLabel("Bio (Optional):");
        bioLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField bioField = new JTextField();
        configureTextField(bioField);

        // Gender Field
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        genderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        configureComboBox(genderComboBox);

        // City Field
        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField cityField = new JTextField();
        configureTextField(cityField);

        // Country Field
        JLabel countryLabel = new JLabel("Country:");
        countryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField countryField = new JTextField();
        configureTextField(countryField);

        // Signup Button
        JButton signupButton = new RoundedButton("Sign Up", new Color(30, 100, 200), Color.WHITE);
        signupButton.setPreferredSize(new Dimension(120,30));
        signupButton.addActionListener(_ -> {
            // Validation for mandatory fields
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            Date dob = dobChooser.getDate();
            String gender = (String) genderComboBox.getSelectedItem();
            String bio = bioField.getText();
            String city = cityField.getText();
            String country = countryField.getText();
            Location location = new Location(city, country);

            StringBuilder errorMessage = new StringBuilder();

            if (userHashTable.isUsernameTaken(username)) {
                errorMessage.append("->Username already taken.\n");
            }
            if (userHashTable.isValidPassword(password)) {
                errorMessage.append("->Password must contain:\n*One Uppercase Letter\n*One Special Character\n*One Number\n");
            }
            if (userHashTable.isValidEmail(email)) {
                errorMessage.append("->Invalid email pattern.\n");
            }
            if (dob != null) {
                LocalDate date = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int age = Period.between(date, LocalDate.now()).getYears();
                if (age <= 15) {
                    errorMessage.append("->Age must be greater than 15.\n");
                }
            }

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob == null || city.isEmpty() || country.isEmpty()) {
                errorMessage.append("->Please fill in all mandatory fields.");
            }
            if (!errorMessage.isEmpty()) {
                JOptionPane.showMessageDialog(frame, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!bio.isEmpty()) {
                userHashTable.signupWithBio(username, password, email, bio, new java.text.SimpleDateFormat("yyyy-MM-dd").format(dob), gender, location);
                JOptionPane.showMessageDialog(frame, "Signup Successful!");
                frame.dispose();
                new LoginPage(userHashTable);
            }else {
                userHashTable.signup(username, password, email, new java.text.SimpleDateFormat("yyyy-MM-dd").format(dob), gender, location);
                JOptionPane.showMessageDialog(frame, "Signup Successful!");
                frame.dispose();
                new LoginPage(userHashTable);
            }
        });

        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(30, 150, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(new Color(30, 100, 200));
            }
        });

        JButton backButton = getjButton();
        backButton.setPreferredSize(new Dimension(120,30));

        // Username Field KeyListener
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String username = usernameField.getText();
                if (userHashTable.isUsernameTaken(username)) {
                    usernameErrorLabel.setText("Username already taken");
                    usernameErrorLabel.setForeground(Color.RED);
                } else if (username.isEmpty()) {
                    usernameErrorLabel.setText("Username cannot be empty");
                    usernameErrorLabel.setForeground(Color.RED);
                } else {
                    usernameErrorLabel.setText("Username available");
                    usernameErrorLabel.setForeground(Color.GREEN);
                }
            }
        });

// Password Field KeyListener
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passwordField.getPassword());
                StringBuilder missingRequirements = new StringBuilder();

                // Check for minimum length (6 characters)
                if (password.length() < 6) {
                    missingRequirements.append("* At least 6 characters\n");
                }

                // Check for missing uppercase letter
                if (!password.matches(".*[A-Z].*")) {
                    missingRequirements.append("* 1 Uppercase Letter\n");
                }

                // Check for missing special character
                if (!password.matches(".*[!@#$%^&*()_+\\-={};':\"\\\\|,.<>?].*")) {
                    missingRequirements.append("* 1 Special Character\n");
                }

                // Check for missing number
                if (!password.matches(".*\\d.*")) {
                    missingRequirements.append("* 1 Number\n");
                }

                // Handle empty password case
                if (password.isEmpty()) {
                    passwordErrorLabel.setText("Password cannot be empty");
                    passwordErrorLabel.setForeground(Color.RED);
                } else if (!missingRequirements.isEmpty()) {
                    passwordErrorLabel.setText("Password must contain:\n" + missingRequirements.toString());
                    passwordErrorLabel.setForeground(Color.RED);
                } else {
                    passwordErrorLabel.setText("Valid password");
                    passwordErrorLabel.setForeground(Color.GREEN);
                }
            }
        });





// Email Field KeyListener
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String email = emailField.getText();
                if (!userHashTable.isValidEmail(email)) {
                    emailErrorLabel.setText("Valid email address");
                    emailErrorLabel.setForeground(Color.GREEN);
                } else if (email.isEmpty()) {
                    emailErrorLabel.setText("Email cannot be empty");
                    emailErrorLabel.setForeground(Color.RED);
                } else {
                    emailErrorLabel.setText("Invalid email address");
                    emailErrorLabel.setForeground(Color.RED);
                }
            }
        });


        panel.add(Box.createVerticalStrut(40));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(usernameErrorLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(passwordErrorLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(emailErrorLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(dobLabel);
        panel.add(dobChooser);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bioLabel);
        panel.add(bioField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(genderLabel);
        panel.add(genderComboBox);
        panel.add(Box.createVerticalStrut(15));
        panel.add(cityLabel);
        panel.add(cityField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(countryLabel);
        panel.add(countryField);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(signupButton);
        buttonPanel.add(Box.createHorizontalStrut(100));
        buttonPanel.add(backButton);


        panel.add(Box.createVerticalStrut(20));

        panel.add(buttonPanel);



        frame.add(panel);
        frame.setSize(400, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton getjButton() {
        JButton backButton = new RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(_ -> {
            frame.dispose();
            new LoginPage(userhashTable);
        });

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(200, 50, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(150, 50, 50));
            }
        });
        return backButton;
    }

    private void configureTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(240, 30)); // Increased size for better visibility
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setOpaque(true);
        field.setBorder(new RoundedBorder(15)); // Rounded corners
    }

    private void configureComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setMaximumSize(new Dimension(240, 30)); // Set the same size as text fields
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        comboBox.setOpaque(true);
        comboBox.setBorder(new RoundedBorder(15)); // Rounded corners
    }

    private void configureDateField(JDateChooser field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(240, 30));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setMaxSelectableDate(new Date());
        field.setOpaque(true);
        field.setBorder(new RoundedBorder(15));
    }

    // Custom Rounded Button Class
    public static class RoundedButton extends JButton {
        private final Color backgroundColor;

        public RoundedButton(String text, Color backgroundColor, Color textColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(textColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed()) {
                g2.setColor(backgroundColor.brighter());
            } else if (getModel().isRollover()) {
                g2.setColor(backgroundColor.brighter());
            } else {
                g2.setColor(backgroundColor);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g2);
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
            return new Insets(5, 5, 5, 5);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.GRAY);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        UserHashTable hashTable=new UserHashTable(100);

        new SignupPage(hashTable);
    }
}
