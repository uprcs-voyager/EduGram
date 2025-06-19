package app.edugram.controllers.Components;

import app.edugram.models.CommentModel;
import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.scene.image.ImageView; // nampilkan gambar profil di komentar

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
public class CommentController {

    @FXML private BorderPane borderpaneComment;
    @FXML private Label currentPostTitle;
    @FXML private TextField commentTextfield;
    @FXML private Button cancelCommentBtn;
    @FXML private Button postCommentBtn;
    @FXML private ScrollPane rootScrollpane;
    @FXML private VBox vboxCommentsContainer;

    private Popup ownerPopup;
    private int postId;
    private String postTitleText;
    private CommentModel commentModel;

    private final String LAPWIING_IMAGE_PATH = "/app/edugram/userData/images/profile_pictures/lapwiing.jpg";

    public void initialize() {
        if (vboxCommentsContainer == null) {
            vboxCommentsContainer = new VBox(10); // Jarak antar komentar 10px
            rootScrollpane.setContent(vboxCommentsContainer);
        }
        vboxCommentsContainer.setPadding(new javafx.geometry.Insets(10)); // Padding dalam container komentar
        vboxCommentsContainer.setSpacing(10); // Spacing antar elemen komentar

        // Event handler untuk tombol
        postCommentBtn.setOnAction(event -> handlePostComment());
        cancelCommentBtn.setOnAction(event -> handleCancelComment());

        // Debugging: Pastikan currentPostTitle terhubung
        if (currentPostTitle == null) {
            System.err.println("FXML Error: currentPostTitle is null. Check fx:id in FXML.");
        }

    }

    public void setPopup(Popup popup) {
        this.ownerPopup = popup;
    }

    public void setPostData(int postId, String postTitle) {
        this.postId = postId;
        this.postTitleText = postTitle;
        currentPostTitle.setText(postTitle); // Set judul postingan di label
        loadDummyComments();
    }

    private void loadDummyComments() {
        vboxCommentsContainer.getChildren().clear(); // Bersihkan komentar lama
        class DummyComment {
            String username;
            String commentText;
            LocalDateTime timestamp;
            String profilePicFileName; // Nama file gambar profil

            DummyComment(String username, String commentText, String profilePicFileName, LocalDateTime timestamp) {
                this.username = username;
                this.commentText = commentText;
                this.profilePicFileName = profilePicFileName;
                this.timestamp = timestamp;
            }
        }

        List<DummyComment> dummyComments = Arrays.asList(
                new DummyComment("user123", "Nice post! Keep up the good work!", "profile1.jpg", LocalDateTime.now().minusDays(2)),
                new DummyComment("coder_gal", "This is really insightful. Thanks for sharing!", "profile2.png", LocalDateTime.now().minusHours(10)),
                new DummyComment("dev_guy", "Agreed! Very helpful. What's next?", "profile3.jpg", LocalDateTime.now().minusHours(2)),
                new DummyComment("design_pro", "Love the design elements in this content.", "lapwiing.jpg", LocalDateTime.now().minusMinutes(30))
        );

        if (!dummyComments.isEmpty()) {
            for (DummyComment comment : dummyComments) {
                // Panggil createCommentView tanpa mempedulikan profilePicFileName dari DummyComment
                vboxCommentsContainer.getChildren().add(
                        createCommentView(comment.username, comment.commentText, comment.timestamp) // profilePicFileName dihilangkan dari parameter
                );
            }
        } else {
            Label noCommentsLabel = new Label("No comments yet. Be the first to comment!");
            noCommentsLabel.getStyleClass().add("no-comments-label");
            vboxCommentsContainer.getChildren().add(noCommentsLabel);
        }
        // Scroll ke bawah setelah memuat komentar
        rootScrollpane.vvalueProperty().bind(vboxCommentsContainer.heightProperty());
    }

