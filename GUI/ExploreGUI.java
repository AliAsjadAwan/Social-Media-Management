package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExploreGUI extends JPanel {

    UserHashTable userHashTable;

    public ExploreGUI(User currentUser, UserHashTable userHashTable) {
        this.userHashTable = userHashTable;

        setLayout(new BorderLayout());
        setBackground(new Color(50, 50, 50));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Posts From Non-Connections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Content Panel (Scrollable area for posts)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(50, 50, 50));

        // Gather and display posts
        List<Post> allPosts = getSortedPostsFromNonConnections(currentUser, userHashTable);

        if (allPosts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No posts available from your non-connections.");
            noPostsLabel.setForeground(Color.WHITE);
            noPostsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noPostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(noPostsLabel);
        } else {
            for (Post post : allPosts) {
                JPanel postPanel = createPostPanel(post, currentUser);
                contentPanel.add(Box.createVerticalStrut(10));
                contentPanel.add(postPanel);
            }
        }

        // Add content panel to a scrollable pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Back Button Panel
        JButton backButton = new LoginPage.RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.addActionListener(e -> {});
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setBackground(new Color(50, 50, 50));
        backPanel.add(backButton);
        add(backPanel, BorderLayout.SOUTH);
        backButton.addActionListener(_ -> {
            // Close the current frame (which contains the AddPostGUI panel)
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
        });
    }

    private List<Post> getSortedPostsFromNonConnections(User currentUser, UserHashTable userHashTable) {
        List<Post> allPosts = new ArrayList<>();

        // Gather posts from all non-connected users (excluding the current user's connections and own posts)
        for (User user : userHashTable.getAllUsers()) {
            if (!currentUser.getConnections().contains(user) && !user.equals(currentUser)) {
                allPosts.addAll(user.getPosts());
            }
        }

        // Sort posts by likes (most likes first)
        allPosts.sort(Comparator.comparingInt(Post::getLikesCount).reversed());
        return allPosts;
    }

    private JPanel createPostPanel(Post post, User currentUser) {
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(new Color(70, 70, 70));
        postPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        postPanel.setPreferredSize(new Dimension(750,150));
        postPanel.setMaximumSize(new Dimension(750,150));
        postPanel.setMinimumSize(new Dimension(750,150));

        // Username and Posting Time
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 70, 70));

        JLabel usernameLabel = new JLabel(post.getUser().getUsername() + " ");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        JLabel timeLabel = new JLabel(formatDate(post.getTimeOfPosting()));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.LIGHT_GRAY);

        // Add the "Add Friend" button
        JButton addFriendButton = new JButton("Add Friend");
        addFriendButton.setFocusable(false);
        addFriendButton.setBackground(new Color(0, 123, 255));
        addFriendButton.setForeground(Color.WHITE);

        // Initialize button text based on current request status
        if (currentUser.getRequestsSent().contains(post.getUser())) {
            addFriendButton.setText("Cancel Request");
        } else if (currentUser.getConnections().contains(post.getUser())) {
            addFriendButton.setText("Already Friends");
        }

        addFriendButton.addActionListener(e -> {
            // Logic for handling friend request toggle
            if (currentUser.getRequestsSent().contains(post.getUser())) {
                currentUser.cancelConnectionRequest(post.getUser());
                addFriendButton.setText("Add Friend");
            } else if (currentUser.getConnections().contains(post.getUser())) {
                addFriendButton.setText("Already Friends");
            } else {
                currentUser.sendConnectionRequest(post.getUser());
                addFriendButton.setText("Cancel Request");
            }
        });

        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.setBackground(new Color(70, 70, 70));
        topRightPanel.add(addFriendButton, BorderLayout.EAST);
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.CENTER);
        topPanel.add(topRightPanel, BorderLayout.EAST);

        postPanel.add(topPanel);

        // Title
        JLabel titleLabel = new JLabel(post.getPostTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        postPanel.add(titleLabel);

        // Content
        JTextArea contentArea = new JTextArea(post.getPostContent());
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 16));
        contentArea.setBackground(new Color(80, 80, 80));
        contentArea.setForeground(Color.WHITE);

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.setPreferredSize(new Dimension(700, 100)); // Uniform content area size
        postPanel.add(contentScroll);

        // Likes count
        JLabel likesLabel = new JLabel("Likes: " + post.getLikesCount());
        likesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        likesLabel.setForeground(Color.WHITE);
        postPanel.add(likesLabel);

        // Buttons: Like and Comment
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(new Color(70, 70, 70));

        JButton likeButton = new JButton("Like");
        JButton commentButton = new JButton("Comment");

        likeButton.setFocusable(false);
        commentButton.setFocusable(false);

        likeButton.setBackground(new Color(0, 123, 255));
        commentButton.setBackground(new Color(0, 123, 255));

        likeButton.setForeground(Color.WHITE);
        commentButton.setForeground(Color.WHITE);

        likeButton.addActionListener(e -> {
            post.like();
            likesLabel.setText("Likes: " + post.getLikesCount());
        });

        buttonsPanel.add(likeButton);
        buttonsPanel.add(commentButton);

        postPanel.add(buttonsPanel);

        return postPanel;
    }

    private static String formatDate(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return timestamp.format(formatter);
    }

    public void showFrame(User currentUser) {
        JFrame frame = new JFrame("Explore Posts");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new ExploreGUI(currentUser, userHashTable));
        frame.setVisible(true);
    }

public static void main(String[] args) {
        // Create the main frame

        UserHashTable userHashTable = new UserHashTable(100);

        // Create mock data
        User currentUser = new User("current_user", "password", "current@example.com", java.time.LocalDate.of(1990, 5, 15), "Male", new Location("Mexico", "USA"));
        User connection1 = new User("john_doe", "password", "john@example.com", java.time.LocalDate.of(1990, 5, 15), "Male", new Location("Mexico", "USA"));
        User connection2 = new User("jane_doe", "password", "jane@example.com", java.time.LocalDate.of(1990, 5, 15), "Male", new Location("Mexico", "USA"));

        userHashTable.insert(currentUser);
        userHashTable.insert(connection1);
        userHashTable.insert(connection2);

        // Add posts to connection1
        connection1.addPost(new Post(connection1, "Post 1 by John", "This is the first post by John."));
        connection1.addPost(new Post(connection1, "Post 2 by John", "John's second post with a longer content to test wrapping."));

        // Add posts to connection2
        connection2.addPost(new Post(connection2, "Jane's Post", "This is Jane's post. She wrote something great here."));
        connection2.addPost(new Post(connection2, "Jane's Post", "This is Jane's Post. Hlo there"));
        Post post = new Post(connection2, "Hlo", "Hlo there");
        post.setTimeOfPosting(java.time.LocalDateTime.of(2024, 12, 18, 22, 48));
        connection2.addPost(post);

        // Add connections to the current user
        currentUser.addConnection(connection1);
        //currentUser.addConnection(connection2);

        // Create the ExploreGUI panel
        ExploreGUI explorePanel = new ExploreGUI(currentUser, userHashTable);
        explorePanel.showFrame(currentUser);
    }

}