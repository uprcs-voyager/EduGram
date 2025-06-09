package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.PageAction;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.Window;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.stage.Stage;

public class TambahPostController {

    @FXML private VBox createPostpopup;
    @FXML private TextField titleInput;
    @FXML private TextArea textareainput;
    @FXML private TextField imagePathInput;
    @FXML private Button browseImageButton;
    @FXML private ImageView imagePreview;
    @FXML private Button cancelButton;
    @FXML private Button createButton;

    private Stage dialogStage;
    private File selectedImageFile;

    public void initialize() {
        browseImageButton.setOnAction(event -> handleBrowseImage());
        cancelButton.setOnAction(event -> handleCancel());
        createButton.setOnAction(event -> handleCreatePost());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Postingan");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Dapatkan owner window dari popup yang memuat controller ini
        Window ownerWindowForFileChooser = null;
        if (dialogStage != null) {
            ownerWindowForFileChooser = dialogStage; // Stage dialog ini adalah pemilik FileChooser
        } else if (createPostpopup != null && createPostpopup.getScene() != null) {
            ownerWindowForFileChooser = createPostpopup.getScene().getWindow(); // Fallback
        }

        selectedImageFile = fileChooser.showOpenDialog(ownerWindowForFileChooser); // FileChooser akan muncul di depan ownerWindowForFileChooser

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
        System.out.println("Membuat postingan dibatalkan.");
        if (dialogStage != null) {
            dialogStage.close(); // <--- UBAH INI: Tutup Stage dialog ini
        }
    }

    private void handleCreatePost() {
        String title = titleInput.getText().trim();
        String description = textareainput.getText().trim();

        Window currentWindowForAlert = null;
        if (dialogStage != null) {
            currentWindowForAlert = dialogStage; // Stage dialog ini adalah pemilik Alert
        } else if (createPostpopup != null && createPostpopup.getScene() != null) {
            currentWindowForAlert = createPostpopup.getScene().getWindow();
        }
        // Validasi input
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Judul postingan tidak boleh kosong.", currentWindowForAlert);
            return;
        }
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Deskripsi postingan tidak boleh kosong.", currentWindowForAlert);
            return;
        }
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Gambar Belum Dipilih", "Mohon pilih gambar untuk postingan Anda.", currentWindowForAlert);
            return;
        }

        // Proses penyimpanan gambar ke direktori aplikasi
        String imageFileName = System.currentTimeMillis() + "_" + selectedImageFile.getName(); // Nama unik untuk gambar
        Path targetDirPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "app", "edugram", "userData", "Images", "posts");

        // Pastikan direktori tujuan ada
        if (!Files.exists(targetDirPath)) {
            try {
                Files.createDirectories(targetDirPath);
                System.out.println("Created image directory: " + targetDirPath.toString());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuat direktori gambar: " + e.getMessage(), currentWindowForAlert);
                return;
            }
        }
        Path targetPath = targetDirPath.resolve(imageFileName);

        try {
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Gambar berhasil disimpan ke: " + targetPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Menyimpan Gambar", "Gagal menyimpan gambar: " + e.getMessage(), currentWindowForAlert);
            return;
        }

        // Buat objek PostModel
        PostModel newPost = new PostModel();
        newPost.setTitle(title);
        newPost.setDescription(description);
        newPost.setPostContent(imageFileName); // Simpan hanya nama file ke database

        // Panggil metode create dari PostModel untuk menyimpan ke database
        boolean success = newPost.create(newPost); // PostModel.create() sudah handle Sessions.getUserId()

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Postingan berhasil dibuat!", currentWindowForAlert);
            if (dialogStage != null) {
                dialogStage.close(); // <--- UBAH INI: Tutup Stage dialog setelah sukses

            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal membuat postingan. Silakan coba lagi.", currentWindowForAlert);
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
