package GUI;

import javax.swing.*;
import java.awt.*;

import Backend.*;

public class AddPostGUI extends JPanel {

    private final JTextField titleField;
    private final JTextArea contentArea;
    private final JLabel messageLabel;
    private final User currentUser;

    public AddPostGUI(User currentUser) {
        this.currentUser = currentUser;
        setLayout(null);
        setBackground(new Color(50, 50, 50));
        setBounds(600, 100, 400, 400);

        // Title Label
        JLabel titleLabel = new JLabel("Add a New Post");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(120, 20, 200, 30);
        add(titleLabel);

        // Post Title Field
        JLabel titleFieldLabel = createLabel("Post Title:", 90);
        add(titleFieldLabel);
        titleField = new JTextField();
        titleField.setBounds(150, 90, 200, 25);
        add(titleField);

        // Post Content Field
        JLabel contentLabel = createLabel("Content:", 150);
        add(contentLabel);
        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBounds(150, 150, 200, 100);
        add(contentScrollPane);

        // Message Label for feedback
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(90, 280, 300, 25);
        add(messageLabel);

        // Add Button
        JButton addButton = new LoginPage.RoundedButton("Add Post", new Color(30, 150, 50), Color.WHITE);
        addButton.setBounds(50, 320, 120, 30);
        addButton.addActionListener(_ -> addPost());
        add(addButton);

        // Back Button
        JButton backButton = new LoginPage.RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.setBounds(230, 320, 120, 30);
        backButton.addActionListener(_ -> {
            // Close the current frame (which contains the AddPostGUI panel)
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
        });
        add(backButton);

        // Set up the JFrame
        JFrame frame = new JFrame("Add Post");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.add(this); // Add the current AddPostGUI panel to the frame
        frame.setVisible(true); // Make the frame visible
    }

    // Add Post Logic
    private void addPost() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            messageLabel.setText("Both title and content are required.");
            return;
        }

        // Create a new Post object
        Post newPost = new Post(currentUser, title, content);

        // Call the addPost method of the User class
        currentUser.addPost(newPost);

        messageLabel.setForeground(new Color(30, 200, 50));
        messageLabel.setText("Post added successfully!");

        // Clear fields after successful addition
        titleField.setText("");
        contentArea.setText("");
    }

    // Helper to create labels
    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setBounds(50, y, 100, 25);
        return label;
    }

    public static void main(String[] args) {
        // Create a simple user object
        User user = new User("Dave", "password", "dave@example.com", java.time.LocalDate.of(1995, 7, 25),"Male", new Location("Mexico", "USA"));

        // Create the AddPostGUI and pass the user
        new AddPostGUI(user); // This will internally create and show the JFrame
    }
}
