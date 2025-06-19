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
        }

        return tags;
    }

}
