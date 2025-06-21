package app.edugram.controllers.Components;

import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.models.ReportModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // Import List

public class ReportedPostController {
    @FXML
    private Label reportedPostTitle;
    @FXML private Label postedByLabel;
    @FXML private Label postIdLabel;
    @FXML private ImageView reportedImageView;
    @FXML private ScrollPane reportScrollPane;
    @FXML private VBox reportReasonContainer;
    @FXML private Button deletePostBtn;
    @FXML private Button noticeReportBtn;

    private PostModel reportedPost;
    private ReportController reportPageController;
    //    consturkutor buat FXMl loader
    public ReportedPostController() {

    }

    public void setReportPageController(ReportController controller) {
        this.reportPageController = controller;
    }


    @FXML
    private void initialize() {
        if (reportReasonContainer != null) {
            reportReasonContainer = new VBox(5);
            reportScrollPane.setContent(reportReasonContainer);
        }
        reportReasonContainer.setPadding(new javafx.geometry.Insets(10));

        deletePostBtn.setOnAction(event -> handleDeletePost());
        noticeReportBtn.setOnAction(event -> handleNoticeReport())  ;
    }

    public void setReportedPostData(PostModel post) {
        this.reportedPost = post;
        reportedPostTitle.setText(post.getTitle());
        postedByLabel.setText("Posted By: "+post.getPostUsername());
        postIdLabel.setText("ID POST"+post.getId());

    }

    public void addReportReason(String reporterUsername, String reason, LocalDateTime timestamp) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("pages/components/reportreason.fxml"));
            VBox reportReasonView = loader.load();
            ReportReasonController controller = loader.getController();
            controller.setReportData(reporterUsername, reason, timestamp);


            if (reportReasonContainer != null) {
                reportReasonContainer.getChildren().add(reportReasonView);
            } else {
                System.err.println("reportReasonsContainer is null. Cannot add report reason view.");
            }
            reportScrollPane.vvalueProperty().bind(reportReasonContainer.heightProperty());
        } catch (IOException e) {
            System.err.println("Failed to load report_reason_view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadAllReportReasons(List<ReportModel.ReportData> reportsForThisPost) {
        if (reportReasonContainer != null) {
            reportReasonContainer.getChildren().clear(); // Bersihkan yang lama
            if (reportsForThisPost != null) {
                for (ReportModel.ReportData report : reportsForThisPost) {
                    addReportReason("User-" + report.getReporterUserId(), report.getReason(), report.getTimestamp());
                }
            }



        }
    }

    private void handleDeletePost() {
        System.out.println("Delete post clicked for Post ID: " + reportedPost.getId());
        // Implementasi logika hapus post dari database
        // Setelah dihapus, mungkin perlu memberi tahu ReportController untuk menghapus reportedPost ini dari tampilan
    }

    private void handleNoticeReport() {
        System.out.println("Notice report clicked for Post ID: " + reportedPost.getId());
        // Implementasi logika untuk menandai laporan ini sudah diperhatikan
        // Mungkin ubah status di database atau pindahkan ke tab "Noticed Report"
    }

    public int getReportedPostId() {
        return reportedPost != null ? reportedPost.getId() : -1;
    }

}
