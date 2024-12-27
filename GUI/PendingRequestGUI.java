package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PendingRequestGUI extends JFrame {
    private final User currentUser; // The current logged-in user
    private final List<User> pendingRequests; // List of users with pending requests

    public PendingRequestGUI(User currentUser) {
        this.currentUser = currentUser;
        this.pendingRequests = currentUser.getPendingRequests();

        setTitle("Pending Connection Requests");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(530, 400); // Adjust size based on content
        setLocationRelativeTo(null); // Center the window

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Stack requests vertically
        mainPanel.setBackground(new Color(70, 70, 70));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Check if there are no pending requests
        if (pendingRequests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No Pending Connection Requests", JLabel.CENTER);
            noRequestsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noRequestsLabel.setForeground(Color.WHITE);
            noRequestsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noRequestsLabel);
        } else {
            // Display each pending request
            for (User user : pendingRequests) {
                JPanel requestPanel = createRequestPanel(user);
                mainPanel.add(requestPanel);
                mainPanel.add(Box.createVerticalStrut(10)); // Small gap between requests
            }
        }

        // Wrap the main panel in a JScrollPane to make it scrollable
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add the scrollable main panel to the frame
        add(scrollPane);
        setVisible(true);
    }

    private JPanel createRequestPanel(User user) {
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
        requestPanel.setBackground(new Color(70, 70, 70));
        requestPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        requestPanel.setMaximumSize(new Dimension(450, 110));
        requestPanel.setPreferredSize(new Dimension(450, 110));
        requestPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel for text details (Username and mutual friends)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(null);  // Using null layout for manual positioning
        detailsPanel.setBackground(new Color(70, 70, 70));
        detailsPanel.setPreferredSize(new Dimension(450, 100));  // Match the height of the connection panel

        // Username label (Left aligned)
        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(10, 10, 200, 15);

        // Mutual connections label (Below username, Left aligned)
        List<User> mutualConnections = currentUser.getMutualConnections(user);
        JLabel mutualConnectionsLabel = new JLabel("Mutual Connections: " + mutualConnections.size());
        mutualConnectionsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mutualConnectionsLabel.setForeground(Color.LIGHT_GRAY);
        mutualConnectionsLabel.setBounds(10, 35, 200, 15);

        // Add username and mutual connections to the details panel
        detailsPanel.add(usernameLabel);
        detailsPanel.add(mutualConnectionsLabel);

        // Buttons Panel for alignment using GridBagLayout
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(new Color(70, 70, 70));

        // Constraints for buttons to ensure fixed size and left-right alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 50, 0, 90); // Adjusted spacing between buttons

        // Accept button (Left aligned)
        JButton acceptButton = getjButton(user);

        gbc.gridx = 0;  // First column for the "Accept" button
        gbc.anchor = GridBagConstraints.WEST; // Align left
        buttonsPanel.add(acceptButton, gbc);

        // Reject button (Right aligned)
        JButton rejectButton = getButton(user);

        gbc.gridx = 1;  // Second column for the "Reject" button
        gbc.anchor = GridBagConstraints.EAST; // Align right
        buttonsPanel.add(rejectButton, gbc);

        // Add all components to the request panel
        requestPanel.add(detailsPanel); // Add text details
        requestPanel.add(Box.createVerticalStrut(7)); // Small gap
        requestPanel.add(buttonsPanel); // Add buttons panel

        // Add a border to distinguish each request
        requestPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        return requestPanel;
    }

    private JButton getButton(User user) {
        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(255, 69, 58));  // Red color for rejection
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusable(false);
        rejectButton.setPreferredSize(new Dimension(140, 30));
        rejectButton.setMaximumSize(new Dimension(140, 30));
        rejectButton.setMinimumSize(new Dimension(140, 30));
        rejectButton.addActionListener(_ -> {
            rejectConnectionRequest(user);
            showMessageDialog("You have rejected the connection request from " + user.getUsername(), "Request Rejected");
        });
        return rejectButton;
    }

    private JButton getjButton(User user) {
        JButton acceptButton = new JButton("Accept");
        acceptButton.setBackground(new Color(100, 149, 237)); // Light blue color
        acceptButton.setForeground(Color.WHITE);
        acceptButton.setFocusable(false);
        acceptButton.setPreferredSize(new Dimension(140, 30));
        acceptButton.setMaximumSize(new Dimension(140, 30));
        acceptButton.setMinimumSize(new Dimension(140, 30));
        acceptButton.addActionListener(_ -> {
            acceptConnectionRequest(user);
            showMessageDialog("You are now connected with " + user.getUsername(), "Request Accepted");
        });
        return acceptButton;
    }

    private void acceptConnectionRequest(User sender) {
        if (pendingRequests.contains(sender)) {
            currentUser.addConnection(sender); // Add sender to the current user's connections
            pendingRequests.remove(sender); // Remove sender from pending requests
            System.out.println("You are now connected with " + sender.getUsername() + ".");
            // Refresh the UI with updated data
            dispose();
            new PendingRequestGUI(currentUser); // Recreate the frame with updated data
        } else {
            System.out.println("No connection request from " + sender.getUsername() + " to accept.");
        }
    }

    private void rejectConnectionRequest(User sender) {
        if (pendingRequests.contains(sender)) {
            pendingRequests.remove(sender); // Remove sender from pending requests
            currentUser.recordActivity("Rejected connection request from " + sender.getUsername());
            System.out.println("You have rejected the connection request from " + sender.getUsername() + ".");
            // Refresh the UI with updated data
            dispose();
            new PendingRequestGUI(currentUser); // Recreate the frame with updated data
        } else {
            System.out.println("No connection request from " + sender.getUsername() + " to reject.");
        }
    }

    private void showMessageDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Simulate some initial data for users
        User currentUser = new User("Dave", "password", "dave@example.com", java.time.LocalDate.of(1995, 7, 25),"Male",new Location("Mexico","USA"));
        User user1 = new User("Alice", "password1", "alice@example.com", java.time.LocalDate.of(1990, 5, 15),"Male",new Location("Mexico","USA"));
        User user2 = new User("Bob", "password2", "bob@example.com", java.time.LocalDate.of(1985, 8, 20),"Male",new Location("Mexico","USA"));
        User user3 = new User("Hassan", "pass", "ali", java.time.LocalDate.of(1995, 7, 25),"Male",new Location("Mexico","USA"));

        // Add pending requests
        currentUser.addConnection(user1);

        currentUser.addPendingRequest(user1);
        currentUser.addPendingRequest(user2);
        currentUser.addPendingRequest(user3);
        currentUser.addPendingRequest(new User("Mubeen", "Pass", "ali", java.time.LocalDate.of(1995, 7, 25),"Male",new Location("Mexico","USA")));

        // Create the PendingRequestsGUI with the current user and pending requests
        SwingUtilities.invokeLater(() -> new PendingRequestGUI(currentUser));
    }
}
