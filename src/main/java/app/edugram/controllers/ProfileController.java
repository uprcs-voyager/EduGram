package app.edugram.controllers;

import app.edugram.Main;
import app.edugram.controllers.Components.PostFrameController;
import app.edugram.controllers.Components.SmallPostFrameController;
import app.edugram.models.FollowModel;
import app.edugram.models.PostModel;
import app.edugram.models.UserModel;
import app.edugram.utils.PostClickHandler;
import app.edugram.utils.Sessions;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProfileController implements Initializable, PostClickHandler {
    // ---- Controller's variabels ----
    public int whoseProfileId = Sessions.getUserId();

    // --- FXML Elements for User Info (dummy for now) ---
    @FXML private VBox contentContainer;
    @FXML private ImageView profilePictureView;
    @FXML private Label usernameLabel;
    @FXML private Label postCountLabel;
    @FXML private Label followersCountLabel;
    @FXML private Label followingCountLabel;
    @FXML private Label bioLabel;
    @FXML private Button editProfileButton;
    @FXML private Button followBtn;

    // --- FXML Elements for Post Grid ---
    @FXML private ScrollPane contentScrollPane;
    @FXML private GridPane userPostsGrid; // Ini akan menampung smallpost.fxml
    @FXML private HBox hbox_profile;
    private List<PostModel> allPostsToDisplay;

    private BaseViewController parentController;

    private Node ProfileGridView;
    private Node HboxProfile;

    public void setParentController(BaseViewController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ProfileGridView = contentScrollPane;
        HboxProfile = hbox_profile;

        loadUserProfileInformation();

        if (editProfileButton != null) {
            editProfileButton.setOnAction(event -> System.out.println("Tombol Edit Profile diklik (dummy)!"));
        }

        // Muat Semua Postingan dari Model ---
        loadAllPostsAndDisplay();
    }

    public void setUserData(int userId) {
        this.whoseProfileId = userId;
        loadAllPostsAndDisplay();
        loadUserProfileInformation();
    }

    public void loadUserProfileInformation(){
        Map<String, String> user = UserModel.findUser(String.valueOf(whoseProfileId));
        boolean isProfileMine = whoseProfileId == Sessions.getUserId();
        if (user != null);
        {
            try {
                String profileImagePath = "/app/edugram/userData/Images/profile_pictures/" + user.get("prof_pic");
                URL resource = getClass().getResource(profileImagePath);
                if (resource != null) {
                    profilePictureView.setImage(new Image(resource.toExternalForm()));
                } else {
                    System.err.println("Profile picture resource not found: " + profileImagePath);
                }
            } catch (Exception e) {
                System.err.println("Could not load profile picture: " + e.getMessage());
            }
            usernameLabel.setText(user.get("username"));
            followersCountLabel.setText(user.get("follower"));
            followingCountLabel.setText(user.get("following"));
            bioLabel.setText("Ini adalah halaman profil sementara yang menampilkan semua postingan.");

            editProfileButton.setVisible(isProfileMine);
            followBtn.setVisible(!isProfileMine);
            System.out.println(user.get("isFollow"));
            followBtn.setText(user.get("isFollow").equals("1") ? "Followed" : "Follow");
        }

        loadDefaultProfilePicture();
    }

    private void loadAllPostsAndDisplay() {
        // Operasi database harus di background thread
        Task<List<PostModel>> loadPostsTask = new Task<List<PostModel>>() {
            @Override
            protected List<PostModel> call() throws Exception {
                PostModel post = new PostModel();
                return post.listAll(whoseProfileId == Sessions.getUserId() ? "myProfile" : "profile-" + whoseProfileId);
            }
        };

        loadPostsTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                allPostsToDisplay = loadPostsTask.getValue();
                System.out.println("Semua postingan berhasil dimuat ke ProfileController: " + allPostsToDisplay.size());
                displayPostsInGrid(allPostsToDisplay);
                postCountLabel.setText(String.valueOf(allPostsToDisplay.size()));
            });
        });

        loadPostsTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                System.err.println("Gagal memuat postingan di ProfileController: " + loadPostsTask.getException().getMessage());
                loadPostsTask.getException().printStackTrace();
                userPostsGrid.getChildren().clear();
                userPostsGrid.add(new Label("Gagal memuat postingan."), 0, 0);
                GridPane.setColumnSpan(userPostsGrid.getChildren().get(0), 3);
            });
        });

        Thread thread = new Thread(loadPostsTask);
        thread.setDaemon(true); // Biarkan thread berhenti saat aplikasi ditutup
        thread.start();
    }

    private void displayPostsInGrid(List<PostModel> postsToDisplay) {
        Platform.runLater(() -> {
            userPostsGrid.getChildren().clear(); // Bersihkan grid sebelumnya

            if (postsToDisplay.isEmpty()) {
                userPostsGrid.add(new Label("Belum ada postingan untuk ditampilkan."), 0, 0);
                GridPane.setColumnSpan(userPostsGrid.getChildren().get(0), 3); // Span label across 3 columns
                return;
            }

            int columns = 0;
            int row = 0;
            int maxCols = userPostsGrid.getColumnConstraints().size();

            try {
                for (PostModel post : postsToDisplay) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(Main.class.getResource("pages/components/smallpost.fxml"));
                    VBox box = fxmlLoader.load(); // Asumsi root smallpost.fxml adalah VBox

                    SmallPostFrameController smallPostFrameController = fxmlLoader.getController();
                    smallPostFrameController.setData(post); // Set data PostModel ke SmallPostFrameController
//                  smallPostFrameController.setProfileController(this);
                    smallPostFrameController.setPostClickHandler(this);

                    // Logic untuk menempatkan di grid (sama seperti di ExploreController)
                    userPostsGrid.add(box, columns, row);
                    GridPane.setMargin(box, new Insets(1, 1, 1, 1));

                    columns++;
                    if (columns == maxCols) {
                        columns = 0;
                        row++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Gagal memuat smallpost.fxml untuk grid profil: " + e.getMessage());
            }
        });
    }

    // --- Helper Methods ---
    public void onPostClicked(PostModel post) {
        detailPost(post);
    }
    public void onPostBackClicked(PostModel post) {
        backToProfile();
    }


    // Menangani klik pada smallpost (mirip ExploreController::ShowPostDetail)
    public void detailPost(PostModel post ) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pages/components/post.fxml"));
            VBox postDetailView = fxmlLoader.load();

            PostFrameController postFrameController = fxmlLoader.getController();
            postFrameController.setData(post);
            postFrameController.setPostClickHandler(this);
            postFrameController.showBackButton(true);

            contentContainer.setAlignment(javafx.geometry.Pos.TOP_CENTER);

            contentContainer.getChildren().clear();
            contentContainer.getChildren().add(postDetailView);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load post details: " + post.getTitle());
        }
    }

    // Helper: Mencari PostModel berdasarkan ID dari daftar 'allPostsToDisplay'
    private PostModel findPostById(int postId) {
        if (allPostsToDisplay != null) {
            for (PostModel post : allPostsToDisplay) {
                if (post.getId() == postId) {
                    return post;
                }
            }
        }
        return null;
    }

    // Helper: Muat gambar placeholder default untuk profil
    private void loadDefaultProfilePicture() {
        try {
            Image dummyProfilePic = new Image(getClass().getResource("/app/edugram/userData/images/profile_pictures/lapwiing.jpg").toExternalForm());
            profilePictureView.setImage(dummyProfilePic);
            profilePictureView.setFitWidth(45);
            profilePictureView.setFitHeight(45);
            Circle clip = new Circle(profilePictureView.getFitWidth() / 2,
                    profilePictureView.getFitHeight() / 2,
                    profilePictureView.getFitWidth() / 2);
            profilePictureView.setClip(clip);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar profil default: " + e.getMessage());
        }
    }

    // Helper: Mendapatkan gambar placeholder default (jika diperlukan di tempat lain)
    private Image getDefaultProfilePlaceholder() {
        try {
            return new Image(getClass().getResource("/app/edugram/userData/images/profile_pictures/lapwiing.jpg").toExternalForm());
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar placeholder default: " + e.getMessage());
            return null;
        }
    }

    public void backToProfile() {
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(ProfileGridView);
    }

    public void clickFollowButton(ActionEvent event) {
        FollowModel follow = new FollowModel();
        System.out.println(whoseProfileId);
        if(!follow.exists(whoseProfileId)){
            System.out.println("PorfileController.clickFollowButton: Setting");
            List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(whoseProfileId));
            follow.set(value);
            followBtn.setText("Followed");
        }else{
            System.out.println("PorfileController.clickFollowButton: Unetting");
            follow.unset(whoseProfileId);
            followBtn.setText("Follow");
        }
    }
}