package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddFriendController {

    String username = SessionManager.getInstance().getUsername();
    @FXML
    private TextField searchUserField;

    public void handleAddFriend() {
        String searchUsername = searchUserField.getText().trim();

        if (searchUsername.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION,"Empty Field","Fill TextField","Write username first!");
            return;
        }

        if (searchUsername.equals(username)) {
            showAlert(Alert.AlertType.INFORMATION,"Not Allowed","Friend Request","Cannot sent request to itself!");
            return;
        }

        int friendId = UserService.loadUserId(searchUsername);
        if (friendId == -1) {
            showAlert(Alert.AlertType.ERROR,"No Record Found","No Record","No username found");
            return;
        }

        int currentUserId = SessionManager.getInstance().getUserId();

        if (isAlreadyFriend(currentUserId, friendId)) {
            showAlert(Alert.AlertType.INFORMATION,"Already a Friend","No change","You are already friends or check requests");
            return;
        }

        try {Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO friendships (user1_id, user2_id, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, friendId);
            stmt.setString(3, "PENDING");
            stmt.executeUpdate();
            showAlert(Alert.AlertType.CONFIRMATION,"Friend Request","Request Sent","Friend Request successfully sent");
        } catch (SQLException e) {
            System.err.println("An error occurred while sending the friend request: " + e.getMessage());
        }
    }

    private boolean isAlreadyFriend(int userId, int friendId) {
        return FriendsService.isAlreadyFriend(userId, friendId);
    }

    @FXML
    private void handleFriendRequests(){
        Stage stage = (Stage) searchUserField.getScene().getWindow();
        stage.close();
        UtilityMethods.switchToScene("FriendRequests");
    }

    void showAlert(Alert.AlertType type, String title , String headerText , String context){
        UtilityMethods.showAlert(type, title, headerText, context);
    }
}
