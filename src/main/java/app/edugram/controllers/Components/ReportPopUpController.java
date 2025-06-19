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

        if (reason.trim().isEmpty()) { // Menggunakan trim() untuk mengabaikan spasi kosong
            System.out.println("Alasan laporan tidak boleh kosong!");

            Alert alert = new Alert(Alert.AlertType.WARNING); // Tipe WARNING
            alert.setTitle("Peringatan");
            alert.setHeaderText(null); // Tidak ada header text
            alert.setContentText("Alasan laporan tidak boleh kosong. Mohon masukkan alasan Anda.");

            // Set owner window agar alert muncul di atas pop-up laporan
            if (ownerPopup != null && ownerPopup.getOwnerWindow() != null) {
                alert.initOwner(ownerPopup.getOwnerWindow());
            } else {
                System.err.println("Warning: Could not set owner window for validation alert.");
            }

            alert.showAndWait(); // Tampilkan alert dan tunggu hingga ditutup
            return; // Hentikan proses jika alasan kosong
        }

        insertReport();

        System.out.println("Tombol Laporkan di pop-up laporan diklik.");
        System.out.println("Postingan Dilaporkan: " + reportedPostTitle);
        System.out.println("Alasan Laporan: " + reason);
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
        if (parentPopup != null) {
            parentPopup.hide();
        }
        System.out.println("Laporan berhasil dikirim! (Demo)");
    }

    private void insertReport(){
        ReportModel rep = new ReportModel();
        rep.setData(
                String.valueOf(currentPost.getId()),
                String.valueOf(Sessions.getUserId()),
                reportReasonTextArea.getText()
        );
        if(rep.validate()){
            Notices.customNote("Already reported", "Anda hanya dapat lapor sekali(1x) saja ");
            System.out.println("ReportPopUpController.insertReport");
            return;
        }

        rep.create(rep);
    }
}


