package Backend;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class UserHashTable {
    public static User[] table;
    private static int size;

    public UserHashTable(int size) {
        this.table = new User[size];
        this.size = size;
    }

    private static int hashFunction(String username, int attempt) {
        int hash = Math.abs(username.hashCode());
        return (hash + attempt * attempt) % size;
    }

    private void rehash() {
        User[] oldTable = table.clone();
        int oldSize = size;
        size = oldSize * 2;
        table = new User[size];

        for (User user : oldTable) {
            if (user != null) {
                int attempt = 0;
                int index;
                do {
                    index = hashFunction(user.getUsername(), attempt);
                    if (table[index] == null) {
                        table[index] = user;
                        break;
                    }
                    attempt++;
                } while (attempt < size);
            }
        }
    }


    private int findUserIndex(String username) {
        int attempt = 0;
        int index;
        do {
            index = hashFunction(username, attempt);
            User user = table[index];
           // System.out.println("Attempt " + attempt + ", index: " + index);
            if (user == null) {
              //  System.out.println("Slot empty at index: " + index);
                return -1; // User not found
            }
            if (user.getUsername().equals(username)) {
                System.out.println("User found at index: " + index);
                return index;
            }
            attempt++;
        } while (attempt < size);
        System.out.println("User not found after " + attempt + " attempts.");
        return -1;
    }


    public boolean isUsernameTaken(String username) {
        return findUserIndex(username) != -1;
    }

    public void signup(String username, String password, String email, String dateOfBirth,String gender,Location location) {
        if (isUsernameTaken(username)) {
            System.out.println("Username already exists. Choose a different username.");
            return;
        }

        if (isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        if (isValidPassword(password)) {
            System.out.println("Password must contain at least one uppercase letter, one special character, and one number.");
            return;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(dateOfBirth);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age <= 15) {
            System.out.println("Signup failed. Age must be greater than 15.");
            return;
        }

        int attempt = 0;
        int index;
        do {
            index = hashFunction(username, attempt);
            if (table[index] == null) {
                User newUser = new User(username, password, email, dob,gender,location);
                table[index] = newUser;
                System.out.println("User added at index: " + index);
                System.out.println(username + " Signed Up Successfully. User added to the hash table.");
                return;
            }
            attempt++;
        } while (attempt < size);

        System.out.println("HashTable is full. Increasing its size");
        size=size*2;
        rehash();
        signup(username, password, email, dob.toString(),gender,location);
    }


    public void signupWithBio(String username, String password, String email,String bio, String dateOfBirth,String gender,Location location) {
        if (isUsernameTaken(username)) {
            System.out.println("Username already exists. Choose a different username.");
            return;
        }

        if (isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        if (isValidPassword(password)) {
            System.out.println("Password must contain at least one uppercase letter, one special character, and one number.");
            return;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(dateOfBirth);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age <= 15) {
            System.out.println("Signup failed. Age must be greater than 15.");
            return;
        }

        int attempt = 0;
        int index;
        do {
            index = hashFunction(username, attempt);
            if (table[index] == null) {
                User newUser = new User(username, password, email,bio, dob,gender,location);
                table[index] = newUser;
                System.out.println("User added at index: " + index);
                System.out.println(username + " Signed Up Successfully. User added to the hash table.");
                return;
            }
            attempt++;
        } while (attempt < size);

        System.out.println("HashTable is full. Increasing its size");
        size=size*2;
        rehash();
        signupWithBio(username, password, email, bio, dob.toString(),gender,location);
    }

    public User login(String username, String password) {
        int index = findUserIndex(username);


        if (index == -1) {
            System.out.println("Username does not exist. Please signup first.");
            return null;
        }

        User user = table[index];
        if (user.getPassword().equals(password)) {
            user.recordActivity("Signed in successfully.");
            System.out.println("Login successful. Welcome, " + username + "!");
            return user;
        } else {
            System.out.println("Incorrect password.");
            return null;
        }
    }


    public void displayTable() {
        for (int i = 0; i < size; i++) {
            if (table[i] != null) {
                System.out.println("Index " + i + ": " + table[i]);
            }
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-.+]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return !Pattern.matches(emailRegex, email);
    }

    public boolean isValidPassword(String password) {
        return !password.matches("^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={};':\"\\\\|,.<>?])(?=.*\\d).{6,}$");
    }


    public static User find(String username) {
        int attempt = 0;
        int index;


        while (attempt < size) {
            index = hashFunction(username, attempt);

            User user = table[index];

            if (user != null && user.getUsername().equals(username)) {
                return user;
            }

            if (user == null) {
                break;
            }

            attempt++;
        }

        return null;
    }

    // In UserHashTable class
    public List<User> getUsersByUsernamePrefix(String prefix, User currentUser) {
        List<User> matchedUsers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (!user.equals(currentUser) && user.getUsername().toLowerCase().startsWith(prefix.toLowerCase())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }

    public List<User> getUsersByLocationPrefix(String prefix, User currentUser) {
        List<User> matchedUsers = new ArrayList<>();
        for (User user : getAllUsers()) {
            if (!user.equals(currentUser)
                    && user.getLocation() != null
                    && user.getLocation().getCity().toLowerCase().startsWith(prefix.toLowerCase())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }



    public List<User> getAllUsers() {
        List<User> Users = new ArrayList<>();
        for (User user : table) {
            if (user != null) {
                Users.add(user);
            }
        }
        return Users;
    }

    public void editProfile(String username, String newUsername, String newPassword, String newEmail, String newBio, String newCity, String newCountry, String enteredPassword) {
        // Find the user by username
        User user = find(username);

        if (user == null) {
            System.out.println("User not found. Profile edit failed.");
            return;
        }

        // Check if the entered password matches the stored password
        if (!user.getPassword().equals(enteredPassword)) {
            System.out.println("Incorrect password. Profile edit failed.");
            return;
        }

        // Check if newUsername is already taken
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            if (isUsernameTaken(newUsername)) {
                System.out.println("Username already exists. Choose a different username.");
                return;
            }
            user.setUsername(newUsername);
        }

        // Check if newPassword is valid
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (isValidPassword(newPassword)) {
                System.out.println("Password must contain at least one uppercase letter, one special character, and one number.");
                return;
            }
            user.setPassword(newPassword);
        }

        // Check if newEmail is valid
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            if (isValidEmail(newEmail)) {
                System.out.println("Invalid email format.");
                return;
            }
            user.setEmail(newEmail);
        }

        // Update bio, city, and country if not null
        if (newBio != null && !newBio.trim().isEmpty()) {
            user.setBio(newBio);
        }
        if (newCity != null && !newCity.trim().isEmpty()) {
            user.getLocation().setCity(newCity);
        }
        if (newCountry != null && !newCountry.trim().isEmpty()) {
            user.getLocation().setCountry(newCountry);
        }

        System.out.println("Profile updated successfully.");
    }


    public void writeUsersToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            for (User user : table) {
                if (user != null) {
                    writer.println(user.getUserId() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getEmail() + "," + user.getDateOfBirth()+","+user.getGender()+","+user.getCity()+"|"+user.getCountry());
                }
            }
            System.out.println("User details successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void writeConnectionsToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            for (User user : table) {
                if (user != null && !user.getConnections().isEmpty()) {
                    writer.print(user.getUsername() + ":");
                    for (int i = 0; i < user.getConnections().size(); i++) {
                        writer.print(user.getConnections().get(i).getUsername());
                        if (i < user.getConnections().size() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println(); // End the line after listing connections
                }
            }
            System.out.println("Connections successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing connections to file: " + e.getMessage());
        }
    }

    public void writePendingRequestsToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            for (User user : table) {
                if (user != null && !user.getPendingRequests().isEmpty()) {
                    writer.print(user.getUsername() + ":");
                    for (int i = 0; i < user.getPendingRequests().size(); i++) {
                        writer.print(user.getPendingRequests().get(i).getUsername());
                        if (i < user.getPendingRequests().size() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println(); // End the line after listing pending requests
                }
            }
            System.out.println("Pending requests successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing pending requests to file: " + e.getMessage());
        }
    }

    public void writeRequestsSentToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            for (User user : table) {
                if (user != null && !user.getRequestsSent().isEmpty()) {
                    writer.print(user.getUsername() + ":");
                    for (int i = 0; i < user.getRequestsSent().size(); i++) {
                        writer.print(user.getRequestsSent().get(i).getUsername());
                        if (i < user.getRequestsSent().size() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println(); // End the line after listing requests sent
                }
            }
            System.out.println("Requests sent successfully written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing requests sent to file: " + e.getMessage());
        }
    }


    public void writePostsToFile(String fileName) {
        boolean postsWritten = false; // Flag to check if any posts were written
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 24-hour format

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            for (User user : table) {
                if (user != null && user.getPosts() != null && !user.getPosts().isEmpty()) {
                    writer.print(user.getUsername() + ":");
                    for (int i = 0; i < user.getPosts().size(); i++) {
                        Post post = user.getPosts().get(i);

                        // Format time using the DateTimeFormatter
                        String formattedTime = post.getTimeOfPosting().format(formatter);

                        // Write post details (PostId, Title, Content, Time)
                        writer.print(post.getPostId() + "|" + post.getPostTitle() + "|" + post.getPostContent() + "|" + formattedTime);

                        // Write likes
                        writer.print("|Likes:" + post.getLikesCount());

                        // Write comments
                        if (post.getComments() != null && !post.getComments().isEmpty()) {
                            writer.print("|Comments:");
                            for (int j = 0; j < post.getComments().size(); j++) {
                                String comment = post.getComments().get(j);
                                writer.print(comment); // Directly write the string representation
                                if (j < post.getComments().size() - 1) {
                                    writer.print(","); // Separate comments with commas
                                }
                            }
                        } else {
                            writer.print("|Comments:None");
                        }

                        if (i < user.getPosts().size() - 1) {
                            writer.print(";"); // Separate posts with semicolons
                        }
                    }
                    writer.println(); // End the line after listing posts
                    postsWritten = true;
                }
            }
            if (postsWritten) {
                System.out.println("Posts successfully written to " + fileName);
            } else {
                System.out.println("No posts found to write to " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error writing posts to file: " + e.getMessage());
        }
    }






    public static UserHashTable loadFromFiles(String usersFile, String connectionsFile, String pendingRequestsFile,String requestSentFile, String postsFile, int size) {
        UserHashTable userHashTable = new UserHashTable(size);

        // Load Users
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    int userId = Integer.parseInt(parts[0]);
                    String username = parts[1];
                    String password = parts[2];
                    String email = parts[3];
                    LocalDate dateOfBirth = LocalDate.parse(parts[4]);
                    String gender = parts[5];
                    String locationStr = parts[6];


                    String[] locationParts = locationStr.split("\\|"); // Assuming '|' separates city and country
                    if (locationParts.length == 2) {
                        String city = locationParts[0].trim();
                        String country= locationParts[1].trim();


                        Location location = new Location(city, country);

                        if (userHashTable.find(username) == null) {
                            userHashTable.signupWithId(userId, username, password, email, dateOfBirth.toString(), gender, location);
                        }
                    } else {
                        System.err.println("Invalid location format for user: " + line);
                    }
                }
            }
            System.out.println("User data loaded from " + usersFile);
        } catch (FileNotFoundException e) {
            System.out.println(usersFile + " not found. Starting with an empty user hash table.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }



        // Load Connections
        try (BufferedReader reader = new BufferedReader(new FileReader(connectionsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    User user = userHashTable.find(parts[0]);
                    if (user != null) {
                        String[] connections = parts[1].split(",");
                        for (String connection : connections) {
                            User connectionUser = userHashTable.find(connection);
                            if (connectionUser != null) {
                                user.addConnection(connectionUser);
                            }
                        }
                    }
                }
            }
            System.out.println("Connection data loaded from " + connectionsFile);
        } catch (FileNotFoundException e) {
            System.out.println(connectionsFile + " not found. Starting with empty connections.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Load Pending Requests
        try (BufferedReader reader = new BufferedReader(new FileReader(pendingRequestsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    User user = userHashTable.find(parts[0]);
                    if (user != null) {
                        String[] requests = parts[1].split(",");
                        for (String request : requests) {
                            User requestUser = userHashTable.find(request);
                            if (requestUser != null) {
                                user.addPendingRequest(requestUser);
                            }
                        }
                    }
                }
            }
            System.out.println("Pending requests loaded from " + pendingRequestsFile);
        } catch (FileNotFoundException e) {
            System.out.println(pendingRequestsFile + " not found. Starting with empty pending requests.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Load Request Sent
        try (BufferedReader reader = new BufferedReader(new FileReader(requestSentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String[] sentRequests = parts[1].split(",");
                    User user = find(username); // Assuming you have a method to find a user by username
                    if (user != null) {
                        for (String requestUsername : sentRequests) {
                            User requestedUser = find(requestUsername);
                            if (requestedUser != null) {
                                user.addSentRequest(requestedUser); // Assuming you have a method to add sent requests
                            }
                        }
                    }
                }
            }
            System.out.println("Requests sent successfully retrieved from " + requestSentFile);
        }catch (FileNotFoundException e) {
            System.out.println(requestSentFile + " not found. Starting with empty pending requests.");
        }
        catch (IOException e) {
            System.err.println("Error retrieving requests sent from file: " + e.getMessage());
        }

        // Load Posts
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 24-hour format

        try (BufferedReader reader = new BufferedReader(new FileReader(postsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Remove any extra spaces

                // Split the line by the first colon (:) to separate username and posts
                String[] parts = line.split(":", 2); // Limit split to 2 parts
                if (parts.length == 2) {
                    String username = parts[0].trim(); // Extract the username
                    String[] postData = parts[1].split(";"); // Split by semicolon for multiple posts

                    User user = userHashTable.find(username); // Find the user by username
                    if (user != null) {
                        for (String postStr : postData) {
                            postStr = postStr.trim(); // Trim spaces around the post data
                            String[] postParts = postStr.split("\\|", 5); // Split into parts: ID, title, content, time, rest

                            if (postParts.length >= 5) {
                                try {
                                    // Parse post details
                                    int postId = Integer.parseInt(postParts[0].trim());
                                    String postTitle = postParts[1].trim();
                                    String postContent = postParts[2].trim();
                                    LocalDateTime timeOfPosting = LocalDateTime.parse(postParts[3].trim(), formatter);

                                    // Extract likes and comments
                                    String[] likesAndComments = postParts[4].split("\\|", 2); // Split into Likes and Comments
                                    int likeCount = Integer.parseInt(likesAndComments[0].replace("Likes:", "").trim());

                                    ArrayList<String> comments = new ArrayList<>();
                                    if (likesAndComments.length > 1) { // If comments exist
                                        String commentSection = likesAndComments[1].trim();
                                        if (!commentSection.equals("Comments:None")) {
                                            String[] commentParts = commentSection.replace("Comments:", "").split(",");
                                            for (String comment : commentParts) {
                                                comments.add(comment.replace("\\,", ",")); // Unescape commas
                                            }
                                        }
                                    }

                                    // Create and add the post to the user
                                    Post post = new Post(user, postId, postTitle, postContent, timeOfPosting, likeCount, comments);
                                    user.addPost(post);
                                } catch (Exception e) {
                                    System.out.println("Error processing post: " + e.getMessage());
                                }
                            } else {
                                System.out.println("Skipping invalid post format: " + postStr);
                            }
                        }
                    } else {
                        System.out.println("User not found: " + username);
                    }
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
            System.out.println("Posts successfully loaded from " + postsFile);
        } catch (FileNotFoundException e) {
            System.out.println(postsFile + " not found. Starting with no posts.");
        } catch (IOException e) {
            System.err.println("Error reading posts from file: " + e.getMessage());
        }


        return userHashTable;
    }


    public void signupWithId(int userId, String username, String password, String email, String dateOfBirth,String gender,Location location) {
        User user = new User(userId, username, password, email, LocalDate.parse(dateOfBirth),gender,location);
        int index = hashFunction(username,0);
        table[index] = user;
    }

    public void insert(User user) {
        int index = hashFunction(user.getUsername(),0);
        table[index]=user;
    }


}