package app.edugram.controllers.Components;
import app.edugram.models.PostModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Random;

import javafx.stage.Stage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class TambahPostController {

    @FXML private VBox createPostpopup;
    @FXML private TextField titleInput;
    @FXML private TextArea textareainput;
    @FXML private TextField imagePathInput;
    @FXML private TextField tagsInput;
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
        String tags = tagsInput.getText().trim();

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
        if (tags.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Deskripsi postingan tidak boleh kosong.", currentWindowForAlert);
            return;
        }

        // Proses penyimpanan gambar ke direktori aplikasi
        String imageFileName = createNewFileName(); // Nama unik untuk gambar
        if(!storeProfileImage(imageFileName)){
            showAlert(Alert.AlertType.WARNING, "Gagal upload image", "Image post gagal diupload!.", currentWindowForAlert);
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

    private String createNewFileName(){
//        ---- create new profile file's name ----
        Random randomInt = new Random();
        String getRandomNumber = String.valueOf(randomInt.nextInt(1000) + 1000);

        LocalDateTime getDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String formatDate = getDateTime.format(formatter);
//        ---- get extension ----
        String imageUrl = imagePreview.getImage().getUrl();
        String imageExtension = "";
        if (imageUrl != null) {
            imageExtension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();
            // Remove any query parameters if present
            if (imageExtension.contains("?")) {
                imageExtension = imageExtension.substring(0, imageExtension.indexOf("?"));
            }
            System.out.println("Extension: " + imageExtension);
        }

        String newProfileName = "post_" + getRandomNumber + formatDate + "." + imageExtension;
        return newProfileName;
    }

    private boolean storeProfileImage(String newFileName) {
        try {
            // Create target directory if it doesn't exist
            Path targetDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "app", "edugram", "userData", "Images", "posts");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 1. Get the image from the ImageView
            Image currentImage = imagePreview.getImage();
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

            System.out.println("Post image stored and compressed: " + newFileName);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to store profile image: " + e.getMessage());
            e.printStackTrace();
            return false;
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
