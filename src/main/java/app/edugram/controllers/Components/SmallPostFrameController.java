package app.edugram.controllers.Components;
import app.edugram.controllers.ExploreController;
import app.edugram.controllers.ProfileController;
import app.edugram.models.PostModel;
import app.edugram.utils.PostClickHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


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
    private ProfileController profileController;
    private PostClickHandler postClickHandler;

    @FXML
    public void initialize() {
        smallpost.setFitWidth(416);
        smallpost.setFitHeight(308);
        smallpost.setPreserveRatio(false);
        smallpost.setMouseTransparent(true);

        postTooltip = new Tooltip();
        Tooltip.install(buttonpost, postTooltip);



    }

//    public void setExploreController(ExploreController exploreController) {
//        this.exploreController = exploreController;
//    }
//
//    public void setProfileController(ProfileController profileController) {
//        this.profileController = profileController;
//    }

    public void setData(PostModel postModel){
        this.currentPost = postModel;

        String imageFilename = postModel.getPostContent();
        try {
            String mainPath = "src/main/resources/app/edugram/userData/Images/";
            Path projectRoot = Paths.get("").toAbsolutePath();
            Path postImagePath = projectRoot.resolve(mainPath + "posts/" + imageFilename);

            File postImageFile = postImagePath.toFile();
            if (postImageFile.exists()) {
                Image image = new Image(postImageFile.toURI().toString());
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

    public void setPostClickHandler(PostClickHandler handler) {
        this.postClickHandler = handler;

        if(buttonpost != null) {
            buttonpost.setOnAction(this::handlePostClick);
        } else {
            System.out.println("buttonpost is null");
        }
    }

    private void handlePostClick(ActionEvent event) {
        if (currentPost == null) {
            System.err.println("ERROR: current post is null");
            return;
        }
        if (postClickHandler != null) {
            postClickHandler.onPostClicked(currentPost);
        }
        else {
            System.err.println("ERROR: profilecontroller is null");
        }
    }

}
