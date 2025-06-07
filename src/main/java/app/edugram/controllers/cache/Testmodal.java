package app.edugram.controllers.cache;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Testmodal {

    @FXML
    private void handleShowModals(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/edugram/pages/cache/testmodal2.fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("Modal Window");
            modalStage.setScene(new Scene(root));
            modalStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
