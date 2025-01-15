package org.example.dsassignment3_4.model;

import java.time.LocalDateTime;

public class Post {
    private int postId;
    private int userId;
    private String username;
    private String content;
    private LocalDateTime timestamp;
    private int likes;
    private String comment;


    public Post(String content, int userId) {
        this.content = content;
        this.userId = userId;
    }

    // Constructor
    public Post(int postId, int userId, String content, LocalDateTime timestamp, int likes) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.likes = likes;
    }

    public Post(int postId, int userId, String username, String content, LocalDateTime timestamp, int likes) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
    }

    public Post(int postId, int userId, String username, String content, LocalDateTime timestamp, int likes, String comment) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    @Override
    public String toString() {
        return timestamp + " - User ID " + userId + ": " + content + " (" + likes + " likes)";
    }
}
