package org.example.dsassignment3_4.service;

import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.GraphModel;

import java.sql.*;

public class GraphManager {
    private final GraphModel graphModel;

    private GraphManager() {
        this.graphModel = new GraphModel();
        loadGraphFromDatabase(); // Load
    }

    private static final class InstanceHolder {
        private static final GraphManager instance = new GraphManager();
    }

    public static GraphManager getInstance() {
        return InstanceHolder.instance;
    }

    private void loadGraphFromDatabase() {
        String query = "SELECT user1_id, user2_id FROM friendships WHERE status = 'ACCEPTED'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int user1 = rs.getInt("user1_id");
                int user2 = rs.getInt("user2_id");
                graphModel.addEdge(user1, user2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendFriendRequest(int fromUser, int toUser) {
        String query = "INSERT INTO friendships (user1_id, user2_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fromUser);
            stmt.setInt(2, toUser);
            stmt.executeUpdate();
            System.out.println("Friend request sent from User " + fromUser + " to User " + toUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void acceptFriendRequest(int user1, int user2) {
        String query = "UPDATE friendships SET status = 'ACCEPTED' WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                graphModel.addEdge(user1, user2);
                System.out.println("Friend request accepted between User " + user1 + " and User " + user2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFriend(int user1, int user2) {
        String query = "DELETE FROM friendships WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user1);
            stmt.setInt(2, user2);
            stmt.setInt(3, user2);
            stmt.setInt(4, user1);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                graphModel.removeEdge(user1, user2);
                System.out.println("Friendship removed between User " + user1 + " and User " + user2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showFriends(int userId) {
        System.out.println("Friends of User " + userId + ": " + graphModel.getFriends(userId));
    }
}
