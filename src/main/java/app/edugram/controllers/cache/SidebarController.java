package app.edugram.controllers.cache;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;


public class SidebarController implements Initializable {
    @FXML
    private ImageView profileImageView;

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        double radius  = profileImageView.getFitWidth() / 2;
        Circle clip = new Circle(radius, radius, radius);
        profileImageView.setClip(clip);
    }
}
