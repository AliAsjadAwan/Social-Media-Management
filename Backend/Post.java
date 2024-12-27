package Backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Post {

    private final int postId;
    private final String postTitle;
    private final String postContent;
    private LocalDateTime timeOfPosting; // Attribute for time of posting
    private int likesCount;
    private final List<String> comments;
    private User user;
    private List<User> likedUsers;

    public Post(User user,String postTitle, String postContent) {
        this.postId = generateRandomId();
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.timeOfPosting = LocalDateTime.now();
        this.likesCount = 0;
        this.comments = new ArrayList<>();
        this.user = user;
        this.likedUsers = new ArrayList<>();
    }
    public Post(User user,int postId, String postTitle, String postContent, LocalDateTime timeOfPosting,int likeCount,ArrayList<String> comments) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.timeOfPosting = timeOfPosting;
        this.user = user;
        this.likesCount = likeCount;
        this.comments = comments;
        this.likedUsers = new ArrayList<>();
    }

    private static int generateRandomId() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }

    public void setTimeOfPosting(LocalDateTime timeOfPosting) {
        this.timeOfPosting = timeOfPosting;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public void like() {
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
            likesCount--;
        } else {
            likedUsers.add(user);
            likesCount++;
            user.recordActivity("Liked "+user+" Post");
        }
    }

    public int getPostId() {
        return postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public LocalDateTime getTimeOfPosting() {
        return timeOfPosting;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public List<String> getComments() {
        return comments;
    }

    public User getUser() {
        return user;
    }



    @Override
    public String toString() {
        // Define the desired format: 12-hour or 24-hour clock
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"); // For 12-hour clock
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"); // For 24-hour clock

        String formattedTime = timeOfPosting.format(formatter);

        return "Post{" +
                "Username=" + user.getUsername() +
                "postId=" + postId +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", timeOfPosting=" + formattedTime +
                ", likesCount=" + likesCount +
                ", comments=" + comments +
                '}';
    }

}

class Comment{

    final int commentId;
    final String content;
    final LocalDateTime timeOfComment;
    final User user;

    public Comment(String content, User user) {
        this.commentId = generateRandomId();
        this.content = content;
        this.timeOfComment = LocalDateTime.now();
        this.user = user;
    }

    private static int generateRandomId() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }

    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimeOfComment() {
        return timeOfComment;
    }

    public User getUser() {
        return user;
    }
}
