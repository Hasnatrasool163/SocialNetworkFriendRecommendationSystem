package org.example.dsassignment3_4.dao;

import java.sql.*;

public class BadgeDAO {

    public static void insertBadge(String description){
        String insertQuery = "INSERT INTO badges (description) VALUES (?)";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, description);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public static int getBadgeIdByDescription(String description){
        String selectQuery = "SELECT badge_id FROM badges WHERE description = ?";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, description);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("badge_id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
}
