package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;


import java.sql.SQLException;

public class FriendRequestsController {

    @FXML
    private ListView<String> friendRequestsListView;

    @FXML
    public void initialize() throws SQLException {
        FriendsService friendsService = new FriendsService();
        int currentUserId = SessionManager.getInstance().getUserId();
        
        ObservableList<String> friendRequestsList = FXCollections.observableArrayList(
                friendsService.getFriendRequests(currentUserId)
        );
        friendRequestsListView.setItems(friendRequestsList);

        friendRequestsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    private final Button acceptButton = new Button("Accept");
                    private final Button declineButton = new Button("Decline");
                    private final HBox content = new HBox();

                    {
                        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
                        acceptButton.setOnAction(event -> {
                            String requestSender = getItem();
                            if (requestSender != null) {
                                acceptRequest(requestSender);
                            }
                        });

                        declineButton.setStyle("-fx-background-color: #FF6347; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
                        declineButton.setOnAction(event -> {
                            String requestSender = getItem();
                            if (requestSender != null) {
                                declineRequest(requestSender);
                            }
                        });

                        HBox.setHgrow(acceptButton, Priority.ALWAYS);
                        content.getChildren().addAll(acceptButton, declineButton);
                        content.setSpacing(10);
                    }

                    @Override
                    protected void updateItem(String requestSender, boolean empty) {
                        super.updateItem(requestSender, empty);
                        if (empty || requestSender == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setText(requestSender);
                            setGraphic(content);
                        }
                    }
                };
            }
        });
    }

    private void acceptRequest(String requestSender) {
        FriendsService friendsService = new FriendsService();
        int currentUserId = SessionManager.getInstance().getUserId();
        try {
            int senderId = UserService.loadUserId(requestSender);
            friendsService.acceptFriendRequest(currentUserId, senderId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Request Accepted");
            alert.setHeaderText(null);
            alert.setContentText(requestSender + " is now your friend!");
            alert.showAndWait();
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void declineRequest(String requestSender) {
        FriendsService friendsService = new FriendsService();
        int currentUserId = SessionManager.getInstance().getUserId();
        try {
            int senderId = UserService.loadUserId(requestSender);
            friendsService.declineFriendRequest(currentUserId, senderId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Request Declined");
            alert.setHeaderText(null);
            alert.setContentText("You have declined the friend request from " + requestSender);
            alert.showAndWait();
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
