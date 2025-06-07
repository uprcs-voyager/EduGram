package app.edugram.controllers.cache;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class Modaltest {

    @FXML
    public AnchorPane commentModal;

    @FXML
    public TextArea commentTextArea;

    @FXML
    public void handleCommentClick() {
        commentModal.setVisible(true);
    }

    @FXML
    public void handleSend() {
        String komentar = commentTextArea.getText();
        System.out.println("Komentar: " + komentar);
        commentTextArea.clear();
        commentModal.setVisible(false);
    }

    @FXML
    public void handleCancel() {
        commentTextArea.clear();
        commentModal.setVisible(false);
    }
}