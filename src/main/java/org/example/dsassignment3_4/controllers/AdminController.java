package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Button connectionsButton;

    @FXML
    private Button postsButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button backButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button visualizeButton;


    void switchScene(String fxmlFile){
       UtilityMethods.switchToScene(fxmlFile);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        visualizeButton.setOnAction(event -> switchScene("Connections"));
        usersButton.setOnAction(event -> switchScene("ManageUsers"));
        connectionsButton.setOnAction(event -> switchScene("ManageFriends"));
        postsButton.setOnAction(event -> switchScene("ManagePosts"));
        backButton.setOnAction(event -> {switchScene("Login");
            Stage oldStage = (Stage) logoutButton.getScene().getWindow();
            oldStage.close();
        });
        logoutButton.setOnAction(event -> System.exit(0));
    }
}
