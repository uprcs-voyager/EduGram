package app.edugram.models;

import app.edugram.utils.Notices;
import app.edugram.utils.Sessions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PostModel extends BaseModel implements Toggleable, CRUDable{
    private String postContent;
    private String like;
    private String dislike;
    private String bookmark;
    private String profile;
    private List<String> tags;
    private String title;
    private String description;
    private String userId;
    private String postUsername;

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean create(Object item) {
        return false;
    }

    @Override
    public Object read(int id) {
        return null;
    }

    @Override
    public boolean update(Object item) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public List<PostModel> listAll() {
        List<PostModel> posts = new ArrayList<>();

        String sql = "SELECT\n" +
                "    p.id_post,p.id_user,u.username,p.img_post,p.title_post,p.desc_post,u.prof_pic,\n" +
                "    COUNT(DISTINCT l.id_like) AS like_count,\n" +
                "    COUNT(DISTINCT dl.id_dislike) AS dislike_count,\n" +
                "    COUNT(DISTINCT c.id_com) AS comment_count,\n" +
                "    CASE\n" +
                "        WHEN COUNT(DISTINCT t.nama_tag) = 0 THEN NULL\n" +
                "        ELSE GROUP_CONCAT(DISTINCT CONCAT(t.nama_tag, '-', type_tag))\n" +
                "    END AS tags\n" +
                "FROM post p\n" +
                "         LEFT JOIN like l ON p.id_post = l.id_post\n" +
                "         LEFT JOIN dislike dl ON p.id_post = dl.id_post\n" +
                "         LEFT JOIN comment c ON p.id_post = c.id_post\n" +
                "         JOIN user u ON p.id_user = u.id_user\n" +
                "         LEFT JOIN postTag pt ON p.id_post = pt.id_post\n" +
                "         LEFT JOIN tag t ON pt.id_tag = t.id_tag\n" +
                "GROUP BY p.id_post;\n";

        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();

        if(con == null) {
            Notices.failedConncetion();
            return posts;
        }

        try(
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            )
        {
            while(rs.next()){
                PostModel post = new PostModel();
                post.setId(rs.getInt("id_post"));
                post.setTitle(rs.getString("title_post"));
                post.setDescription(rs.getString("desc_post"));
                post.setUserId(rs.getString("id_user"));
                post.setPostUsername(rs.getString("username"));
                post.setPostContent(rs.getString("img_post"));
                post.setLike(rs.getString("like_count"));
                post.setDislike(rs.getString("dislike_count"));
                post.setProfile(rs.getString("prof_pic"));

                String getPostTags = rs.getString("tags");
                if(getPostTags != null && !getPostTags.isEmpty()){
                    post.setTags(Arrays.asList(getPostTags.split(",")));
                }else{
                    post.setTags(new ArrayList<>());
                }

                posts.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }
        return posts;
    }

    @Override
    public boolean set(String dbTable, List<String> dbField, List<String> dbValue ) {
        String query = "INSERT INTO " + dbTable + " (";
        for(int i = 0; i < dbField.size(); i++){
            query += dbField.get(i);
            if(i != dbField.size()-1){query += ",";}
        }
        query += ") VALUES (";
        for(int i = 0; i < dbValue.size(); i++){
            query += dbValue.get(i);
            if(i != dbValue.size()-1){query += ",";}
        }
        query += ")";
        return startQueryExecution(query, true);
    }

    @Override
    public boolean unset(String table, int tableId) {
        String sql = "DELETE FROM " + table + " WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();

        return startQueryExecution(sql, true);
    }

    @Override
    public boolean exists(String table, int tableId) {
        String sql = "SELECT * FROM " + table + " WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();
        System.out.println(Sessions.getUserId());

        return startQueryExecution(sql, false);
    }

    private boolean startQueryExecution(String query, boolean isUpdate) {
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

    public int getAvgLike(String idPost){
        String sql = "SELECT \n" +
                "\tCOUNT(DISTINCT l.id_like) AS like_count,\n" +
                "\tCOUNT(DISTINCT dl.id_dislike) AS dislike_count\n" +
                "FROM post p \n" +
                "\tLEFT JOIN like l ON p.id_post = l.id_post\n" +
                "\tLEFT JOIN dislike dl ON p.id_post = dl.id_post\n" +
                "\tWHERE p.id_post = " + idPost +
                " GROUP BY p.id_post";
        int sumCount;

        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();

        if(con == null) {
            Notices.failedConncetion();
            return 0;
        }

        try(
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
        )
        {
            sumCount = rs.getInt("like_count") - rs.getInt("dislike_count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.closeConnection();
        }
        return sumCount;
    }

//    ================================ GETTER SETTER ================================
//     GETTER SETTERGETTER SETTERGETTER SETTERGETTER SETTERGETTER SETTERGETTER SETTER
//    ===============================================================================

    public String getPostContent() {
        return postContent;
    }
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getLike() {
        return like;
    }
    public void setLike(String like) {
        this.like = like;
    }

    public String getDislike() {
        return dislike;
    }
    public void setDislike(String dislike) {
        this.dislike = dislike;
    }

    public String getBookmark() {
        return bookmark;
    }
    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostUsername() {
        return postUsername;
    }
    public void setPostUsername(String postUsername) {
        this.postUsername = postUsername;
    }
}

