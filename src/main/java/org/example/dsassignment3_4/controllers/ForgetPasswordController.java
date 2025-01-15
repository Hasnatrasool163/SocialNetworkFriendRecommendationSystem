package org.example.dsassignment3_4.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgetPasswordController {

    @FXML
    private Button updatePasswordButton;
    @FXML
    private TextField answer1Field;

    @FXML
    private TextField answer2Field;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Button fetchButton;
    @FXML
    private Label questionLabel1;
    @FXML
    private Label questionLabel2;
    @FXML
    private Label statusLabel;

    @FXML
    public void verifyAnswers() {
        if(usernameField.getText().isEmpty()){
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Username is empty");
            return;
        }
        if(questionLabel1.getText().isEmpty() || questionLabel2.getText().isEmpty()){

            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("No Saved Questions Found!");
            return;
        }

        try {
            int userId = getCurrentUserId(usernameField.getText());
            int question1Id = getQuestionId(questionLabel1.getText());
            int question2Id = getQuestionId(questionLabel2.getText());
            String answer1 = answer1Field.getText().trim();
            String answer2 = answer2Field.getText().trim();

            if(answer1.isEmpty() || answer2.isEmpty()){
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Fill Answers");
                return;
            }

            if (verifyAnswersInDatabase(userId, question1Id, answer1) && verifyAnswersInDatabase(userId, question2Id, answer2)) {
                newPasswordField.setEditable(true);
                updatePasswordButton.setDisable(false);
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Answers verified! You can now set a new password.");
            } else {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("One or both answers are incorrect. Please try again.");
                UtilityMethods.showPopup("One or both answers are incorrect. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("An error occurred while verifying answers.");
        }
    }



    @FXML
    public void updatePassword() {
        try {
            int userId = getCurrentUserId(usernameField.getText());
            String newPassword = newPasswordField.getText().trim();

            if (newPassword.isEmpty()) {
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Password cannot be empty.");
                return;
            }
            if(newPassword.length()<8){
                statusLabel.setTextFill(Color.RED);
                statusLabel.setText("Password cannot be less than 8 characters.");
                return;
            }

            updatePasswordInDatabase(userId, newPassword);
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Password updated successfully!");
            newPasswordField.setEditable(false);
            updatePasswordButton.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("An error occurred while updating the password.");
        }
    }



    private boolean verifyAnswersInDatabase(int userId, int questionId, String answer) {
        String query = """
        SELECT COUNT(*) AS match_count
        FROM User_Security_Questions
        WHERE user_id = ? AND question_id = ? AND answer = ?;
    """;

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            // Debug logs
            System.out.println("UserID: " + userId);
            System.out.println("QuestionID: " + questionId);
            System.out.println("Answer: " + answer);

            statement.setInt(1, userId);
            statement.setInt(2, questionId);
            statement.setString(3, answer);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("match_count");
                System.out.println("Match count: " + count);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    private void updatePasswordInDatabase(int userId, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE id = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newPassword);
            statement.setInt(2, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                UtilityMethods.showPopup("Password Updated Successfully!");
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                stage.close();
                System.out.println("Password updated successfully.");
            } else {
                System.out.println("Failed to update the password.");
                UtilityMethods.showPopup("Failed to update the password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getQuestionId(String question){
        try{
            Connection conn = DBConnection.getConnection();
            String sql = "select question_id from Security_Questions where question_text = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,question);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return rs.getInt("question_id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }


    private int getCurrentUserId(String username) {
        int userId = -1;
        String query = "SELECT id FROM users WHERE username = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }


    public void handleFetchQuestions(){
        if(usernameField.getText().isEmpty()){
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Username is empty.");
            return;
        }
        fetchQuestions(usernameField.getText(),questionLabel1,questionLabel2);
    }

    private void fetchQuestions(String username, Label questionLabel1, Label questionLabel2) {
        String query = """
        SELECT sq.question_text 
        FROM User_Security_Questions usq
        JOIN Security_Questions sq ON usq.question_id = sq.question_id
        JOIN users u ON usq.user_id = u.id
        WHERE u.username = ?;
    """;

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                String questionText = resultSet.getString("question_text");
                if (count == 0) {
                    questionLabel1.setText(questionText);
                } else if (count == 1) {
                    questionLabel2.setText(questionText);
                }
                count++;
            }

            // If no questions are found
            if (count == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Questions Found");
                alert.setHeaderText("Invalid Username");
                alert.setContentText("No security questions found for the provided username.");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error Fetching Questions");
            alert.setContentText("An error occurred while fetching security questions. Please try again.");
            alert.show();
        }
    }


}
