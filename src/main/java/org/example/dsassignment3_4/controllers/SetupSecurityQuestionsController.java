package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SetupSecurityQuestionsController implements Initializable {

    @FXML
    private ComboBox<String> question1Combo;

    @FXML
    private ComboBox<String> question2Combo;

    @FXML
    private TextField answer1Field;

    @FXML
    private TextField answer2Field;

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField usernameField;
    @FXML
    private Button fetchButton;
    @FXML
    private Label questionLabel1;
    @FXML
    private Label questionLabel2;
    @FXML
    private Label statusLabel;

    private String[] questions = {
            "What's your mother's maiden name?",
            "What's your favorite childhood memory?",
            "What's the name of your first pet?",
            "What's your dream vacation spot?",
    };

    private String[] questions2 = {
            "What's your favorite book?",
            "What's your favorite movie?",
            "What's your favorite sports team?",
            "What's your favorite food?"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize
        question1Combo.setItems(FXCollections.observableArrayList(questions));
        question2Combo.setItems(FXCollections.observableArrayList(questions2));
    }

    @FXML
    public void saveQuestions() {
        int userId = getCurrentUserId();
        String answer1 = answer1Field.getText();
        String answer2 = answer2Field.getText();

        if(answer1.isEmpty() || answer2.isEmpty() || answer1.length() >50 || answer2.length()>50){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incorrect Values");
            alert.setHeaderText("Value Error");
            alert.setContentText("Fill answers correctly");
            alert.show();
            return;
        }

        int question1 =  getQuestionId(question1Combo.getSelectionModel().getSelectedItem());
        int question2 =  getQuestionId(question2Combo.getSelectionModel().getSelectedItem());

        saveToDatabase(userId, question1, answer1);
        saveToDatabase(userId, question2, answer2);
        UtilityMethods.showPopup("Questions Successfully Saved!");
    }


    private void saveToDatabase(int userId, int questionId, String answer) {
        try{
            Connection connection = DBConnection.getConnection();
            String sql = "insert into User_Security_Questions (user_id, question_id ,answer) values (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
            statement.setInt(2,questionId);
            statement.setString(3,answer);
            statement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean verifyAnswersInDatabase(int userId, String question, String answer) {
        String query = """
        SELECT COUNT(*) AS match_count
        FROM User_Security_Questions usq
        JOIN Security_Questions sq ON usq.question_id = sq.question_id
        WHERE usq.user_id = ? AND sq.question_text = ? AND usq.answer = ?;
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, question);
            statement.setString(3, answer);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("match_count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void updatePasswordInDatabase(int userId, String newPassword) {
        String query = "UPDATE users SET password = SHA2(?, 256) WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newPassword);
            statement.setInt(2, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password updated successfully.");
            } else {
                System.out.println("Failed to update the password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void submit(ActionEvent event) {
        saveQuestions();
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

    private int getCurrentUserId() {
        return SessionManager.getInstance().getUserId();
    }


    private void fetchQuestions(String username, Label questionLabel1, Label questionLabel2) {
        String query = """
        SELECT sq.question_text 
        FROM User_Security_Questions usq
        JOIN Security_Questions sq ON usq.question_id = sq.question_id
        JOIN users u ON usq.user_id = u.id
        WHERE u.username = ?;
    """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

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
