package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.example.dsassignment3_4.dao.UserDAO;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.SQLException;
import java.util.Objects;

public class SelectAvatarController {
    @FXML
    private HBox avatarContainer;

    private int selectedAvatar = 1;

    @FXML
    public void initialize() {
        for (int i = 1; i <= 8; i++) {
            ImageView avatar = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/" + i + ".png"))));
            avatar.setFitHeight(40);
            avatar.setFitWidth(40);
            int finalI = i;
            avatar.setOnMouseClicked(event -> {
                selectedAvatar = finalI;
                        UtilityMethods.showPopup("Click on update to set this!");
                    }
            );
            avatar.setStyle("-fx-cursor: hand;");
            avatar.setOnMouseEntered(event -> avatar.setStyle(
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0, 0, 255, 0.6), 10, 0, 0, 0);" +
                            "-fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
            avatar.setOnMouseExited(event -> avatar.setStyle("-fx-cursor: hand;"));
            avatarContainer.getChildren().add(avatar);
        }
    }

    @FXML
    public void updateAvatar() {
        int id = SessionManager.getInstance().getUserId();
        UserDAO userDAO = new UserDAO();

        try {
            userDAO.updateUserAvatar(id, selectedAvatar);
            SessionManager.getInstance().setAvatar(selectedAvatar);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avatar Updated");
            alert.setContentText("Your avatar has been updated successfully!" +
                    "it will be effected on next run!.");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setContentText("An error occurred while updating your avatar.");
            alert.showAndWait();
        }
    }

}
