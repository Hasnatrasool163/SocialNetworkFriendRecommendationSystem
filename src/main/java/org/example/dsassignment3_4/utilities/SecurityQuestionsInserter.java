package org.example.dsassignment3_4.utilities;

import org.example.dsassignment3_4.dao.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SecurityQuestionsInserter {

    private static String[] questions = {
            "What's your mother's maiden name?",
            "What's your favorite childhood memory?",
            "What's the name of your first pet?",
            "What's your dream vacation spot?",
    };

    private static String[] questions2 = {
            "What's your favorite book?",
            "What's your favorite movie?",
            "What's your favorite sports team?",
            "What's your favorite food?"
    };

    public static void main(String[] args) {


        String insertQuery = "INSERT INTO Security_Questions (question_text) VALUES (?)";

        try {Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            for (String question : questions) {
                preparedStatement.setString(1, question);
                preparedStatement.executeUpdate();
            }

            for (String question : questions2) {
                preparedStatement.setString(1, question);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
