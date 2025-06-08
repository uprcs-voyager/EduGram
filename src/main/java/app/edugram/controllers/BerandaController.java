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

public class BerandaController extends BaseController implements Initializable, TambahPostController.PostCreationListener {

    private List<PostModel> posts;

    @FXML private GridPane postContentGrid;
    @FXML private VBox contentContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAndDisplayPosts();
    }

    public void loadAndDisplayPosts() {
        posts = new ArrayList<>(initData());

        int columns = 0;
        int row = 1;

        try {
            for (int i = 0; i < posts.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Main.class.getResource("pages/components/post.fxml"));
                VBox box = fxmlLoader.load();

                PostFrameController postFrameController = fxmlLoader.getController();
                postFrameController.setData(posts.get(i));

                if (columns == 1) {
                    columns = 0;
                    ++row;
                }

                postContentGrid.add(box, columns++, row);
                GridPane.setMargin(box, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostCreated() {
        System.out.println("Notifikasi post baru diterima. Memuat ulang postingan di Beranda.");
        loadAndDisplayPosts(); // Panggil metode yang sama untuk me-refresh
    }


    @Override
    public List<PostModel> initData() {
        PostModel post = new PostModel();
        return post.listAll();
    }
}
