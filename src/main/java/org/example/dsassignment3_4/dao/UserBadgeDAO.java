package org.example.dsassignment3_4.dao;

import java.sql.*;

public class UserBadgeDAO {

    public static void awardBadgeToUser(int userId, int badgeId) {
        String insertQuery = "INSERT INTO user_badges (user_id, badge_id, status) VALUES (?, ?, TRUE)";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, userId);
            statement.setInt(2, badgeId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean userHasBadge(int userId, int badgeId) {
        String selectQuery = "SELECT status FROM user_badges WHERE user_id = ? AND badge_id = ?";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setInt(1, userId);
            statement.setInt(2, badgeId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("status");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeBadgeFromUser(int userId, int badgeId) {
        String deleteQuery = "DELETE FROM user_badges WHERE user_id = ? AND badge_id = ?";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setInt(1, userId);
            statement.setInt(2, badgeId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateBadgeStatus(int userId, int badgeId, boolean status) {
        String updateQuery = "UPDATE user_badges SET status = ? WHERE user_id = ? AND badge_id = ?";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setBoolean(1, status);
            statement.setInt(2, userId);
            statement.setInt(3, badgeId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
