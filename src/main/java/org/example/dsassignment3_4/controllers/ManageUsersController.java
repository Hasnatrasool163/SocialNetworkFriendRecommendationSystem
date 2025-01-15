package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.User;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


public class ManageUsersController implements Initializable {

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
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User,Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> dobColumn;
    @FXML
    private TableColumn<User, String> genderColumn;
//    @FXML
//    private TableColumn<User, String> passwordColumn;


    public void loadUserData() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        UserService userService = new UserService();

        try {
            List<User> users = userService.loadData();
            userList.addAll(users);

            usersTable.setItems(userList);

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
            genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
//            passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Failed to load user data.", Color.RED);
        }
    }


    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String dob = dobField.getValue() != null ? dobField.getValue().toString() : "";
        String gender = genderComboBox.getValue();

        // Input validations
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob.isEmpty() || gender == null) {
            setStatus("Please fill in all fields.", Color.RED);
            return;
        }

        if (password.length() < 5) {
            setStatus("Password must be at least 5 characters.", Color.RED);
            return;
        }

        if (!isUsernameUnique(username)) {
            setStatus("Username already exists.", Color.RED);
            return;
        }

        if (!isValidEmail(email)) {
            setStatus("Invalid email format.", Color.RED);
            return;
        }

        if (!isValidDOB(LocalDate.parse(dob))) {
            setStatus("You must be at least 13 years old to register.", Color.RED);
            return;
        }


        // Store user data in both tables
        try{
            Connection conn = DBConnection.getConnection();
            String userInsertQuery = "INSERT INTO users (username, password) VALUES (?,?)";
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
            loadUserData();
            clear();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> choices = FXCollections.observableArrayList("Male", "Female");
        genderComboBox.setItems(choices);
        loadUserData();
        setupTableListener();
    }

    void setupTableListener(){
        usersTable.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue)->{
            if(newValue!=null){
                statusLabel.setText("usedId: "+ newValue.getId()+" is selected!");
                usernameField.setText(newValue.getUsername());
                emailField.setText(newValue.getEmail());
                passwordField.setText(newValue.getPassword());
                dobField.setValue(LocalDate.parse(newValue.getDob().toString()));
                genderComboBox.setValue(newValue.getGender());
            }
        });
    }

    private void handleDelete() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            setStatus("Please enter a username.", Color.RED);
            return;
        }

        if (!isUsernameUnique(username)) {
            try {Connection conn = DBConnection.getConnection();
                conn.setAutoCommit(false);

                String fetchUserIdQuery = "SELECT id FROM users WHERE username = ?";
                PreparedStatement fetchStmt = conn.prepareStatement(fetchUserIdQuery);
                fetchStmt.setString(1, username);
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");

                    String deletePostLikesQuery = "DELETE FROM post_likes WHERE post_id IN (SELECT post_id FROM posts WHERE user_id = ?)";
                    PreparedStatement deletePostLikesStmt = conn.prepareStatement(deletePostLikesQuery);
                    deletePostLikesStmt.setInt(1, userId);
                    deletePostLikesStmt.executeUpdate();

                    String deleteFriendshipsQuery = "DELETE FROM friendships WHERE user1_id = ? OR user2_id = ?";
                    PreparedStatement deleteFriendshipsStmt = conn.prepareStatement(deleteFriendshipsQuery);
                    deleteFriendshipsStmt.setInt(1, userId);
                    deleteFriendshipsStmt.setInt(2, userId);
                    deleteFriendshipsStmt.executeUpdate();

                    String deletePostsQuery = "DELETE FROM posts WHERE user_id = ?";
                    PreparedStatement deletePostsStmt = conn.prepareStatement(deletePostsQuery);
                    deletePostsStmt.setInt(1, userId);
                    deletePostsStmt.executeUpdate();

                    String deleteMessageQuery = "DELETE FROM messages WHERE user_id = ? OR friend_id = ?";
                    PreparedStatement deleteMessageStmt = conn.prepareStatement(deleteMessageQuery);
                    deleteMessageStmt.setInt(1, userId);
                    deleteMessageStmt.setInt(2, userId);
                    deleteMessageStmt.executeUpdate();

                    String deleteNotificationsQuery = "DELETE FROM notifications WHERE user_id = ?";
                    PreparedStatement NotificationsStmt = conn.prepareStatement(deleteNotificationsQuery);
                    NotificationsStmt.setInt(1, userId);
                    NotificationsStmt.executeUpdate();

                    String deleteUserInfoQuery = "DELETE FROM userinfo WHERE user_id = ?";
                    PreparedStatement deleteUserInfoStmt = conn.prepareStatement(deleteUserInfoQuery);
                    deleteUserInfoStmt.setInt(1, userId);
                    deleteUserInfoStmt.executeUpdate();

                    String deleteCompleteProfileQuery = "DELETE FROM completeProfile WHERE user_id = ?";
                    PreparedStatement deleteCompleteProfileStmt = conn.prepareStatement(deleteCompleteProfileQuery);
                    deleteCompleteProfileStmt.setInt(1, userId);
                    deleteCompleteProfileStmt.executeUpdate();

                    String deleteBadgesQuery = "DELETE FROM  user_badges WHERE user_id = ?";
                    PreparedStatement deleteBadgesStmt = conn.prepareStatement(deleteBadgesQuery);
                    deleteBadgesStmt.setInt(1, userId);
                    deleteBadgesStmt.executeUpdate();

                    String deleteUserQuery = "DELETE FROM users WHERE id = ?";
                    PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery);
                    deleteUserStmt.setInt(1, userId);
                    deleteUserStmt.executeUpdate();

                    conn.commit();
                    setStatus("User deleted successfully.", Color.GREEN);
                    loadUserData();
                } else {
                    setStatus("User does not exist.", Color.RED);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                setStatus("Failed to delete user.", Color.RED);
            }
        } else {
            setStatus("User does not exist.", Color.RED);
        }
    }

    private void handleUpdate(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String dob = dobField.getValue() != null ? dobField.getValue().toString() : "";

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob.isEmpty()) {
            setStatus("Please fill in all fields.", Color.RED);
            return;
        }

            try {
                Connection conn = DBConnection.getConnection();
                String query = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, password);
                pstmt.setString(2, username);
                pstmt.executeUpdate();

                conn = DBConnection.getConnection();
                query = "SELECT id FROM users WHERE username = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                int id =0;
                if(rs.next()){
                    id = rs.getInt(1);
                }

                query = "UPDATE userinfo SET email = ?, dob = ? WHERE user_id=?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, email);
                pstmt.setDate(2, Date.valueOf(dob));
                pstmt.setInt(3,id);
                pstmt.executeUpdate();

                setStatus("User updated successfully.", Color.GREEN);
                UtilityMethods.showPopup("User Updated Successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                setStatus("Failed to update user.", Color.RED);
            }

    }

    public void updateUserRecord(){
        handleUpdate();
    }
    public void deleteUserRecord(){
        handleDelete();
    }

    public void clear(){
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        dobField.getEditor().clear();
        statusLabel.setText("");
    }

    public void addUserRecord(){
        handleRegister();
    }

}
