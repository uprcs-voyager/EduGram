package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import javafx.fxml.FXMLLoader;
import java.io.File; // Untuk memuat dari File
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths; // Untuk Path
import java.util.List;
import java.util.ResourceBundle;
import app.edugram.utils.Sessions;
import java.net.MalformedURLException; // Untuk URL gambar
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox; // Atau HBox, tergantung root smallpost.fxml Anda
import javafx.scene.shape.Circle;
import javafx.application.Platform; // Untuk pembaruan UI di JavaFX Thread

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {
    // === Elemen FXML untuk Informasi Profil ===
    @FXML private VBox rootContainer;
    @FXML private ImageView profilePictureView;
    @FXML private Label usernameLabel;
    @FXML private Label postCountLabel;
    @FXML private Label followersCountLabel;
    @FXML private Label followingCountLabel;
    @FXML private Label bioLabel;
    @FXML private Button editProfileButton;

    // === Elemen FXML untuk Grid Postingan ===
    @FXML private ScrollPane contentScrollPane;
    @FXML private GridPane userPostsGrid;

    // Optional: Untuk navigasi antar halaman jika diperlukan
    private BaseViewController parentController;

    public void setParentController(BaseViewController parentController) {
        this.parentController = parentController;
    }

    public void initialize() {
        // --- Bagian 1: Mengisi Data Informasi Profil (Dummy untuk Tampilan Awal) ---
        // Anda bisa mengganti ini nanti dengan data asli dari database
        usernameLabel.setText("NamaPenggunaAnda");
        postCountLabel.setText("XX");    // Akan diisi dari DB nanti
        followersCountLabel.setText("YY"); // Akan diisi dari DB nanti
        followingCountLabel.setText("ZZ"); // Akan diisi dari DB nanti
        bioLabel.setText("Ini adalah biografi singkat saya. Selamat datang di profil saya!");

        // Muat gambar profil dummy/placeholder
        try {
            // Pastikan Anda memiliki 'default_profile.png' di src/main/resources/app/edugram/assets/Image/
            Image dummyProfilePic = new Image(getClass().getResource("/app/edugram/assets/Image/default_profile.png").toExternalForm());
            profilePictureView.setImage(dummyProfilePic);
            // Kunci ukuran agar gambar profil tetap konsisten
            profilePictureView.setFitWidth(120);
            profilePictureView.setFitHeight(120);
            // Clip agar bulat (sesuai FXML)
            Circle clip = new Circle(profilePictureView.getFitWidth() / 2,
                    profilePictureView.getFitHeight() / 2,
                    profilePictureView.getFitWidth() / 2);
            profilePictureView.setClip(clip);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar profil default: " + e.getMessage());
        }

        // Tambahkan event handler dummy untuk tombol edit
        if (editProfileButton != null) {
            editProfileButton.setOnAction(event -> System.out.println("Tombol Edit Profile diklik (dummy)!"));
        }

        // --- Bagian 2: Mengisi Grid Postingan Menggunakan Logika Mirip Explore ---
        // Panggil metode untuk memuat semua postingan dari database
        // (Ini akan sama persis dengan yang dilakukan di BerandaController untuk explore)
        loadAllPostsIntoUserGrid();
    }

    // Metode ini akan mengambil semua postingan dari database, mirip dengan halaman Explore.
    // Nanti, Anda bisa memodifikasi query ini untuk hanya mengambil postingan pengguna tertentu.
    private void loadAllPostsIntoUserGrid() {
        List<PostModel> allPosts = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            // Sesuaikan query SQL ini dengan struktur database Anda.
            // Pastikan kolom-kolom yang diambil sesuai dengan PostModel Anda.
            String query = "SELECT p.id, p.user_id, p.title, p.description, p.image_filename, p.tags, p.created_at, " +
                    "u.username, u.profile_picture_filename " +
                    "FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC";

            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PostModel post = new PostModel(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_filename"), // Ini adalah nama file gambar post
                        rs.getString("tags"),
                        rs.getTimestamp("created_at")
                );
                // Penting: Set username dan profile picture dari JOIN untuk SmallPostController
                post.setPostUsername(rs.getString("username"));
                post.setProfile(rs.getString("profile_picture_filename"));

                allPosts.add(post);
            }
        } catch (SQLException e) {
            System.err.println("Error memuat semua postingan untuk grid profil: " + e.getMessage());
            e.printStackTrace();
        }

        // Setelah semua PostModel berhasil dimuat, tampilkan di GridPane
        displayPostsInGrid(allPosts);
    }

    // Metode ini bertanggung jawab menempatkan setiap PostModel ke dalam smallpost.fxml
    // dan menambahkannya ke GridPane. Ini adalah inti dari re-use komponen.
    private void displayPostsInGrid(List<PostModel> posts) {
        // Penting: Pastikan operasi UI di JavaFX Application Thread
        Platform.runLater(() -> {
            userPostsGrid.getChildren().clear(); // Bersihkan grid sebelumnya jika ada
            int col = 0;
            int row = 0;
            int maxCols = userPostsGrid.getColumnConstraints().size(); // Ambil jumlah kolom dari FXML

            for (PostModel post : posts) {
                try {
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("components/smallpost.fxml"));
                    VBox smallPostCard = loader.load(); // Root dari smallpost.fxml harus VBox atau HBox
                    SmallPostController smallPostController = loader.getController();

                    // Panggil metode setData di SmallPostController yang menerima PostModel
                    // SmallPostController Anda akan menggunakan data ini untuk memuat gambar
                    smallPostController.setData(post);

                    // Tambahkan komponen smallpost ke grid
                    userPostsGrid.add(smallPostCard, col, row);

                    // Tambahkan event handler untuk klik pada smallpost (opsional, tapi bagus untuk interaksi)
                    final int postId = post.getId();
                    smallPostCard.setOnMouseClicked(event -> handleSmallPostClick(postId));

                    col++;
                    if (col == maxCols) { // Pindah ke baris baru jika kolom sudah penuh
                        col = 0;
                        row++;
                    }
                } catch (IOException e) {
                    System.err.println("Gagal memuat smallpost.fxml untuk post ID " + post.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // Metode untuk menangani klik pada smallpost (mirip BerandaController)
    private void handleSmallPostClick(int postId) {
        System.out.println("Thumbnail post ID " + postId + " diklik di halaman Profile.");
        // Di sini Anda bisa memanggil parentController untuk navigasi ke tampilan detail post
        // if (parentController != null) {
        //     parentController.loadPage("post_detail_page", String.valueOf(postId));
        // }
    }
}