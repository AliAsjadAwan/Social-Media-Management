package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RequestSentGUI extends JFrame {
    private final User currentUser;
    private final List<User> requestsSent;

    public RequestSentGUI(User currentUser) {
        this.currentUser = currentUser;
        this.requestsSent = currentUser.getRequestsSent();

        setTitle("Requests Sent");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(530, 400); // Adjust size based on content
        setLocationRelativeTo(null); // Center the window

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Stack requests vertically
        mainPanel.setBackground(new Color(70, 70, 70));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Check if there are no requests sent
        if (requestsSent.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No Request Sent", JLabel.CENTER);
            noRequestsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noRequestsLabel.setForeground(Color.WHITE);
            noRequestsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noRequestsLabel);
        } else {
            // Display each request
            for (User user : requestsSent) {
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

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(null);
        detailsPanel.setBackground(new Color(70, 70, 70));
        detailsPanel.setPreferredSize(new Dimension(450, 100));

        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(10, 10, 200, 15);

        List<User> mutualConnections = currentUser.getMutualConnections(user);
        JLabel mutualConnectionsLabel = new JLabel("Mutual Connections: " + mutualConnections.size());
        mutualConnectionsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mutualConnectionsLabel.setForeground(Color.LIGHT_GRAY);
        mutualConnectionsLabel.setBounds(10, 35, 200, 15);

        detailsPanel.add(usernameLabel);
        detailsPanel.add(mutualConnectionsLabel);

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(new Color(70, 70, 70));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 50, 0, 90);

        // Button to cancel request
        JButton cancelButton = getCancelButton(user);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        buttonsPanel.add(cancelButton, gbc);

        requestPanel.add(detailsPanel);
        requestPanel.add(Box.createVerticalStrut(7));
        requestPanel.add(buttonsPanel);

        requestPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        return requestPanel;
    }

    private JButton getCancelButton(User user) {
        JButton cancelButton = new JButton("Cancel Request");
        cancelButton.setBackground(new Color(255, 69, 58));  // Red color for cancellation
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusable(false);
        cancelButton.setPreferredSize(new Dimension(140, 30));
        cancelButton.setMaximumSize(new Dimension(140, 30));
        cancelButton.setMinimumSize(new Dimension(140, 30));

        cancelButton.addActionListener(e -> {
            cancelRequest(user);
            cancelButton.setText("Request Canceled");
        });
        return cancelButton;
    }

    private void cancelRequest(User user) {
        currentUser.cancelConnectionRequest(user);
        System.out.println("Request canceled for " + user.getUsername());
        // Refresh the UI with updated data
        dispose();
        new RequestSentGUI(currentUser); // Recreate the frame with updated data
    }

    public static void main(String[] args) {
        User currentUser = new User("Dave", "password", "dave@example.com", java.time.LocalDate.of(1995, 7, 25), "Male", new Location("Mexico", "USA"));
        User user1 = new User("Alice", "password1", "alice@example.com", java.time.LocalDate.of(1990, 5, 15), "Female", new Location("Mexico", "USA"));
        currentUser.sendConnectionRequest(user1);
        currentUser.sendConnectionRequest(new User("Bob", "password2", "bob@example.com", java.time.LocalDate.of(1985, 8, 20), "Male", new Location("Mexico", "USA")));

        SwingUtilities.invokeLater(() -> new RequestSentGUI(currentUser));
    }
}
