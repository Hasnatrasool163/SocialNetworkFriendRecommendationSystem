package org.example.dsassignment3_4.service;

import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.dao.FriendShipDAO;
import org.example.dsassignment3_4.dao.GraphFriendSuggestion;
import org.example.dsassignment3_4.model.FriendShip;

import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.*;
import java.util.*;

public class FriendsService {

    public List<String> getFriends(String username) {
        List<String> friends = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        int currentUserId = SessionManager.getInstance().getUserId();
        String query = """
            SELECT u.username 
            FROM friendships f
            JOIN users u ON (f.user1_id = u.id OR f.user2_id = u.id)
            WHERE ? IN (f.user1_id, f.user2_id)
              AND u.id != ?
              AND f.status = 'ACCEPTED'
        """;

        try {PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentUserId);
            statement.setInt(2, currentUserId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                friends.add(resultSet.getString("username"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public List<FriendShip> getMutualFriends(String username) {
        List<FriendShip> ships = new ArrayList<>();
        GraphFriendSuggestion suggestion = new GraphFriendSuggestion();
        Connection connection = DBConnection.getConnection();
        int currentUserId = SessionManager.getInstance().getUserId();
        String query = """
            SELECT u.id, u.username 
            FROM friendships f
            JOIN users u ON (f.user1_id = u.id OR f.user2_id = u.id)
            WHERE ? IN (f.user1_id, f.user2_id)
              AND u.id != ?
              AND f.status = 'ACCEPTED'
        """;

        try {PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentUserId);
            statement.setInt(2, currentUserId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
               String usernam=  resultSet.getString("username");
               int mutualFriendCount = suggestion.findMutualFriends(getIdByUsername(username),getIdByUsername(usernam));
                ships.add(new FriendShip(usernam,mutualFriendCount));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return ships;
    }

    public Map<String, String[]> loadFriendConnections() {
        Map<String, ArrayList<String>> connectionsMap = new HashMap<>();

        String query = "SELECT u1.username AS user1, u2.username AS user2 " +
                "FROM friendships f " +
                "JOIN users u1 ON f.user1_id = u1.id " +
                "JOIN users u2 ON f.user2_id = u2.id " +
                "WHERE f.status = 'ACCEPTED'";

        try {Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String user1 = rs.getString("user1");
                String user2 = rs.getString("user2");

                connectionsMap.putIfAbsent(user1, new ArrayList<>());
                connectionsMap.putIfAbsent(user2, new ArrayList<>());

                connectionsMap.get(user1).add(user2);
                connectionsMap.get(user2).add(user1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        Map<String, String[]> userConnections = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : connectionsMap.entrySet()) {
            userConnections.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }

        return userConnections;
    }

    public static void handleAddFriend(String username) {
        if (username.isBlank()) {
            return;
        }

        int friendId = UserService.loadUserId(username);
        if (friendId == -1) {
            UtilityMethods.showPopupWarning("No user Found!");
            return;
        }

        int currentUserId = SessionManager.getInstance().getUserId();

        if (isAlreadyFriend(currentUserId, friendId)) {
            UtilityMethods.showPopupWarning("You are already friends with this user or a request is pending.");
            return;
        }

        try {Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO friendships (user1_id, user2_id, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, friendId);
            stmt.setString(3, "PENDING");
            stmt.executeUpdate();
            UtilityMethods.showPopup("Friend Request send to : " +username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAlreadyFriend(int userId, int friendId) {
        return FriendShipDAO.isAlreadyFriend(userId, friendId);
    }

    public List<String> getFriendRequests(int userId) throws SQLException {
        List<String> friendRequests = new ArrayList<>();
        String query = "SELECT user1_id FROM friendships WHERE user2_id = ? AND status = 'PENDING'";

        try {Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int user1Id = rs.getInt("user1_id");
                String username = getUsernameById(user1Id);
                friendRequests.add(username);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return friendRequests;
    }

    public void acceptFriendRequest(int receiverId, int senderId) throws SQLException {
        String updateQuery = "UPDATE friendships SET status = 'ACCEPTED' WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try {Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, receiverId);
            stmt.setInt(4, senderId);
            stmt.executeUpdate();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void declineFriendRequest(int receiverId, int senderId) throws SQLException {
        String updateQuery = "UPDATE friendships SET status = 'DECLINED' WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try {Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, receiverId);
            stmt.setInt(4, senderId);
            stmt.executeUpdate();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUsernameById(int userId) throws SQLException {
        return FriendShipDAO.getUsernameById(userId);
    }


    public static int getIdByUsername(String username) throws SQLException {
        return FriendShipDAO.getIdByUsername(username);
    }

    public int fetchTotalFriends(int userId) {
        int total = 0;
        String query = "SELECT COUNT(*) AS total_friends FROM friendships WHERE status =? and user2_id =? " +
                "or status =? and user1_id= ?;";

        try {Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "ACCEPTED");
            statement.setInt(2, userId);
            statement.setString(3, "ACCEPTED");
            statement.setInt(4, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                total= resultSet.getInt("total_friends");
                return total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public static void unfriendUser(String friend)  {
        FriendShipDAO.unfriendUser(friend);
    }

}
