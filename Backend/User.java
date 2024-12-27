package Backend;

import java.time.LocalDate;
import java.util.*;

public class User {
    private final int userId;
    private String username;
    private String password;
    private String email;
    private final LocalDate DateOfBirth;
    private String bio;
    private final Location location;
    private final String gender;
    private final List<User> connections;
    private final List<User> pendingRequests;
    private final List<User> requestsSent;
    private boolean isActive;
    private final List<Post> posts;
    private final Deque<String> activityLog;

    public User(String username, String password, String email,LocalDate DateOfBirth,String gender,Location location) {
        this.userId = generateRandomId();
        this.username = username;
        this.password = password;
        this.email = email;
        this.DateOfBirth = DateOfBirth;
        this.bio = null;
        this.location = location;
        this.gender = gender;
        this.connections = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.requestsSent = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.activityLog = new ArrayDeque<>();
        this.isActive = true;
    }

    public User(int userId, String username, String password, String email, LocalDate dateOfBirth, String gender, Location location) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.DateOfBirth = dateOfBirth;
        this.bio = null;
        this.location = location;
        this.gender = gender;
        this.connections = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.requestsSent = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.activityLog = new ArrayDeque<>();
        this.isActive = true;
    }

    public User(String username, String password, String email, String bio,LocalDate dateOfBirth, String gender, Location location) {
        this.userId = generateRandomId();
        this.username = username;
        this.password = password;
        this.email = email;
        this.DateOfBirth = dateOfBirth;
        this.bio = bio;
        this.location = location;
        this.gender = gender;
        this.connections = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.requestsSent = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.activityLog = new ArrayDeque<>();
        this.isActive = true;
    }
    private static int generateRandomId() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }

    public void displayConnections() {
        if (!connections.isEmpty()) {
        for (User user : connections) {
            System.out.println(user.getUsername());
            }
        }
        else {
            System.out.println("No connections found");
        }
    }

    public void addConnection(User user) {
        if (!connections.contains(user)) {
            connections.add(user);
            user.connections.add(this);
            user.requestsSent.remove(this);
            recordActivity("Added connection with " + user.getUsername());
        } else {
            System.out.println("Request already sent to " + user.getUsername());
        }
    }

    public void removeConnection(User user) {
        if (connections.contains(user)) {
            connections.remove(user);
            user.connections.remove(this);
            recordActivity("Removed connection with " + user.getUsername());
            System.out.println("Removed connection with " + user.getUsername());
        } else {
            System.out.println("Friend does not exist with name " + user.getUsername());
        }
    }

    public List<User> getMutualConnections(User otherUser) {
        List<User> mutualConnections = new ArrayList<>(this.connections);
        mutualConnections.retainAll(otherUser.getConnections());
        for (User user:mutualConnections){
            System.out.println(user.getUsername());
        }
        recordActivity("Viewed Mutual Connections with "+ otherUser.getUsername());
        return mutualConnections;
    }

    public List<User> getMutualConnections1(User otherUser) {
        List<User> mutualConnections = new ArrayList<>();
        for (User connection : this.connections) {
            if (otherUser.getConnections().contains(connection)) {
                mutualConnections.add(connection);
            }
        }
        recordActivity("Viewed Mutual Connections with "+ otherUser.getUsername());
        return mutualConnections;
    }

    public List<User> getSuggestedConnections() {
        List<User> suggestions = new ArrayList<>();
        for (User connection : this.connections) {
            for (User friendOfConnection : connection.getConnections()) {
                if (!this.connections.contains(friendOfConnection) && !friendOfConnection.equals(this)) {
                    if (!suggestions.contains(friendOfConnection)) {
                        suggestions.add(friendOfConnection);
                    }
                }
            }
        }

        for (User user : suggestions) {
            System.out.println(user.getUsername());
        }
        return suggestions;
    }

    public ArrayList<String> suggestUsersByLocation(UserHashTable userHashTable) {
        ArrayList<String> locationBasedSuggestions = new ArrayList<>();

        for (User potentialUser : userHashTable.table) {
            if (potentialUser != null && !potentialUser.getUsername().equals(this.username)) {
                boolean alreadyConnected = this.connections.contains(potentialUser);
                boolean alreadyRequested = this.requestsSent.contains(potentialUser);
                boolean alreadyReceivedRequest = potentialUser.getPendingRequests().contains(this);

                // Skip users already connected or involved in pending requests
                if (!alreadyConnected && !alreadyRequested && !alreadyReceivedRequest) {
                    String basis = "";

                    // Suggest based on city
                    if (this.getCity().equalsIgnoreCase(potentialUser.getCity())) {
                        basis = "City";
                    }
                    // Suggest based on country if city doesn't match
                    else if (this.getCountry().equalsIgnoreCase(potentialUser.getCountry())) {
                        basis = "Country";
                    }

                    // Add suggestion with basis if a match is found
                    if (!basis.isEmpty()) {
                        locationBasedSuggestions.add(
                                potentialUser.getUsername() + " (Suggested based on " + basis + ")"
                        );
                    }
                }
            }
        }

        // Print suggestions
        if (locationBasedSuggestions.isEmpty()) {
            System.out.println("No users found in your city or country.");
        } else {
            System.out.println("Suggestions based on your location:");
            for (String suggestion : locationBasedSuggestions) {
                System.out.println("- " + suggestion);
            }
        }

        return locationBasedSuggestions;
    }


    public void sendConnectionRequest(User recipient) {
        if (!this.isActive) {
            System.out.println("Your account is deactivated. Cannot send requests.");
            return;
        }

        if (!recipient.isActive()) {
            System.out.println(recipient.getUsername() + "'s account is deactivated. Cannot send a request.");
            return;
        }

        if (!recipient.pendingRequests.contains(this)) {
            recipient.pendingRequests.add(this);
            this.requestsSent.add(recipient);
            recordActivity("Sent connection request to " + recipient.getUsername());
            System.out.println("Connection request sent to " + recipient.getUsername() + ".");
        } else {
            System.out.println("You have already sent a connection request to " + recipient.getUsername() + ".");
        }
    }

    public void cancelConnectionRequest(User recipient) {
        if (!this.isActive) {
            System.out.println("Your account is deactivated. Cannot cancel requests.");
            return;
        }

        if (recipient.pendingRequests.contains(this)) {
            recipient.pendingRequests.remove(this);
            this.requestsSent.remove(recipient);
            recordActivity("Cancelled connection request to " + recipient.getUsername());
            System.out.println("Connection request to " + recipient.getUsername() + " has been cancelled.");
        } else {
            System.out.println("No connection request was sent to " + recipient.getUsername() + " to cancel.");
        }
    }

    public void acceptConnectionRequest(User sender) {
        if (pendingRequests.contains(sender)) {
            addConnection(sender);
            pendingRequests.remove(sender);
            sender.requestsSent.remove(this);
            System.out.println("You are now connected with " + sender.getUsername() + ".");
        } else {
            System.out.println("No connection request from " + sender.getUsername() + " to accept.");
        }
    }

    public void rejectConnectionRequest(User sender) {
        if (pendingRequests.contains(sender)) {
            pendingRequests.remove(sender);
            sender.requestsSent.remove(this);
            recordActivity("Rejected connection request from " + sender.getUsername());
            System.out.println("You have rejected the connection request from " + sender.getUsername() + ".");
        } else {
            System.out.println("No connection request from " + sender.getUsername() + " to reject.");
        }
    }

    public void displayPendingRequests() {
        if (pendingRequests.isEmpty()) {
            System.out.println("No pending connection requests.");
        } else {
            System.out.print("Pending connection requests: ");
            for (User user : pendingRequests) {
                System.out.print(user.getUsername() + " ");
            }
            System.out.println();
        }
    }

    public void displayRequestsSent() {
        if (requestsSent.isEmpty()) {
            System.out.println("No requests sent to the user.");
        }
        else {
            System.out.println("Sent requests: ");
            for (User user : requestsSent) {
                System.out.print(user.getUsername() + " ");
            }
            System.out.println();
        }
    }

    public void addPost(Post post) {
        posts.add(post);
        posts.sort(Comparator.comparing(Post::getTimeOfPosting).reversed());
        recordActivity("Added a post with ID " + post.getPostId());
        System.out.println("Post added: " + post);
    }

    public void deletePost(int postId) {
        Post postToRemove = null;
        for (Post post : posts) {
            if (post.getPostId() == postId) {
                postToRemove = post;
                break;
            }
        }

        if (postToRemove != null) {
            posts.remove(postToRemove);
            posts.sort(Comparator.comparing(Post::getTimeOfPosting).reversed());
            recordActivity("Deleted a post with ID " + postId);
            System.out.println("Post with ID " + postId + " has been deleted.");
        } else {
            System.out.println("Post does not exist.");
        }
    }


    public void displayPosts() {
        if (posts.isEmpty()) {
            System.out.println("No Posts to show.");
            return;
        }
        System.out.println("Posts:");
        for (Post post : posts) {
            System.out.println(post);
        }
    }

    public void displayConnectedUsersPosts() {
        if (connections.isEmpty()) {
            System.out.println("You have no connections to display posts from.");
            return;
        }

        List<Post> allPosts = new ArrayList<>();

        // Collect all posts from all connections
        for (User connection : connections) {
            allPosts.addAll(connection.getPosts());
        }

        // If no posts are found, display a message
        if (allPosts.isEmpty()) {
            System.out.println("No posts available from your connections.");
            return;
        }

        // Sort all posts by time of posting in descending order (most recent first)
        allPosts.sort(Comparator.comparing(Post::getTimeOfPosting).reversed());

        // Display the sorted posts
        System.out.println("Posts from your connections (most recent first):");
        for (Post post : allPosts) {
            System.out.println(post);
        }
    }


    public void recordActivity(String activity) {
        activityLog.push(activity);
    }

    public void displayActivityLog() {
        if (activityLog.isEmpty()) {
            System.out.println("No activities to display.");
            return;
        }

        System.out.println("User Activity Log (most recent first):");
        for (String activity : activityLog) {
            System.out.println(activity);
        }
    }

    public void editProfile() {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Show options for what the user wants to edit
        System.out.println("What would you like to edit?");
        System.out.println("1. Username");
        System.out.println("2. Password");
        System.out.println("3. Email");
        System.out.println("4. Bio");
        System.out.print("Enter the number of the field you want to edit: ");
        int choice = scanner.nextInt();
        scanner.nextLine();


        String newValue = "";
        switch (choice) {
            case 1:
                System.out.print("Enter new username: ");
                newValue = scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter new password: ");
                newValue = scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter new email: ");
                newValue = scanner.nextLine();
                break;
            case 4:
                System.out.print("Enter new bio: ");
                newValue = scanner.nextLine();
                break;
            default:
                System.out.println("Invalid choice. Exiting.");
                return;
        }

        System.out.print("Please enter your password to confirm: ");
        String enteredPassword = scanner.nextLine();

        if (!this.password.equals(enteredPassword)) {
            System.out.println("Incorrect password. Profile edit failed.");
            return;
        }

        switch (choice) {
            case 1:
                this.username = newValue;
                System.out.println("Username updated successfully.");
                break;
            case 2:
                this.password = newValue;
                System.out.println("Password updated successfully.");
                break;
            case 3:
                this.email = newValue;
                System.out.println("Email updated successfully.");
                break;
            case 4:
                this.bio = newValue;
                System.out.println("Bio updated successfully.");
                break;
        }
    }



    @Override
    public String toString() { return username; }

    public String getPassword() { return password; }

    public String getEmail() { return email; }

    public String getBio() { return bio; }

    public void setBio(String bio) { this.bio = bio; }

    public LocalDate getDateOfBirth() { return DateOfBirth; }

    public boolean isActive() { return isActive; }

    public void setActive(boolean isActive) { this.isActive = isActive; }

    public List<Post> getPosts() { return new ArrayList<>(posts); }

    public int getUserId() { return userId; }

    public String getUsername() { return username; }

    public List<User> getConnections() { return connections; }

    public List<User> getPendingRequests() { return pendingRequests; }

    public void addPendingRequest(User pendingRequest) { pendingRequests.add(pendingRequest); }

    public String getGender() { return gender; }

    public Location getLocation() { return location; }

    public String getCity() { return location.getCity(); }

    public String getCountry() { return location.getCountry(); }

    public List<User> getRequestsSent() { return requestsSent; }

    public Deque<String> getActivityLog(){ return activityLog; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setEmail(String email) { this.email = email; }

    public void addSentRequest(User requestedUser) {
        this.requestsSent.add(requestedUser);
    }

    public boolean hasSentConnectionRequest(User user) {
        return this.requestsSent.contains(user);
    }
}

