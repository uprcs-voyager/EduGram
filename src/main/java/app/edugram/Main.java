package app.edugram;
import app.edugram.models.ConnectDB;
import java.sql.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pages/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1306, 840);
        stage.setTitle("EduGram - login");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}