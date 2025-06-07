package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
import javafx.scene.control.Label;
import app.edugram.models.PostModel;


public class ReportPopUpController {
    @FXML private VBox reportPopupRoot;
    @FXML private Label postTitle;
    @FXML private TextArea reportReasonTextArea;
    @FXML private Button cancelReportButton;
    @FXML private Button submitReportButton;

    private Popup ownerPopup;
    private Popup parentPopup;
    private PostModel currentPost;

    public void initialize() {
        cancelReportButton.setOnAction(e -> handleCancelReport());
        submitReportButton.setOnAction(e ->handleSubmitReport());
    }

    public void setPopup(Popup popup) {
        this.ownerPopup = popup;
    }

    public void setParentPopup(Popup parentPopup) {
        this.parentPopup = parentPopup;
    }

    public void setPostData(PostModel post) {
        this.currentPost = post;
        if (postTitle != null && currentPost != null) {
            postTitle.setText(post.getTitle());
        } else {
            System.out.println("Post title is null");
        }
    }

    private void handleCancelReport() {
        System.out.println("Cancel report");
        if(ownerPopup != null) {
            ownerPopup.hide();
        }
    }

    private void handleSubmitReport() {
        String reason = reportReasonTextArea.getText();
        String reportedPostTitle = (currentPost != null) ? currentPost.getTitle() : "N/A";
        System.out.println("Tombol Laporkan di pop-up laporan diklik.");
        System.out.println("Postingan Dilaporkan: " + reportedPostTitle);
        System.out.println("Alasan Laporan: " + reason);
        if(reason.trim().isEmpty()) {
            System.out.println("isi laporan tidak boleh kosong");
            return;
        }
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
        if (parentPopup != null) {
            parentPopup.hide();
        }
        System.out.println("Laporan berhasil dikirim! (Demo)");
    }
    }


