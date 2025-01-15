package org.example.dsassignment3_4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


public class Main extends Application {


    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/views/splash.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(String.valueOf(getClass().getResource("/css/GlobalStyles.css")));
            stage.setTitle("Friend Connections Graph");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
