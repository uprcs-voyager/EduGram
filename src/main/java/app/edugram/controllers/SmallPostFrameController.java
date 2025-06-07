package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class SmallPostFrameController {
    @FXML private ImageView postContent;
    @FXML private Label postDesc;
    @FXML private Label postLike;
    @FXML private Label postTitle;
    @FXML private Button dislikeBtn;
    @FXML private Button likeBtn;
    @FXML private Button profileBtn;
    @FXML private ImageView postProfile;
    @FXML private Button saveBtn;
    @FXML private Button tag;
    @FXML private HBox tagBox;
    @FXML private VBox rootContainer;

    private PostModel currentPost;
    @FXML
    public Button buttonpost;

    private Tooltip postTooltip;

    @FXML
    private ImageView smallpost;

    private PostModel getCurrentPost;
    private ExploreController exploreController;


    @FXML
    public void initialize() {
        smallpost.setFitWidth(420);
        smallpost.setFitHeight(308);
        smallpost.setPreserveRatio(false);
        smallpost.setMouseTransparent(true);

        postTooltip = new Tooltip();
        Tooltip.install(buttonpost, postTooltip);

        if (buttonpost != null) {
            buttonpost.setOnAction(this::handlePostClick);
        }

    }

    public void setExploreController(ExploreController exploreController) {
        this.exploreController = exploreController;
    }

    public void setData(PostModel postModel){
        this.currentPost = postModel;

        String imageFilename = postModel.getPostContent();
        try {
            URL imageResource = getClass().getResource("/app/edugram/userData/Images/posts/" + imageFilename);


            if (imageResource != null) {
                Image image = new Image(imageResource.toExternalForm());
                smallpost.setImage(image);
            } else {
                System.err.println("ERROR: Image file not found in resources: " + imageFilename);
            }
        } catch (Exception e) {
            System.err.println("FATAL: Could not load image. Error: " + e.getMessage());
        }

        if (postTooltip != null && postModel.getTitle() != null) {
            postTooltip.setText(postModel.getTitle());
        } else {
            postTooltip.setText("No Title Available"); // Fallback jika judul kosong
        }

    }

    private void handlePostClick(ActionEvent event) {
        if (currentPost == null) {
            System.err.println("ERROR: current post is null");
            return;
        }
        if (exploreController != null) {
            exploreController.ShowPostDetail(currentPost);
        } else {
            System.err.println("ERROR: exploreController is null");
        }
    }

}
