package org.example.dsassignment3_4.service;

import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    public static List<User> searchUsersByUsername(String username) {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username FROM users WHERE username LIKE ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, "%" + username + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                users.add(new User(resultSet.getInt("id"),
                        resultSet.getString("username")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
