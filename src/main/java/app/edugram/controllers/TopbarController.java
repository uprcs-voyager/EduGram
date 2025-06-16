package app.edugram.controllers;
import app.edugram.Main;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.control.Separator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.stage.Popup;
import javafx.stage.Window;

public class TopbarController implements Initializable {

    // FXML Components
    @FXML private ImageView logoImageView;
    @FXML private Label appNameLabel;
    @FXML private TextField searchTextField;
    @FXML private ImageView searchIconImageView;
    @FXML private ImageView profileImageView;
    @FXML private Button usernameButton;

    @FXML private VBox suggestionVBoxContent;
    private Popup suggestionPopup;
    private boolean isFullyInitialized = false;

    // Callback interface untuk mengirim tag yang dipilih kembali ke controller utama


    @Override public void initialize(URL location, ResourceBundle resources) {
        setupPopup();
        setupSearchField();
        setupUserProfile();
        Platform.runLater(() -> {
            isFullyInitialized = true;
        });
    }

    private void setupPopup() {
        suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true); // Popup akan otomatis sembunyi jika klik di luar

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("pages/components/suggestioncontent.fxml"));
            suggestionVBoxContent = loader.load();

            suggestionPopup.getContent().add(suggestionVBoxContent);
        } catch (IOException e) {
            System.err.println("Gagal memuat suggestion_content.fxml: " + e.getMessage());
            e.printStackTrace();
        }

        // Listener untuk menyembunyikan popup saat kehilangan fokus
        suggestionPopup.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !searchTextField.isFocused()) {
                hideSuggestions();
            }
        });
    }


    private void setupSearchField() {
        if (searchTextField != null) {
            // Listener fokus untuk menampilkan/menyembunyikan popup
            searchTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) { // Jika mendapatkan fokus
                    if (isFullyInitialized || !searchTextField.getText().isEmpty()) {
                        showSuggestions();
                        searchTextField.selectAll();
                    }
                } else {
                    Platform.runLater(() -> { // Beri sedikit waktu agar klik item saran terdaftar
                        if (!searchTextField.isFocused() && !suggestionPopup.isShowing()) {
                            hideSuggestions();
                        }
                    });
                }
            });

            // Listener untuk klik pada TextField
            searchTextField.setOnMouseClicked(event -> {
                if (!suggestionPopup.isShowing()) {
                    showSuggestions();
                }
            });

            // Listener untuk klik pada ikon pencarian
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
            // You can set the username dynamically here
            // usernameButton.setText("Current User");
        }
    }

    @FXML
    private void onUsernameButtonClick(ActionEvent event) {
        System.out.println("Username button clicked!");
        // Add your username button logic here
        // For example: show user profile, logout menu, etc.
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
            // Add your search logic here
            // For example: filter posts, navigate to search results, etc.
        }
    }


    private void showSuggestions() {
        if (suggestionPopup != null && suggestionVBoxContent != null && !suggestionPopup.isShowing()) {
            populateSuggestions(); // Isi saran sebelum ditampilkan

            // Hitung posisi popup
            Bounds boundsInScene = searchTextField.localToScreen(searchTextField.getBoundsInLocal());
            double x = boundsInScene.getMinX();
            double y = boundsInScene.getMaxY(); // Tepat di bawah TextField

            // Dapatkan Window dari Scene TextField
            Window window = searchTextField.getScene().getWindow();


            // Tampilkan popup
            suggestionPopup.show(window, x, y);

            // Terapkan animasi pada konten VBox di dalam popup
            suggestionVBoxContent.setOpacity(0);
            suggestionVBoxContent.setScaleY(0.8);
            suggestionVBoxContent.setTranslateY(-10);
            FadeTransition ft = new FadeTransition(Duration.millis(200), suggestionVBoxContent);
            ft.setFromValue(0); ft.setToValue(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(200), suggestionVBoxContent);
            st.setFromY(0.8); st.setToY(1);
            TranslateTransition tt = new TranslateTransition(Duration.millis(200), suggestionVBoxContent);
            tt.setFromY(-10); tt.setToY(0);

            ft.play(); st.play(); tt.play();
            System.out.println("Saran pencarian ditampilkan (via Popup).");
        }
    }


    private void hideSuggestions() {
        if (suggestionPopup != null && suggestionPopup.isShowing()) {
            // Animasikan konten VBox di dalam popup sebelum menyembunyikan popup itu sendiri
            if (suggestionVBoxContent != null) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), suggestionVBoxContent);
                ft.setFromValue(1); ft.setToValue(0);

                ScaleTransition st = new ScaleTransition(Duration.millis(200), suggestionVBoxContent);
                st.setFromY(1); st.setToY(0.8);

                TranslateTransition tt = new TranslateTransition(Duration.millis(200), suggestionVBoxContent);
                tt.setFromY(0); tt.setToY(-10);

                ft.setOnFinished(e -> {
                    suggestionPopup.hide(); // Sembunyikan popup setelah animasi selesai
                });

                ft.play(); st.play(); tt.play();
            } else {
                suggestionPopup.hide();
            }
            System.out.println("Saran pencarian disembunyikan (via Popup).");
        }
    }

    private void populateSuggestions() {
        if (suggestionVBoxContent == null) return;
        suggestionVBoxContent.getChildren().clear(); // Hapus saran sebelumnya
        // --- Ambil data dari database ---
        List<String> profileNames = UserModel.getAllUsernames();       // Ambil semua username dari UserModel
        List<String> tagNames = UserPrefTagModel.getAllTags();     // GANTI: Ambil semua nama tag dari UserPrefTagModel
        // --- Akhir pengambilan data dari database ---


        // Tambahkan saran Profil
        if (!profileNames.isEmpty()) {
            Label profileHeader = new Label("Profil");
            profileHeader.getStyleClass().add("suggestion-category-label");
            suggestionVBoxContent.getChildren().add(profileHeader);

            for (String profileName : profileNames) {
                HBox item = createSuggestionItem(profileName);
                suggestionVBoxContent.getChildren().add(item);
            }
            // Tambahkan margin bawah setelah kategori Profil jika ada kategori lain
            VBox.setMargin(profileHeader, new javafx.geometry.Insets(5, 0, 5, 0));
        }

        // Tambahkan saran Tag
        if (!tagNames.isEmpty()) {
            if (!profileNames.isEmpty()) {
                // Tambahkan sedikit pemisah visual jika ada kategori Profil sebelumnya
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
        HBox.setHgrow(label, Priority.ALWAYS); // Izinkan label untuk tumbuh

        // Tambahkan penanganan klik untuk setiap item saran
        item.setOnMouseClicked(event -> {
            System.out.println("Saran diklik: " + text);
            searchTextField.setText(text); // Isi bidang pencarian dengan saran yang dipilih
            hideSuggestions(); // Sembunyikan saran setelah pemilihan dengan animasi
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
}