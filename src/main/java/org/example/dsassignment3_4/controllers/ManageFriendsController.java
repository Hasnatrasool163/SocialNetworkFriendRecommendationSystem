package org.example.dsassignment3_4.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.example.dsassignment3_4.dao.DBConnection;
import org.example.dsassignment3_4.model.FriendShip;
import org.example.dsassignment3_4.model.Status;
import org.example.dsassignment3_4.service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Objects;


public class ManageFriendsController {

    @FXML
        private TextField usernameField;
     @FXML
        private TextField username2Field;
    @FXML
        private ComboBox<Integer> user2ComboBox ;
    @FXML
        private ComboBox<Integer> user1ComboBox ;
     @FXML
        private ComboBox<Status> statusComboBox;

     @FXML
    private TableView<FriendShip> friendsTable;
    @FXML
    private TableColumn<FriendShip, Integer> idColumn;
     @FXML
    private TableColumn<FriendShip, String> usernameColumn;
    @FXML
    private TableColumn<FriendShip,String> usernameColumn2;
//    @FXML
//    private TableColumn<FriendShip,Double> scoreColumn;
    @FXML
    private TableColumn<FriendShip, String> statusColumn;
//    @FXML
//    private TableColumn<FriendShip,String> mutualFriendsColumn;

    private ObservableList<FriendShip> friendshipList;
    @FXML
    private Label statusLabel;


    @FXML
    public void initialize() {
        friendshipList = FXCollections.observableArrayList();
        statusComboBox.setItems(FXCollections.observableArrayList(Status.values()));
        setupTableColumn();
        loadUserIds();
        loadFriendsData();
//        setupListeners();
        setupTableListener();
        user1ComboBox.setValue(1);
    }

    void setupTableColumn(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn2.setCellValueFactory(new PropertyValueFactory<>("username2"));
//        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
//        mutualFriendsColumn.setCellValueFactory(new PropertyValueFactory<>("mutualFriends"));
    }

    void setupListeners(){
        user1ComboBox.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue )-> {
            if(newValue!=null){
                fetchAndSetUsername(newValue,usernameField);
            }
        });

        user2ComboBox.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue )-> {
            if(newValue!=null){
                fetchAndSetUsername(newValue,username2Field);
            }
        });

    }

    void setupTableListener(){
        friendsTable.getSelectionModel().selectedItemProperty().addListener((observable , oldValue , newValue)->{
            if(newValue!=null){

                statusLabel.setText("usedId: "+ newValue.getId()+" is selected!");
                usernameField.setText(newValue.getUsername());
                username2Field.setText(newValue.getUsername2());
                user1ComboBox.setValue(newValue.getUser1Id());
                user2ComboBox.setValue(newValue.getUser2Id());
                statusComboBox.setValue(newValue.getStatus());
            }
        });
    }

    void loadUserIds(){
        UserService userService = new UserService();
        ObservableList<Integer> list;
        try {
            list = userService.loadUserIds();
            user1ComboBox.setItems(list);
            user2ComboBox.setItems(list);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void fetchAndSetUsername(Integer userId, TextField usernameField) {
        String query = "SELECT username FROM users WHERE id = ?";
        try {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
            statement.setInt(1,userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                usernameField.setText(resultSet.getString("username"));
            } else {
                usernameField.setText("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void loadFriendsData() {
        friendshipList.clear();
        try {
            Connection connection = DBConnection.getConnection();
            String query = """
                SELECT f.id, u1.username AS user1, u2.username AS user2, f.score, f.status, f.mutual_friends
                FROM friendships f
                JOIN users u1 ON f.user1_id = u1.id
                JOIN users u2 ON f.user2_id = u2.id;
            """;
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FriendShip friendship = new FriendShip(
                        rs.getInt("id"),
                        rs.getString("user1"),
                        rs.getString("user2"),
                        rs.getDouble("score"),
                        Status.valueOf(rs.getString("status").toUpperCase()),
                        rs.getInt("mutual_friends")
                );
                friendshipList.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        friendsTable.setItems(friendshipList);
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

   public void addFriendRecord() {
        if(Objects.equals(user1ComboBox.getValue(), user2ComboBox.getValue())){
            setStatus("ID's must be different",Color.RED);
            return;
        }
        if(statusComboBox.getValue()==null || user1ComboBox.getValue()==null || user2ComboBox.getValue()==null){
            setStatus("select all values",Color.RED);
            return;
        }
        try {
            Connection connection = DBConnection.getConnection();
            String query = "INSERT INTO friendships (user1_id, user2_id, status, score, mutual_friends) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, user1ComboBox.getValue());
            stmt.setInt(2, user2ComboBox.getValue());
            stmt.setString(3, statusComboBox.getValue().name());
            stmt.setDouble(4, 0);
            stmt.setInt(5, 0);
            stmt.executeUpdate();

            clear();
            loadFriendsData();
            setStatus("Record Inserted",Color.GREEN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteFriendRecord() {
        FriendShip selectedFriendship = friendsTable.getSelectionModel().getSelectedItem();
        if (selectedFriendship != null) {
            try {
                Connection connection = DBConnection.getConnection();
                String query = "DELETE FROM friendships WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, selectedFriendship.getId());
                stmt.executeUpdate();

                friendshipList.remove(selectedFriendship);
                setStatus("Record Deleted",Color.GREEN);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleUpdate(){
        String username = usernameField.getText();
        String username2 = username2Field.getText();
        String user1Id = user1ComboBox.getValue() != null ? user1ComboBox.getValue().toString() : "";
        String user2Id = user2ComboBox.getValue() != null ? user2ComboBox.getValue().toString() : "";
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue().toString() : "";

        if (username.isEmpty() || username2.isEmpty() || user1Id.isEmpty() || status.isEmpty()) {
            setStatus("Please fill in all fields.", Color.RED);
            return;
        }

        try{
            Connection conn = DBConnection.getConnection();
            String sql ="update friendships set status =? where user1_id =? and user2_id=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, user1Id);
            statement.setString(2, user2Id);
            statement.setString(3, status);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void clear(){
        user1ComboBox.getSelectionModel().clearSelection();
        user2ComboBox.getSelectionModel().clearSelection();
        usernameField.clear();
        username2Field.clear();
        statusComboBox.getSelectionModel().clearSelection();
    }
}
