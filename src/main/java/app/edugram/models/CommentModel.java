package app.edugram.models;

import app.edugram.utils.Sessions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentModel extends BaseModel implements Toggleable {

    private String username;
    private String commentText;
    private String profilePicture;
    private String createdAt;

    // Proper setter method
    public void setData(String username, String commentText, String profilePicture, String createdAt) {
        this.username = username;
        this.commentText = commentText;
        this.profilePicture = profilePicture;
        this.createdAt = createdAt;
    }

    // Getters
    public String getUsername() { return username; }
    public String getCommentText() { return commentText; }
    public String getProfilePicture() { return profilePicture; }
    public String getComCreatedAt() { return createdAt; }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(List<String> dbValue) {
        String query = "INSERT INTO comment (id_user, id_post, comment_txt, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        ConnectDB db = new ConnectDB();

        try (
                Connection con = db.getConnetion();
                PreparedStatement ps = con.prepareStatement(query)
        ) {
            // Set semua nilai dari dbValue ke prepared statement
            for (int i = 0; i < dbValue.size(); i++) {
                ps.setString(i + 1, dbValue.get(i));  // karena index PreparedStatement mulai dari 1
            }

            return ps.executeUpdate() > 0; // return true jika berhasil insert
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return false;
    }


    @Override
    public boolean unset(int tableId) {
        String sql = "DELETE FROM comment WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();
        return ConnectDB.startQueryExecution(sql, true);
    }

    @Override
    public boolean exists(int tableId) {
        String query = "SELECT * FROM comment WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();
        return ConnectDB.startQueryExecution(query, false);
    }

    public List<CommentModel> listAll(int postId) {
        String sql = "SELECT u.username, c.comment_txt, c.created_at, u.prof_pic " +
                "FROM comment c " +
                "LEFT JOIN user u ON c.id_user = u.id_user " +
                "WHERE c.id_post = ?";

        List<CommentModel> comments = new ArrayList<>();
        ConnectDB db = new ConnectDB();

        try (Connection con = db.getConnetion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String commentText = rs.getString("comment_txt");
                String createdAt = rs.getString("created_at");
                String profilePicture = rs.getString("prof_pic");

                CommentModel comment = new CommentModel();
                comment.setData(username, commentText, profilePicture, createdAt);
                comments.add(comment);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }
}
