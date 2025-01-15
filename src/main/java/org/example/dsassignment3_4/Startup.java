package org.example.dsassignment3_4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Startup extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/graph_view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/css/GlobalStyles.css")));
        stage.setTitle("Social Network Friends Recommendation System");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}