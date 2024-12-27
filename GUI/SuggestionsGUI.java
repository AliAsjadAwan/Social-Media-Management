package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuggestionsGUI {
    private final JFrame frame;
    private final User currentUser;
    private UserHashTable userHashTable;

    public SuggestionsGUI(User currentUser, UserHashTable userHashTable) {
        this.currentUser = currentUser;
        this.userHashTable = userHashTable;

        frame = new JFrame("Suggestions");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Panel for suggestion type buttons
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.setPreferredSize(new Dimension(600, 50));

        // "From Friends" button
        JButton friendsButton = new LoginPage.RoundedButton("From Friends", new Color(100, 149, 237), Color.WHITE);
        friendsButton.setFocusable(false);
        friendsButton.setBounds(20, 10, 150, 30);

        // "From Location" button
        JButton locationButton = new LoginPage.RoundedButton("From Location", new Color(100, 149, 237), Color.WHITE);
        locationButton.setFocusable(false);
        locationButton.setBounds(430, 10, 150, 30);

        // Add buttons to the panel
        buttonPanel.add(friendsButton);
        buttonPanel.add(locationButton);

        // Container to dynamically load suggestions
        JPanel suggestionsContainer = new JPanel();
        suggestionsContainer.setBackground(new Color(50, 50, 50));
        suggestionsContainer.setLayout(new BoxLayout(suggestionsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(suggestionsContainer);

        // Action for "From Friends" button
        friendsButton.addActionListener(_ -> {
            suggestionsContainer.removeAll();
            Set<String> displayedUsers = new HashSet<>();
            List<User> suggestions = currentUser.getSuggestedConnections();

            if (suggestions.isEmpty()) {
                JLabel noSuggestionsLabel = new JLabel("No friend suggestions available.");
                noSuggestionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                noSuggestionsLabel.setForeground(Color.LIGHT_GRAY);
                suggestionsContainer.add(noSuggestionsLabel);
            } else {
                for (User user : suggestions) {
                    if (!displayedUsers.contains(user.getUsername())) {
                        displayedUsers.add(user.getUsername());
                        JPanel suggestionPanel = createSuggestionPanel(user, "Mutual Friend");
                        suggestionsContainer.add(suggestionPanel);
                        suggestionsContainer.add(Box.createVerticalStrut(10));
                    }
                }
            }
            suggestionsContainer.revalidate();
            suggestionsContainer.repaint();
        });

        // Action for "From Location" button
        locationButton.addActionListener(_ -> {
            suggestionsContainer.removeAll();
            Set<String> displayedUsers = new HashSet<>();
            List<String> locationSuggestions = currentUser.suggestUsersByLocation(userHashTable);

            if (locationSuggestions.isEmpty()) {
                JLabel noSuggestionsLabel = new JLabel("No location-based suggestions available.");
                noSuggestionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                noSuggestionsLabel.setForeground(Color.LIGHT_GRAY);
                suggestionsContainer.add(noSuggestionsLabel);
            } else {
                for (String suggestion : locationSuggestions) {
                    String[] parts = suggestion.split(" \\(Suggested based on ");
                    String username = parts[0];
                    String basis = parts[1].replace(")", "");

                    if (!displayedUsers.contains(username)) {
                        User suggestedUser = userHashTable.find(username);
                        if (suggestedUser != null) {
                            displayedUsers.add(username);
                            JPanel suggestionPanel = createSuggestionPanel(suggestedUser, basis);
                            suggestionsContainer.add(suggestionPanel);
                            suggestionsContainer.add(Box.createVerticalStrut(10));
                        }
                    }
                }
            }
            suggestionsContainer.revalidate();
            suggestionsContainer.repaint();
        });

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createSuggestionPanel(User user, String basis) {
        JPanel suggestionPanel = new JPanel();
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        suggestionPanel.setBackground(new Color(70, 70, 70));
        suggestionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        suggestionPanel.setMaximumSize(new Dimension(550, 110));
        suggestionPanel.setPreferredSize(new Dimension(550, 110));
        suggestionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username label
        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);

        // Basis label
        JLabel basisLabel = new JLabel("Suggested based on: " + basis);
        basisLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        basisLabel.setForeground(Color.LIGHT_GRAY);

        // Mutual friends count
        List<User> mutualConnections = currentUser.getMutualConnections(user);
        JLabel mutualFriendsLabel = new JLabel("Mutual Friends: " + mutualConnections.size());
        mutualFriendsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mutualFriendsLabel.setForeground(Color.LIGHT_GRAY);

        // Add Friend button
        JButton addFriendButton = new JButton(currentUser.hasSentConnectionRequest(user) ? "Request Sent" : "Add Friend");
        addFriendButton.setBackground(new Color(100, 149, 237));
        addFriendButton.setForeground(Color.WHITE);
        addFriendButton.setPreferredSize(new Dimension(140, 30));
        addFriendButton.setFocusable(false);

        // Toggle button text and send/remove friend request
        addFriendButton.addActionListener(_ -> {
            if (addFriendButton.getText().equals("Add Friend")) {
                currentUser.sendConnectionRequest(user);
                addFriendButton.setText("Request Sent");
                JOptionPane.showMessageDialog(
                        frame,
                        "Friend request sent to " + user.getUsername() + ".",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                currentUser.cancelConnectionRequest(user);
                addFriendButton.setText("Add Friend");
                JOptionPane.showMessageDialog(
                        frame,
                        "Friend request to " + user.getUsername() + " has been canceled.",
                        "Request Canceled",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        suggestionPanel.add(usernameLabel);
        suggestionPanel.add(mutualFriendsLabel);
        suggestionPanel.add(basisLabel);
        suggestionPanel.add(Box.createVerticalStrut(10));
        suggestionPanel.add(addFriendButton);

        return suggestionPanel;
    }

    public static void main(String[] args) {
        // Mock data
        Location mockLocation = new Location("Lahore", "Pakistan");
        User currentUser = new User(
                "JohnDoe",
                "password123",
                "john@example.com",
                LocalDate.of(1995, 5, 15),
                "Male",
                mockLocation
        );
        User user1=new User("Ali","Pass","ali",java.time.LocalDate.of(1990,11,23),"Male",mockLocation);
        User user2=new User("Kamran","pass","ali",java.time.LocalDate.of(1990,11,23),"Male",mockLocation);
        UserHashTable userHashTable = new UserHashTable(10);
        // Add mock users to userHashTable and connections here
        userHashTable.insert(user1);
        userHashTable.insert(user2);
        user1.addConnection(user2);
        currentUser.addConnection(user1);
        // Launch GUI
        new SuggestionsGUI(currentUser, userHashTable);
    }
}
