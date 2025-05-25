package app.edugram.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Display {
    @FXML
    public Label welcomeText;
    public void onHelloButtonClick(ActionEvent event){
        welcomeText.setText("Hello World!");
    }

}
