package org.example.dsassignment3_4.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.dsassignment3_4.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserDAO {

    public static int getUserAvatar(int userId) {
        String query = "SELECT avatar FROM completeProfile WHERE user_id = ?";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("avatar");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void updateUserAvatar(int id, int avatar) throws SQLException {
        System.out.println("updating avatar for " + id +"user "+ avatar +"avatar id");
        String query = "UPDATE completeProfile SET avatar = ? WHERE user_id = ?";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, avatar);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadData() throws SQLException {
        Connection connection = DBConnection.getConnection();
        String query = """
            SELECT u.id AS userID, u.username, u.password, ui.email, ui.dob, ui.gender
            FROM users u
            INNER JOIN userinfo ui ON u.id = ui.user_id
            """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            int userID = resultSet.getInt("userID");
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            Date dob = resultSet.getDate("dob");
            String gender = resultSet.getString("gender");

            User user = new User(userID, username, email, password, dob, gender);
            users.add(user);
        }
        return users;
    }

    public static ObservableList<Integer> loadUserIds() throws SQLException{
        Connection connection = DBConnection.getConnection();
        String query = """ 
                SELECT id FROM users """;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        ObservableList<Integer> ids = FXCollections.observableArrayList();
        while (resultSet.next()) {
            int userID = resultSet.getInt("id");
            ids.add(userID);
        }
        return ids;
    }

    public static int loadUserId(String username) {
        Connection connection = DBConnection.getConnection();
        String query = "SELECT id FROM users where username= ? ";

        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public static String loadUsername(int id) {
        Connection connection = DBConnection.getConnection();
        String query = "SELECT username FROM users where id= ?";

        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public static void handleOnlineStatus(int userId){
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "update users set is_online = ? where user_id =?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,1);
            stmt.setInt(2,userId);
            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

}
