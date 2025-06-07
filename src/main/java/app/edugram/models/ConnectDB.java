package app.edugram.models;

import java.sql.*;

public class ConnectDB {
    private Connection connection;

    public ConnectDB(){
        String url = "jdbc:sqlite:edugramDB";
        try{
            connection = DriverManager.getConnection(url);
        }catch (SQLException e){
            System.out.println("==== Connection: failed ====");
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
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean startQueryExecution(String query, boolean isUpdate) {
        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();

        if (con == null) {
            System.out.println("Connection failed.");
            return false;
        }

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            if (isUpdate) {
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            } else {
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }
    }
}
