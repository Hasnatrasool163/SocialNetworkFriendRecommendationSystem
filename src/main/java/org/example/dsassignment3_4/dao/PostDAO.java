package org.example.dsassignment3_4.dao;

import org.example.dsassignment3_4.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class PostDAO {

    public static void savePostToDatabase(int userId, String postText) {
        String insertQuery = "INSERT INTO posts (user_id, post_text) VALUES (?, ?)";
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(insertQuery);

            statement.setInt(1, userId);
            statement.setString(2, postText);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfFirstPost(int userId) {
        int count;
        String sql = "select count(*) as count from posts where user_id =?";
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet rs =statement.executeQuery();
            if(rs.next()){
                count = rs.getInt("count");
                if(count==1){
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkIfPostDone(int userId) {
        int count;
        String sql = "select count(*) as count from posts where user_id =?";
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet rs =statement.executeQuery();
            if(rs.next()){
                count = rs.getInt("count");
                if(count>0){
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkIfPostGotTenLikes(int userId) {
        int count;
        String sql = "select count(*) as count from posts where likes >=10 and user_id=?";
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet rs =statement.executeQuery();
            if(rs.next()){
                count = rs.getInt("count");
                if(count>0){
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean savePostRating(int postId, int userId) {
        String checkQuery = "SELECT * FROM post_likes WHERE post_id = ? AND user_id = ?";
        String insertQuery = "INSERT INTO post_likes (post_id, user_id) VALUES (?, ?)";
        String updateQuery = "UPDATE posts SET likes = likes + 1 WHERE post_id = ?";

        try {Connection connection = DBConnection.getConnection();
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            checkStatement.setInt(1, postId);
            checkStatement.setInt(2, userId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                return false;
            }

            insertStatement.setInt(1, postId);
            insertStatement.setInt(2, userId);
            insertStatement.executeUpdate();

            updateStatement.setInt(1, postId);
            updateStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void deletePost(int postId) {
        String query = "DELETE FROM posts WHERE post_id = ?";
        try {Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasUserLikedPost(int postId, int userId) {
        String query = "SELECT 1 FROM post_likes WHERE post_id = ? AND user_id = ?";
        try {Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, postId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updatePostContent(int postId,String content){
        String query = "update posts set post_text= ? WHERE post_id = ?";
        try {Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, content);
            statement.setInt(2, postId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Queue<Post> fetchPostsFromDatabase()  {
        Queue<Post> posts = new LinkedList<>();
        String selectQuery = "SELECT p.post_id, p.post_text, p.likes, u.id, u.username, p.created_at " +
                "FROM posts p " +
                "JOIN users u ON p.user_id = u.id " +
                "ORDER BY p.created_at DESC";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Post post = new Post(
                        resultSet.getInt("post_id"),
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("post_text"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getInt("likes"));
                posts.add(post);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return posts;
    }

    public static Queue<Post> fetchPostsFromDatabase(int userId)  {
        Queue<Post> posts = new LinkedList<>();
        String selectQuery = "select * from posts where user_id=?";
        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setInt(1,userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Post post = new Post(
                        resultSet.getInt("post_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("post_text"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getInt("likes"));
                posts.add(post);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return posts;
    }

}
