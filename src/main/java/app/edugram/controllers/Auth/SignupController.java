package app.edugram.controllers;
import app.edugram.utils.PageAction;
import  javafx.scene.shape.Path;
import app.edugram.utils.Notices;
import app.edugram.utils.cookies.CookieUtil;
import app.edugram.utils.cookies.UserCookie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import app.edugram.models.UserModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.PostClickHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.io.File;
import java.io.IOException;

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


//        register.setOnAction(this::handleRegisterButton);
        login.setOnAction(this::handleLoginButton);


}
    private void loadDefaultProfilePicture() {
        try {
            // Pastikan path ini benar dan file ada
            File defaultImage = new File(getClass().getResource("app/edugram/userData/images/profile_pictures/hiboss.jpg").toURI());
            selectedProfileImageFile = defaultImage; // Set default image sebagai selected
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



//    private void handleRegisterButton(ActionEvent event) {
//        String nama = nama_textfield.getText().trim();
//        String username = username_textfield.getText().trim();
//        String email = email_textfield.getText().trim();
//        String password = password_textfield.getText(); // Tidak perlu trim password
//
//        // Validasi input dasar
//        if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
//            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Semua kolom harus diisi.", register.getScene().getWindow());
//            return;
//        }
//
//        // Validasi gambar profil (harus ada, bahkan jika itu placeholder default)
//        if (selectedProfileImageFile == null || !selectedProfileImageFile.exists()) {
//            showAlert(Alert.AlertType.WARNING, "Gambar Profil Belum Dipilih", "Mohon pilih gambar profil Anda.", register.getScene().getWindow());
//            return;
//        }
//
//        // Cek validasi registrasi (cek duplikasi username/email)
//        // Panggil ValidateRegistration yang baru di UserModel
//        boolean isRegistrationValid = UserModel.ValidateRegistration(username, password, nama, email);
//        if (!isRegistrationValid) {
//            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username atau Email sudah terdaftar. Mohon gunakan yang lain.", register.getScene().getWindow());
//            return;
//        }
//
//
//        // Buat objek UserModel baru
//        UserModel newUser = new UserModel();
//        newUser.setNama(nama);
//        newUser.setUsername(username);
//        newUser.setEmail(email);
//        newUser.setPassword(password);
//        newUser.setProfilePic("pfp_placeholder.jpg"); // Atur default dulu
//
//
//        // Coba daftarkan pengguna ke database
//        boolean registrationSuccess = newUser.create(newUser); // Panggil metode create() di UserModel
//
//        if (registrationSuccess) {
//            // Setelah registrasi berhasil, ID pengguna baru sudah ada di objek newUser
//            // berkat Statement.RETURN_GENERATED_KEYS di metode create().
//
//            // --- Proses Menyalin Gambar Profil ke Direktori Aplikasi ---
//            // Hanya salin jika gambar yang dipilih BUKAN gambar placeholder default
//            try {
//                // Dapatkan path sumber dari selectedProfileImageFile
//                String sourcePath = selectedProfileImageFile.getAbsolutePath();
//                String defaultPlaceholderPath = new File(getClass().getResource("/app/edugram/assets/Image/Icons/pfp_placeholder.jpg").toURI()).getAbsolutePath();
//
//                if (!sourcePath.equals(defaultPlaceholderPath)) { // Hanya salin jika bukan placeholder
//                    // Buat nama file unik untuk gambar profil (gunakan ID pengguna)
//                    String profileImageFileName = "user_" + newUser.getId() + "_" + System.currentTimeMillis() + "_" + selectedProfileImageFile.getName();
//                    Path targetDirPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "app", "edugram", "userData", "Images", "profile_pictures");
//
//                    if (!Files.exists((java.nio.file.Path) targetDirPath)) {
//                        Files.createDirectories((java.nio.file.Path) targetDirPath);
//                        System.out.println("SignupController: Created profile picture directory: " + targetDirPath.toString());
//                    }
//
//                    Path targetPath = (Path) ((java.nio.file.Path) targetDirPath).resolve(profileImageFileName);
//                    Files.copy(selectedProfileImageFile.toPath(), (java.nio.file.Path) targetPath, StandardCopyOption.REPLACE_EXISTING);
//                    System.out.println("SignupController: Gambar profil berhasil disimpan ke: " + targetPath.toString());
//
//
//                    boolean updatePicSuccess = newUser.updateProfilePicture(newUser.getId(), profileImageFileName);
//                    if (updatePicSuccess) {
//                        System.out.println("SignupController: Path gambar profil di database berhasil diperbarui.");
//                    } else {
//                        System.err.println("SignupController: Gagal memperbarui path gambar profil di database.");
//                    }
//                } else {
//                    System.out.println("SignupController: Menggunakan gambar profil default, tidak perlu menyalin.");
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                showAlert(Alert.AlertType.ERROR, "Error Menyimpan Gambar", "Gagal menyimpan gambar profil: " + e.getMessage(), register.getScene().getWindow());
//            } catch (Exception e) {
//                e.printStackTrace();
//                showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan saat mengelola gambar profil: " + e.getMessage(), register.getScene().getWindow());
//            }
//
//            showAlert(Alert.AlertType.INFORMATION, "Registrasi Sukses", "Akun Anda berhasil dibuat!", register.getScene().getWindow());
//            PageAction.loadPage("pages/login.fxml", register); // Redirect ke halaman login
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Gagal mendaftar. Terjadi kesalahan pada database.", register.getScene().getWindow());
//        }
//    }

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


