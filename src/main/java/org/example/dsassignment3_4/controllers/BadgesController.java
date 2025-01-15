package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.example.dsassignment3_4.dao.BadgeDAO;
import org.example.dsassignment3_4.dao.UserBadgeDAO;
import org.example.dsassignment3_4.service.FriendsService;
import org.example.dsassignment3_4.service.PostService;
import org.example.dsassignment3_4.service.SessionManager;

public class BadgesController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button btn1;

    @FXML
    private Button btn2;

    @FXML
    private Button btn3;

    @FXML
    private Button btn4;

    @FXML
    private Button btn5;

    @FXML
    private Button btn6;

    private PopOver popover;

    int currentUser = SessionManager.getInstance().getUserId();

    public void initialize() {
        btn1.setDisable(true);
        btn2.setDisable(true);
        btn3.setDisable(true);
        btn4.setDisable(true);
        btn5.setDisable(true);
        btn6.setDisable(true);

        loadBadges();
        addTooltips();
        initializePopover();

        FriendsService friendsService = new FriendsService();
        int totalFriends = friendsService.fetchTotalFriends(currentUser);
        if(totalFriends>=100){
            if(!UserBadgeDAO.userHasBadge(currentUser, 1)) handleAchievement1();
        }
        if(totalFriends>=50){
            if(!UserBadgeDAO.userHasBadge(currentUser, 6)) handleAchievement6();

        }
        if(totalFriends>10){
            if(!UserBadgeDAO.userHasBadge(currentUser, 5)) handleAchievement5();

        }
        if(PostService.checkIfPostDone(currentUser)){
            if(!UserBadgeDAO.userHasBadge(currentUser, 3)) {
                handleAchievement3();
                return;
            }
            btn3.setDisable(false);
        }
        if(PostService.checkIfPostGotTenLikes(currentUser)){
            if (UserBadgeDAO.userHasBadge(currentUser,2)){
                handleAchievement2();
                return;
            }
            btn2.setDisable(false);
        }
    }

    public void handleAchievement1() {
        btn1.setDisable(false);
        awardBadge("First 100 Friends");
    }

    public void handleAchievement2() {
        btn2.setDisable(false);
        awardBadge("Top Contributor");
    }

    public void handleAchievement3() {
        btn3.setDisable(false);
        awardBadge("First Post");
    }

    public void handleAchievement4() {
        btn4.setDisable(false);
        awardBadge("Complete Profile");
    }

    public void handleAchievement5() {
        btn5.setDisable(false);
        awardBadge("Friendship Master");
    }

    public void handleAchievement6() {
        btn6.setDisable(false);
        awardBadge("Community Leader");
    }

    private void loadBadges() {
        int userId = SessionManager.getInstance().getUserId();
        if (UserBadgeDAO.userHasBadge(userId, 1)) {
            btn1.setDisable(false);
        }
        if (UserBadgeDAO.userHasBadge(userId, 2)) {
            btn2.setDisable(false);
        }
        if (UserBadgeDAO.userHasBadge(userId, 3)) {
            btn3.setDisable(false);
        }
        if (UserBadgeDAO.userHasBadge(userId, 4)) {
            btn4.setDisable(false);
        }
        if (UserBadgeDAO.userHasBadge(userId, 5)) {
            btn5.setDisable(false);
        }
        if (UserBadgeDAO.userHasBadge(userId, 6)) {
            btn6.setDisable(false);
        }
    }

    public static void awardBadge(String badgeDescription) {
        int userId = SessionManager.getInstance().getUserId();
        int badgeId = BadgeDAO.getBadgeIdByDescription(badgeDescription);
        if (badgeId != -1) {
            if (!UserBadgeDAO.userHasBadge(userId, badgeId)) {
                UserBadgeDAO.awardBadgeToUser(userId, badgeId);
            }
        }
    }

    private void addTooltips() {
        Tooltip tooltip1 = new Tooltip("Awarded for getting 100 friends!");
        btn1.setTooltip(tooltip1);
        Tooltip tooltip2 = new Tooltip("Awarded for contributing a lot to the community!");
        btn2.setTooltip(tooltip2);
        Tooltip tooltip3 = new Tooltip("Awarded for making your first post!");
        btn3.setTooltip(tooltip3);
        Tooltip tooltip4 = new Tooltip("Awarded for completing your profile details!");
        btn4.setTooltip(tooltip4);
        Tooltip tooltip5 = new Tooltip("Awarded for being a true friend!");
        btn5.setTooltip(tooltip5);
        Tooltip tooltip6 = new Tooltip("Awarded for being a community leader!");
        btn6.setTooltip(tooltip6);
    }

    private void initializePopover() {
        VBox content = new VBox(150);
        content.setPrefWidth(220);
        content.setPrefHeight(210);

        popover = new PopOver(content);
        popover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        Label guide = getGuide();
        content.getChildren().add(guide);
        welcomeLabel.setOnMouseEntered(event -> popover.show(welcomeLabel));
        welcomeLabel.setOnMouseExited(event -> popover.hide());
    }

    private static Label getGuide() {
        Label guide = new Label();
        guide.setWrapText(true);
        guide.setText("""
                1)Awarded for getting 100 friends!\
                
                2)Awarded for getting 10 likes on a post!!\
                
                3)Awarded for making your first post!\
                
                4)Awarded for completing your profile details!\
                
                5)Awarded for getting 10 Friends\
                
                6)Awarded for getting 50 Friends!""");
        return guide;
    }

}
