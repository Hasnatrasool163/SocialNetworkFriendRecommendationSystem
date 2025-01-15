package org.example.dsassignment3_4.utilities;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.example.dsassignment3_4.controllers.ViewProfileController;
import org.example.dsassignment3_4.service.SessionManager;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Properties;

import java.util.regex.Pattern;

public class UtilityMethods {

    String stylesheet;
    private static final String CONFIG_FILE = "/properties/config.properties";

    public static void switchToScene(Node node, String fxmlFile) {
        try {
            Stage stage = (Stage) node.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(UtilityMethods.class.getResource("/views/"+fxmlFile+".fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
            stage.setTitle("Friend Connections Graph");
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(event -> {
                System.exit(0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchToScene(String fxmlFile) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(UtilityMethods.class.getResource("/views/"+fxmlFile+".fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
            stage.setTitle("Friend Connections Graph");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStatus(Label statusLabel ,String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }


    // check email format
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    //  check DOB
    public static boolean isValidDOB(LocalDate dob) {
        LocalDate today = LocalDate.now();
        if (dob.isAfter(today)) {
            return false;
        }
        int age = Period.between(dob, today).getYears();
        return age >= 13;
    }

    public static void showAlert(Alert.AlertType type, String title , String headerText , String context){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(context);
        alert.show();
    }

    public static int getCurrentUsersId(){
        return SessionManager.getInstance().getUserId();
    }

    public static String getCurrentUsername(){
        return SessionManager.getInstance().getUsername();
    }


    public static void showPopup(String message) {
        Notifications.create()
                .title("Notification")
                .text(message)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(javafx.util.Duration.seconds(1.5))
                .darkStyle()
                .hideCloseButton()
                .showInformation();
    }

    public static void showPopupWarning(String message) {
        Notifications.create()
                .title("Warning")
                .text(message)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(javafx.util.Duration.seconds(1.5))
                .darkStyle()
                .hideCloseButton()
                .showError();
    }

    public static void openUserProfile(int userId) {

        try {
            FXMLLoader loader = new FXMLLoader(UtilityMethods.class.getResource("/views/ViewProfile.fxml"));
            Parent root = loader.load();

            ViewProfileController controller = loader.getController();
            controller.loadProfileDetails(userId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(Objects.requireNonNull(getClass().getResource(CONFIG_FILE)).getFile())) {
            properties.load(input);
            stylesheet = properties.getProperty("stylesheet", "NightMode.css");
        } catch (IOException e) {
            System.out.println("Could not load properties file, using defaults.");
            stylesheet = "NightMode.css";
        }
    }

    public static void updateThemeInProperties(String theme) {
        String CONFIG_FILE = "/properties/config.properties";
        try {
            Properties properties = new Properties();
            InputStream input = UtilityMethods.class.getResourceAsStream(CONFIG_FILE);
            if (input != null) {
                properties.load(input);
                input.close();
            }

            properties.setProperty("stylesheet", theme);
            try (OutputStream output = new FileOutputStream(
                    Objects.requireNonNull(UtilityMethods.class.getResource(CONFIG_FILE)).getFile())) {
                properties.store(output, "Updated stylesheet theme");
            }
        } catch (IOException | NullPointerException e) {
            System.out.println("Could not update or save the properties file: " + e.getMessage());
        }
    }

}
