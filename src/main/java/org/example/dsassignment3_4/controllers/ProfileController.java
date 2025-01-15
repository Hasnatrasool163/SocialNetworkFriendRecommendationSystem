package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.stage.Stage;
import org.example.dsassignment3_4.model.UserProfile;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.util.Objects;

public class ProfileController {

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
    private Button visualizeButton;

    private int user_id = SessionManager.getInstance().getUserId();

    @FXML
    private Button addFriendButton;

    @FXML
    void visualizeConnections(){
      UtilityMethods.switchToScene("UserConnections");
    }


    public void setStatus(boolean isOnline) {
        statusCircle.setFill(isOnline ? Color.GREEN : Color.RED);
    }

    @FXML
    public void initialize(){
        usernameLabel.setText(SessionManager.getInstance().getUsername());
        fetchTotalFriends();
        loadProfileDetails(SessionManager.getInstance().getUserId());
        setStatus(SessionManager.getInstance().getStatus() == 1);
        loadProfileImage();

    }

    private void loadProfileImage() {
        int id =SessionManager.getInstance().getAvatar();
        Image avatarImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/" + id + ".png")));
        profileImage.setImage(avatarImage);
    }


    public void loadProfileDetails(int user_id) {
       UserProfile userProfile=  UserService.loadProfileDetails(user_id);
       if(userProfile!=null){
           emailLabel.setText(userProfile.getEmail());
           dobLabel.setText(userProfile.getDob());
           genderLabel.setText(userProfile.getGender());
           locationLabel.setText(userProfile.getLocation());
       }
    }

    @FXML
    private void editProfile() {
       Stage stage = (Stage)locationLabel.getScene().getWindow();
       stage.close();
        UtilityMethods.switchToScene("UserProfile");
    }

    @FXML
    private void logout() {
        System.exit(0);
    }

    @FXML
    private void setupSecurityQuestions(){
       UtilityMethods.switchToScene("SetupSecurityQuestions");
    }


    public void fetchTotalFriends() {
        FriendsService friendsService = new FriendsService();
        totalFriendsLabel.setText(friendsService.fetchTotalFriends(user_id)+"");
    }


}
