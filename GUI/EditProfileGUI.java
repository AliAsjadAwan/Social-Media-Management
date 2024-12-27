package GUI;

import javax.swing.*;
import java.awt.*;

import Backend.Location;
import Backend.User;
import Backend.UserHashTable;

public class EditProfileGUI extends JPanel {

    private final JTextField usernameField, emailField, bioField, cityField, countryField;
    private final JPasswordField passwordField, confirmPasswordField;
    private final JLabel messageLabel;
    private final JLabel currentUsernameLabel, currentPassword,currentEmailLabel, currentBioLabel, currentCityLabel, currentCountryLabel;
    private final User currentUser;
    private final UserHashTable userHashTable;

    public EditProfileGUI(User currentUser,UserHashTable hashTable) {
        this.currentUser = currentUser;
        this.userHashTable=hashTable;

        // Frame setup
        JFrame frame = new JFrame("Edit Profile");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null); // Center the frame

        // Panel setup
        setLayout(null);
        setBackground(new Color(50, 50, 50));
        setBounds(550, 100, 500, 600); // Panel size

        // Title Label
        JLabel titleLabel = new JLabel("Edit Your Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(160, 20, 200, 30);
        add(titleLabel);

        // Username Field
        JLabel usernameLabel = createLabel("Username:", 70);
        add(usernameLabel);
        usernameField = createTextField(73);
        add(usernameField);
        currentUsernameLabel = createCurrentLabel("Current: " + currentUser.getUsername(), 90);
        add(currentUsernameLabel);

        // Password Field
        JLabel passwordLabel = createLabel("Password:", 120);
        add(passwordLabel);
        passwordField = createPasswordField(123);
        add(passwordField);
        currentPassword=createCurrentLabel("Current: " + maskPassword(currentUser.getPassword()), 140);
        add(currentPassword);

        // Email Field
        JLabel emailLabel = createLabel("Email:", 170);
        add(emailLabel);
        emailField = createTextField(173);
        add(emailField);
        currentEmailLabel = createCurrentLabel("Current: " + currentUser.getEmail(), 190);
        add(currentEmailLabel);

        // Bio Field
        JLabel bioLabel = createLabel("Bio:", 220);
        add(bioLabel);
        bioField = createTextField(223);
        add(bioField);
        currentBioLabel = createCurrentLabel("Current: " + currentUser.getBio(), 240);
        add(currentBioLabel);

        // City Field
        JLabel cityLabel = createLabel("City:", 270);
        add(cityLabel);
        cityField = createTextField(273);
        add(cityField);
        currentCityLabel = createCurrentLabel("Current: " + currentUser.getLocation().getCity(), 290);
        add(currentCityLabel);

        // Country Field
        JLabel countryLabel = createLabel("Country:", 320);
        add(countryLabel);
        countryField = createTextField(323);
        add(countryField);
        currentCountryLabel = createCurrentLabel("Current: " + currentUser.getLocation().getCountry(), 340);
        add(currentCountryLabel);

        // Confirm Password Field
        JLabel confirmPasswordLabel = createLabel("Confirm Password:", 370);
        add(confirmPasswordLabel);
        confirmPasswordField = createPasswordField(373);
        add(confirmPasswordField);

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(130, 430, 250, 20);
        add(messageLabel);

        // Save Button
        JButton saveButton = new RoundedButton("Save", new Color(30, 150, 50), Color.WHITE);
        saveButton.setBounds(50, 480, 120, 30);
        saveButton.addActionListener(_ -> saveChanges());
        add(saveButton);

        // Back Button
        JButton backButton = new RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.setBounds(350, 480, 120, 30);
        backButton.addActionListener(_ -> {frame.dispose();});
        add(backButton);

        // Add panel to frame and display
        frame.add(this);
        frame.setVisible(true);
    }

    private String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return password;
        }
        return password.charAt(0) + "*".repeat(password.length() - 2) + password.charAt(password.length() - 1);
    }

    // Save changes logic
    private void saveChanges() {
        String enteredPassword = new String(confirmPasswordField.getPassword());

        // Password confirmation
        if (!currentUser.getPassword().equals(enteredPassword)) {
            messageLabel.setText("Incorrect password. Try again.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        // Update fields
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        String newEmail = emailField.getText();
        String newBio = bioField.getText();
        String newCity = cityField.getText();
        String newCountry = countryField.getText();


        if (userHashTable.isUsernameTaken(newUsername)) {
            messageLabel.setText("Username taken. Try again.");
            return;
        }if (userHashTable.isValidEmail(newEmail)) {
            messageLabel.setText("Invalid Email. Try again.");
            return;
        }if (userHashTable.isValidPassword(newPassword)) {
            messageLabel.setText("Invalid Password. Try again.");
            return;
        }

        // Call the editProfile method from the User class
        userHashTable.editProfile(currentUser.getUsername(),newUsername, newPassword, newEmail, newBio, newCity, newCountry, enteredPassword);

        // Update the current values labels
        currentUsernameLabel.setText("Current: " + currentUser.getUsername());
        currentEmailLabel.setText("Current: " + currentUser.getEmail());
        currentBioLabel.setText("Current: " + currentUser.getBio());
        currentCityLabel.setText("Current: " + currentUser.getLocation().getCity());
        currentCountryLabel.setText("Current: " + currentUser.getLocation().getCountry());

        messageLabel.setForeground(new Color(30, 200, 50));
        messageLabel.setText("Profile updated successfully!");
    }

    // Helper to create labels
    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBounds(50, y, 150, 30);
        return label;
    }

    // Helper to create current value labels (under text fields)
    private JLabel createCurrentLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.LIGHT_GRAY);
        label.setBounds(250, y, 200, 30);
        return label;
    }

    // Helper to create text fields
    private JTextField createTextField(int y) {
        JTextField textField = new JTextField();
        textField.setBounds(250, y, 150, 25);
        return textField;
    }

    // Helper to create password fields
    private JPasswordField createPasswordField(int y) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(250, y, 150, 25);
        return passwordField;
    }

    // RoundedButton class (reused)
    public static class RoundedButton extends JButton {
        private final Color backgroundColor;
        private final Color textColor;

        public RoundedButton(String text, Color backgroundColor, Color textColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(textColor); // Initialize text color
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
            g2.dispose();
        }
    }

    // Main method to simulate the GUI and backend integration
    public static void main(String[] args) {
        // Create a simple user object
        User user = new User("Dave", "user123", "dave@example.com", java.time.LocalDate.of(1995, 7, 25), "Male", new Location("Lahore", "Pakistan"));
        UserHashTable userHashTable1=new UserHashTable(10);
        // Create an ActionListener for the back button
        // Close the frame when the back button is clicked

        // Create and display the EditProfileGUI
        new EditProfileGUI(user,userHashTable1);
    }
}
