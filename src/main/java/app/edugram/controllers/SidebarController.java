package app.edugram.controllers;

import app.edugram.utils.PageAction;
import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.w3c.dom.events.Event;

import javafx.event.ActionEvent;
import java.io.IOException;

public class SidebarController {

    @FXML private Button berandaButton;
    @FXML private Button exploreButton;
    @FXML private Button profileButton;

    @FXML private Line berandaLine;
    @FXML private Line exploreLine;
    @FXML private Line profileLine;

    @FXML private ImageView navProfilePicture;
    @FXML private Label navUsername;
    @FXML private Label navFollowing;
    @FXML private Label navFollowers;
    @FXML private Label navPosts;
    @FXML private Label accountLabel;

    @FXML private ImageView navCreatePost;
    @FXML private ImageView navSetting;
    @FXML private ImageView navBookmarks;

    // Interface for handling navigation events
    public interface NavigationHandler {
        void onNavigateToPage(String pageName);
    }

    private NavigationHandler navigationHandler;
    private String currentPage = "Beranda"; // Default current page

    public void initialize() {
        // Set initial selection state
        setNavData();
//        updateSelectionState(currentPage);
    }

    // Setter for navigation handler
    public void setNavigationHandler(NavigationHandler handler) {
        this.navigationHandler = handler;
    }

    // Method to set current page and update UI accordingly
    public void setCurrentPage(ActionEvent event, String pageName) {
        this.currentPage = pageName;
        PageAction.switchPage(event, pageName+".fxml");
        updateSelectionState(pageName);
    }

    // Update visual state based on current page
    private void updateSelectionState(String selectedPage) {
        // Remove selected class from all buttons
        berandaButton.getStyleClass().remove("activated_button");
        berandaButton.getStyleClass().remove("over");
        exploreButton.getStyleClass().remove("activated_button");
        exploreButton.getStyleClass().remove("over");
        profileButton.getStyleClass().remove("activated_button");
        profileButton.getStyleClass().remove("over");

        // Add selected class to current page button
        switch (selectedPage) {
            case "beranda":
//                if (!berandaButton.getStyleClass().contains("activated_button") && !berandaButton.getStyleClass().contains("over")) {
                    berandaButton.getStyleClass().add("activated_button");
                    berandaButton.getStyleClass().add("over");
                    System.out.println(berandaButton.getStyleClass());
                break;
            case "explore":
//                if (!exploreButton.getStyleClass().contains("activated_button") && !exploreButton.getStyleClass().contains("over")) {
                    exploreButton.getStyleClass().add("activated_button");
                    exploreButton.getStyleClass().add("over");
                    System.out.println(exploreButton.getStyleClass());
                break;
            case "profile":
//                if (!profileButton.getStyleClass().contains("activated_button") && !profileButton.getStyleClass().contains("over")) {
                    profileButton.getStyleClass().add("activated_button");
                    profileButton.getStyleClass().add("over");
                break;
        }
    }

    @FXML
    private void onBerandaClick(ActionEvent event) {
        System.out.println("SidebarController: onBerandaClick");
        setCurrentPage(event, "beranda");
    }

    @FXML
    private void onExploreClick(ActionEvent event) {
        setCurrentPage(event, "explore");
    }

    @FXML
    private void onProfileClick() {
        if (navigationHandler != null && !currentPage.equals("Profile")) {
            navigationHandler.onNavigateToPage("Profile");
        }
    }

    @FXML
    private void onCreatePostClick(MouseEvent event) {
        if (navigationHandler != null) {
            navigationHandler.onNavigateToPage("CreatePost");
        }
    }

    @FXML
    private void onSettingsClick(MouseEvent event) {
        if (navigationHandler != null) {
            navigationHandler.onNavigateToPage("Settings");
        }
    }

    @FXML
    private void onBookmarksClick(MouseEvent event) {
        if (navigationHandler != null) {
            navigationHandler.onNavigateToPage("Bookmarks");
        }
    }

    // Methods to update user data
    public void updateUserData(String username, String following, String followers, String posts) {
        if (navUsername != null) navUsername.setText("@" + username);
        if (navFollowing != null) navFollowing.setText(following);
        if (navFollowers != null) navFollowers.setText(followers);
        if (navPosts != null) navPosts.setText(posts);
    }

    public void updateProfilePicture(String imagePath) {
        // Implementation for updating profile picture
        // You can load a new image here
    }

    public void updateAccountLabel(String labelText) {
        if (accountLabel != null) {
            accountLabel.setText(labelText);
        }
    }
    private void setNavData() {
        String profileImagePath = "/app/edugram/userData/Images/profile_pictures/"+ Sessions.getProfilePicture();
        Image image = new Image(getClass().getResource(profileImagePath).toExternalForm());
        navProfilePicture.setImage(image);

        navUsername.setText("@"+Sessions.getUsername());
    }
}