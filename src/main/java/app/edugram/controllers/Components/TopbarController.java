package app.edugram.controllers.Components;

import app.edugram.Main;
import app.edugram.models.TagModel;
import app.edugram.models.UserModel;
import app.edugram.models.UserPrefTagModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane; // Tambahkan import ScrollPane
import javafx.stage.Popup;
import javafx.stage.Window;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TopbarController implements Initializable {

    // FXML Components dari topbar.fxml
    @FXML private ImageView logoImageView;
    @FXML private Label appNameLabel;
    @FXML private TextField searchTextField;
    @FXML private ImageView searchIconImageView;
    @FXML private ImageView profileImageView;
    @FXML private Button usernameButton;

    // FXML IDs dari suggestioncontent.fxml
    // suggestionContent adalah root (StackPane) dari suggestioncontent.fxml
    private StackPane suggestionContent;
    // mengambilnya secara manual
    private VBox suggestionVBoxContent;

    private Popup suggestionPopup;
    private boolean isFullyInitialized = false;

    @Override public void initialize(URL location, ResourceBundle resources) {
        loadDefaultProfilePicture();
        setupPopup();
        setupSearchField();
        setupUserProfile();
        Platform.runLater(() -> {
            isFullyInitialized = true;
            hideSuggestions();
        });
    }

    private void setupPopup() {
        suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true);

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("pages/components/suggestioncontent.fxml"));
            // Muat StackPane sebagai root dari FXML suggestioncontent.fxml
            suggestionContent = loader.load();

            // *** PENTING: Ambil VBox di dalam StackPane secara manual ***
            // suggestionContent (StackPane) punya anak ScrollPane
            // ScrollPane punya content VBox
            if (suggestionContent.getChildren().get(0) instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) suggestionContent.getChildren().get(0);
                if (scrollPane.getContent() instanceof VBox) {
                    suggestionVBoxContent = (VBox) scrollPane.getContent();
                } else {
                    System.err.println("Error: Konten ScrollPane di suggestioncontent.fxml bukan VBox.");
                }
            } else {
                System.err.println("Error: Anak pertama StackPane di suggestioncontent.fxml bukan ScrollPane.");
            }

            if (suggestionVBoxContent == null) {
                System.err.println("FATAL ERROR: suggestionVBoxContent null setelah setup popup. Pop-up mungkin tidak berfungsi.");

            }

            suggestionPopup.getContent().add(suggestionContent); // Tambahkan StackPane ke popup
        } catch (IOException e) {
            System.err.println("Gagal memuat suggestioncontent.fxml: " + e.getMessage());
            e.printStackTrace();
        }

        suggestionPopup.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !searchTextField.isFocused() && !suggestionPopup.isShowing()) {
                Platform.runLater(this::hideSuggestions);
            }
        });

        if (suggestionContent != null) {
            suggestionContent.setOnMouseExited(event -> {
                if (!searchTextField.isFocused()) {
                    hideSuggestions();
                }
            });
        }
    }


    private void setupSearchField() {
        if (searchTextField != null) {
            searchTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    if (isFullyInitialized) {
                        showSuggestions();
                        searchTextField.selectAll();
                    }
                } else {
                    Platform.runLater(() -> {
                        if (!searchTextField.isFocused() && (suggestionContent == null || !suggestionContent.isHover())) {
                            hideSuggestions();
                        }
                    });
                }
            });

            searchTextField.setOnMouseClicked(event -> {
                if (!suggestionPopup.isShowing()) {
                    showSuggestions();
                }
            });

            if (searchIconImageView != null) {
                searchIconImageView.setOnMouseClicked(event -> {
                    if (!suggestionPopup.isShowing()) {
                        showSuggestions();
                    }
                });
            }

            searchTextField.setOnKeyPressed(this::onSearchKeyPressed);
        }
    }

    private void setupUserProfile() {
        if (usernameButton != null) {
            // Set username di sini jika diperlukan
        }
    }

    @FXML
    private void onUsernameButtonClick(ActionEvent event) {
        System.out.println("Username button clicked!");
    }

    private void onSearchKeyPressed(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            performSearch();
            hideSuggestions();
        }
    }

    private void performSearch() {
        String searchQuery = searchTextField.getText().trim();
        if (!searchQuery.isEmpty()) {
            System.out.println("Searching for: " + searchQuery);
        }
    }

    private void showSuggestions() {
        // PENTING: Pastikan suggestionVBoxContent tidak null sebelum memanggil populateSuggestions
        if (suggestionPopup != null && suggestionContent != null && suggestionVBoxContent != null && !suggestionPopup.isShowing()) {
            populateSuggestions();

            Bounds boundsInScene = searchTextField.localToScreen(searchTextField.getBoundsInLocal());
            double x = boundsInScene.getMinX();
            double y = boundsInScene.getMaxY() + 5; // Tambahkan 5 piksel ke bawah

            Window window = searchTextField.getScene().getWindow();
            suggestionPopup.show(window, x, y);

            // Terapkan animasi pada StackPane suggestionContent (root popup)
            suggestionContent.setOpacity(0);
            suggestionContent.setScaleY(0.8);
            suggestionContent.setTranslateY(0);

            FadeTransition ft = new FadeTransition(Duration.millis(200), suggestionContent);
            ft.setFromValue(0); ft.setToValue(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(200), suggestionContent);
            st.setFromY(0.8); st.setToY(1);

            TranslateTransition tt = new TranslateTransition(Duration.millis(200), suggestionContent);
            tt.setFromY(0);
            tt.setToY(0);

            ft.play(); st.play(); tt.play();
            System.out.println("Saran pencarian ditampilkan (via Popup).");
        } else if (suggestionVBoxContent == null) {
            System.err.println("Warning: suggestionVBoxContent is null, cannot show suggestions.");
        }
    }

    private void hideSuggestions() {
        if (suggestionPopup != null && suggestionPopup.isShowing()) {
            if (suggestionContent != null) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), suggestionContent);
                ft.setFromValue(1); ft.setToValue(0);

                ScaleTransition st = new ScaleTransition(Duration.millis(200), suggestionContent);
                st.setFromY(1); st.setToY(0.8);

                TranslateTransition tt = new TranslateTransition(Duration.millis(200), suggestionContent);
                tt.setFromY(0);
                tt.setToY(0);

                ft.setOnFinished(e -> {
                    suggestionPopup.hide();
                });

                ft.play(); st.play(); tt.play();
            } else {
                suggestionPopup.hide();
            }
            System.out.println("Saran pencarian disembunyikan (via Popup).");
        }
    }

    private void populateSuggestions() {
        if (suggestionVBoxContent == null) {
            System.err.println("Error: suggestionVBoxContent is null, cannot populate suggestions.");
            return;
        }
        suggestionVBoxContent.getChildren().clear();

        List<String> profileNames = UserModel.getAllUsernames();
        List<String> tagNames = UserPrefTagModel.getAllTags();

        if (!profileNames.isEmpty()) {
            Label profileHeader = new Label("Profil");
            profileHeader.getStyleClass().add("suggestion-category-label");
            suggestionVBoxContent.getChildren().add(profileHeader);

            for (String profileName : profileNames) {
                HBox item = createSuggestionItem(profileName);
                suggestionVBoxContent.getChildren().add(item);
            }
            VBox.setMargin(profileHeader, new javafx.geometry.Insets(5, 0, 5, 0));
        }

        if (!tagNames.isEmpty()) {
            if (!profileNames.isEmpty()) {
                suggestionVBoxContent.getChildren().add(new Separator());
            }
            Label tagHeader = new Label("Tag");
            tagHeader.getStyleClass().add("suggestion-category-label");
            suggestionVBoxContent.getChildren().add(tagHeader);

            for (String tagName : tagNames) {
                String displayTagName = "#" + tagName;
                HBox item = createSuggestionItem(displayTagName);
                suggestionVBoxContent.getChildren().add(item);
            }
            VBox.setMargin(tagHeader, new javafx.geometry.Insets(5, 0, 5, 0));
        }

        if (profileNames.isEmpty() && tagNames.isEmpty()) {
            suggestionVBoxContent.getChildren().add(new Label("Tidak ada saran tersedia."));
        }
    }

    private HBox createSuggestionItem(String text) {
        HBox item = new HBox();
        item.getStyleClass().add("suggestion-item");
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label label = new Label(text);
        label.getStyleClass().add("suggestion-text");
        item.getChildren().add(label);
        HBox.setHgrow(label, Priority.ALWAYS);

        item.setOnMouseClicked(event -> {
            System.out.println("Saran diklik: " + text);
            searchTextField.setText(text);
            hideSuggestions();
        });

        return item;
    }

    // Public methods for external access
    public void setUsername(String username) {
        if (usernameButton != null) {
            usernameButton.setText(username);
        }
    }

    public String getSearchText() {
        return searchTextField != null ? searchTextField.getText() : "";
    }

    public void clearSearch() {
        if (searchTextField != null) {
            searchTextField.clear();
        }
    }

    public void setProfileImage(String imagePath) {
        if (profileImageView != null) {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(imagePath);
                profileImageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading profile image: " + e.getMessage());
            }
        }
    }

    private void loadDefaultProfilePicture() {
        try {
            Image dummyProfilePic = new Image(getClass().getResource("/app/edugram/userData/images/profile_pictures/lapwiing.jpg").toExternalForm());
            profileImageView.setImage(dummyProfilePic);
            profileImageView.setFitWidth(45);
            profileImageView.setFitHeight(45);
            Circle clip = new Circle(profileImageView.getFitWidth() / 2,
                    profileImageView.getFitHeight() / 2,
                    profileImageView.getFitWidth() / 2);
            profileImageView.setClip(clip);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar profil default: " + e.getMessage());
        }
    }

    @FXML
    public void searchBarAction(KeyEvent keyEvent) {
        String searchBarContent = searchTextField.getText();
        System.out.println(searchBarContent);
        TagFilterController.loadAndDisplayTags(TagModel.listAll(searchBarContent));
    }
}