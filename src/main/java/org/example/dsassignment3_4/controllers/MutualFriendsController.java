package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.example.dsassignment3_4.dao.GraphFriendSuggestion;
import org.example.dsassignment3_4.model.FriendShip;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class MutualFriendsController {

    @FXML
    private Label userNameLabel;
    @FXML
    private ListView<HBox> mutualFriendsListView;
    @FXML
    private Button viewMutualFriendsButton;
    @FXML
    private Button backButton;

    private final ObservableList<HBox> friendsDisplayList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> initialize());
        userNameLabel.setText(SessionManager.getInstance().getUsername());
        try {
            populateFriendsList();
        } catch (SQLException e) {
            showAlert("Error", "Unable to load friends.", Alert.AlertType.ERROR);
        }
    }

    private void populateFriendsList() throws SQLException {
        mutualFriendsListView.getItems().clear();
        FriendsService friendsService  = new FriendsService();
        List<FriendShip> friendsList = friendsService.getMutualFriends(SessionManager.getInstance().getUsername());

        mutualFriendsListView.setCellFactory(param -> new ListCell<>() {
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

        for (FriendShip friend : friendsList) {
            HBox friendItem = new HBox();
            friendItem.setSpacing(10);
            friendItem.setStyle("-fx-background-color: #34495E; -fx-padding: 10px;");

            Label friendNameLabel = new Label(friend.getUsername2());
            friendNameLabel.setFont(new Font(16));
            friendNameLabel.setStyle("-fx-text-fill: white;");

            Label mutualFriendsCountLabel = new Label(friend.getMutualFriends() + " Mutual Friends");
            mutualFriendsCountLabel.setFont(new Font(14));
            mutualFriendsCountLabel.setStyle("-fx-text-fill: #A9DFBF;");

            friendItem.getChildren().addAll(friendNameLabel, mutualFriendsCountLabel);
            friendsDisplayList.add(friendItem);
        }
        mutualFriendsListView.setItems(friendsDisplayList);
    }

    @FXML
    private void viewMutualFriends(ActionEvent event) {
        HBox selectedItem = mutualFriendsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Label friendNameLabel = (Label) selectedItem.getChildren().get(0);
            String selectedFriendName = friendNameLabel.getText();

            try {
                List<String> mutualFriends = fetchMutualFriends(selectedFriendName);
                System.out.println(mutualFriends);
                displayMutualFriends(mutualFriends);
            } catch (SQLException e) {
                showAlert("Error", "Unable to fetch mutual friends.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "No friend selected.", Alert.AlertType.WARNING);
        }
    }


    private List<String> fetchMutualFriends(String friendName) throws SQLException {
        GraphFriendSuggestion friendSuggestion =  new GraphFriendSuggestion();
        return friendSuggestion.getMutualFriends(SessionManager.getInstance().getUserId(), FriendsService.getIdByUsername(friendName));
    }

    private void displayMutualFriends(List<String> mutualFriends) {
        mutualFriendsListView.getItems().clear();
        for (String friend : mutualFriends) {
            HBox friendItem = new HBox();
            friendItem.setSpacing(10);
            friendItem.setStyle("-fx-background-color: #34495E; -fx-padding: 10px;");

            Label friendNameLabel = new Label(friend);
            friendNameLabel.setFont(new Font(16));
            friendNameLabel.setStyle("-fx-text-fill: white;");

            friendItem.getChildren().add(friendNameLabel);
            mutualFriendsListView.getItems().add(friendItem);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
