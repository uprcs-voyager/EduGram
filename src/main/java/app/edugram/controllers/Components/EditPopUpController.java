package app.edugram.controllers.Components;
import app.edugram.models.PostModel; // Pastikan PostModel sudah ada dan memiliki metode update
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.stage.Window;



public class EditPopUpController {
    @FXML private TextField titlePostField;
    @FXML private TextArea descPostArea;
    @FXML private TextField imagePostField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Popup ownerPopup; // Referensi ke Popup ini sendiri
    private PostModel postToEdit; // Postingan yang sedang diedit
    private PostFrameController postFrameController; // Opsional: Untuk merefresh UI jika perlu

    public void initialize() {
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
    }

    /**
     * Mengatur Popup yang memiliki controller ini.
     * Digunakan untuk menyembunyikan popup.
     * @param popup Instans Popup
     */
    public void setPopup(Popup popup) {
        this.ownerPopup = popup;
    }

    /**
     * Mengatur data PostModel yang akan diedit.
     * Mengisi field input dengan data postingan saat ini.
     * @param post PostModel yang akan diedit
     */
    public void setPostData(PostModel post) {
        this.postToEdit = post;
        if (postToEdit != null) {
            titlePostField.setText(postToEdit.getTitle());
            descPostArea.setText(postToEdit.getDescription());
            imagePostField.setText(postToEdit.getPostContent());
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
        }
    }

}
