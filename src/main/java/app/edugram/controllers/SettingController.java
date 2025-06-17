package app.edugram.controllers;

import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.PostClickHandler;
import app.edugram.utils.Sessions;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML private StackPane rootContainer;
    @FXML private VBox contentContainer;
    @FXML private ImageView profilePicture;

    @FXML private TextField usernameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField nameTextField;
    @FXML private PasswordField passwordField;

    @FXML private Button changeProfilePictureBtn;
    @FXML private Button changeUsernameBtn;
    @FXML private Button changeEmailBtn;
    @FXML private Button changeNameBtn;
    @FXML private Button changePasswordBtn;

    private BaseViewController parentController;

    public int whoseProfileId = Sessions.getUserId();

    public void initialize(URL location, ResourceBundle resources) {
    loadUserProfileInfomation();
    }

    public void setUserData(int userId) {
        this.whoseProfileId = userId;
    }

    public void loadUserProfileInfomation() {
        usernameTextField.setText( Sessions.getUsername());
        emailTextField.setText("@"+ Sessions.getEmail());

        loadDefaultProfilePicture();
    }

    private void loadDefaultProfilePicture() {
        try {
            Image dummyProfilePic = new Image(getClass().getResource("/app/edugram/userData/images/profile_pictures/lapwiing.jpg").toExternalForm());
            profilePicture.setImage(dummyProfilePic);
            profilePicture.setFitWidth(120);
            profilePicture.setFitHeight(120);
            Circle clip = new Circle(profilePicture.getFitWidth() / 2,
                    profilePicture.getFitHeight() / 2,
                    profilePicture.getFitWidth() / 2);
            profilePicture.setClip(clip);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar profil default: " + e.getMessage());
        }
    }

    // Helper: Mendapatkan gambar placeholder default (jika diperlukan di tempat lain)
    private Image getDefaultProfilePlaceholder() {
        try {
            return new Image(getClass().getResource("/app/edugram/userData/images/profile_pictures/lapwiing.jpg").toExternalForm());
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar placeholder default: " + e.getMessage());
            return null;
        }
    }

}
