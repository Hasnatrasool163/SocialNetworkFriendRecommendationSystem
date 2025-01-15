package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.model.User;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardController {

    @FXML
    private ListView<User> userListView;
    @FXML
    private Canvas graphCanvas;
    @FXML
    private Button showConnectionsButton;

    private Map<String, String[]> userConnections = new HashMap<>();

    public void initialize() throws SQLException {
        FriendsService friendshipService = new FriendsService();
        userConnections = friendshipService.loadFriendConnections();
        UserService userService = new UserService();
        ObservableList<User> users = FXCollections.observableArrayList(userService.loadData());
        userListView.setItems(users);

        showConnectionsButton.setOnAction(this::showConnections);
    }

    private void showConnections(ActionEvent event) {
        if(userListView.getSelectionModel().getSelectedItem() ==null){
            return;
        }
        String selectedUser = userListView.getSelectionModel().getSelectedItem().toString();
        if (selectedUser == null) {
            System.out.println("No user selected.");
            return;
        }

        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        drawUser(gc, selectedUser, 250, 200);

        String[] friends = userConnections.get(selectedUser);
        System.out.println(Arrays.toString(friends));
        if (friends != null) {
            double angleIncrement = 360.0 / friends.length;

            for (int i = 0; i < friends.length; i++) {

                double angle = Math.toRadians(i * angleIncrement);
                double radius = 150;
                double friendX = 250 + radius * Math.cos(angle);
                double friendY = 200 + radius * Math.sin(angle);

                drawUser(gc, friends[i], friendX, friendY);

                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeLine(250, 200, friendX, friendY);
            }
        }
    }

    private void drawUser(GraphicsContext gc, String user, double x, double y) {
        gc.setFill(Color.ORANGE);
        gc.fillOval(x - 20, y - 20, 40, 40);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(16));
        gc.fillText(user, x - 20, y + 30);
    }
}
