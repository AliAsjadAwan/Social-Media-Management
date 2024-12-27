package GUI;

import Backend.*;

import javax.swing.*;
import java.awt.*;

public class ViewProfilePanel extends JPanel {
    private final JFrame parentFrame;

    public ViewProfilePanel(User currentUser, UserHashTable hashTable) {
        this.parentFrame = new JFrame("View Profile");

        // Set the size of the frame inside the constructor
        parentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        parentFrame.setSize(400, 500); // Increased height to fit the new password label
        parentFrame.setLocationRelativeTo(null);


        // Panel properties
        setLayout(null);
        setBackground(new Color(50, 50, 50));

        // Title Label
        JLabel profileLabel = new JLabel("Your Profile");
        profileLabel.setFont(new Font("Arial", Font.BOLD, 20));
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setBounds(130, 20, 200, 30);
        add(profileLabel);

        // Username Label
        JLabel usernameLabel = createLabel("Username: " + currentUser.getUsername(), 70);
        add(usernameLabel);

        // Email Label
        JLabel emailLabel = createLabel("Email: " + currentUser.getEmail(), 120);
        add(emailLabel);

        // Bio Label
        JLabel bioLabel = createLabel("Bio: " + currentUser.getBio(), 170);
        add(bioLabel);

        // Date of Birth Label
        JLabel dobLabel = createLabel("Date of Birth: " + currentUser.getDateOfBirth(), 220);
        add(dobLabel);

        // Gender Label
        JLabel genderLabel = createLabel("Gender: " + currentUser.getGender(), 270);
        add(genderLabel);

        // Location Label
        JLabel locationLabel = createLabel("Location: " + currentUser.getLocation(), 320);
        add(locationLabel);

        // Password Label (Masked)
        String maskedPassword = maskPassword(currentUser.getPassword());
        JLabel passwordLabel = createLabel("Password: " + maskedPassword, 370);
        add(passwordLabel);

        // Back Button
        JButton backButton = new RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.setBounds(50, 420, 120, 30);
        backButton.addActionListener(_ -> parentFrame.dispose()); // Close the frame
        add(backButton);

        // Add this panel to the frame
        parentFrame.add(this);
        parentFrame.setVisible(true);
    }

    // Method to create labels
    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBounds(50, y, 300, 30);
        return label;
    }

    // Method to mask password, showing first and last character and stars in between
    private String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return password;
        }
        return password.charAt(0) + "*".repeat(password.length() - 2) + password.charAt(password.length() - 1);
    }

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
            setForeground(textColor);
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

    public static void main(String[] args) {
        // Create a sample user
        User user = new User(
                "Dave",
                "password",
                "dave@example.com",
                java.time.LocalDate.of(1995, 7, 25),
                "Male",
                new Location("Mexico", "USA")
        );

        // Create a sample UserHashTable with an initial size
        UserHashTable userHashTable = new UserHashTable(10);

        // Define the ActionListener for the "Back" button

        // Create the ViewProfilePanel instance
        new ViewProfilePanel(user, userHashTable);
    }

}
