package app.edugram.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagModel {
    public static List<String> listAll(String keyword) {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT nama_tag FROM tag WHERE nama_tag LIKE '%"+keyword+"%' LIMIT 10";

        ConnectDB db = new ConnectDB();
        Connection conn = db.getConnetion();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tags.add(rs.getString("nama_tag"));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Bisa diganti dengan logging atau error handling yang lebih baik
        } finally {
            db.closeConnection();
        }

        return tags;
    }

    public static boolean validate(String tagName){
        String sql = "SELECT nama_tag FROM tag WHERE nama_tag = '"+tagName+"'";

        ConnectDB db = new ConnectDB();
        Connection conn = db.getConnetion();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Bisa diganti dengan logging atau error handling yang lebih baik
        }finally {
            db.closeConnection();
        }

        return false;
    }

    public static boolean create(String tagName) {
        String sql = "INSERT INTO tag (nama_tag, type_tag) VALUES (?, 'u')";
        ConnectDB db = new ConnectDB();
        Connection conn = db.getConnetion();
        try(PreparedStatement psmts = conn.prepareStatement(sql)){
            psmts.setString(1, tagName);
            psmts.executeUpdate();
            return true;
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            db.closeConnection();
        }
        return false; 
    }

    public void checkTag(List<String> tags){
        for(String tag : tags){
            if(!validate(tag)){
                create(tag);
            }
        }
    }

    public static String fetchIdTag(String tag){
        String sql = "SELECT id_tag FROM tag WHERE nama_tag = ?";
        ConnectDB db = new ConnectDB();
        String idTag = null;

        try(Connection con = db.getConnetion();
            PreparedStatement psmts = con.prepareStatement(sql)){
            psmts.setString(1, tag);
            ResultSet rs = psmts.executeQuery();
            if(rs.next()){
                idTag = rs.getString("id_tag");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            db.closeConnection();
        }

        return idTag;
    }

}
