package org.example.dsassignment3_4.controllers;

import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.User;
import org.example.dsassignment3_4.model.UserInfo;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FindFriendsController {

    @FXML
    private JFXCheckBox ageGroupCheckBox;

    @FXML
    private JFXCheckBox locationCheckBox;

    @FXML
    private JFXCheckBox hobbiesCheckBox;

    @FXML
    private JFXCheckBox educationCheckBox;

    @FXML
    private Button findButton;

    @FXML
    private ListView<Label> friendsListView;

    private ObservableList<Label> filteredUsersList = FXCollections.observableArrayList();
    List<UserInfo> users;


    @FXML
    public void initialize() {
        friendsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        friendsListView.setStyle(".list-cell:hover; -fx-background-color: #f0f0f0;");

    }


    @FXML
    private void handleFindButtonClick(ActionEvent event) {
        filteredUsersList.clear();
        users = fetchFilteredUsers();

        for (User user : users) {

                Label userLabel = new Label(user.getUsername());
                userLabel.setFont(new Font(16));
                filteredUsersList.add(userLabel);
        }

        friendsListView.setItems(filteredUsersList);
    }


    private List<UserInfo> fetchFilteredUsers() {
        List<UserInfo> users = new ArrayList<>();
        String query = buildQuery();

        try {
            ResultSet resultSet = getResultSet(query);
            if (resultSet == null) {
                UtilityMethods.showPopupWarning("First Complete Your profile!");
                return users;
            }
            while (resultSet.next()) {
                UserInfo user = new UserInfo(
                        resultSet.getString("username"),
                        resultSet.getInt("age"),
                        resultSet.getString("location"),
                        resultSet.getString("education"),
                        resultSet.getString("hobbies")
                );
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    private ResultSet getResultSet(String query) throws SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        UserInfo currentUser = getCurrentUserDetails();
        if (currentUser == null) {
            return null;
        }

        int paramIndex = 1;
        if (ageGroupCheckBox.isSelected()) {
            preparedStatement.setInt(paramIndex++, currentUser.getAge());
        }
        if (locationCheckBox.isSelected()) {
            preparedStatement.setString(paramIndex++, currentUser.getLocation());
        }
        if (hobbiesCheckBox.isSelected()) {
            preparedStatement.setString(paramIndex++,  "%" + currentUser.getHobbies() + "%");
        }
        if (educationCheckBox.isSelected()) {
            preparedStatement.setString(paramIndex++,  currentUser.getEducation());
        }

        return preparedStatement.executeQuery();
    }

    private String buildQuery() {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT u.username, cp.age, cp.location, cp.education, cp.hobbies " +
                        "FROM users u " +
                        "JOIN completeProfile cp ON u.id = cp.user_id " +
                        "WHERE 1=1 "
        );

        if (ageGroupCheckBox.isSelected()) {
            queryBuilder.append("AND cp.age >= ?-5 "); // 5 year gap allow
        }
        if (locationCheckBox.isSelected()) {
            queryBuilder.append("AND cp.location = ? ");
        }
        if (hobbiesCheckBox.isSelected()) {
            queryBuilder.append("AND cp.hobbies LIKE ? ");
        }
        if (educationCheckBox.isSelected()) {
            queryBuilder.append("AND cp.education = ? ");
        }

//        System.out.println(queryBuilder);
        return queryBuilder.toString();
    }

    @FXML
    void addFriend(){
        String username =friendsListView.getSelectionModel().getSelectedItem().getText();
        handleAddFriend(username);
    }

    private void handleAddFriend(String username) {
        int friendId;
        int userId = SessionManager.getInstance().getUserId();;
        friendId = getFriendsId(username);
        if(username.equals(SessionManager.getInstance().getUsername())){
            UtilityMethods.showPopupWarning("You cannot send request to yourself!");
            return;
        }
        if (FriendsService.isAlreadyFriend(userId, friendId)) {
            UtilityMethods.showPopupWarning("You are already friends with this user!");
            return;
        }
        try {Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO friendships (user1_id, user2_id, status) VALUES (?, ?, 'PENDING')");
            stmt.setInt(1, SessionManager.getInstance().getUserId());
            stmt.setInt(2, friendId);
            stmt.executeUpdate();
            UtilityMethods.showPopup("Friend Request Sent!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        int getFriendsId(String username){
            return UserService.loadUserId(username);
        }

    private UserInfo getCurrentUserDetails() {
        return UserService.getCurrentUserDetails();
    }
}
