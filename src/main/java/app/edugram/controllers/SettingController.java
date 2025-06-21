package app.edugram.controllers;

import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.models.UserModel;
import app.edugram.utils.Notices;
import app.edugram.utils.PostClickHandler;
import app.edugram.utils.Sessions;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
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
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML private StackPane rootContainer;
    @FXML private VBox contentContainer;
    @FXML private ImageView profilePicture;

    @FXML private TextField usernameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField nameTextField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveChangeBtn;
    @FXML private Button changeProfilePictureBtn;
    @FXML private Button changeUsernameBtn;
    @FXML private Button changeEmailBtn;
    @FXML private Button changeNameBtn;
    @FXML private Button changePasswordBtn;

    private BaseViewController parentController;
    private File selectedProfileImageFile;
    public int whoseProfileId = Sessions.getUserId();

    public void initialize(URL location, ResourceBundle resources) {
    loadUserProfileInfomation();

    changeProfilePictureBtn.setOnAction(event -> handleBrowseImage());}

    public void setUserData(int userId) {
        this.whoseProfileId = userId;
    }

    public void loadUserProfileInfomation() {
        usernameTextField.setText("@"+  Sessions.getUsername());
        emailTextField.setText(Sessions.getEmail());
        nameTextField.setText(Sessions.getNama());
        passwordField.setText(Sessions.getPassword());

        loadDefaultProfilePicture();
    }

    private void loadDefaultProfilePicture() {
        if (profilePicture != null && Sessions.getProfilePicture() != null) {
            try {
                String profileImagePath = "/app/edugram/userData/Images/profile_pictures/" + Sessions.getProfilePicture();
                URL resource = getClass().getResource(profileImagePath);
                if (resource != null) {
                    profilePicture.setImage(new Image(resource.toExternalForm()));
                } else {
                    System.err.println("Profile picture resource not found: " + profileImagePath);
                }
            } catch (Exception e) {
                System.err.println("Could not load profile picture: " + e.getMessage());
            }
        }
    }


    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Profil Baru");
        // Filter untuk hanya menampilkan file gambar
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        // Tampilkan dialog pemilihan file
        // Get the owner window from any FXML component's scene
        Window ownerWindow = rootContainer.getScene().getWindow();
        File chosenFile = fileChooser.showOpenDialog(ownerWindow);

        if (chosenFile != null) {
            selectedProfileImageFile = chosenFile; // Simpan file yang dipilih sementara
            try {
                // Buat Image dari file yang dipilih dan set ke ImageView
                Image newProfileImage = new Image(selectedProfileImageFile.toURI().toURL().toExternalForm());
                profilePicture.setImage(newProfileImage);

                // Pertahankan properti seperti fitWidth, fitHeight, dan clip
                profilePicture.setFitWidth(120);
                profilePicture.setFitHeight(120);
                Circle clip = new Circle(profilePicture.getFitWidth() / 2,
                        profilePicture.getFitHeight() / 2,
                        profilePicture.getFitWidth() / 2);
                profilePicture.setClip(clip);

                System.out.println("Gambar profil sementara diganti dengan: " + selectedProfileImageFile.getAbsolutePath());

            } catch (MalformedURLException e) {
                System.err.println("Error creating URL for selected image: " + e.getMessage());
            }
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

    @FXML
    public void onSaveClicked(ActionEvent event) {
        String getUsername = usernameTextField.getText().trim();
        String getEmail = emailTextField.getText().trim();
        String getNama = nameTextField.getText();
        String getPassword = passwordField.getText().trim();

        if(getUsername.isEmpty() || getEmail.isEmpty() || getNama.isEmpty() || getPassword.isEmpty()) {
            Notices.customNote("Empty field", "Please fill all fields before saving");
            return;
        }

        // Create UserModel instance with all the data
        UserModel userToUpdate = new UserModel();

        String imageFileName = null;

        // Only create new file name and store image if a new image was selected
        if (selectedProfileImageFile != null && !selectedProfileImageFile.getAbsolutePath().equals(userToUpdate.getProfilePic())) {
            imageFileName = createNewFileName();
            if (!storeProfileImage(imageFileName)) {
                Notices.customNote("Profile picture already exists", "Profile picture already exists");
                return;
            }
        } else {
            // Keep existing image filename
            imageFileName = userToUpdate.getProfilePic();
        }

        userToUpdate.setUsername(getUsername.startsWith("@") ? getUsername.substring(1) : getUsername); // Remove @ if present
        userToUpdate.setEmail(getEmail);
        userToUpdate.setNama(getNama);
        userToUpdate.setPassword(getPassword);

        // Handle profile picture
        String profilePicName = null;
        if(selectedProfileImageFile != null) {
            // Use the selected image file name
            profilePicName = imageFileName;
        } else {
            // Keep current profile picture if no new one selected
            profilePicName = Sessions.getProfilePicture();
        }
        userToUpdate.setProfilePic(profilePicName);

        // Call update method
        boolean updateSuccess = userToUpdate.update(userToUpdate);

        if(updateSuccess) {
            // Update session data with new values
            Sessions.setUser(
                    Sessions.getUserId(),
                    getEmail,
                    getUsername.startsWith("@") ? getUsername.substring(1) : getUsername,
                    getNama,
                    profilePicName,
                    getPassword,
                    Sessions.getLevel(),
                    Sessions.getCurrentPage()
            );

            Notices.customNote("Success", "Profile updated successfully!");

            // Clear the selected file since it's now saved
            selectedProfileImageFile = null;
        } else {
            Notices.customNote("Error", "Failed to update profile. Please try again.");
        }
    }

    private String createNewFileName(){
        // Create new file name - same as TambahPostController
        Random randomInt = new Random();
        String getRandomNumber = String.valueOf(randomInt.nextInt(1000) + 1000);

        LocalDateTime getDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String formatDate = getDateTime.format(formatter);

        // Get extension
        String imageUrl = profilePicture.getImage().getUrl();
        String imageExtension = "";
        if (imageUrl != null) {
            imageExtension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();
            // Remove any query parameters if present
            if (imageExtension.contains("?")) {
                imageExtension = imageExtension.substring(0, imageExtension.indexOf("?"));
            }
            System.out.println("Extension: " + imageExtension);
        }

        String newProfileName = "pfp_" + getRandomNumber + formatDate + "." + imageExtension;
        return newProfileName;
    }

    private boolean storeProfileImage(String newFileName) {
        try {
            // Create target directory if it doesn't exist - same as TambahPostController
            Path targetDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "app", "edugram", "userData", "Images", "profile_pictures");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Get the image from the ImageView
            Image currentImageFromView = this.profilePicture.getImage();
            BufferedImage originalBufferedImage = SwingFXUtils.fromFXImage(currentImageFromView, null);

            // Convert to RGB color space to prevent "Bogus input colorspace" error
            BufferedImage rgbImage = new BufferedImage(
                    originalBufferedImage.getWidth(),
                    originalBufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Draw the original image onto the new RGB image
            rgbImage.createGraphics().drawImage(originalBufferedImage, 0, 0, null);

            File outputFile = targetDir.resolve(newFileName).toFile();

            // Compress and save the RGB-converted image
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.8f); // 80% quality

            // Write the rgbImage
            writer.write(null, new IIOImage(rgbImage, null, null), param);

            ios.close();
            writer.dispose();

            System.out.println("Post image stored and compressed: " + newFileName);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to store profile image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
