package app.edugram.controllers.Components;
import app.edugram.Main;
import app.edugram.models.PostModel; // Pastikan PostModel sudah ada dan memiliki metode update
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.image.Image; // Import yang benar untuk Image
import javafx.scene.image.ImageView; // Import yang benar untuk ImageView
import javafx.scene.control.TextArea;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class EditPopUpController {

    @FXML private VBox editPostpopup;
    @FXML private TextField currentTitle;
    @FXML private TextArea currentDesc;
    @FXML private TextField currentImageUrlField;
    @FXML private TextField currentTags;
    @FXML private ImageView currentImage;
    @FXML private Button browseImageButton;
    @FXML private Button editPost;
    @FXML private Button cancelEditPost;


    private Popup ownerPopup; // Referensi ke Popup ini sendiri
    private PostModel postToEdit; // Postingan yang sedang diedit
    private PostFrameController postFrameController; // Opsional: Untuk merefresh UI jika perlu
    private Popup parentPopup;
    private Stage dialogStage;
    private File selectedImageFile;


    public void initialize() {
        editPost.setOnAction(event -> handleSave());
        cancelEditPost.setOnAction(event -> handleCancel());
        browseImageButton.setOnAction(event -> handleBrowseImage());
    }

    public void setPopup(Popup popup) {
        this.ownerPopup = popup;
    }




    public void setParentPopup(Popup parentPopup) {
        this.parentPopup = parentPopup;
    }

    /**
     * Mengatur Popup yang memiliki controller ini.
     * Digunakan untuk menyembunyikan popup.
     * @param popup Instans Popup
     */


    /**
     * Mengatur data PostModel yang akan diedit.
     * Mengisi field input dengan data postingan saat ini.
     * @param post PostModel yang akan diedit
     */
    public void setPostData(PostModel post) {
        this.postToEdit = post;
        if (postToEdit != null) {
            currentTitle.setText(postToEdit.getTitle());
            currentDesc.setText(postToEdit.getDescription());
            List<String> tagsList = postToEdit.getTags();
            if (tagsList != null && !tagsList.isEmpty()) {
                currentTags.setText(String.join(", ", tagsList));
            } else {
                currentTags.setText("");
            }


            String imagePath = postToEdit.getPostContent();
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                System.out.println("Attempting to load image from path: " + imagePath);
                System.out.println("Absolute path of the file object: " + file.getAbsolutePath());
                if (file.exists()) {
                    try {
                        Image image = new Image(file.toURI().toString()); // <--- Konversi path ke URI
                        currentImage.setImage(image);
                        currentImageUrlField.setText(imagePath); // Tampilkan path asli di TextField
                    } catch (Exception e) {
                        System.err.println("Error loading local image from: " + imagePath + " - " + e.getMessage());
                        currentImage.setImage(null);
                        currentImageUrlField.setText(imagePath); // Biarkan path tetap di field
                    }
                } else {
                    System.err.println("Local image file not found: " + imagePath);
                    currentImage.setImage(null); // Kosongkan jika file tidak ditemukan
                    currentImageUrlField.setText(imagePath); // Tetap tampilkan path yang tidak valid
                }
            } else {
                currentImage.setImage(null);
                currentImageUrlField.setText("");
            }
        }
    }

    /**
     * Mengatur PostFrameController (jika diperlukan untuk refresh UI setelah edit).
     * @param controller Instans PostFrameController
     */
    public void setPostFrameController(PostFrameController controller) {
        this.postFrameController = controller;
    }

    private void handleSave() {
        String title = currentTitle.getText().trim();
        String description = currentDesc.getText().trim();
        List<String> tags = Arrays.asList(currentTags.getText().trim().split("\\s+"));

        Window currentWindowForAlert = null;
        if (dialogStage != null) {
            currentWindowForAlert = dialogStage; // Stage dialog ini adalah pemilik Alert
        } else if (editPostpopup != null && editPostpopup.getScene() != null) {
            currentWindowForAlert = editPostpopup.getScene().getWindow();
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

    private void handleCancel() {
        System.out.println("Cancel button clicked!");
        if (ownerPopup != null) {
            ownerPopup.hide(); // Sembunyikan popup
        } else {
            System.out.println("Dialog stage is null!");
        }
    }


    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Postingan");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Dapatkan owner window dari popup yang memuat controller ini
        Stage mainStage = Main.getPrimaryStage();

        if (mainStage != null) {

            boolean wasOwnerPopupShowing = false;
            boolean wasParentPopupShowing = false;

            if (ownerPopup != null && ownerPopup.isShowing()) {
                ownerPopup.hide();
                wasOwnerPopupShowing = true;
            }
            if (parentPopup != null && parentPopup.isShowing()) {
                parentPopup.hide();
                wasParentPopupShowing = true;
            }

            selectedImageFile = fileChooser.showOpenDialog(mainStage);


            if (wasOwnerPopupShowing && ownerPopup != null) {
                // Tampilkan kembali di posisi terakhirnya
                ownerPopup.show(mainStage, ownerPopup.getX(), ownerPopup.getY());
            }

            if (wasParentPopupShowing && parentPopup != null) {
                 parentPopup.show(mainStage, parentPopup.getX(), parentPopup.getY());
             }

        } else {
            System.err.println("Error: Main application Stage is not available. FileChooser might not appear correctly.");
            selectedImageFile = fileChooser.showOpenDialog(null); // Fallback
        }

        if (selectedImageFile != null) {
            String filePathUri = selectedImageFile.toURI().toString();
            currentImageUrlField.setText(selectedImageFile.getAbsolutePath()); // Simpan absolute path ke TextField
            try {
                Image image = new Image(filePathUri); // Muat gambar dari URI
                currentImage.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat memuat gambar: " + e.getMessage(), getOwnerWindowForAlert());
            }
        }
    }

    private Window getOwnerWindowForAlert() {
        if (ownerPopup != null && ownerPopup.getOwnerWindow() != null) {
            return ownerPopup.getOwnerWindow();
        } else if (editPostpopup != null && editPostpopup.getScene() != null && editPostpopup.getScene().getWindow() != null) {
            return editPostpopup.getScene().getWindow();
        }
        return null;
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

    private String createNewFileName(){
//        ---- create new profile file's name ----
        Random randomInt = new Random();
        String getRandomNumber = String.valueOf(randomInt.nextInt(1000) + 1000);

        LocalDateTime getDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String formatDate = getDateTime.format(formatter);
//        ---- get extension ----
        String imageUrl = currentImage.getImage().getUrl();
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
            Image currentImage = this.currentImage.getImage();
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

}
