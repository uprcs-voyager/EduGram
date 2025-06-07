package app.edugram.controllers;

import app.edugram.utils.Sessions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private Button berandaButton;
    @FXML private Button exploreButton;
    @FXML private Button profileButton;

    @FXML private Line berandaLine;
    @FXML private Line exploreLine;
    @FXML private Line profileLine;

    // ... sisa field @FXML Anda ...
    @FXML private ImageView navProfilePicture;
    @FXML private Label navUsername;

    public void onCreatePostClick(MouseEvent mouseEvent) {
    }

    public void onSettingsClick(MouseEvent mouseEvent) {
    }

    public void onBookmarksClick(MouseEvent mouseEvent) {
    }


    // Interface untuk berkomunikasi dengan BaseViewController
    public interface NavigationHandler {
        void onNavigateToPage(String pageName);
    }

    private NavigationHandler navigationHandler;

    // Method ini akan dipanggil oleh BaseViewController
    public void setNavigationHandler(NavigationHandler handler) {
        this.navigationHandler = handler;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set data pengguna awal
        setNavData();
    }

    // Method ini dipanggil oleh BaseViewController untuk mengubah style tombol
    public void updateSelectionState(String selectedPage) {
        // Reset semua style
        berandaButton.getStyleClass().remove("activated_button");
        exploreButton.getStyleClass().remove("activated_button");
        profileButton.getStyleClass().remove("activated_button");

        berandaLine.setVisible(false);
        exploreLine.setVisible(false);
        profileLine.setVisible(false);

        // Terapkan style ke tombol yang benar
        switch (selectedPage.toLowerCase()) {
            case "explore":
                exploreButton.getStyleClass().add("activated_button");
                exploreLine.setVisible(true);
                break;
            case "profile":
                profileButton.getStyleClass().add("activated_button");
                profileLine.setVisible(true);
                break;
            case "beranda":
            default:
                berandaButton.getStyleClass().add("activated_button");
                berandaLine.setVisible(true);
                break;
        }
    }

    // --- Event Handler ---
    @FXML
    public void onBerandaClick(ActionEvent event) {
        if (navigationHandler != null) {
            navigationHandler.onNavigateToPage("beranda");
        }
    }

    @FXML
    public void onExploreClick(ActionEvent event) {
        if (navigationHandler != null) {
            navigationHandler.onNavigateToPage("explore");
        }
    }

    @FXML
    public void onProfileClick(ActionEvent event) {
        if (navigationHandler != null) {
            navigationHandler.onNavigateToPage("profile");
        }
    }

    // --- Method Helper ---
    private void setNavData() {
        if (navProfilePicture != null && Sessions.getProfilePicture() != null) {
            try {
                String profileImagePath = "/app/edugram/userData/Images/profile_pictures/" + Sessions.getProfilePicture();
                URL resource = getClass().getResource(profileImagePath);
                if (resource != null) {
                    navProfilePicture.setImage(new Image(resource.toExternalForm()));
                } else {
                    System.err.println("Profile picture resource not found: " + profileImagePath);
                }
            } catch (Exception e) {
                System.err.println("Could not load profile picture: " + e.getMessage());
            }
        }
        if (navUsername != null) {
            navUsername.setText("@" + Sessions.getUsername());
        }
    }
}
