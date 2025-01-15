package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.dsassignment3_4.dao.UserDAO;
import org.example.dsassignment3_4.model.UserProfile;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;

import java.util.Objects;


public class ViewProfileController {

    @FXML
    private ImageView profileImage;
    @FXML
    private Circle statusCircle;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label totalFriendsLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Button quickMessageButton;
    @FXML
    private Button addFriendButton;


    private String friendUsername ;
    private int friendId ;


    boolean is_private = SessionManager.getInstance().isIs_private();

    public void handleAddFriend() {
        FriendsService.handleAddFriend(friendUsername);
    }

    public void setStatus(boolean isOnline) {
        statusCircle.setFill(isOnline ? Color.GREEN : Color.RED);
    }

    @FXML
    public void initialize(){
        fetchTotalFriends();
        setStatus(SessionManager.getInstance().getStatus() == 1);
        quickMessageButton.setOnAction(e -> openChat());

    }

    private void loadProfileImage(int friendId) {
        int avatarId= UserDAO.getUserAvatar(friendId);
        Image avatarImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/" + avatarId + ".png")));
        profileImage.setImage(avatarImage);
    }

    private void openChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MessageView.fxml"));
            Parent chatRoot = loader.load();

            MessageController messageController = loader.getController();
            messageController.loadChat(friendId,friendUsername);

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat");
            chatStage.setScene(new Scene(chatRoot));
            chatStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        System.exit(0);

    }

    public void loadProfileDetails(int userId) {
        friendId = userId;
        if (is_private) {
            emailLabel.setText("******");
            dobLabel.setText("**/**/**");
            genderLabel.setText("******");
        } else {
            System.out.println("Searching for userid :" + userId);
            UserProfile userProfile = UserService.loadProfileDetails(userId);
            if (userProfile != null) {
                emailLabel.setText(userProfile.getEmail());
                dobLabel.setText(userProfile.getDob());
                genderLabel.setText(userProfile.getGender());
                locationLabel.setText(userProfile.getLocation());
                loadProfileImage(friendId);
            }

            friendUsername = UserService.loadUsername(friendId);
            usernameLabel.setText(friendUsername);
            fetchTotalFriends();

        }
    }

    public void fetchTotalFriends() {
        FriendsService friendsService = new FriendsService();
        totalFriendsLabel.setText(friendsService.fetchTotalFriends(friendId)+"");
    }

}
