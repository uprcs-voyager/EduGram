package app.edugram.controllers.Components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class TopbarController implements Initializable {

    // FXML Components
    @FXML
    private ImageView logoImageView;

    @FXML
    private Label appNameLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private ImageView searchIconImageView;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button usernameButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSearchField();
        setupUserProfile();
    }

    private void setupSearchField() {
        if (searchTextField != null) {
            // Add search functionality on Enter key press
            searchTextField.setOnKeyPressed(this::onSearchKeyPressed);

            // Add placeholder behavior
            searchTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    searchTextField.selectAll();
                }
            });
        }
    }

    private void setupUserProfile() {
        if (usernameButton != null) {
            // You can set the username dynamically here
            // usernameButton.setText("Current User");
        }
    }

    @FXML
    private void onUsernameButtonClick(ActionEvent event) {
        System.out.println("Username button clicked!");
        // Add your username button logic here
        // For example: show user profile, logout menu, etc.
    }

    private void onSearchKeyPressed(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            performSearch();
        }
    }

    private void performSearch() {
        String searchQuery = searchTextField.getText().trim();
        if (!searchQuery.isEmpty()) {
            System.out.println("Searching for: " + searchQuery);
            // Add your search logic here
            // For example: filter posts, navigate to search results, etc.
        }
    }

    // Public methods for external access
    public void setUsername(String username) {
        if (usernameButton != null) {
            usernameButton.setText(username);
        }
    }

    public String getSearchText() {
        return searchTextField != null ? searchTextField.getText() : "";
    }

    public void clearSearch() {
        if (searchTextField != null) {
            searchTextField.clear();
        }
    }

    public void setProfileImage(String imagePath) {
        if (profileImageView != null) {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(imagePath);
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
            }
        }
    }
}