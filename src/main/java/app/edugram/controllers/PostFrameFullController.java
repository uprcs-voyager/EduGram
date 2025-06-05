package app.edugram.controllers;

import app.edugram.models.PostModel;
import app.edugram.utils.Sessions;
import javafx.event.ActionEvent; // You need to import this for onAction methods
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;

public class PostFrameFullController {
    // Your FXML fields are already correct!
    @FXML private ImageView postContent;
    @FXML private Label postDesc;
    @FXML private Label postLike;
    @FXML private Label postTitle;
    @FXML private Button dislikeBtn;
    @FXML private Button likeBtn;
    @FXML private Button profileBtn;
    @FXML private Button saveBtn;
    @FXML private Button tag;
    @FXML private HBox tagBox;

    // We need this field to remember which post this controller is managing
    private PostModel currentPost;

    // --- YOUR UPDATED setData METHOD ---
    // Changed to public to be more accessible from the FXMLLoader
    public void setData(PostModel postModel){
        this.currentPost = postModel;

        String imageFilename = postModel.getPostContent();
        try {
            // Your path is correct, we'll use that.
            URL imageResource = getClass().getResource("/app/edugram/userData/images/posts/" + imageFilename);

            if (imageResource != null) {
                Image image = new Image(imageResource.toExternalForm());
                postContent.setImage(image);
            } else {
                System.err.println("ERROR: Image file not found in resources: " + imageFilename);
                // Optionally set a placeholder image here
            }
        } catch (Exception e) {
            System.err.println("FATAL: Could not load image. Error: " + e.getMessage());
        }

        postTitle.setText(postModel.getTitle());
        postDesc.setText(postModel.getDescription());
        countAvgPostLike(String.valueOf(postModel.getId()));
        profileBtn.setText(postModel.getPostUsername());

        if(postModel.exists("like", postModel.getId())){
            likeBtn.setText("hasLiked");
        }
        if(postModel.exists("dislike", postModel.getId())){
            dislikeBtn.setText("hasDisliked");
        }
        if(postModel.exists("save", postModel.getId())){
            saveBtn.setText("hasSaved");
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
        System.out.println(dislikeBtn.getText());
        if(dislikeBtn.getText().equals("hasDisliked")){
            dislikeAction();
        }
        likeAction();
        countAvgPostLike(String.valueOf(currentPost.getId()));
    }
    void likeAction(){
        if(currentPost.exists("like", currentPost.getId())){
            System.out.println("like: unsetting");
            currentPost.unset("like", currentPost.getId());
            likeBtn.setText("like");
            return;
        }
        System.out.println("like: setting");
        List<String> field = List.of( "id_user", "id_post");
        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getId()));
        currentPost.set("like", field, value);
        likeBtn.setText("hasLiked");
    }

    @FXML
    void onDislikeClicked(ActionEvent event) {
        if(likeBtn.getText().equals("hasLiked")){
            likeAction();
        }
        dislikeAction();
        countAvgPostLike(String.valueOf(currentPost.getId()));
    }

    void dislikeAction(){
        if(currentPost.exists("dislike", currentPost.getId())){
            System.out.println("dislike: unsetting");
            currentPost.unset("dislike", currentPost.getId());
            dislikeBtn.setText("dislike");
            return;
        }
        System.out.println("dislike: setting");
        List<String> field = List.of( "id_user", "id_post");
        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getId()));
        currentPost.set("dislike", field, value);
        dislikeBtn.setText("hasDisliked");
    }

    void countAvgPostLike(String idPost) {
        String sumCount = String.valueOf(currentPost.getAvgLike(idPost));
        postLike.setText(sumCount);
    }

    @FXML
    void onSaveClicked(ActionEvent event) {
        if(saveBtn.getText().equals("hasSaved")){
            System.out.println("Save: Unsetting");
            currentPost.unset("save", currentPost.getId());
            saveBtn.setText("save");
            return;
        }
        System.out.println("save: setting");
        List<String> field = List.of("id_user", "id_post");
        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getId()));
        currentPost.set("save", field, value);
        saveBtn.setText("hasSaved");
    }

    @FXML
    void onProfileClicked(ActionEvent event) {
        // Your logic for viewing the post author's profile goes here
        System.out.println("Profile button clicked for user " + currentPost.getPostUsername());
    }
}