    private void handlePostComment() {

        String commentText = commentTextfield.getText().trim();
        if (commentText.isEmpty()) {
            return;
        }

        // Ini hanya dummy fungsionalitas untuk frontend
        String currentUser = "You"; // Atau ambil dari Sessions.getUserName() jika ada
        LocalDateTime now = LocalDateTime.now();

        vboxCommentsContainer.getChildren().add(
                createCommentView(currentUser, commentText, now)
        );
        commentTextfield.clear(); // Bersihkan input field
        System.out.println("Dummy comment added: " + commentText);

        // Scroll ke komentar terbaru
        rootScrollpane.vvalueProperty().bind(vboxCommentsContainer.heightProperty());







//        String commentText = commentTextfield.getText().trim();
//        if (commentText.isEmpty()) {
//            // Tampilkan pesan kesalahan atau peringatan jika perlu
//            return;
//        }
//
//        int userId = Sessions.getUserId(); // Dapatkan ID pengguna yang sedang login
//        String username = Sessions.getUsername(); // Dapatkan username yang sedang login
//        if (userId == -1 || username == null || username.isEmpty()) {
//            System.err.println("User session not valid. Cannot post comment.");
//            // Tampilkan alert ke pengguna
//            return;
//        }
//
//        CommentModel.Comment newComment = new CommentModel.Comment(
//                -1, // ID akan di-generate oleh DB
//                postId,
//                userId,
//                commentText,
//                LocalDateTime.now(),
//                username
//        );
//
//        boolean success = commentModel.addComment(newComment);
//
//        if (success) {
//            commentTextfield.clear();
//            loadComments(); // Muat ulang semua komentar
//            System.out.println("Comment posted successfully!");
//        } else {
//            System.err.println("Failed to post comment.");
//            // Tampilkan alert error ke pengguna
//        }
    }


    private void handleCancelComment() {
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
    }

    private HBox createCommentView(String username, String commentText, LocalDateTime timestamp) {
        HBox hbox = new HBox(10); // Spacing 10px antar elemen di HBox
        hbox.setPadding(new javafx.geometry.Insets(5));
        hbox.getStyleClass().add("comment-entry");

        // Gambar Profil
        ImageView commentedUserPic = new ImageView();
        commentedUserPic.setFitHeight(59.0);
        commentedUserPic.setFitWidth(64.0);
        commentedUserPic.setPreserveRatio(false);
        commentedUserPic.setPickOnBounds(true);



        Image profileImage = null;
        try {
            profileImage = new Image(getClass().getResource(LAPWIING_IMAGE_PATH).toExternalForm());
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading image from resource: " + LAPWIING_IMAGE_PATH);
            e.printStackTrace();
        }

        if (profileImage != null) {
            commentedUserPic.setImage(profileImage);
        } else {
            System.err.println("Failed to load profile image for " + username + ". Check path: " + LAPWIING_IMAGE_PATH);
               }

        // Bulatkan gambar profil
        Circle clip = new Circle(commentedUserPic.getFitWidth() / 2, commentedUserPic.getFitHeight() / 2, commentedUserPic.getFitWidth() / 2);
        commentedUserPic.setClip(clip);

        // VBox untuk Username, Komentar, dan Timestamp
        VBox vboxTextContent = new VBox(2); // Spacing 2px
        vboxTextContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(vboxTextContent, javafx.scene.layout.Priority.ALWAYS);

        Label commentedUsernameLabel = new Label("@" + username);
        commentedUsernameLabel.getStyleClass().add("comment-username-label");

        Label commentedTextLabel = new Label(commentText);
        commentedTextLabel.setWrapText(true); // untuk teks panjang
        commentedTextLabel.getStyleClass().add("comment-text-label");

        Label timestampLabel = new Label(timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        timestampLabel.getStyleClass().add("comment-timestamp-label");


        vboxTextContent.getChildren().addAll(commentedUsernameLabel, commentedTextLabel, timestampLabel);

        hbox.getChildren().addAll(commentedUserPic, vboxTextContent);
        return hbox;
    }



}




