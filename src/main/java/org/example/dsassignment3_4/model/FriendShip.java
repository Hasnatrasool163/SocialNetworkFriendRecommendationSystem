package org.example.dsassignment3_4.model;

public class FriendShip {

    private int id;
    private String username;
    private int user1Id;
    private int user2Id;
    private String username2;
    private double score;
    private Status status;
    private int mutualFriends;

    public FriendShip(int id, String username, String username2, double score, Status status, int mutualFriends) {
        this.id = id;
        this.username = username;
        this.username2 = username2;
        this.score = score;
        this.status = status;
        this.mutualFriends = mutualFriends;
    }

    public FriendShip(int id, int user1Id, int user2Id, double score, Status status, int mutualFriends) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.score = score;
        this.status = status;
        this.mutualFriends = mutualFriends;
    }

    public FriendShip(String friendName, int mutualFriendsCount) {
        this.username2=friendName;
        this.mutualFriends=mutualFriendsCount;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUsername2() {
        return username2;
    }

    public double getScore() {
        return score;
    }

    public Status getStatus() {
        return status;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setMutualFriends(int mutualFriends) {
        this.mutualFriends = mutualFriends;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    @Override
    public String toString() {
        return username2 ;
    }
}
