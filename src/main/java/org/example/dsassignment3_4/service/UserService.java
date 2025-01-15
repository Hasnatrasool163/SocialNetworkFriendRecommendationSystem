package org.example.dsassignment3_4.service;

import javafx.collections.ObservableList;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.dao.NotificationDao;
import org.example.dsassignment3_4.dao.UserDAO;
import org.example.dsassignment3_4.model.User;
import org.example.dsassignment3_4.model.UserInfo;
import org.example.dsassignment3_4.model.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    public  List<User> loadData() throws SQLException {
        return UserDAO.loadData();
    }

    public ObservableList<Integer> loadUserIds() throws SQLException{
        return UserDAO.loadUserIds();
    }

    public static int loadUserId(String username) {
        return UserDAO.loadUserId(username);
    }

    public static String loadUsername(int id) {
        return UserDAO.loadUsername(id);
    }

    public static void handleOnlineStatus(int userId){
        UserDAO.handleOnlineStatus(userId);
    }

    public static void markNotificationAsRead(int userId,String content) {
            NotificationDao.markNotificationAsRead(userId,content);
    }

    public static boolean checkNotificationStatus(int userId) {
       return NotificationDao.checkNotificationStatus(userId);
    }


    public static UserProfile loadProfileDetails(int userId) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = """
            SELECT u.email, u.dob, u.gender, c.location
            FROM userinfo u
            LEFT JOIN completeProfile c ON u.user_id = c.user_id
            WHERE u.user_id = ?
        """;

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            UserProfile    userProfile ;
            if (rs.next()) {
                String email = rs.getString("email");
                String dob = rs.getString("dob");
                String gender = rs.getString("gender");
                String location = rs.getString("location");
                userProfile = new UserProfile(email,dob,gender,location);
                return userProfile;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching profile details: " + e.getMessage());
        }
        return null;
    }

    public static UserInfo getCurrentUserDetails() {
        int currentUserId = SessionManager.getInstance().getUserId();
        try {Connection conn = DBConnection.getConnection();
            String query = "SELECT cp.age, cp.location, cp.education, cp.hobbies " +
                    "FROM completeProfile cp WHERE cp.user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserInfo(
                        rs.getInt("age"),
                        rs.getString("location"),
                        rs.getString("education"),
                        rs.getString("hobbies")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
