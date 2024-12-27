package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.Deque;

public class ActivityLogGUI extends JPanel {

    private User currentUser;

    public ActivityLogGUI(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(new Color(50, 50, 50));


        // Create the main frame for this panel
        JFrame frame = new JFrame("Activity Log");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Activity Log");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Activity Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(50, 50, 50));

        // Create a text area to display the activities
        JTextArea activityTextArea = new JTextArea();
        activityTextArea.setEditable(false); // Disable editing of the log
        activityTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        activityTextArea.setForeground(Color.WHITE);
        activityTextArea.setBackground(new Color(70, 70, 70));
        activityTextArea.setCaretColor(Color.WHITE);
        activityTextArea.setLineWrap(true);
        activityTextArea.setWrapStyleWord(true);

        // Gather and display activity log
        Deque<String> activityLog = currentUser.getActivityLog();

        if (activityLog.isEmpty()) {
            activityTextArea.setText("No activities available.");
        } else {
            StringBuilder logContent = new StringBuilder();
            for (String activity : activityLog) {
                logContent.append(activity).append("\n\n"); // Add some spacing between activities
            }
            activityTextArea.setText(logContent.toString());
        }

        // Add the text area to a scroll pane
        JScrollPane scrollPane = new JScrollPane(activityTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Back Button Panel
        JButton backButton = new LoginPage.RoundedButton("Back", new Color(150, 50, 50), Color.WHITE);
        backButton.addActionListener(_ -> frame.dispose());
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setBackground(new Color(50, 50, 50));
        backPanel.add(backButton);
        add(backPanel, BorderLayout.SOUTH);

        // Set the frame properties
        frame.add(this);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Create mock data
        User currentUser = new User("current_user", "password", "current@example.com", java.time.LocalDate.of(1990, 5, 15), "Male", new Location("Mexico", "USA"));
        currentUser.recordActivity("Logged in");
        currentUser.recordActivity("Liked a post");
        currentUser.recordActivity("Updated profile");

        // Create the ActivityLogGUI with the user and back button functionality
        new ActivityLogGUI(currentUser);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
