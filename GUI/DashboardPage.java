package GUI;

import Backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardPage extends JFrame implements ActionListener {

    private final JButton[] buttons = new JButton[13];
    private int index = 0;
    private final JPanel sidePanel; // Side navigation panel

    private final User currentUser; // Logged-in user object
    private final UserHashTable userHashTable; // Hash table for managing users

    public DashboardPage(User user, UserHashTable hashTable) {
        this.currentUser = user;
        this.userHashTable = hashTable;


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                userHashTable.writeUsersToFile("Backend/users.txt");
                userHashTable.writeConnectionsToFile("Backend/connections.txt");
                userHashTable.writePendingRequestsToFile("Backend/pending_Requests.txt");
                userHashTable.writeRequestsSentToFile("Backend/request_sent.txt");
                userHashTable.writePostsToFile("Backend/posts.txt");
            }
        });

        // Setting up the main frame
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Dashboard");
        setLayout(null); // Ensure absolute positioning for components

        // Initialize side panel
        sidePanel = new JPanel();
        sidePanel.setLayout(null); // Absolute positioning
        sidePanel.setBounds(5, 5, 270, 820);
        sidePanel.setBackground(new Color(3, 45, 48));
        add(sidePanel); // Add side panel first

        // Initialize the main panel with null layout
        // Main content panel
        JPanel mainPanel = new JPanel(null); // Absolute positioning
        mainPanel.setBounds(280, 5, 1238, 820);
        mainPanel.setBackground(Color.WHITE); // Default background color for main content
        add(mainPanel); // Add main panel after the side panel

        // Add dashboard as the default panel
        JPanel dashboardPanel = createDashboardPanel();
        dashboardPanel.setBounds(0, 0, 1238, 820);
        mainPanel.add(dashboardPanel);

        // Create feature buttons
        addFeatureButton("View Profile");
        addFeatureButton("Edit Profile");
        addFeatureButton("View Connections");
        addFeatureButton("View Suggested Connections");
        addFeatureButton("Send Connection Request");
        addFeatureButton("View Pending Requests");
        addFeatureButton("View Requests Sent");
        addFeatureButton("Add Post");
        addFeatureButton("View Posts");
        addFeatureButton("Explore");
        addFeatureButton("Display Friends' Posts");
        addFeatureButton("Display Activity");

        JButton logoutButton = new RoundedButton("Logout", new Color(150, 50, 50), Color.WHITE);
        logoutButton.setBounds(30, 30+(40*index), 200, 30); // Place it in the bottom center area of the mainPanel
        logoutButton.addActionListener(this);
        buttons[index] = logoutButton; // Store in the buttons array
        sidePanel.add(logoutButton);



        // Add mouse effects for buttons
        addMouseEffectsToButtons();

        // Add the logo to the side panel
        //addLogoToSidePanel();

        // Make the frame visible
        setVisible(true);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(3, 45, 48));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(350, 10, 470, 80);
        panel.add(welcomeLabel);

        return panel;
    }

    private void addFeatureButton(String text) {
        JButton button = new RoundedButton(text, new Color(30, 100, 200), Color.WHITE);
        button.setBounds(30, 30 + (index * 40), 200, 30);
        button.addActionListener(this);
        buttons[index++] = button;
        sidePanel.add(button);
    }

    private void addMouseEffectsToButtons() {
        for (JButton button : buttons) {
            if (button != null) {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        button.setForeground(Color.BLACK);
                        button.setBackground(Color.WHITE);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        button.setForeground(Color.WHITE);
                        button.setBackground(new Color(30, 100, 200));
                    }
                });
            }
        }

        // Special hover effect for logout button
        JButton logoutButton = buttons[buttons.length - 1];
        if (logoutButton != null) {
            logoutButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    logoutButton.setForeground(new Color(200, 50, 50));
                    logoutButton.setBackground(Color.WHITE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    logoutButton.setForeground(Color.WHITE);
                    logoutButton.setBackground(new Color(150, 50, 50));
                }
            });
        }
    }

    private void addLogoToSidePanel() {
        ImageIcon logoIcon = new ImageIcon("src/Visuals/Logo.jpg");
        if (logoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBounds(35, 680, logoIcon.getIconWidth(), logoIcon.getIconHeight());
            sidePanel.add(logoLabel);
        } else {
            JOptionPane.showMessageDialog(this, "Logo not found! Please check the file path.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Do not remove the dashboard panel, just add the new feature panel above it
        JPanel dashboardPanel = createDashboardPanel();
        dashboardPanel.setBounds(0, 0, 1238, 820);
       // JPanel newPanel=null;

        if (source == buttons[0]) { // "View Profile" button
            // Create and configure ViewProfilePanel
            new ViewProfilePanel(currentUser, userHashTable);
            
        } else if (source == buttons[1]) { // "Edit Profile"
            // Create and configure EditProfilePanel
            new EditProfileGUI(currentUser, userHashTable);

        } else if (source == buttons[2]) { // "View Connections"
            // Create and configure ViewConnectionsPanel
            new ConnectionsGUI(currentUser);

        } else if (source == buttons[3]) {
            //Create and configure SuggestionGUI
            new SuggestionsGUI(currentUser,userHashTable);
            
        } else if (source == buttons[4]) {
            // Create and configure SendConnectionGUI
            new SendConnectionGUI(currentUser, userHashTable);
            
        } else if (source == buttons[5]) {

            new PendingRequestGUI(currentUser);

        } else if (source == buttons[6]) {

            new RequestSentGUI(currentUser);

        } else if (source == buttons[7]) {

            new AddPostGUI(currentUser);

        } else if (source == buttons[8]) {

            JFrame frame = new JFrame("View Your Posts");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ViewOwnPostsGUI viewPanel = new ViewOwnPostsGUI(currentUser);
            frame.add(viewPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } else if (source == buttons[9]) {

            ExploreGUI gui=new ExploreGUI(currentUser, userHashTable);
            gui.showFrame(currentUser);

        } else if (source == buttons[10]) {

            ViewConnectionsPostsPanel newPanel=new ViewConnectionsPostsPanel(currentUser);
            newPanel.showFrame(currentUser);

        } else if (source == buttons[11]) {

            new ActivityLogGUI(currentUser);

        } else if (source == buttons[buttons.length - 1]) { // "Logout"
            dispose();
            userHashTable.writeUsersToFile("Backend/users.txt");
            userHashTable.writeConnectionsToFile("Backend/connections.txt");
            userHashTable.writePendingRequestsToFile("Backend/pending_Requests.txt");
            userHashTable.writeRequestsSentToFile("Backend/request_sent.txt");
            userHashTable.writePostsToFile("Backend/posts.txt");
            new LoginPage(userHashTable);

        } else {
            JOptionPane.showMessageDialog(this, "This feature is under development.");
        }

    }


    // RoundedButton Class (unchanged)
    public static class RoundedButton extends JButton {
        private Color backgroundColor;
        private Color textColor;

        public RoundedButton(String text, Color backgroundColor, Color textColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(textColor);
        }

        @Override
        public void setBackground(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            repaint();
        }

        @Override
        public void setForeground(Color textColor) {
            this.textColor = textColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.setColor(textColor);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
            g2.dispose();
        }
    }

}
