package app.edugram.controllers.Auth;
import app.edugram.controllers.Components.PilihGambarProfile;
import app.edugram.models.UserModel;
import app.edugram.utils.PageAction;
import app.edugram.utils.cookies.CookieUtil;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import app.edugram.Main;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import javax.imageio.stream.ImageOutputStream;
import java.net.CookieHandler;
import java.time.LocalDateTime;
import java.util.Iterator;

import java.io.IOException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class SignupController {
    @FXML private ImageView image_signup_bg;
    @FXML private ImageView pfp_imageview;
    @FXML private Button addprofile_button;

    @FXML private TextField nama_textfield;
    @FXML private TextField username_textfield;
    @FXML private TextField email_textfield;
    @FXML private PasswordField password_textfield;
    @FXML private Button register; // Ini adalah tombol "Register" Anda
    @FXML private Button login;

    private File selectedProfileImageFile;

    @FXML
    public void initialize() {
        try{
            Region parent = (Region) image_signup_bg.getParent();

            image_signup_bg.fitWidthProperty().bind(parent.widthProperty());
            image_signup_bg.fitHeightProperty().bind(parent.heightProperty());
            image_signup_bg.setPreserveRatio(false); // Biar selalu isi seluruh area
        }catch (Exception e){
            System.out.println("Auth.Initialize: (notice) This content doesn't load / contain image");
        }

        addprofile_button.setOnAction(event -> handleAddProfilePicture());
        loadDefaultProfilePicture();
        register.setOnAction(this::handleRegisterButton);

        CookieUtil.clearCookie();
        login.setOnAction(this::handleLoginButton);

    }
    private void loadDefaultProfilePicture() {
        try {
            // Use leading slash for absolute path from resources root
            File defaultImage = new File(Objects.requireNonNull(getClass().getResource("/app/edugram/userData/Images/profile_pictures/pfp_placeholder.jpg")).toURI());
            selectedProfileImageFile = defaultImage;
            Image image = new Image(defaultImage.toURI().toString());
            pfp_imageview.setImage(image);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar placeholder default: " + e.getMessage());
        }
    }

    private void handleAddProfilePicture() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource("pages/components/choose_pfp_picture.fxml"));
            Parent root = fxmlLoader.load();

            PilihGambarProfile controller = fxmlLoader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pilih Gambar Profil");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(addprofile_button.getScene().getWindow()); // Owner window adalah window signup
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage); // Set stage ke controller popup
            dialogStage.showAndWait(); // Tampilkan popup dan tunggu hingga ditutup

            // Setelah popup ditutup, periksa apakah gambar berhasil dipilih
            String imagePath = controller.getSavedImagePath();
            if (imagePath != null) {
                selectedProfileImageFile = new File(imagePath);
                Image image = new Image(selectedProfileImageFile.toURI().toString());
                pfp_imageview.setImage(image);
            } else {
                // Gambar tidak dipilih atau dibatalkan, biarkan pfp_imageview tetap default
                System.out.println("Pemilihan gambar profil dibatalkan atau tidak ada gambar dipilih.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat popup pemilihan gambar profil.", addprofile_button.getScene().getWindow());
        }
    }

    private void handleRegisterButton(ActionEvent event) {
        String nama = nama_textfield.getText().trim();
        String username = username_textfield.getText().trim();
        String email = email_textfield.getText().trim();
        String password = password_textfield.getText();

        // Validasi input dasar
        if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Semua kolom harus diisi.", register.getScene().getWindow());
            return;
        }

        boolean isRegistrationValid = UserModel.ValidateRegistration(username, password, nama, email);
        if (!isRegistrationValid) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username atau Email sudah terdaftar. Mohon gunakan yang lain.", register.getScene().getWindow());
            return;
        }

        // Buat objek UserModel baru
        UserModel newUser = new UserModel();
        newUser.setNama(nama);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        String newProfileName = isProfilePictureChanged() ? createNewProfileName() : "pfp_placeholder.jpg";
        newUser.setProfilePic(newProfileName);

        if (newUser.create(newUser)) {
            storeProfileImage(newProfileName);
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Sukses", "Akun Anda berhasil dibuat!", register.getScene().getWindow());
            PageAction.switchPage(event, "login.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Gagal mendaftar. Terjadi kesalahan pada database.", register.getScene().getWindow());
        }
    }

    private void storeProfileImage(String newFileName) {
        if (!isProfilePictureChanged()) {
            System.out.println("Using default placeholder, skipping image storage");
            return;
        }

        try {
            // Create target directory if it doesn't exist
            Path targetDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "app", "edugram", "userData", "Images", "profile_pictures");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 1. Get the image from the ImageView
            Image currentImage = pfp_imageview.getImage();
            BufferedImage originalBufferedImage = SwingFXUtils.fromFXImage(currentImage, null);

            // 2. *** FIX: Convert to RGB color space to prevent "Bogus input colorspace" error ***
            // Create a new BufferedImage with a compatible RGB type
            BufferedImage rgbImage = new BufferedImage(
                    originalBufferedImage.getWidth(),
                    originalBufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Draw the original image onto the new RGB image. This performs the conversion.
            rgbImage.createGraphics().drawImage(originalBufferedImage, 0, 0, null);
            // **********************************************************************************

            File outputFile = targetDir.resolve(newFileName).toFile();

            // 3. Compress and save the NEW RGB-converted image
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.8f); // 80% quality

            // Write the rgbImage, NOT the original bufferedImage
            writer.write(null, new IIOImage(rgbImage, null, null), param);

            ios.close();
            writer.dispose();

            System.out.println("Profile image stored and compressed: " + newFileName);

        } catch (IOException e) {
            System.err.println("Failed to store profile image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isProfilePictureChanged() {
        Image currentImage = pfp_imageview.getImage();
        if (currentImage == null) {
            return false;
        }

        String currentUrl = currentImage.getUrl();
        System.out.println(currentUrl);
        return currentUrl != null && !currentUrl.contains("pfp_placeholder.jpg");
    }

    private String createNewProfileName(){
//        ---- create new profile file's name ----
        Random randomInt = new Random();
        String getRandomNumber = String.valueOf(randomInt.nextInt(1000) + 1000);

        LocalDateTime getDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String formatDate = getDateTime.format(formatter);
//        ---- get extension ----
        String imageUrl = pfp_imageview.getImage().getUrl();
        String imageExtension = "";
        if (imageUrl != null) {
            imageExtension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();
            // Remove any query parameters if present
            if (imageExtension.contains("?")) {
                imageExtension = imageExtension.substring(0, imageExtension.indexOf("?"));
            }
            System.out.println("Extension: " + imageExtension);
        }

        String newProfileName = "profile_" + getRandomNumber + formatDate + "." + imageExtension;
        return newProfileName;
    }

    private void handleLoginButton(ActionEvent event) {
        PageAction.switchPage(event, "login.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message, Window ownerWindow) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (ownerWindow != null) {
            alert.initOwner(ownerWindow);
        }
        alert.showAndWait();
    }


}


