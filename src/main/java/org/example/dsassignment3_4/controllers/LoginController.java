package org.example.dsassignment3_4.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.dao.UserDAO;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

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
    private Button switchButton;
    @FXML
    private Label showPassword;
    @FXML
    private ImageView logoLabel;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        checkServerConnection();

        logoLabel.setOnMouseEntered(event -> logoLabel.setStyle("-fx-effect: dropshadow(three-pass-box,#6cb4cc,71.92,0.1,0.0,0.0)"));
        logoLabel.setOnMouseExited(event -> logoLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0.5, 0, 0);"));
        logoLabel.setOnMouseClicked(event -> UtilityMethods.showPopup("Welcome to FriendzClub"));

        loginButton.setOnMouseEntered(e -> applyButtonFadeEffect(loginButton, 1.0));
        loginButton.setOnMouseExited(e -> applyButtonFadeEffect(loginButton, 0.8));
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(usernameField, Validator.createEmptyValidator("This field cannot be empty"));
    }

        private void applyButtonFadeEffect(Button button, double toValue) {
            FadeTransition fade = new FadeTransition(Duration.millis(300), button);
            fade.setToValue(toValue);
            fade.play();
        }

    public void showPassword(){
        if(passwordField.getText().isEmpty() || passwordField.getText().isBlank()){
            return;
        }
        UtilityMethods.showPopup("Your Typed password : "+passwordField.getText());
    }

    private void checkServerConnection() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                statusLabel.setTextFill(Color.GREEN);
                setStatus("Connected to server.", Color.GREEN);
                UtilityMethods.showPopup("Connected to server");
            } else {
                setStatus("Failed to connect to server.", Color.RED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Error checking server connection.", Color.RED);
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();


        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please fill in all fields.", Color.RED);
            UtilityMethods.showPopupWarning("Please Fill in all Fields");
            return;
        }

        try  {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT id,is_admin FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();


            if (rs.next()) {
                if(rs.getInt("is_admin")==1){
                    setStatus("Welcome Admin.", Color.GREEN);
                    UtilityMethods.showPopup("Welcome : "+username);
                    switchScene("AdminDashboard");
                    return;
                }
                setStatus("Login successful.", Color.GREEN);
                UtilityMethods.showPopup("Welcome : "+username);
                int id = rs.getInt("id");
                SessionManager.getInstance().setUserId(id);
                SessionManager.getInstance().setUsername(username);
                SessionManager.getInstance().setStatus(1);
//                updateStatus(id);
                SessionManager.getInstance().setAvatar(UserDAO.getUserAvatar(id));
                System.out.println(username + "is logged in!"+ "has id " + id);
                switchScene("Dashboard");

            } else {
                UtilityMethods.showPopupWarning("Invalid Username or Password!");
                setStatus("Invalid username or password.", Color.RED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Database error occurred.", Color.RED);
        }
    }

    public void switchToRegisterScene() {
        UtilityMethods.switchToScene(switchButton,"Register");

    }

    void switchScene(String fxmlFile){
      UtilityMethods.switchToScene(switchButton,fxmlFile);
    }

    public void showForgetPasswordScene(){
       UtilityMethods.switchToScene("ForgetPassword");
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

    void updateStatus(int userId){
        UserService.handleOnlineStatus(userId);
    }
}
