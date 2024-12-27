package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MutualConnectionsGUI extends JFrame {


    public MutualConnectionsGUI(User currentUser, User selectedUser) {
        // This will hold the selected user whose mutual connections are to be shown


        setTitle("Mutual Connections with " + selectedUser.getUsername());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null); // Center the window

        // Panel to hold the mutual connections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(70, 70, 70));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get the mutual connections of the selected user
        List<User> mutualConnections = currentUser.getMutualConnections(selectedUser);
        if (mutualConnections.isEmpty()) {
            JLabel noMutualLabel = new JLabel("No mutual connections.");
            noMutualLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            noMutualLabel.setForeground(Color.LIGHT_GRAY);
            mainPanel.add(noMutualLabel);
        } else {
            // Create and add panels for each mutual connection
            for (User mutualConnection : mutualConnections) {
                JPanel connectionPanel = createConnectionPanel(mutualConnection);
                mainPanel.add(connectionPanel);
                mainPanel.add(Box.createVerticalStrut(10)); // Small gap between connection panels
            }
        }

        // Add a back button to go back to the previous GUI
        JButton backButton = new LoginPage.RoundedButton("Back to Connections",new Color(150, 50, 50),Color.WHITE);
        backButton.setFocusable(false);
        backButton.addActionListener(_ -> {
            dispose(); // Close the current window
            new ConnectionsGUI(currentUser); // Open the ConnectionsGUI again
        });

        mainPanel.add(backButton); // Add the back button at the bottom

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createConnectionPanel(User mutualConnection) {
        JPanel connectionPanel = new JPanel();
        connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));
        connectionPanel.setBackground(new Color(70, 70, 70));
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Username label
        JLabel usernameLabel = new JLabel(mutualConnection.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);

        // Add components to the panel
        connectionPanel.add(usernameLabel);

        return connectionPanel;
    }
    public static void main(String[] args) {
        // Simulate some initial data for users
        User currentUser = new User("Dave", "password", "dave@example.com", java.time.LocalDate.of(1995, 7, 25),"Male",new Location("Mexico","USA"));
        User user1 = new User("Alice", "password1", "alice@example.com", java.time.LocalDate.of(1990, 5, 15),"Male",new Location("Mexico","USA"));
        User user2 = new User("Bob", "password2", "bob@example.com", java.time.LocalDate.of(1985, 8, 20),"Male",new Location("Mexico","USA"));
        User user3 = new User("Charlie", "password3", "charlie@example.com", java.time.LocalDate.of(2000, 1, 10),"Male",new Location("Mexico","USA"));

        // Add mutual connections
        currentUser.addConnection(user3);
        currentUser.addConnection(user1);
        user1.addConnection(user3);
        user2.addConnection(user1);

        // Open the MutualConnectionsGUI directly with the current user
        SwingUtilities.invokeLater(() -> new MutualConnectionsGUI(currentUser, user3));
    }

}

