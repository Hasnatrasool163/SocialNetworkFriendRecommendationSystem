package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.controlsfx.control.Rating;

import org.example.dsassignment3_4.extras.PostCell;
import org.example.dsassignment3_4.model.Post;
import org.example.dsassignment3_4.service.PostService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Queue;
import java.util.ResourceBundle;

public class PostsController implements Initializable {

    @FXML
    private ListView<VBox> postsListView;

    int userId = SessionManager.getInstance().getUserId();
    String username = SessionManager.getInstance().getUsername();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        postsListView.setCellFactory(event -> new PostCell());
        loadPosts();
    }

    private void loadPosts() {
        Queue<Post> postQueue ;
             try {
            postQueue = fetchPostsFromDatabase(userId);
            if(postQueue.isEmpty()){
                VBox container = new VBox(new Label("No Posts Found!"));
                postsListView.getItems().add(container);
                return;
            }
            for (Post post : postQueue) {
                VBox postContainer = createPostView(post);
                postsListView.getItems().add(postContainer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createPostView(Post post) {
        VBox postContainer = new VBox();
        postContainer.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-spacing: 10;");
        postContainer.setPrefWidth(750);

        HBox postHeader = new HBox(10);
        ImageView profileImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/"+SessionManager.getInstance().getAvatar()+".png"))));
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        Label postIdLabel  = new Label(String.valueOf(post.getPostId()));
        postIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-fill: white");
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        postHeader.getChildren().addAll(profileImage,postIdLabel, usernameLabel);

        Label postContentLabel = new Label();
        postContentLabel.setWrapText(true);
        postContentLabel.setText(post.getContent());
        postContentLabel.setStyle("-fx-font-size: 16;");
        postContentLabel.setWrapText(true);

        HBox actionButtons = new HBox(10);
        Label timeLabel = new Label(post.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timeLabel.setStyle("-fx-font-size: 14;");

        Label likeCount = new Label();
        likeCount.setStyle("-fx-font-size: 16");
        likeCount.setText(String.valueOf(post.getLikes()));

        Rating rating = new Rating();
        rating.setMax(1);

        rating.setOnMouseClicked(event -> {
            double ratingValue = rating.getRating();
            System.out.println("Rating for post " + post.getPostId() + ": " + ratingValue);
        });

        actionButtons.getChildren().addAll(rating, timeLabel);

        postContainer.getChildren().addAll(postHeader, postContentLabel, actionButtons);
        ContextMenu contextMenu = new ContextMenu();
        if (post.getUserId() == userId) {
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDeletePost(post));
            contextMenu.getItems().add(deleteItem);
        }

        postContainer.setOnContextMenuRequested(event -> contextMenu.show(postContainer, event.getScreenX(), event.getScreenY()));


        return postContainer;
    }

    private static Queue<Post> fetchPostsFromDatabase(int user_id) throws SQLException {
        return PostService.fetchPostsFromDatabase(user_id);
    }

    private void handleDeletePost(Post post) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this post?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                PostService.deletePost(post.getPostId());
                postsListView.getItems().removeIf(node -> {
                    Label postIdLabel = (Label) ((HBox) node.getChildren().get(0)).getChildren().get(1);
                    return Integer.parseInt(postIdLabel.getText()) == post.getPostId();
                });
                UtilityMethods.showPopup("Post deleted successfully!");
            }
        });
    }

}
