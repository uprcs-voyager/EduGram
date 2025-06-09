package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BerandaController extends BaseController implements Initializable {

    @FXML private GridPane postContentGrid;
    @FXML private VBox contentContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAndDisplayPosts();
    }

    public void loadAndDisplayPosts() {
        List<PostModel> posts = new ArrayList<>(initData());

        int columns = 0;
        int row = 1;

        try {
            for (PostModel post : posts) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Main.class.getResource("pages/components/post.fxml"));
                VBox box = fxmlLoader.load();

                PostFrameController postFrameController = fxmlLoader.getController();
                postFrameController.setData(post);

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
    public List<PostModel> initData() {
        PostModel post = new PostModel();
        return post.listAll();
    }
}
