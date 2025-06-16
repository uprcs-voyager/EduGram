package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
import javafx.scene.control.Alert;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.scene.control.ButtonType;

public class PostOptionPopUpController {
    @FXML private VBox popupRoot;
    @FXML private Button reportButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Popup ownerPopup; // Referensi ke Popup itu sendiri
    private PostFrameController postFrameController;
    private PostModel postModel;
    private PostModel currentPost;

    public void initialize() {
        reportButton.setOnAction(event -> handleReport());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());

        // Styling dasar (Anda bisa pindahkan ini ke CSS jika mau)
        reportButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        editButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white;");
    }

    public void setPostData(PostModel post) {
        this.currentPost = post;
        boolean isVisible = currentPost.getUserId().equals(String.valueOf(Sessions.getUserId()));
        editButton.setVisible(isVisible);
        editButton.setManaged(isVisible);
        deleteButton.setVisible(isVisible);
        deleteButton.setManaged(isVisible);
    }

    public void setPostFrameController(PostFrameController controller) {
        this.postFrameController = controller;
    }

    public void setPopup(Popup popup) {
        this.ownerPopup = popup;
    }


    private void handleReport() {
        System.out.println("Report button clicked! (Front-end demo)");
        if (ownerPopup != null) {
            ownerPopup.hide(); // Sembunyikan pop-up setelah aksi
        }
        showreportpopup();
    }

    private void handleEdit() {
        System.out.println("Edit button clicked! (Front-end demo)");
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
    }

    private void handleDelete() {
        System.out.println("Delete button clicked! (Front-end demo)");
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
        String postTitle = (currentPost != null) ? currentPost.getTitle() : "Postingan ini";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Penghapusan");
        alert.setHeaderText(null); // Tidak perlu header text
        alert.setContentText("Apakah Anda yakin ingin menghapus \"" + postTitle + "\"?\n" +
                "Tindakan ini tidak dapat dibatalkan.");

        if (ownerPopup != null && ownerPopup.getOwnerWindow() != null) {
            alert.initOwner(ownerPopup.getOwnerWindow());
        } else {
            System.err.println("Warning: Could not set owner window for delete alert.");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            PostModel del = new PostModel();
            del.delete(currentPost.getId());
        } else {
            // Pengguna mengklik Batal atau menutup alert
            System.out.println("Delete action cancelled for post: " + postTitle);
             if (ownerPopup != null) {
                 ownerPopup.show(ownerPopup.getOwnerWindow(), ownerPopup.getX(), ownerPopup.getY());
             }
        }

    }


    private void showreportpopup () {
        try {
            Popup reportPopup = new Popup();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("pages/components/popup_option/report_popup.fxml"));
            VBox reportPopupContent = loader.load();

            ReportPopUpController reportController = loader.getController();
            reportController.setPopup(reportPopup);
            reportController.setParentPopup(this.ownerPopup);
            reportController.setPostData(this.currentPost);

            reportPopup.getContent().add(reportPopupContent);

            if (ownerPopup != null && ownerPopup.getOwnerWindow() != null) {
                Window ownerWindow = ownerPopup.getOwnerWindow();
                double x = ownerWindow.getX() + (ownerWindow.getWidth() / 2) - (reportPopupContent.prefWidth(-1) / 2);
                double y = ownerWindow.getY() + 50.0;

                reportPopup.show(ownerWindow, x, y);

                System.out.println("Report popup shown at top center of window.");
            } else {
                System.err.println("Owner window not available for positioning report popup.");
                reportPopup.show(null);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load report popup: " + e.getMessage());
    }

} }
