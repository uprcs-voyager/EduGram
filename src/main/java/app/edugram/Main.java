package app.edugram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage; // Deklarasikan static Stage

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("pages/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("EduGram - login");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}