package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class SendConnectionGUI {
    private final JFrame frame;
    private final User currentUser;

    public SendConnectionGUI(User currentUser, UserHashTable userHashTable) {
        this.currentUser = currentUser;

        frame = new JFrame("Search Users");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(50, 50, 50));
        searchPanel.setPreferredSize(new Dimension(500, 50));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(350, 30));
        configureTextField(searchField);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Arial",Font.BOLD,14));
        searchLabel.setForeground(Color.WHITE);


        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        frame.add(searchPanel, BorderLayout.NORTH);

        // Container for displaying search results
        JPanel resultsContainer = new JPanel();
        resultsContainer.setBackground(new Color(50, 50, 50));
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsContainer);

        frame.add(scrollPane, BorderLayout.CENTER);


        // Add key listener to search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().trim().toLowerCase();
                resultsContainer.removeAll();

                if (query.isEmpty()) {
                    resultsContainer.revalidate();
                    resultsContainer.repaint();
                    return;
                }

                List<User> matchedUsers = userHashTable.getUsersByUsernamePrefix(query,currentUser);
                matchedUsers.addAll(userHashTable.getUsersByLocationPrefix(query,currentUser));

                if (matchedUsers.isEmpty()) {
                    JLabel noResultsLabel = new JLabel("No users found for '" + query + "'.");
                    noResultsLabel.setForeground(Color.LIGHT_GRAY);
                    noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                    resultsContainer.add(noResultsLabel);
                } else {
                    for (User user : matchedUsers) {
                        JPanel userPanel = createUserPanel(user);
                        resultsContainer.add(userPanel);
                        resultsContainer.add(Box.createVerticalStrut(10));
                    }
                }

                resultsContainer.revalidate();
                resultsContainer.repaint();
            }
        });

        frame.setVisible(true);
    }

    private JPanel createUserPanel(User user) {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(70, 70, 70));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userPanel.setMaximumSize(new Dimension(550, 110));
        userPanel.setPreferredSize(new Dimension(550, 110));
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username label
        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);

        // Location label
        String locationText = (user.getLocation() != null)
                ? user.getLocation().getCity() + ", " + user.getLocation().getCountry()
                : "Location not available";
        JLabel locationLabel = new JLabel(locationText);
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        locationLabel.setForeground(Color.LIGHT_GRAY);

        // Add Friend button
        JButton addFriendButton = new JButton("Add Friend");
        addFriendButton.setBackground(new Color(100, 149, 237));
        addFriendButton.setForeground(Color.WHITE);
        addFriendButton.setPreferredSize(new Dimension(140, 30));
        addFriendButton.setFocusable(false);

        // Check the connection status for the current user
        if (currentUser.getConnections().contains(user)) {
            addFriendButton.setText("Friends");
            addFriendButton.setEnabled(false);
        } else if (currentUser.getRequestsSent().contains(user)) {
            addFriendButton.setText("Request Sent");
            addFriendButton.setEnabled(false);
        }

        // Action listener for adding/removing friend requests
        addFriendButton.addActionListener(_ -> {
            if (addFriendButton.getText().equals("Add Friend")) {
                currentUser.sendConnectionRequest(user);
                addFriendButton.setText("Request Sent");

                // Show a dialog confirming the request
                JOptionPane.showMessageDialog(
                        frame,
                        "Friend request sent to " + user.getUsername() + ".",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                currentUser.cancelConnectionRequest(user);
                addFriendButton.setText("Add Friend");

                // Show a dialog confirming the cancellation
                JOptionPane.showMessageDialog(
                        frame,
                        "Friend request to " + user.getUsername() + " has been canceled.",
                        "Request Canceled",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        userPanel.add(usernameLabel);
        userPanel.add(locationLabel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(addFriendButton);

        return userPanel;
    }

    public static void configureTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(350, 30));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setOpaque(true);
        field.setBorder(new LoginPage.RoundedBorder(30));
    }



    public static void main(String[] args) {
        // Mock data
        Location location1 = new Location("Lahore", "Pakistan");
        Location location2 = new Location("Karachi", "Pakistan");

        User user1 = new User("Ali", "pass123", "ali@example.com", null, "Male", location1);
        User user2 = new User("Ahmed", "pass123", "ahmed@example.com", null, "Male", location2);
        User user3 = new User("Ayesha", "pass123", "ayesha@example.com", null, "Female", location1);
        User user4=new User("AllahYar","pass123", "allahyar@example.com", null, "Male", location2);
        User user5=new User("Kamran","pass123","kamran@example.com",java.time.LocalDate.of(2004,7,12),"Male",new Location("Lahore","Pakistan"));
        UserHashTable userHashTable = new UserHashTable(100);
        userHashTable.insert(user1);
        userHashTable.insert(user2);
        userHashTable.insert(user3);
        userHashTable.insert(user4);
        userHashTable.insert(user5);

        User currentUser = new User("JohnDoe", "pass123", "john@example.com", null, "Male", null);

        new SendConnectionGUI(currentUser, userHashTable);
    }
}
