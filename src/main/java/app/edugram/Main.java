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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("EduGram - login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        ConnectDB db = new ConnectDB();
//        Connection connection = db.getConnetion();
//        if (connection != null){
//            try{
//                Statement statement = connection.createStatement();
//                String createTable = "CREATE TABLE IF NOT EXISTS user(" +
//                        "id_user INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                        "username TEXT NOT NULL,\n" +
//                        "password TEXT NOT NULL,\n" +
//                        "nama TEXT,\n" +
//                        "email TEXT,\n" +
//                        "created_at TEXT,\n" +
//                        "updated_at TEXT,\n" +
//                        "prof_pic TEXT\n)";
//                statement.executeUpdate(createTable);
//
//                ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
//                System.out.println("ID\tUsername");
//
//                while(resultSet.next()){
//                    int id = resultSet.getInt("id_user");
//                    String username =resultSet.getString("username");
//                    System.out.println(id + "\t" + username);
//                }
//
//                db.closeConnection();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        launch();
    }
}