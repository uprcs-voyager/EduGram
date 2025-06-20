package app.edugram.controllers.Components;
import app.edugram.models.PostModel;
import app.edugram.models.ReportModel;
import app.edugram.utils.Notices;
import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Popup;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;

import java.time.LocalDateTime;


public class ReportPopUpController {
    @FXML private VBox reportPopupRoot;
    @FXML private Label postTitle;
    @FXML private TextArea reportReasonTextArea;
    @FXML private Button cancelReportButton;
    @FXML private Button submitReportButton;

    private Popup ownerPopup;
    private Popup parentPopup;
    private PostModel currentPost;

    private ReportController reportPageController;

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
//    setter untuk ReportController
    public void setReportPageController(ReportController controller) {
        this.reportPageController = controller;
    }

    private void handleCancelReport() {
        System.out.println("Cancel report");
        if(ownerPopup != null) {
            ownerPopup.hide();
        }
    }

    private void handleSubmitReport() {
        String reason = reportReasonTextArea.getText().trim(); // Gunakan trim() untuk validasi

        if (reason.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(null);
            alert.setContentText("Alasan laporan tidak boleh kosong. Mohon masukkan alasan Anda.");
            if (ownerPopup != null && ownerPopup.getOwnerWindow() != null) {
                alert.initOwner(ownerPopup.getOwnerWindow());
            }
            alert.showAndWait();
            return;
        }

        // Buat objek ReportModel baru
        ReportModel rep = new ReportModel();
        rep.setData(
                String.valueOf(currentPost.getId()),
                String.valueOf(Sessions.getUserId()),
                reason
        );

        if(rep.validate()){ // Cek apakah user sudah melaporkan post ini sebelumnya
            Notices.customNote("Already reported", "Anda hanya dapat lapor sekali(1x) saja.");
            System.out.println("Post already reported by this user.");
            // Tutup pop-up jika sudah dilaporkan
            if (ownerPopup != null) ownerPopup.hide();
            if (parentPopup != null) parentPopup.hide();
            return;
        }

        // Jika belum dilaporkan, buat laporan baru
        rep.create(rep);
        System.out.println("Report submitted successfully.");
        if (reportPageController != null) {
            // Ambil PostModel dari currentPost yang sudah diset
            // Ambil username pelapor dari Sessions (atau dari User Model)
            String reporterUsername = Sessions.getUsername(); // Asumsi Sessions.getUsername() tersedia
            LocalDateTime reportTimestamp = LocalDateTime.now(); // Ambil timestamp saat ini
            reportPageController.addOrUpdateReportedPost(currentPost, reporterUsername, reason, reportTimestamp);
        } else {
            System.err.println("ReportPageController is not set. Cannot update report UI.");
        }
        // --- Akhir Pembaruan UI ---

        // Tutup pop-up setelah pengiriman
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
        if (parentPopup != null) {
            parentPopup.hide();
        }
    }


}


