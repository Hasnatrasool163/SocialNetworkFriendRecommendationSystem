package org.example.dsassignment3_4.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SocialNetworkDashboard extends Application {

    private SocialNetworkGraph network;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        network = new SocialNetworkGraph();

        // TabPane
        TabPane tabPane = new TabPane();

        // add user Tab
        Tab addUserTab = createAddUserTab();

        // add friends Tab
        Tab addFriendTab = createAddFriendTab();

        // remove friends Tab
        Tab removeFriendTab = createRemoveFriendTab();

        // display mutual friends Tab
        Tab mutualFriendsTab = createMutualFriendsTab();

        // suggest friends Tab
        Tab suggestFriendsTab = createSuggestFriendsTab();

        // add posts Tab
        Tab addPostTab = createAddPostTab();

        // display posts Tab
        Tab userPostsTab = createUserPostsTab();

        // displaying network connections Tab
        Tab displayNetworkTab = createDisplayNetworkTab();

        // BFS Traversal Tab
        Tab bfsTraversalTab = createBFSTraversalTab();

        // Adding all tabs to the TabPane
        tabPane.getTabs().addAll(addUserTab,
                addFriendTab,
                removeFriendTab,
                mutualFriendsTab,
                suggestFriendsTab,
                addPostTab,
                userPostsTab,
                displayNetworkTab,
                bfsTraversalTab);


        VBox root = new VBox(tabPane);
        Scene scene = new Scene(root, 600, 500);

        primaryStage.setTitle("Social Network Friend Recommendation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createAddUserTab() {
        Tab tab = new Tab("Add User");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter User ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter User Name");

        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> {
            String userId = userIdField.getText();
            String name = nameField.getText();
            if (!userId.isEmpty() && !name.isEmpty()) {
                network.addUser(userId, name);
                userIdField.clear();
                nameField.clear();
            }
        });

        layout.getChildren().addAll(userIdField, nameField, addUserButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createAddFriendTab() {
        Tab tab = new Tab("Add Friend");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField userId1Field = new TextField();
        userId1Field.setPromptText("Enter User ID 1");

        TextField userId2Field = new TextField();
        userId2Field.setPromptText("Enter User ID 2");

        Button addFriendButton = new Button("Add Friend");
        addFriendButton.setOnAction(e -> {
            String userId1 = userId1Field.getText();
            String userId2 = userId2Field.getText();
            if (!userId1.isEmpty() && !userId2.isEmpty()) {
                network.addFriend(userId1, userId2);
                userId1Field.clear();
                userId2Field.clear();
            }
        });

        layout.getChildren().addAll(userId1Field, userId2Field, addFriendButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createRemoveFriendTab() {
        Tab tab = new Tab("Remove Friend");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField removeUserId1Field = new TextField();
        removeUserId1Field.setPromptText("Enter User ID 1");

        TextField removeUserId2Field = new TextField();
        removeUserId2Field.setPromptText("Enter User ID 2");

        Button removeFriendButton = new Button("Remove Friend");
        removeFriendButton.setOnAction(e -> {
            String userId1 = removeUserId1Field.getText();
            String userId2 = removeUserId2Field.getText();
            if (!userId1.isEmpty() && !userId2.isEmpty()) {
                network.removeFriend(userId1, userId2);
                removeUserId1Field.clear();
                removeUserId2Field.clear();
            }
        });

        layout.getChildren().addAll(removeUserId1Field, removeUserId2Field, removeFriendButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createMutualFriendsTab() {
        Tab tab = new Tab("Mutual Friends");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField userId1Field = new TextField();
        userId1Field.setPromptText("Enter User ID 1");

        TextField userId2Field = new TextField();
        userId2Field.setPromptText("Enter User ID 2");

        Button findMutualFriendsButton = getFindMutualFriendsButton(userId1Field, userId2Field);

        layout.getChildren().addAll(userId1Field, userId2Field, findMutualFriendsButton);
        tab.setContent(layout);
        return tab;
    }

    private Button getFindMutualFriendsButton(TextField userId1Field, TextField userId2Field) {
        Button findMutualFriendsButton = new Button("Find Mutual Friends");
        findMutualFriendsButton.setOnAction(e -> {
            String userId1 = userId1Field.getText();
            String userId2 = userId2Field.getText();
            if (!userId1.isEmpty() && !userId2.isEmpty()) {
                var mutualFriends = network.getMutualFriends(userId1, userId2);
                mutualFriends.forEach(user -> System.out.println(user.getName()));
                userId1Field.clear();
                userId2Field.clear();
            }
        });
        return findMutualFriendsButton;
    }

    private Tab createSuggestFriendsTab() {
        Tab tab = new Tab("Suggest Friends");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter User ID");

        Button suggestFriendsButton = new Button("Suggest Friends");
        suggestFriendsButton.setOnAction(e -> {
            String userId = userIdField.getText();
            if (!userId.isEmpty()) {
                var suggestedFriends = network.suggestFriends(userId);
                suggestedFriends.forEach(user -> System.out.println(user.getName()));
                userIdField.clear();
            }
        });

        layout.getChildren().addAll(userIdField, suggestFriendsButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createAddPostTab() {
        Tab tab = new Tab("Add Post");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter User ID");

        TextArea postArea = new TextArea();
        postArea.setPromptText("Write your post here...");

        Button addPostButton = new Button("Add Post");
        addPostButton.setOnAction(e -> {
            String userId = userIdField.getText();
            String postContent = postArea.getText();
            if (!userId.isEmpty() && !postContent.isEmpty()) {
                network.addUserPost(userId, postContent);
                userIdField.clear();
                postArea.clear();
            }
        });

        layout.getChildren().addAll(userIdField, postArea, addPostButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createUserPostsTab() {
        Tab tab = new Tab("User Posts");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter User ID");

        Button showPostsButton = new Button("Show Posts");
        showPostsButton.setOnAction(e -> {
            String userId = userIdField.getText();
            network.displayUserPosts(userId);
            userIdField.clear();
        });

        layout.getChildren().addAll(userIdField, showPostsButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createDisplayNetworkTab() {
        Tab tab = new Tab("Display Network");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button displayNetworkButton = new Button("Display Network");
        displayNetworkButton.setOnAction(e -> network.displayNetwork());

        layout.getChildren().add(displayNetworkButton);
        tab.setContent(layout);
        return tab;
    }

    private Tab createBFSTraversalTab() {
        Tab tab = new Tab("BFS Traversal");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField startUserIdField = new TextField();
        startUserIdField.setPromptText("Enter Start User ID");

        Button bfsTraversalButton = new Button("Start BFS");
        bfsTraversalButton.setOnAction(e -> {
            String userId = startUserIdField.getText();
            network.bfsTraversal(userId);
            startUserIdField.clear();
        });

        layout.getChildren().addAll(startUserIdField, bfsTraversalButton);
        tab.setContent(layout);
        return tab;
    }
}
