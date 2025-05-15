package app.edugram.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private Connection connection;

    public ConnectDB(){
        String url = "jdbc:sqlite:edugramDB";
        try{
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database");
        }catch (SQLException e){
            System.out.println("Connection Error: failes to connect");
            e.printStackTrace();
        }
    }

    public Connection getConnetion(){
        return connection;
    }

    public void closeConnection(){
        try{
                if(connection != null){
                    connection.close();
                    System.out.println("Connection closed");
                }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
