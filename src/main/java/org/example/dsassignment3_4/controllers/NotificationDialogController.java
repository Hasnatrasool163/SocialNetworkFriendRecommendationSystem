package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

public class NotificationDialogController {

    @FXML
    private VBox notificationBox;
    @FXML
    private Button markAsReadButton;

    private Stage dialogStage;
    public static boolean markAsRead;

    private final Stack<String> notificationStack = new Stack<>();

    int userId = SessionManager.getInstance().getUserId();

    public Button getMarkAsReadButton() {
        return markAsReadButton;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void closeNotification() {
        dialogStage.close();
    }

    public void handleMarkAsReadButton() {
        if (!notificationStack.isEmpty()) {
            String latestNotification = notificationStack.pop();
            markNotificationAsRead(SessionManager.getInstance().getUserId(), latestNotification);
            markAsRead = true;
            UtilityMethods.showPopup("Marked as read: " + latestNotification);
            displayNotifications();
        } else {
            UtilityMethods.showPopupWarning("No Pending Notifications");
        }
    }

    @FXML
    public void initialize() {
        loadNotifications();
        displayNotifications();
    }

    private void displayNotifications() {
        notificationBox.getChildren().clear();

        for (String message : notificationStack) {
            Label notificationLabel = new Label(message);
            notificationLabel.setStyle("-fx-font-size: 14px;");
            notificationBox.getChildren().add(notificationLabel);
        }
    }

    private void loadNotifications() {
        notificationStack.clear();
        try {
            Connection connection = DBConnection.getConnection();
            String query = "SELECT content FROM notifications WHERE user_id = ? AND status = 'unread' ORDER BY created_at DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String content = rs.getString("content");
                notificationStack.push(content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markNotificationAsRead(int userId,String content) {
        UserService.markNotificationAsRead(userId,content);
    }
}
