package org.example.dsassignment3_4.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.dsassignment3_4.dao.GraphFriendSuggestion;
import org.example.dsassignment3_4.model.SuggestedFriend;
import org.example.dsassignment3_4.model.SuggestionLevel;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.util.List;

public class RecommendedUsersController {

    @FXML
    private ListView<HBox> recommendedUsersListView = new ListView<>();

    @FXML
    public void initialize() {
        GraphFriendSuggestion friendSuggestion = new GraphFriendSuggestion();
        List<SuggestedFriend> suggestedFriends = friendSuggestion.getSuggestedFriends(SessionManager.getInstance().getUserId());
        System.out.println(suggestedFriends);
        recommendedUsersListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });

        if(suggestedFriends.isEmpty()){
            recommendedUsersListView.getItems().add(new HBox(new Label("No Recommended Users Currently")));
            recommendedUsersListView.getItems().add(new HBox(new Label("Search by Id or use find friends!")));
            return;
        }
        for (SuggestedFriend user : suggestedFriends) {
            HBox userItem = new HBox();
            userItem.setSpacing(10);
            userItem.setStyle("-fx-background-color: #34495E; -fx-padding: 10px;");
            
            Label usernameLabel = new Label(user.getUsername());
            usernameLabel.setFont(new Font(14));
            usernameLabel.setStyle("-fx-text-fill: white;");

            double score = user.getScore();
            Label matchingScoreLabel = new Label(SuggestionLevel.getLevel(score)+"");
            matchingScoreLabel.setFont(new Font(14));
            matchingScoreLabel.setStyle("-fx-text-fill: #A9DFBF;");
            
            Button addFriendButton = new Button("Add Friend");
            addFriendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            addFriendButton.setOnAction(event -> handleAddFriend(user.getUsername()));

            Button viewProfileButton = new Button("View Profile");
            viewProfileButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white;");
            viewProfileButton.setOnAction(event -> handleViewProfile(user.getId()));

            userItem.getChildren().addAll(usernameLabel, matchingScoreLabel, addFriendButton, viewProfileButton);
            
            recommendedUsersListView.getItems().add(userItem);
        }
    }

    private void openUserProfile(int userId) {
        UtilityMethods.openUserProfile(userId);
    }

    public void handleAddFriend(String username) {
        FriendsService.handleAddFriend(username);
    }

    private void handleViewProfile(int userId) {
        openUserProfile(userId);
    }

}
