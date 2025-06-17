package app.edugram.controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Alert;

import java.io.File;

public class PilihGambarProfile {

    @FXML private VBox profilePictureChooserPopup; // Root VBox dari popup
    @FXML private TextField imagePathInput;
    @FXML private Button browseImageButton;
    @FXML private ImageView imagePreview;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Stage dialogStage;
    private File selectedImageFile;
    private String savedImagePath; // Untuk menyimpan path gambar yang berhasil dipilih/disimpan

    public void initialize() {
        browseImageButton.setOnAction(event -> handleBrowseImage());
        cancelButton.setOnAction(event -> handleCancel());
        saveButton.setOnAction(event -> handleSavePicture());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Metode ini akan dipanggil dari SignupController untuk mendapatkan path gambar
    public String getSavedImagePath() {
        return savedImagePath;
    }

    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Window ownerWindowForFileChooser = dialogStage != null ? dialogStage : profilePictureChooserPopup.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(ownerWindowForFileChooser);

        if (selectedImageFile != null) {
            imagePathInput.setText(selectedImageFile.getAbsolutePath());
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                imagePreview.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat memuat gambar: " + e.getMessage(), ownerWindowForFileChooser);
            }
        }
    }

    private void handleCancel() {
        savedImagePath = null; // Set null jika dibatalkan
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void handleSavePicture() {
        Window currentWindowForAlert = dialogStage != null ? dialogStage : profilePictureChooserPopup.getScene().getWindow();

        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Gambar Belum Dipilih", "Mohon pilih gambar profil Anda.", currentWindowForAlert);
            return;
        }

        // Simpan path sementara ini, kita tidak akan menyalin file di sini.
        // File akan disalin oleh SignupController setelah registrasi berhasil.
        savedImagePath = selectedImageFile.getAbsolutePath();
        showAlert(Alert.AlertType.INFORMATION, "Gambar Dipilih", "Gambar profil berhasil dipilih.", currentWindowForAlert);
        if (dialogStage != null) {
            dialogStage.close();
        }
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