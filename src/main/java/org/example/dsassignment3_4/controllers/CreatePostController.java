package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.dsassignment3_4.dao.UserBadgeDAO;
import org.example.dsassignment3_4.service.PostService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

public class CreatePostController {

    @FXML private ComboBox<String> fontComboBox;
    @FXML private ComboBox<Integer> fontSizeComboBox;
    @FXML private ColorPicker textColorPicker;
    @FXML private TextArea postTextArea;
    @FXML private Button boldButton;

    private boolean isBold = false;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        if (fontComboBox != null) {
            fontComboBox.getSelectionModel().select("Arial");
        }
        if (fontSizeComboBox != null) {
            fontSizeComboBox.getSelectionModel().select(14);
        }
        if (textColorPicker != null) {
            textColorPicker.setValue(Color.BLACK);
        }

        if (fontComboBox != null) {
            fontComboBox.setOnAction(event -> updateTextStyle());
        }
        if (fontSizeComboBox != null) {
            fontSizeComboBox.setOnAction(event -> updateTextStyle());
        }
        if (textColorPicker != null) {
            textColorPicker.setOnAction(event -> updateTextColor());
        }
        if (boldButton != null) {
            boldButton.setOnAction(event -> toggleBold());
        }
    }

    private void updateTextStyle() {
        if (fontComboBox.getValue() != null && fontSizeComboBox.getValue() != null) {
            String font = fontComboBox.getValue();
            int fontSize = fontSizeComboBox.getValue();

            int start = postTextArea.getSelection().getStart();
            int end = postTextArea.getSelection().getEnd();

            if (start != end) {
                postTextArea.replaceText(start, end, postTextArea.getSelectedText());
                postTextArea.setStyle("-fx-font-family: " + font + "; -fx-font-size: " + fontSize + "px;");
            }
        }
    }

    private void updateTextColor() {
        if (textColorPicker.getValue() != null) {
            Color color = textColorPicker.getValue();
            int start = postTextArea.getSelection().getStart();
            int end = postTextArea.getSelection().getEnd();

            if (start != end) {
                postTextArea.replaceText(start, end, postTextArea.getSelectedText());
                postTextArea.setStyle("-fx-text-fill: #" + color.toString().substring(2, 8) + ";");
            }
        }
    }

    private void toggleBold() {
        isBold = !isBold;
        String boldStyle = isBold ? "-fx-font-weight: bold;" : "";

        int start = postTextArea.getSelection().getStart();
        int end = postTextArea.getSelection().getEnd();

        if (start != end) { // Text is selected
            postTextArea.replaceText(start, end, postTextArea.getSelectedText());
            postTextArea.setStyle(boldStyle);
        }
    }

    @FXML
    private void handlePostAction() {
        String postContent = postTextArea.getText().trim();
        if (!postContent.isEmpty() && dashboardController != null) {
            dashboardController.handlePostAction(postContent, SessionManager.getInstance().getUserId());
            if(PostService.checkIfFirstPost(SessionManager.getInstance().getUserId())){
                UserBadgeDAO.awardBadgeToUser(SessionManager.getInstance().getUserId(), 3);
            }
            ((Stage) postTextArea.getScene().getWindow()).close();
        } else {
            UtilityMethods.showAlert(Alert.AlertType.WARNING,"Empty post","Invalid format","Fill some content to post!");
        }
    }

    @FXML
    private void clear(){
        postTextArea.clear();
    }
}
