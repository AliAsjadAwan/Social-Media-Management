package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class ViewOwnPostsGUI extends JPanel {

    private final JPanel contentPanel;
    private final User currentUser;

    public ViewOwnPostsGUI(User currentUser) {
        this.currentUser = currentUser;

        // Set the size of the panel
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(50, 50, 50));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Your Posts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        this.add(titlePanel, BorderLayout.NORTH);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(50, 50, 50));

        // Gather and display posts
        List<Post> userPosts = getSortedPostsFromUser(currentUser);

        if (userPosts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No posts available.");
            noPostsLabel.setForeground(Color.WHITE);
            noPostsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noPostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(noPostsLabel);
        } else {
            for (Post post : userPosts) {
                JPanel postPanel = createPostPanel(post);
                contentPanel.add(Box.createVerticalStrut(10));
                contentPanel.add(postPanel);
            }
        }

        // Add content panel to a scrollable pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);

        // Back Button Panel
        JButton backButton = new LoginPage.RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.addActionListener(_ -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
        });
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setBackground(new Color(50, 50, 50));
        backPanel.add(backButton);
        this.add(backPanel, BorderLayout.SOUTH);
    }

    private List<Post> getSortedPostsFromUser(User currentUser) {
        List<Post> userPosts = new ArrayList<>(currentUser.getPosts());
        userPosts.sort(Comparator.comparing(Post::getTimeOfPosting).reversed());
        return userPosts;
    }

    private JPanel createPostPanel(Post post) {
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(new Color(70, 70, 70));
        postPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        postPanel.setMaximumSize(new Dimension(750, 150));

        // Username and Posting Time
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 70, 70));
        JLabel usernameLabel = new JLabel(post.getUser().getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        JLabel timeLabel = new JLabel(formatDate(post.getTimeOfPosting()));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.LIGHT_GRAY);
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.EAST);
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
        postPanel.add(contentScroll);

        // Likes Count and Delete Button
        JLabel likesLabel = new JLabel("Likes: " + post.getLikesCount());
        likesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        likesLabel.setForeground(Color.WHITE);
        postPanel.add(likesLabel);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 50, 50));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(_ -> {
            deleteButton.setEnabled(false);
            currentUser.deletePost(post.getPostId());
            JOptionPane.showMessageDialog(this, "Post has been deleted.");
            refreshPosts();
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(new Color(70, 70, 70));
        buttonsPanel.add(deleteButton);
        postPanel.add(buttonsPanel);

        return postPanel;
    }

    private String formatDate(java.time.LocalDateTime timestamp) {
        return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void refreshPosts() {
        contentPanel.removeAll();
        List<Post> userPosts = getSortedPostsFromUser(currentUser);

        if (userPosts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No posts available.");
            noPostsLabel.setForeground(Color.WHITE);
            noPostsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noPostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(noPostsLabel);
        } else {
            for (Post post : userPosts) {
                JPanel postPanel = createPostPanel(post);
                contentPanel.add(Box.createVerticalStrut(10));
                contentPanel.add(postPanel);
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        User currentUser = new User("current_user", "password", "current@example.com", java.time.LocalDate.of(1990, 5, 15), "Male", new Location("Mexico", "USA"));
        currentUser.addPost(new Post(currentUser, "Post 1", "This is the first post."));
        currentUser.addPost(new Post(currentUser, "Post 2", "This is the second post."));

        JFrame frame = new JFrame("View Your Posts");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ViewOwnPostsGUI viewPanel = new ViewOwnPostsGUI(currentUser);
        frame.add(viewPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
