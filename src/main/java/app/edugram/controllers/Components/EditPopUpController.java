package app.edugram.controllers.Components;
import app.edugram.Main;
import app.edugram.models.PostModel; // Pastikan PostModel sudah ada dan memiliki metode update
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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


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
        System.out.println("Save button clicked!");

//        String newTitle = titlePostField.getText();
//        String newDescription = descPostArea.getText();
//        String newImageUrl = imagePostField.getText();
//
//        if (postToEdit != null) {
//            // Lakukan validasi input jika diperlukan
//            if (newTitle.isEmpty() || newDescription.isEmpty() || newImageUrl.isEmpty()) {
//                // Tampilkan pesan error ke pengguna
//                System.err.println("Semua field harus diisi!");
//                return;
//            }
//
//            // Update data di model
//            postToEdit.setTitle(newTitle);
//            postToEdit.setDescription(newDescription);
//            postToEdit.setPostContent(newImageUrl);
//
//            // Panggil metode update di PostModel (ini harus Anda implementasikan di PostModel Anda)
//            // Contoh:
//            try {
//                // Pastikan PostModel Anda memiliki metode update yang mengambil ID dan data yang diupdate
//                // Contoh hipotesis:
//                postToEdit.update(
//                        postToEdit.getId(),
//                        newTitle,
//                        newDescription,
//                        newImageUrl
//                );
//                System.out.println("Post updated successfully!");
//
//                // Refresh UI di PostFrameController jika ada
//                if (postFrameController != null) {
//                    postFrameController.refreshPostDisplay(postToEdit); // Contoh metode refresh
//                }
//
//                if (ownerPopup != null) {
//                    ownerPopup.hide(); // Sembunyikan popup setelah berhasil
//                }
//
//            } catch (Exception e) {
//                System.err.println("Failed to update post: " + e.getMessage());
//                e.printStackTrace();
//                // Tampilkan alert error ke pengguna
//            }
//
//        } else {
//            System.err.println("No post data to save.");
//        }
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



}
