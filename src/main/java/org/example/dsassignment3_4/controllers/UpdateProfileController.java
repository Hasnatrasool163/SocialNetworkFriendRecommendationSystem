package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class UpdateProfileController implements Initializable {

    @FXML
    private ImageView profileImage;
    @FXML
    private DatePicker dobField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button updateButton;

    @FXML
    private Label messageLabel;

    private Connection conn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadUserData();
            loadProfileImage();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading data.");
        }
    }

    private void loadProfileImage() {
        int id =SessionManager.getInstance().getAvatar();
        Image avatarImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/" + id + ".png")));
        profileImage.setImage(avatarImage);
    }

    private void loadUserData() throws SQLException {
        conn = DBConnection.getConnection();

        String query = """
            SELECT u.id AS userID, u.username, u.password, ui.email, ui.dob, ui.gender
            FROM users u
            INNER JOIN userinfo ui ON u.id = ? and ui.user_id= ?;
        """;

        PreparedStatement preparedStatement = conn.prepareStatement(query);
        int user_id = SessionManager.getInstance().getUserId();
        preparedStatement.setInt(1,user_id);
        preparedStatement.setInt(2,user_id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            usernameField.setText(resultSet.getString("username"));
            emailField.setText(resultSet.getString("email"));
            dobField.setValue(resultSet.getDate("dob").toLocalDate());
            genderComboBox.setValue(resultSet.getString("gender"));
            passwordField.setText(resultSet.getString("password"));
        }
    }

    @FXML
    private void handleUpdate() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String dob = dobField.getValue() != null ? dobField.getValue().toString() : "";

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob.isEmpty()) {
            messageLabel.setText("All fields must be filled.");
            return;
        }

        try {
            // Password update
            String query = "UPDATE users SET password =? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, password);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

            // Get user ID for updating userinfo table
            query = "SELECT id FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
            }

            // Update userinfo table
            query = "UPDATE userinfo SET email = ? , dob = ?, gender = ? WHERE user_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setDate(2, Date.valueOf(dob));
            pstmt.setString(3, genderComboBox.getValue());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();

            messageLabel.setText("Profile updated successfully!");
            UtilityMethods.showPopup("Profile updated sucessfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error updating profile.");
        }
    }

    @FXML
    private void handleDelete() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            messageLabel.setText("Username is required for deletion.");
            return;
        }

        try {
            String query = "DELETE FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.executeUpdate();

            messageLabel.setText("User deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error deleting user.");
        }
    }

}
