package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.dsassignment3_4.dao.UserDAO;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.SQLException;
import java.util.Objects;

public class FriendsListController {

    @FXML
    private ListView<String> friendsListView;

    @FXML
    public void initialize() {
        FriendsService friendsService = new FriendsService();
        ObservableList<String> friendsList = FXCollections.observableArrayList(
                friendsService.getFriends(SessionManager.getInstance().getUsername()));
        friendsListView.setItems(friendsList);

        friendsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    private final Label nameLabel = new Label();
                    private final ImageView avatar = new ImageView();
                    private final Button unfriendButton = new Button("Unfriend");
                    private final Button viewProfileButton = new Button("Profile");
                    private final HBox content = new HBox();

                    {
                        avatar.setFitHeight(40);
                        avatar.setFitWidth(40);
                        unfriendButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
                        unfriendButton.setOnAction(event -> {
                            String friend = getItem();
                            if (friend != null) {
                                showConfirmationDialog(friend);
                            }
                        });
                        viewProfileButton.setOnAction(event -> {
                            String friend = getItem();
                            openUserProfile(getUsernameById(friend));
                        });

                        HBox.setHgrow(unfriendButton, Priority.ALWAYS);
                        content.getChildren().addAll(unfriendButton,viewProfileButton,avatar,nameLabel);
                        content.setSpacing(10);
                    }

                    @Override
                    protected void updateItem(String friend, boolean empty) {
                        super.updateItem(friend, empty);
                        if (empty || friend == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            int avatarId = UserDAO.getUserAvatar(getUsernameById(friend));
                            Image avatarImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/" + avatarId + ".png")));
                            avatar.setImage(avatarImage);
                            nameLabel.setText(friend);
                            setGraphic(content);
                        }
                    }

                };
            }
        });
    }

    private void showConfirmationDialog(String friend) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Unfriend Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to unfriend " + friend + "?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                friendsListView.getItems().remove(friend);
                FriendsService.unfriendUser(friend);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Unfriended");
                alert.setHeaderText(null);
                alert.setContentText(friend + " has been removed from your friend list.");
                alert.showAndWait();
            }
        });
    }

    private void openUserProfile(int userId) {
        Stage stage = (Stage) friendsListView.getScene().getWindow();
        stage.close();
        UtilityMethods.openUserProfile(userId);
    }

    int getUsernameById(String username){
        return UserService.loadUserId(username);
    }
}
