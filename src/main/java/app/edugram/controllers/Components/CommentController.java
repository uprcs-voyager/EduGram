package app.edugram.controllers.Components;

import app.edugram.models.CommentModel;
import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.scene.image.ImageView; // nampilkan gambar profil di komentar

import javax.swing.*;
import javax.xml.stream.events.Comment;
import java.awt.event.ActionEvent;
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

    private final String commentorImagePath = "/app/edugram/userData/Images/profile_pictures/";

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

        List<CommentModel> commentData = new CommentModel().listAll(postId);

        if (!commentData.isEmpty()) {
            for (CommentModel comment : commentData) {
                // Panggil createCommentView tanpa mempedulikan profilePicFileName dari DummyComment
                vboxCommentsContainer.getChildren().add(
                        createCommentView(
                                comment.getUsername(),
                                comment.getCommentText(),
                                comment.getComCreatedAt(),
                                comment.getProfilePicture()) // profilePicFileName dihilangkan dari parameter
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

        // Validasi input kosong
        if (commentText.isEmpty()) {
            System.out.println("Komentar tidak boleh kosong.");
            return;
        }

        String currentDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now());

        List<String> value = List.of(
                String.valueOf(Sessions.getUserId()),
                String.valueOf(postId),
                commentText,
                currentDate,
                currentDate
        );

        CommentModel comment = new CommentModel();
        boolean isSuccess = comment.set(value);

        if (isSuccess) {
            // Bersihkan field komentar
            commentTextfield.clear();

            // Refresh komentar
            loadDummyComments();

            // Opsional: Fokus balik ke kolom komentar
            commentTextfield.requestFocus();
        } else {
            System.out.println("Gagal mengirim komentar.");
            // Kamu bisa juga munculkan alert di sini jika mau.
        }
    }



    private void handleCancelComment() {
        if (ownerPopup != null) {
            ownerPopup.hide();
        }
    }

    private HBox createCommentView(String username, String commentText, String timestamp, String commenPfp) {
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
            profileImage = new Image(getClass().getResource(commentorImagePath + commenPfp).toExternalForm());
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading image from resource: " + commentorImagePath);
            e.printStackTrace();
        }

        if (profileImage != null) {
            commentedUserPic.setImage(profileImage);
        } else {
            System.err.println("Failed to load profile image for " + username + ". Check path: " + commentorImagePath);
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

        Label timestampLabel = new Label(timestamp.format(String.valueOf(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
        timestampLabel.getStyleClass().add("comment-timestamp-label");

        vboxTextContent.getChildren().addAll(commentedUsernameLabel, commentedTextLabel, timestampLabel);

        hbox.getChildren().addAll(commentedUserPic, vboxTextContent);
        return hbox;
    }



}




