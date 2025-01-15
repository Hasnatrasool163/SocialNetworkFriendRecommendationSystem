package org.example.dsassignment3_4.model;

public class SuggestedFriend {
    private int id;
    private final String username;
    private final double score;

    public SuggestedFriend(int id, String username, double score) {
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public SuggestedFriend(String username, double score) {
        this.username = username;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return username +"-"+ score;
    }
}
