package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ConnectionsGUI extends JPanel {

    private final JFrame frame;

    public ConnectionsGUI(User currentUser) {

        frame = new JFrame("Your Connections");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Your Connections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main content panel (Scrollable area for connections)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(50, 50, 50));

        // Check if user has connections
        List<User> connections = currentUser.getConnections();
        if (connections.isEmpty()) {
            JLabel noConnectionsLabel = new JLabel("You have no connections.");
            noConnectionsLabel.setForeground(Color.WHITE);
            noConnectionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noConnectionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(noConnectionsLabel);
        } else {
            for (User connection : connections) {
                mainPanel.add(createConnectionPanel(connection, currentUser));
                mainPanel.add(Box.createVerticalStrut(20));  // Add space between user panels
            }
        }

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(50, 50, 50));
        frame.add(scrollPane, BorderLayout.CENTER);

        // Close Button at the bottom
        JButton closeButton = new LoginPage.RoundedButton("Close", new Color(150, 50, 50), Color.WHITE);
        closeButton.addActionListener(_ -> frame.dispose());

        // Add a close button to the frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.add(closeButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Make the frame visible
        frame.setVisible(true);
    }

    private JPanel createConnectionPanel(User connection, User currentUser) {
        // Panel to hold the connection details
        JPanel connectionPanel = new JPanel();
        connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));
        connectionPanel.setBackground(new Color(70, 70, 70));
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        connectionPanel.setMaximumSize(new Dimension(450, 110)); // Fixed width for all posts
        connectionPanel.setPreferredSize(new Dimension(450, 110));  // Set a preferred height to prevent the panel from growing too large
        connectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel for text details (Username and mutual friends)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(null);  // Using null layout for manual positioning
        detailsPanel.setBackground(new Color(70, 70, 70));
        detailsPanel.setPreferredSize(new Dimension(450, 100));  // Increased the height to 100px to fit both labels

        // Username label (Left aligned)
        JLabel usernameLabel = new JLabel(connection.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(10, 10, 200, 15);  // Manually set the position and size

        // Mutual connections label (Below username, Left aligned)
        List<User> mutualConnections = currentUser.getMutualConnections(connection);
        JLabel mutualConnectionsLabel = new JLabel("Mutual Connections: " + mutualConnections.size());
        mutualConnectionsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mutualConnectionsLabel.setForeground(Color.LIGHT_GRAY);
        mutualConnectionsLabel.setBounds(10, 35, 200, 15);  // Position below the username label

        // Add username and mutual connections to the details panel
        detailsPanel.add(usernameLabel);
        detailsPanel.add(mutualConnectionsLabel);  // This will now appear below the username label

        // Buttons Panel for alignment using GridBagLayout
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(new Color(70, 70, 70));

        // Constraints for buttons to ensure fixed size and left-right alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 50, 0, 90); // Spacing between buttons

        // Show Mutual button (Left aligned)
        JButton showMutual = new JButton("Show Mutual");
        showMutual.setBackground(new Color(100, 149, 237)); // Light blue color
        showMutual.setForeground(Color.WHITE);
        showMutual.setFocusable(false);
        showMutual.setPreferredSize(new Dimension(140, 30));
        showMutual.setMinimumSize(new Dimension(140, 30));
        showMutual.setMaximumSize(new Dimension(140, 30));
        // When Show Mutual is clicked
        showMutual.addActionListener(_ -> SwingUtilities.invokeLater(() -> {
            frame.dispose();
            new MutualConnectionsGUI(currentUser, connection);  // Pass the selected connection to the MutualConnectionsGUI
        }));


        gbc.gridx = 0;  // First column for the "Show Mutual" button
        gbc.anchor = GridBagConstraints.WEST; // Align left
        buttonsPanel.add(showMutual, gbc);

        // Remove Friend button (Right aligned)
        JButton removeButton = new JButton("Remove Friend");
        removeButton.setBackground(new Color(255, 69, 58));  // Red color for removal
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusable(false);
        removeButton.setPreferredSize(new Dimension(140, 30));
        removeButton.setMinimumSize(new Dimension(140, 30));
        removeButton.setMaximumSize(new Dimension(140, 30));
        removeButton.addActionListener(_ -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to remove " + connection.getUsername() + "?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                currentUser.removeConnection(connection); // Proper removal
                frame.dispose(); // Close the current frame
                new ConnectionsGUI(currentUser); // Recreate GUI with updated list
            }
        });

        gbc.gridx = 1;  // Second column for the "Remove Friend" button
        gbc.anchor = GridBagConstraints.EAST; // Align right
        buttonsPanel.add(removeButton, gbc);

        // Add components to the connection panel
        connectionPanel.add(detailsPanel); // Add text details
        connectionPanel.add(Box.createVerticalStrut(7)); // Small gap
        connectionPanel.add(buttonsPanel); // Add buttons panel

        return connectionPanel;
    }


    public static void main(String[] args) {
        // Simulated current user and connections for testing
        User currentUser = new User("Dave", "password", "dave@example.com", java.time.LocalDate.of(1995, 7, 25),"Male",new Location("Mexcico","USA"));
        User user1 = new User("Alice", "password1", "alice@example.com", java.time.LocalDate.of(1990, 5, 15),"Male",new Location("Mexcico","USA"));
        User user2 = new User("Bob", "password2", "bob@example.com", java.time.LocalDate.of(1985, 8, 20),"Male",new Location("Mexcico","USA"));
        User user3 = new User("Charlie", "password3", "charlie@example.com", java.time.LocalDate.of(2000, 1, 10),"Male",new Location("Mexcico","USA"));

        // Adding some dummy connections for the test
        currentUser.addConnection(user1);
        currentUser.addConnection(user2);
        currentUser.addConnection(user3);
        currentUser.addConnection(new User("Hassaan","pass","hassaan@example.com", java.time.LocalDate.of(1990, 5, 15),"Male",new Location("Mexcico","USA")));
        currentUser.addConnection(new User("Mubin","pass","mubin@example.com",java.time.LocalDate.of(1990, 5, 15),"Male",new Location("Mexcico","USA")));
        currentUser.addConnection(new User("Umar","pass","mubin@example.com",java.time.LocalDate.of(1990, 5, 15),"Male",new Location("Mexcico","USA")));
        user1.addConnection(user2);

        // Creating and displaying the GUI
        SwingUtilities.invokeLater(() -> new ConnectionsGUI(currentUser));
    }
}
