package app.edugram.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class PageAction {
    public static void switchPage(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(PageAction.class.getResource("/app/edugram/" + fxmlPath));
            Parent root = loader.load();

            // Get stage from the event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set new root to the scene
            Scene scene = stage.getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
