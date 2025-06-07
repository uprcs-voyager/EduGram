package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
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

public class PostOptionPopUpController {
    @FXML private VBox popupRoot;
    @FXML private Button reportButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Popup ownerPopup; // Referensi ke Popup itu sendiri
    private PostFrameController postFrameController;

    public void initialize() {
        reportButton.setOnAction(event -> handleReport());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());

        // Styling dasar (Anda bisa pindahkan ini ke CSS jika mau)
        reportButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        editButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white;");

        // Untuk tujuan demonstrasi front-end saja, kita asumsikan ini adalah post milik user
        // sehingga Edit dan Delete terlihat. Di produksi, ini akan disesuaikan.
        editButton.setVisible(true);
        editButton.setManaged(true);
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
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
    }

}
