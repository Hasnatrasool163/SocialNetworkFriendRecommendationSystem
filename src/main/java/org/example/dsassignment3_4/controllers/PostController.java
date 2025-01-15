package org.example.dsassignment3_4.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.Post;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.*;
import java.time.LocalDateTime;

public class PostController {
    @FXML
    private TableView<Post> postTable;
    @FXML
    private TableColumn<Post, Integer> colPostId;
    @FXML
    private TableColumn<Post, Integer> colUserId;
    @FXML
    private TableColumn<Post, String> colContent;
    @FXML
    private TableColumn<Post, LocalDateTime> colTimestamp;
    @FXML
    private TableColumn<Post, Integer> colLikes;

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnRefresh;

    private ObservableList<Post> postList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colPostId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPostId()));
        colUserId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getUserId()));
        colContent.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContent()));
        colTimestamp.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTimestamp()));
        colLikes.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLikes()));

        loadPosts();

        btnDelete.setOnAction(e -> deleteSelectedPost());
        btnRefresh.setOnAction(e ->{
            UtilityMethods.showPopup("Posts Refreshed!");
            loadPosts();});
    }

    private void loadPosts() {
        postList.clear();

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM posts";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int postId = rs.getInt("post_id");
                int userId = rs.getInt("user_id");
                String content = rs.getString("post_text");
                LocalDateTime timestamp = rs.getTimestamp("created_at").toLocalDateTime();
                int likes = rs.getInt("likes");

                postList.add(new Post(postId, userId, content, timestamp, likes));
            }

            postTable.setItems(postList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedPost() {
        Post selectedPost = postTable.getSelectionModel().getSelectedItem();
        if (selectedPost != null) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "DELETE FROM post_likes where post_id =?";
                String query2 = "DELETE FROM posts WHERE post_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selectedPost.getPostId());
                stmt.executeUpdate();
                stmt = conn.prepareStatement(query2);
                stmt.setInt(1, selectedPost.getPostId());
                stmt.executeUpdate();
                postList.remove(selectedPost);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Post Selected", "Please select a post to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
