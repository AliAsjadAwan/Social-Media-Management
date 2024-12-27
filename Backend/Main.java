package Backend;

import GUI.*;
import java.util.Scanner;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        final String USERS_FILE_NAME = "Backend/users.txt";
        final String CONNECTION_FILE_NAME = "Backend/connections.txt";
        final String PENDING_REQUEST_FILE_NAME = "Backend/pending_Requests.txt";
        final String REQUEST_SENT_FILE_NAME = "Backend/request_sent.txt";
        final String POSTS_FILE_NAME = "Backend/posts.txt";

        final int HASH_TABLE_SIZE = 100;

        // Load the hash table from the file
        UserHashTable userHashTable = UserHashTable.loadFromFiles(USERS_FILE_NAME, CONNECTION_FILE_NAME, PENDING_REQUEST_FILE_NAME,REQUEST_SENT_FILE_NAME, POSTS_FILE_NAME, HASH_TABLE_SIZE);

        new LoginPage(userHashTable);

        Scanner scanner = new Scanner(System.in);
        User currentUser = null;

        while (true) {
            if (currentUser == null) {
                // If no user is logged in, show the login/signup menu
                System.out.println("\n========== User Management System ==========");
                System.out.println("1. Signup");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                int choice = getIntInput(scanner);
                scanner.nextLine();
                switch (choice) {
                    case 1: // Signup
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();

                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();

                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();

                        System.out.print("Enter date of birth (YYYY-MM-DD): ");
                        String dateOfBirth = scanner.nextLine();

                        System.out.print("Enter Gender:");
                        String gender = scanner.nextLine();

                        System.out.print("Enter Your City: ");
                        String city = scanner.nextLine();

                        System.out.print("Enter Your Country: ");
                        String country = scanner.nextLine();

                        Location location=new Location(city,country);
                        userHashTable.signup(username, password, email, dateOfBirth, gender, location);
                        break;

                    case 2: // Login
                        System.out.print("Enter username: ");
                        String loginUsername = scanner.nextLine();

                        System.out.print("Enter password: ");
                        String loginPassword = scanner.nextLine();

                        currentUser = userHashTable.login(loginUsername, loginPassword);
                        if (currentUser != null) {
                            userFeatureMenu(currentUser, userHashTable);
                            currentUser = null;
                        }
                        break;

                    case 3: // Exit
                        userHashTable.writeUsersToFile(USERS_FILE_NAME);
                        userHashTable.writeConnectionsToFile(CONNECTION_FILE_NAME);
                        userHashTable.writePendingRequestsToFile(PENDING_REQUEST_FILE_NAME);
                        userHashTable.writeRequestsSentToFile(REQUEST_SENT_FILE_NAME);
                        userHashTable.writePostsToFile(POSTS_FILE_NAME);

                        System.out.println("Exiting the program. Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void userFeatureMenu(User user, UserHashTable userHashTable) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n========== User Features ==========");
            System.out.println("1.  View Profile");
            System.out.println("2.  View Connections");
            System.out.println("3.  View Mutual Connections");
            System.out.println("4.  View Suggested Connections");
            System.out.println("5.  Send Connection Request");
            System.out.println("6.  Remove Connection");
            System.out.println("7.  Accept Connection Request");
            System.out.println("8.  Reject Connection Request");
            System.out.println("9.  View Pending Requests");
            System.out.println("10. Add Post");
            System.out.println("11. View Posts");
            System.out.println("12. Delete Post");
            System.out.println("13. Display Friend's posts");
            System.out.println("14. Display Activity");
            System.out.println("15. Edit Profile");
            System.out.println("16. Logout");

            int choice = getIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1: // View Profile
                    System.out.println("\n========== User Profile ==========");
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Email: " + user.getEmail());
                    System.out.println("Date of Birth: " + user.getDateOfBirth());
                    System.out.println("Bio: " + user.getBio());
                    break;

                case 2: // View Connections
                    System.out.println("Your connections: ");
                    for (User connection : user.getConnections()) {
                        System.out.println(connection.getUsername());
                    }
                    break;

                case 3: // View Mutual Connections
                    System.out.print("Enter the username to view mutual connections: ");
                    String mutualUsername = scanner.nextLine();
                    User mutualUser = UserHashTable.find(mutualUsername);
                    if (mutualUser != null) {
                        user.getMutualConnections(mutualUser);
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case 4: // View Suggested Connections
                    user.getSuggestedConnections();
                    System.out.println("Enter the username you want to connect with: ");
                    String suggestedUsername = scanner.nextLine();
                    User suggestedUser = UserHashTable.find(suggestedUsername);
                    if (suggestedUser != null) {
                        user.sendConnectionRequest(suggestedUser);
                    }
                    else {
                        System.out.println("User not found.");
                    }
                    break;

                case 5: // Send Connection Request
                    userHashTable.displayTable();
                    System.out.print("Enter the username of the person you want to connect with: ");
                    String connectionUsername = scanner.nextLine();
                    User connectionUser = UserHashTable.find(connectionUsername); // Retrieve User object
                    if (connectionUser != null) {
                        user.sendConnectionRequest(connectionUser);
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case 6: // Remove connection
                    user.displayConnections();
                    System.out.print("Enter the username of the person you want to remove: ");
                    String removeUsername = scanner.nextLine();
                    User removeUser = UserHashTable.find(removeUsername);
                    if (removeUser != null) {
                        user.removeConnection(removeUser);
                    }else {
                        System.out.println("User not found.");
                    }
                    break;
                case 7: // Accept Connection Request
                    user.displayPendingRequests();
                    System.out.print("Enter the username of the request you want to accept: ");
                    String acceptUsername = scanner.nextLine();
                    User acceptUser = UserHashTable.find(acceptUsername); // Retrieve User object
                    if (acceptUser != null) {
                        user.acceptConnectionRequest(acceptUser);
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case 8: // Reject Connection Request
                    user.displayPendingRequests();
                    System.out.print("Enter the username of the request you want to reject: ");
                    String rejectUsername = scanner.nextLine();
                    User rejectUser = UserHashTable.find(rejectUsername); // Retrieve User object
                    if (rejectUser != null) {
                        user.rejectConnectionRequest(rejectUser);
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case 9: // View Pending Requests
                    user.displayPendingRequests();
                    break;

                case 10: // Add Post
                    System.out.print("Enter Title of post: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Content of your post: ");
                    String content = scanner.nextLine();
                    user.addPost(new Post(user,title, content));
                    break;

                case 11: // View Posts
                    user.displayPosts();
                    break;

                case 12: // Delete Post
                    System.out.print("Enter the post number to delete: ");
                    int postId = getIntInput(scanner); // Safely get post ID
                    user.deletePost(postId);
                    break;

                case 13:
                    user.displayConnectedUsersPosts();
                    break;

                case 14:
                    user.displayActivityLog();
                    break;
                case 15:
                    user.editProfile();
                    break;
                case 16: // Logout
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static int getIntInput(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }
    }
}
