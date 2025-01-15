package org.example.dsassignment3_4.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mariadb://localhost:3306/social_network";
    private static final String USER = "test_user";
    private static final String PASSWORD = "your_password";
    private static Connection connection;

    // Method to make  connection
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }

    private DBConnection() {
    }
}
