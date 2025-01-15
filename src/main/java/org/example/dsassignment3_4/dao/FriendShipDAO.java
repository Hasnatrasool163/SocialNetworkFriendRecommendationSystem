package org.example.dsassignment3_4.dao;

import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;


import java.sql.*;


public class FriendShipDAO {

    public static boolean isAlreadyFriend(int userId, int friendId) {
        String sql = "SELECT status FROM friendships WHERE " +
                "((user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?))";

        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return status.equalsIgnoreCase("ACCEPTED") || status.equalsIgnoreCase("PENDING");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public static String getUsernameById(int userId) throws SQLException {
        String query = "SELECT username FROM users WHERE id = ?";
        try {Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    public static int getIdByUsername(String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";
        try {Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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
        int currentUserId = SessionManager.getInstance().getUserId();
        int friendId = UserService.loadUserId(friend);

        String query = "DELETE FROM friendships WHERE " +
                "(user1_id = ? AND user2_id = ?) OR " +
                "(user1_id = ? AND user2_id = ?)";
        try {Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentUserId);
            preparedStatement.setInt(2, friendId);
            preparedStatement.setInt(3, friendId);
            preparedStatement.setInt(4, currentUserId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
