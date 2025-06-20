package app.edugram.controllers.Components;
import app.edugram.Main;
import app.edugram.controllers.ProfileController;
import app.edugram.models.*;
import app.edugram.utils.PageAction;
import app.edugram.utils.Sessions;
import app.edugram.utils.PostClickHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.Map;

public class ReportController {
    @FXML
    private Button currentReportBtn;
    @FXML
    private Button noticedReportBtn;
    @FXML
    private ScrollPane currentReportScrollPane;
    @FXML
    private VBox reportedPostsContainer;


    private Map<Integer, ReportedPostController> activeReportedPosts = new HashMap<>();
    private Map<Integer, Node> activeReportedPostNodes = new HashMap<>();

    @FXML
    public void initialize() {
        if (reportedPostsContainer != null) {
            reportedPostsContainer = new VBox(5);
            reportedPostsContainer.setPadding(new javafx.geometry.Insets(10));
            currentReportScrollPane.setContent(reportedPostsContainer);
        }

        currentReportBtn.setOnAction(event -> loadCurrentReport());
        noticedReportBtn.setOnAction(event -> loadNoticedReport());

        loadCurrentReport();
    }

    public void addOrUpdateReportedPost(PostModel post, String reporterUsername, String reportReason, LocalDateTime timestamp) {
        int postId = post.getId();

        if (activeReportedPosts.containsKey(postId)) {
            //            post sudah sudah ada, tambahin report nya saja
            ReportedPostController existingController = activeReportedPosts.get(postId);
            existingController.addReportReason(reporterUsername, reportReason, timestamp);
            System.out.println("menambahkan report baru untuk post dengan ID: " + postId);
        } else {
//            post belum pernah di report buat reportPost baru
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/report/reportedpost.fxml"));
                HBox reportedPostView = loader.load();

                ReportedPostController controller = loader.getController();
                controller.setReportedPostData(post); // set data di postingan
                controller.addReportReason(reporterUsername, reportReason, timestamp);

                // simpan referensi controller dan node ke map
                activeReportedPosts.put(postId, controller);
                activeReportedPostNodes.put(postId, reportedPostView);

                // nambahin container (HBox reported post) ke VBox yang ada di dalam scrollpane
                reportedPostsContainer.getChildren().add(reportedPostView);
                System.out.println("Created new reported post card for Post ID: " + postId);

            } catch (IOException e) {
                System.err.println("Failed to load reported_post.fxml for Post ID " + postId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void loadCurrentReport() {
        reportedPostsContainer.getChildren().clear();
        activeReportedPosts.clear();
        activeReportedPostNodes.clear();

        // Ambil semua laporan yang BELUM dinotcice dari database
        Map<Integer, List<ReportModel.ReportData>> groupedReports = ReportModel.getAllGroupedReportsFromDatabase();

        for (Map.Entry<Integer, List<ReportModel.ReportData>> entry : groupedReports.entrySet()) {
            int postId = entry.getKey();
            List<ReportModel.ReportData> reportsForThisPost = entry.getValue();

            if (!reportsForThisPost.isEmpty()) {
                // Ambil data post dari database menggunakan PostModel.read()
                PostModel postModel = new PostModel(); // Buat instance PostModel
                PostModel reportedPost = postModel.read(postId); // Ambil post dari DB

                if (reportedPost != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/edugram/pages/components/reportedpost.fxml"));
                        HBox reportedPostView = loader.load();
                        ReportedPostController controller = loader.getController();
                        controller.setReportedPostData(reportedPost);

                        // Muat semua alasan laporan
                        controller.loadAllReportReasons(reportsForThisPost);
                        activeReportedPosts.put(postId, controller);
                        activeReportedPostNodes.put(postId, reportedPostView);
                        reportedPostsContainer.getChildren().add(reportedPostView);

                    } catch (IOException e) {
                        System.err.println("Failed to load reported_post.fxml for Post ID " + postId + " during initial load: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Post data not found for reported Post ID: " + postId);
                }
            }
        }

        // Gulir ke atas saat memuat laporan
        currentReportScrollPane.setVvalue(0.0);
    }

    private void loadNoticedReport() {
        reportedPostsContainer.getChildren().clear();
        activeReportedPosts.clear();
        activeReportedPostNodes.clear();

        // Ambil semua laporan yang SUDAH DITANDAI dari database
        Map<Integer, List<ReportModel.ReportData>> groupedReports = ReportModel.getAllNoticedReportsFromDatabase();

        if (groupedReports.isEmpty()) {
            Label noNoticed = new Label("Belum ada laporan yang ditandai.");
            reportedPostsContainer.getChildren().add(noNoticed);
        } else {
            for (Map.Entry<Integer, List<ReportModel.ReportData>> entry : groupedReports.entrySet()) {
                int postId = entry.getKey();
                List<ReportModel.ReportData> reportsForThisPost = entry.getValue();

                if (!reportsForThisPost.isEmpty()) {
                    PostModel postModel = new PostModel();
                    PostModel reportedPost = postModel.read(postId);

                    if (reportedPost != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/edugram/pages/components/reportedpost.fxml"));
                            HBox reportedPostView = loader.load();
                            ReportedPostController controller = loader.getController();
                            controller.setReportedPostData(reportedPost);
                            controller.loadAllReportReasons(reportsForThisPost); // Muat semua alasan laporan


                            reportedPostsContainer.getChildren().add(reportedPostView);

                        } catch (IOException e) {
                            System.err.println("Gagal memuat reportedpost.fxml untuk Post ID " + postId + " saat memuat laporan diperhatikan: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Data Post tidak ditemukan untuk Post ID yang diperhatikan: " + postId);
                    }
                }
            }
        }
        currentReportScrollPane.setVvalue(0.0);
    }


}


