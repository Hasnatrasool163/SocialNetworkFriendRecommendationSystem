package org.example.dsassignment3_4.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Rating;
import org.example.dsassignment3_4.dao.UserBadgeDAO;
import org.example.dsassignment3_4.extras.PostCell;
import org.example.dsassignment3_4.model.Post;
import org.example.dsassignment3_4.model.User;
import org.example.dsassignment3_4.service.PostService;
import org.example.dsassignment3_4.service.SearchService;
import org.example.dsassignment3_4.service.SessionManager;
import org.example.dsassignment3_4.service.UserService;
import org.example.dsassignment3_4.utilities.UtilityMethods;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardController {

    public BorderPane pane;
    @FXML
    private ImageView profileImage;
    @FXML
    private Label usernameLbl;
    @FXML
    private Button logoutButton2;
    @FXML
    private TextField searchField;
    @FXML
    private TextArea postTextArea;
    @FXML
    private Button postButton, clearButton ,likeButton,logoutButton,notificationsButton;
    @FXML
    private Pane centerPane;
    @FXML
    private VBox notificationsList;
    @FXML
    private VBox searchResultsVBox;
    @FXML
    private Label likes;
    @FXML
    private FontAwesomeIcon bellIcon;
    @FXML
    private Label timeLabel;
    @FXML
    private ImageView logo;
    @FXML
    private Button badgesButton;

    @FXML
    private ListView<VBox> postsListView;
    Rating rating;

    private Queue<Post> postQueue = new LinkedList<>();
    private int userId = SessionManager.getInstance().getUserId();
    private String username = SessionManager.getInstance().getUsername();

    static  DashboardController instance;

    public static  DashboardController getInstance(){
        if(instance==null){
            return new DashboardController();
        }
        return instance;
    }


    @FXML
    public void initialize() {
        usernameLbl.setText(username);
        profileImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/avatars/" + SessionManager.getInstance().getAvatar() + ".png"))));
        loadPosts();
        loadBellIcon();
        profileImage.setOnMouseEntered(event -> profileImage.setStyle("-fx-effect: dropshadow(gaussian,#6cb4cc,10.92,0.1,0.0,0.0)"));
        profileImage.setOnMouseExited(event -> profileImage.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 10, 0.5, 0, 0);"));
        timeLabel.setText(String.valueOf(LocalDate.now()));
        searchResultsVBox.setVisible(false);
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && searchField.getText().trim().isEmpty()) {
                hideSearchResults();
            }
        });
    }

    private void loadBellIcon() {
        if(UserService.checkNotificationStatus(SessionManager.getInstance().getUserId())) bellIcon.setGlyphName("BELL_SLASH");
        else bellIcon.setGlyphName("BELL");
    }

    private void loadPosts() {
        postQueue = fetchPostsFromDatabase();
        postsListView.setCellFactory(listView -> new PostCell());
        VBox postContainer;
        for (Post post : postQueue) {
            postContainer = createPostView(post);
            postsListView.getItems().add(postContainer);
        }
        postsListView.setPrefHeight(Math.min(1980,postQueue.size()*180));

    }

    private VBox createPostView(Post post) {
        VBox postContainer = new VBox();
        postContainer.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #d3d3d3; -fx-border-width: 1; -fx-spacing: 10;");
        postContainer.setPrefWidth(750);

        HBox postHeader = new HBox(10);
        ImageView profileImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/logo.png"))));
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        Label postIdLabel  = new Label(String.valueOf(post.getPostId()));
        postIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-fill: white");
        Label usernameLabel = new Label(post.getUsername());
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        postHeader.getChildren().addAll(profileImage,postIdLabel, usernameLabel);

        Label postContentLabel = new Label();
        postContentLabel.setWrapText(true);
        postContentLabel.setText(post.getContent());
        postContentLabel.setStyle("-fx-font-size: 16;");
        postContentLabel.setWrapText(true);

        HBox actionButtons = new HBox(10);
        Label timeLabel = new Label(post.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timeLabel.setStyle("-fx-font-size: 14;");

        Label likeCount = new Label();
        likeCount.setStyle("-fx-font-size: 16");
        likeCount.setText(String.valueOf(post.getLikes()));

        rating = new Rating();
        rating.setMax(1);

        boolean userLiked = PostService.hasUserLikedPost(post.getPostId(), userId);
        rating.setRating(userLiked ? 1 : 0);

        rating.setOnMouseClicked(event -> {
                if(savePostRating(post.getPostId(), userId)){
                    post.setLikes(post.getLikes() + 1);
                    UtilityMethods.showPopup("Post liked successfully!");
                    likeCount.setText(String.valueOf(post.getLikes()));
                }
                else{
                    UtilityMethods.showPopupWarning("You have already liked this post!");
                }
        });

        actionButtons.getChildren().addAll(rating,likeCount, timeLabel);
        ContextMenu contextMenu = new ContextMenu();
        if (post.getUserId() == userId) {
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDeletePost(post));
            contextMenu.getItems().add(deleteItem);
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> handleEditPost(post));
            contextMenu.getItems().add(editItem);
        }
        else{
            MenuItem openProfileItem = new MenuItem("Open Profile");
            openProfileItem.setOnAction(event -> openUserProfile(post.getUserId()));
            contextMenu.getItems().add(openProfileItem);
            MenuItem openMyPostsItem = new MenuItem("Open MyPosts");
            openMyPostsItem.setOnAction(event -> UtilityMethods.switchToScene("PostView"));
            contextMenu.getItems().add(openMyPostsItem);
        }

        postContainer.setOnContextMenuRequested(event -> contextMenu.show(postContainer, event.getScreenX(), event.getScreenY()));
        postContainer.getChildren().addAll(postHeader, postContentLabel, actionButtons);

        return postContainer;
    }

    void handleEditPost(Post post){
        if(postTextArea.getText().isEmpty()){
            UtilityMethods.showPopupWarning("PostTextArea is empty!");
            return;
        }
        PostService.updatePostContent(post.getPostId(),postTextArea.getText());
    }
    private boolean savePostRating(int postId, double ratingValue) {
        if(PostService.savePostRating(postId, userId))return true;
        if(PostService.checkIfPostGotTenLikes(userId)){
            BadgesController badgesController = new BadgesController();
            badgesController.handleAchievement2();
        }
        return false;
    }
    public void handlePostAction(String Content,int userId) {
        savePostToDatabase(userId, Content);
        UtilityMethods.showPopup("Post added successfully!");
        postsListView.getItems().clear();
        loadPosts();
        postTextArea.clear();
    }

    public void handleClearAction() {
        postTextArea.clear();
    }

    private void savePostToDatabase(int userId, String postText) {
        PostService.savePostToDatabase(userId, postText);
    }

    private Queue<Post> fetchPostsFromDatabase()  {
       return PostService.fetchPostsFromDatabase();
    }

        @FXML
    private void handleNotificationsNavigation() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/NotificationsDialog.fxml"));
                VBox notificationDialog = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Notifications");
                dialogStage.setScene(new Scene(notificationDialog));

                NotificationDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);

                dialogStage.showAndWait();
                dialogStage.centerOnScreen();
                dialogStage.alwaysOnTopProperty();

                if(NotificationDialogController.markAsRead){
                    bellIcon.setGlyphName("BELL");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @FXML
    private void handleMyProfileNavigation(){
        UtilityMethods.switchToScene("Profile");
    }

    @FXML
    private void handleAboutMeNavigation(){
        UtilityMethods.switchToScene("CompleteProfile");
    }
    @FXML
    private void handleSeeFriendsNavigation() {
        UtilityMethods.switchToScene("Friends");
    }

    @FXML
    private void handleSuggestionsNavigation() {
       UtilityMethods.switchToScene("Recommendations");
    }

    @FXML
    private void handleFindFriends(){
      UtilityMethods.switchToScene("FindFriends");
    }


    @FXML
    private void navigateToCreatePost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreatePost.fxml"));
            Parent createPostRoot = loader.load();

            CreatePostController createPostController = loader.getController();
            createPostController.setDashboardController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(createPostRoot));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Post");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToRefresh() {
        postsListView.getItems().clear();
        loadPosts();
        UtilityMethods.showPopup("Application Refresh!");
    }


    public void showMutualFriends(){
      UtilityMethods.switchToScene("MutualFriends");
    }

    public void handleBadges(){
        UtilityMethods.switchToScene("Badges");
    }

    public void setUserOffline(){
        SessionManager.getInstance().setStatus(0);
    }

    public void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure want to logout?");
        alert.setTitle("Logout");
        alert.setHeaderText("Want to Exit?");
        if(alert.showAndWait().orElse(ButtonType.CANCEL) ==ButtonType.OK){
            setUserOffline();
            UtilityMethods.switchToScene(postButton,"Login");
        }
    }

    public void AddFriend(ActionEvent event) {
        UtilityMethods.switchToScene("AddFriend");
    }

    public void handlePostAction(ActionEvent event) {
        if(postTextArea.getText().isEmpty() || postTextArea.getText().isBlank()){
            UtilityMethods.showPopupWarning("Post cannot be empty");
            return;
        }
        handlePostAction(postTextArea.getText(),userId);
        if(PostService.checkIfFirstPost(userId)){
            UserBadgeDAO.awardBadgeToUser(userId,3);
        }
    }

    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText().trim();

        if (!searchText.isEmpty()) {
            List<User> searchResults = searchUsersByUsername(searchText);
            displaySearchResults(searchResults);
        } else {
            UtilityMethods.showPopup("Search Field is empty");
            hideSearchResults();
        }
    }

    private List<User> searchUsersByUsername(String username) {
        return SearchService.searchUsersByUsername(username);
    }

    private void displaySearchResults(List<User> users) {
        searchResultsVBox.getChildren().clear();

        if (users.isEmpty()) {
            Label noResultsLabel = new Label("No users found.");
            searchResultsVBox.getChildren().add(noResultsLabel);
            return;
        }

        for (User user : users) {
            Button userButton = new Button(user.getUsername());
            userButton.setOnAction(event -> System.out.println("Selected user: " + user.getUsername()));
            userButton.setOnAction(event -> openUserProfile(user.getId()));
            userButton.setPrefWidth(searchResultsVBox.getPrefWidth());
            searchResultsVBox.getChildren().add(userButton);
        }
        searchResultsVBox.setVisible(true);
    }

    private void openUserProfile(int userId) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ViewProfile.fxml"));
            Parent root = loader.load();

            ViewProfileController controller = loader.getController();
            controller.loadProfileDetails(userId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideSearchResults(){
        searchResultsVBox.setVisible(false);
    }

    private void handleDeletePost(Post post) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this post?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                PostService.deletePost(post.getPostId());
                postsListView.getItems().removeIf(node -> {
                    Label postIdLabel = (Label) ((HBox) node.getChildren().get(0)).getChildren().get(1);
                    return Integer.parseInt(postIdLabel.getText()) == post.getPostId();
                });
                UtilityMethods.showPopup("Post deleted successfully!");
            }
        });
    }

    public void buttonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        FontAwesomeIcon icon = (FontAwesomeIcon) button.getGraphic();

        ScaleTransition st = new ScaleTransition(Duration.millis(200), icon);
        st.setToX(1.4);
        st.setToY(1.4);
        st.setCycleCount(1);
        st.play();
    }

    public void buttonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        FontAwesomeIcon icon = (FontAwesomeIcon) button.getGraphic();

        ScaleTransition st = new ScaleTransition(Duration.millis(200), icon);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(1);
        st.play();
    }

}
