package app.edugram.controllers;
import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;

public class PostFrameController {
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
    @FXML private Button backtoexplore;

    private PostModel currentPost;
    private Runnable returnToExploreCallBack;

    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = rootContainer.getScene();
            if (scene != null) {
                postContent.fitWidthProperty().bind(rootContainer.widthProperty().multiply(0.46));
                double radius = Math.min(postProfile.getFitWidth(), postProfile.getFitHeight()) / 2;
                Circle clip = new Circle(radius, radius, radius);
                postProfile.setClip(clip);
            }
        });
        if (backtoexplore != null) {
            backtoexplore.setOnAction((ActionEvent event) -> {
                if(returnToExploreCallBack != null) {
                    returnToExploreCallBack.run();
                }
            });
        }
        showBackButton(false);
    }

    public void setReturnToExploreCallBack (Runnable CallBack) {
        this.returnToExploreCallBack = CallBack;
    }

    public void showBackButton(boolean show) {
        if(backtoexplore != null) {
            backtoexplore.setVisible(show);
            backtoexplore.setManaged(show);
        }
    }

    public void setData(PostModel postModel){
        this.currentPost = postModel;

        String imageFilename = postModel.getPostContent();
        String profileFilename = postModel.getProfile();
        try {
            URL imageResource = getClass().getResource("/app/edugram/userData/Images/posts/" + imageFilename);
            URL profileResource = getClass().getResource("/app/edugram/userData/Images/profile_pictures/" + profileFilename);

            if (imageResource != null && profileResource != null) {
                Image image = new Image(imageResource.toExternalForm());
                postContent.setImage(image);

                Image profile = new Image(profileResource.toExternalForm());
                postProfile.setImage(profile);
            } else {
                System.err.println("ERROR: Image file not found in resources: " + imageFilename);
            }
        } catch (Exception e) {
            System.err.println("FATAL: Could not load image. Error: " + e.getMessage());
        }

        postTitle.setText(postModel.getTitle());
        postDesc.setText(postModel.getDescription());
        countAvgPostLike(String.valueOf(postModel.getId()));
        profileBtn.setText(postModel.getPostUsername());

        if(postModel.exists("like", postModel.getId())){
            likeBtn.setUserData("hasLiked");
        } else {
            likeBtn.setUserData("like");
        }
        if(postModel.exists("dislike", postModel.getId())){
            dislikeBtn.setUserData("hasDisliked");
        } else {
            dislikeBtn.setUserData("dislike");
        }
        if(postModel.exists("save", postModel.getId())){
            saveBtn.setUserData("hasSaved");
        } else {
            saveBtn.setUserData("save");
        }

        tagBox.getChildren().clear();

        List<String> tags = currentPost.getTags();
        if(tags != null && !tags.isEmpty()){
            for(String tagName : tags){
                Button tagButton = new Button(tagName);
                tagButton.getStyleClass().add("tag-button");
                tagButton.setOnAction(event -> {
                    System.out.println("Tag Clicked: " + tagName);
                });
                tagBox.getChildren().add(tagButton);
            }
        }
    }

    @FXML
    void onLikeClicked(ActionEvent event) {
        if("hasDisliked".equals(dislikeBtn.getUserData())){
            dislikeAction();
        }
        likeAction();
        countAvgPostLike(String.valueOf(currentPost.getId()));
    }

    void likeAction(){
        if("hasLiked".equals(likeBtn.getUserData())){
            System.out.println("like: unsetting");
            currentPost.unset("like", currentPost.getId());
            likeBtn.setUserData("like");
            return;
        }
        System.out.println("like: setting");
        List<String> field = List.of("id_user", "id_post");
        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getId()));
        currentPost.set("like", field, value);
        likeBtn.setUserData("hasLiked");
    }

    @FXML
    void onDislikeClicked(ActionEvent event) {
        if("hasLiked".equals(likeBtn.getUserData())){
            likeAction();
        }
        dislikeAction();
        countAvgPostLike(String.valueOf(currentPost.getId()));
    }

    void dislikeAction(){
        if("hasDisliked".equals(dislikeBtn.getUserData())){
            System.out.println("dislike: unsetting");
            currentPost.unset("dislike", currentPost.getId());
            dislikeBtn.setUserData("dislike");
            return;
        }
        System.out.println("dislike: setting");
        List<String> field = List.of("id_user", "id_post");
        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getId()));
        currentPost.set("dislike", field, value);
        dislikeBtn.setUserData("hasDisliked");
    }

    void countAvgPostLike(String idPost) {
        String sumCount = String.valueOf(currentPost.getAvgLike(idPost));
        postLike.setText(sumCount);
    }

    @FXML
    void onSaveClicked(ActionEvent event) {
        if("hasSaved".equals(saveBtn.getUserData())){
            System.out.println("Save: Unsetting");
            currentPost.unset("save", currentPost.getId());
            saveBtn.setUserData("save");
            return;
        }
        System.out.println("save: setting");
        List<String> field = List.of("id_user", "id_post");
        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getId()));
        currentPost.set("save", field, value);
        saveBtn.setUserData("hasSaved");
    }

    @FXML
    void onProfileClicked(ActionEvent event) {
        System.out.println("Profile button clicked for user " + currentPost.getPostUsername());
    }
}
