package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.service.SessionManager;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageController {

    @FXML
    private Label friendNameLabel;
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextArea messageTextArea;
    @FXML
    private Button sendButton;

    private int userId = SessionManager.getInstance().getUserId();
    private int friendId;

    private final ObservableList<String> messages = FXCollections.observableArrayList();

    public void initialize() {
        messageListView.setItems(messages);
        sendButton.setOnAction(e -> sendMessage());
        loadRecentMessages();
    }


    public void loadChat(int friendId, String friendName) {
        this.friendId = friendId;
        friendNameLabel.setText(friendName);
        //  last 5 messages
        loadRecentMessages();
    }


    private void loadRecentMessages() {
        messages.clear();
        try {
            Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT content, time FROM messages WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?) ORDER BY time DESC LIMIT 10");

            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String content = rs.getString("content");
                Timestamp time = rs.getTimestamp("time");

                messages.add(formatMessage(content, time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String formatMessage(String content, Timestamp time) {
        String formattedTime = time.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return String.format("[%s] %s", formattedTime, content);
    }


    private void sendMessage() {
        String messageContent = messageTextArea.getText().trim();
        if (messageContent.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Message cannot be empty!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO messages (user_id, friend_id, content, time) VALUES (?, ?, ?, ?)");

            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setString(3, messageContent);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                messages.add(0, formatMessage(messageContent, Timestamp.valueOf(LocalDateTime.now())));
                addNotification(friendId, "You have a new message: " + messageContent);
                messageTextArea.clear();
                loadRecentMessages();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNotification(int recipientId, String content) {
        String insertNotification = "INSERT INTO notifications (user_id, sender_id, type, content) VALUES (?, ?, ?, ?)";
        try {Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertNotification);
            statement.setInt(1, recipientId);
            statement.setInt(2, userId);
            statement.setString(3, "message");
            statement.setString(4, content);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
