package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.GraphModel;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private DatePicker dobField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView logoLabel;


    GraphModel graphModel ;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String dob = dobField.getValue() != null ? dobField.getValue().toString() : "";
        String gender = genderComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob.isEmpty() || gender.isBlank() || gender.isEmpty()) {
            setStatus("Please fill in all fields.", Color.RED);
            UtilityMethods.showPopupWarning("Please Fill in all Fields");
            return;
        }

        if (password.length() < 8) {
            setStatus("Password must be at least 8 characters.", Color.RED);
            UtilityMethods.showPopupWarning("Password must be at least 8 characters long");
            return;
        }

        if (!isUsernameUnique(username)) {
            setStatus("Username already exists.", Color.RED);
            UtilityMethods.showPopupWarning("Username");
            return;
        }

        if (!isValidEmail(email)) {
            setStatus("Invalid email format.", Color.RED);
            UtilityMethods.showPopupWarning("Invalid email format");
            return;
        }

        if (!isValidDOB(LocalDate.parse(dob))) {
            setStatus("You must be at least 13 years old to register.", Color.RED);
            return;
        }


        // Store user data in both tables
        try{
            Connection conn = DBConnection.getConnection();
            String userInsertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pstmt1 = conn.prepareStatement(userInsertQuery);
            pstmt1.setString(1, username);
            pstmt1.setString(2, password);
            pstmt1.executeUpdate();

            conn = DBConnection.getConnection();
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            int id =0;
            if(rs.next()){
                id = rs.getInt(1);
            }

            String userInfoInsertQuery = "INSERT INTO userinfo (user_id, email, dob, gender) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt2 = conn.prepareStatement(userInfoInsertQuery);
            pstmt2.setInt(1,id);
            pstmt2.setString(2, email);
            pstmt2.setString(3, dob);
            pstmt2.setString(4, gender);
            pstmt2.executeUpdate();

            setStatus("Registration successful.", Color.GREEN);
            graphModel = new GraphModel();
            graphModel.addVertex(id);

            switchToLoginScene();
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Registration failed due to a database error.", Color.RED);
        }
    }

    private boolean isUsernameUnique(String username) {
        try  {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT username FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Error checking username uniqueness.", Color.RED);
            return false;
        }
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

    private boolean isValidEmail(String email) {
        return UtilityMethods.isValidEmail(email);
    }

    private boolean isValidDOB(LocalDate dob) {
        return UtilityMethods.isValidDOB(dob);
    }

    @FXML
    private void switchToLoginScene() {
      UtilityMethods.switchToScene(usernameField,"Login");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> choices ;
        choices = FXCollections.observableArrayList();
        choices.add("Male");
        choices.add("Female");
        genderComboBox.setItems(choices);
        logoLabel.setOnMouseEntered(event -> logoLabel.setStyle("-fx-effect: dropshadow(three-pass-box,#6cb4cc,71.92,0.1,0.0,0.0)"));
        logoLabel.setOnMouseExited(event -> logoLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0.5, 0, 0);"));
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(usernameField, Validator.createEmptyValidator("This field cannot be empty"));

    }

    public void showPassword() {
        if(passwordField.getText().isEmpty()){
            setStatus("Password field is empty", Color.RED);
            return;
        }
        UtilityMethods.showPopup(passwordField.getText());
    }
}
