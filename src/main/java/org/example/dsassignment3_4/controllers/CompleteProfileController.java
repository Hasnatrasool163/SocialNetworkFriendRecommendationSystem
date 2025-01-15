package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.dao.UserBadgeDAO;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CompleteProfileController implements Initializable {

    @FXML
    private ComboBox<String> locationComboBox;
    @FXML
    private ComboBox<String> educationComboBox;
    @FXML
    private ComboBox<String> hobbiesComboBox;
    @FXML
    private ComboBox<String> ageGroupComboBox;
    @FXML
    private ComboBox<String> extrasComboBox;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button updateButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button mapButton;

    private WorldMapViewController worldMapViewController;

    int user_id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameLabel.setText(SessionManager.getInstance().getUsername());
        user_id = SessionManager.getInstance().getUserId();
        loadProfileDetails();
        ObservableList<String> choices = FXCollections.observableArrayList();
        choices.addAll("Punjab", "Sindh", "KPK", "Balochistan", "GB", "Kashmir");
        locationComboBox.setItems(choices);

        ObservableList<String> choices2 = FXCollections.observableArrayList();
        choices2.addAll("Matric", "FSC", "O-LEVEL", "A-LEVEL", "BS", "MS", "PHD", "None", "Others");
        educationComboBox.setItems(choices2);

        ObservableList<String> choices3 = FXCollections.observableArrayList();
        choices3.addAll("Video-Games", "Scrolling", "Sports", "Coding", "Reading", "Cooking", "Writing", "Drawing","Joking");
        hobbiesComboBox.setItems(choices3);

        ObservableList<String> choices4 = FXCollections.observableArrayList();
        choices4.addAll("13-16", "17-19", "20-25", "25-35", "36-55", "56-80", "80-");
        ageGroupComboBox.setItems(choices4);

        ObservableList<String> choices5 = FXCollections.observableArrayList();
        choices5.addAll("Animal-Lover", "Gardening","Food-Lover");
        extrasComboBox.setItems(choices5);

        locationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
        educationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
        hobbiesComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
        ageGroupComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
        updateProgressBar();

        usernameLabel.setOnMouseClicked(event -> handlePopup());

    }

    public void handlePopup(){
        VBox box = new VBox();
        box.setPrefHeight(120);
        box.setPrefWidth(150);
        PopOver popOver = new PopOver(box);
        popOver.setAnimated(true);
        popOver.setCloseButtonEnabled(true);
        Label guide = new Label("Select all Values To Get Correct Recommendations");
        guide.setWrapText(true);
        box.getChildren().add(guide);
        popOver.show(usernameLabel);

    }

    private void updateProgressBar() {
        double progress = 0.0;

        if (ageGroupComboBox.getSelectionModel().getSelectedItem() != null) progress += 0.2;
        if (locationComboBox.getSelectionModel().getSelectedItem() != null) progress += 0.25;
        if (hobbiesComboBox.getSelectionModel().getSelectedItem() != null) progress += 0.25;
        if (educationComboBox.getSelectionModel().getSelectedItem() != null) progress += 0.25;
        if (extrasComboBox.getSelectionModel().getSelectedItem() != null) progress += 0.05;
        progressBar.setProgress(progress);

        if(progress==1.0){
            if(!(UserBadgeDAO.userHasBadge(SessionManager.getInstance().getUserId(), 4))){
            UtilityMethods.showPopup("New Badge Unlocked! Profile 100% Completed!");
            UserBadgeDAO.awardBadgeToUser(SessionManager.getInstance().getUserId(), 4);
            }
        }
    }

    public void handleUpdateAction() {
        String ageGroup = ageGroupComboBox.getSelectionModel().getSelectedItem();
        String location = locationComboBox.getSelectionModel().getSelectedItem();
        String education = educationComboBox.getSelectionModel().getSelectedItem();
        String hobbies = hobbiesComboBox.getSelectionModel().getSelectedItem();
        String extras = extrasComboBox.getSelectionModel().getSelectedItem();

        Integer age = null;
        if (ageGroup != null && !ageGroup.isEmpty()) {
            try {
                age = Integer.parseInt(ageGroup.split("-")[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        Connection conn = DBConnection.getConnection();
        String query = "INSERT INTO completeProfile (user_id, age, location, education, hobbies, extras) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE age = ?, location = ?, education = ?, hobbies = ?, extras = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);

            //  insert
            statement.setInt(1, user_id);
            statement.setObject(2, age);
            statement.setString(3, location);
            statement.setString(4, education);
            statement.setString(5, hobbies);
            statement.setString(6, extras);

            // update
            statement.setObject(7, age);
            statement.setString(8, location);
            statement.setString(9, education);
            statement.setString(10, hobbies);
            statement.setString(11, extras);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating profile in database: " + e.getMessage());
        }
    }

    public void loadProfileDetails() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "select age , location , education , hobbies , extras from completeProfile where user_id=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, user_id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int age = rs.getInt("age");
                String education = rs.getString("education");
                String location = rs.getString("location");
                String hobbies = rs.getString("hobbies");
                String extras = rs.getString("extras");

                ageGroupComboBox.setValue(String.valueOf(age));
                educationComboBox.setValue(education);
                locationComboBox.setValue(location);
                hobbiesComboBox.setValue(hobbies);
                extrasComboBox.setValue(extras);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleMap() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WorldMapView.fxml"));
            Parent root = loader.load();

            WorldMapViewController worldMapViewController = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Choose Location");

            stage.showAndWait();

            String location1 = worldMapViewController.getSelectedLocation();

            if (location1 != null) {
                locationComboBox.setValue(location1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openAvatarSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SelectAvatar.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Select Avatar");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
