package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.models.PostModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExploreController extends BaseController implements Initializable {
    @FXML
    private GridPane postContentGrid;
    private List<PostModel> posts;

    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private VBox contentContainer;

    @FXML
    private HBox hbox_explore;

    @FXML
    private List<PostModel> postsList;
    private Node exploreGridView;
    private Node tulisanExplore;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exploreGridView = contentScrollPane;
        tulisanExplore = hbox_explore;
        posts = new ArrayList<>(initData());

        int columns = 0;
        int row = 0;

        try{
            for(int i=0; i<posts.size(); i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Main.class.getResource("pages/components/smallpost.fxml"));
                VBox box = fxmlLoader.load();

                SmallPostFrameController SmallPostFrameController = fxmlLoader.getController();
                SmallPostFrameController.setData(posts.get(i));
                SmallPostFrameController.setExploreController(this);
                if(columns == 3){
                    columns = 0;
                    ++row;
                }

                postContentGrid.add(box,columns++,row);
                GridPane.setMargin(box, new Insets(2, 2, 1, 2));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<PostModel> initData() {
        PostModel post = new PostModel();
        return post.listAll();
    }

    public void ShowPostDetail(PostModel post){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pages/components/post.fxml"));
            VBox postDetailView = fxmlLoader.load();

            PostFrameController postFrameController = fxmlLoader.getController();
            postFrameController.setData(post);
            postFrameController.setReturnToExploreCallBack(() -> showExploreGrid());
            postFrameController.showBackButton(true);

            contentContainer.getChildren().clear();
            contentContainer.getChildren().add(postDetailView);


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load post details: " + post.getTitle());
        }
    }

    public void showExploreGrid(){
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(tulisanExplore);
        contentContainer.getChildren().add(exploreGridView);

    }

}
