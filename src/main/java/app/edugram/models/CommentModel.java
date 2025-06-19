package app.edugram.models;

import app.edugram.utils.Notices;
import app.edugram.utils.Sessions;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Gemini attempt


public class CommentModel {

    public static class Comment {
        private int id;
        private int postId;
        private int userId;
        private String commentText;
        private LocalDateTime timestamp;
        private String username;

        public Comment(int id, int postId, int userId, String commentText, LocalDateTime timestamp, String username) {
            this.id = id;
            this.postId = postId;
            this.userId = userId;
            this.commentText = commentText;
            this.timestamp = timestamp;
            this.username = username;
        }

        public int getId() { return id; }
        public int getPostId() { return postId; }
        public int getUserId() { return userId; }
        public String getCommentText() { return commentText; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getUsername() { return username; }
    }

    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO comments (post_id, user_id, comment_text, created_at) VALUES (?, ?, ?, ?)";
        ConnectDB db = new ConnectDB();
        try (Connection conn = db.getConnetion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, comment.getPostId());
            pstmt.setInt(2, comment.getUserId());
            pstmt.setString(3, comment.getCommentText());
            pstmt.setTimestamp(4, Timestamp.valueOf(comment.getTimestamp()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Optional: Get generated ID if needed
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Comment> getCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.id, c.post_id, c.user_id, c.comment_text, c.created_at, u.username " +
                "FROM comments c JOIN users u ON c.user_id = u.id " +
                "WHERE c.post_id = ? ORDER BY c.created_at ASC";

        ConnectDB db = new ConnectDB();
        try (Connection conn = db.getConnetion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String commentText = rs.getString("comment_text");
                LocalDateTime timestamp = rs.getTimestamp("created_at").toLocalDateTime();
                String username = rs.getString("username");

                comments.add(new Comment(id, postId, userId, commentText, timestamp, username));
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments for post: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    // Metode baru untuk mendapatkan nama file gambar profil berdasarkan userId
    public String getProfilePictureFileName(int userId) {
        String sql = "SELECT prof_pic FROM users WHERE id = ?";
        ConnectDB db = new ConnectDB();
        try (Connection conn = db.getConnetion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("profile_picture");
            }
        } catch (SQLException e) {
            System.err.println("Error getting profile picture file name for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Mengembalikan null jika tidak ditemukan atau terjadi error
    }
}