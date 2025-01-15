package org.example.dsassignment3_4.model;

import java.util.*;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Date  dob;
    private String gender;
    private double score;

    private String status;
    private Set<User> friends;
    private Queue<Post> postsQueue;

    public User(String username){
        this.username=username;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
        this.status = "";
        this.friends = new HashSet<>();
        this.postsQueue = new LinkedList<>();
        this.score=0;
    }

    public User(int id, String username, String email, String password, Date dob, String gender) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.friends = new HashSet<>();
        this.postsQueue = new LinkedList<>();
        this.score=0;
    }

    public User(String username, double score) {
        this.username = username;
        this.score=score;
    }

    public User(int id, String gender, String email, Date dob) {
        this.id = id;
        this.gender = gender;
        this.email = email;
        this.dob = dob;
    }

    public User(){

    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail(){return email;}
    public String getPassword() {return password;}
    public Date getDob() {return dob;}
    public String getGender() {return gender;}
    public String getStatus() { return status; }
    public double getScore(){return score;}
    public void setStatus(String status) { this.status = status; }
    public Set<User> getFriends() { return friends; }
    public Queue<Post> getPostsQueue() { return postsQueue; }

    // Add a post to the postsQueue
    public void addPost(Post post) {
        postsQueue.add(post);
    }

    @Override
    public String toString() {
        return  username ;
    }
}
