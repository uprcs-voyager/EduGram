package app.edugram.models;

import app.edugram.utils.Notices;
import app.edugram.utils.Sessions;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostModel extends BaseModel implements CRUDable{
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
        String query = "INSERT INTO post " +
                "(title_post, desc_post, img_post, created_at, updated_at, id_user)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PostModel post = (PostModel) item;
        ConnectDB db = new ConnectDB();
        try (Connection con = db.getConnetion();
             PreparedStatement stmt = con.prepareStatement(query)) {

            // Check connection
            if (con == null) {
                return false;
            }

            // Set parameters
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getDescription());
            stmt.setString(3, post.getPostContent());

            // Auto-generate timestamps
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, now);  // created_at
            stmt.setTimestamp(5, now);  // updated_at

            stmt.setInt(6, Sessions.getUserId());

            // Execute and return result
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Log the error (replace with your logging framework)
            System.err.println("Error creating post: " + e.getMessage());
            return false;
        }finally {
            db.closeConnection();
        }
    }

    @Override
    public Object read(int id) {
        return null;
    }

    @Override
    public boolean update(Object item) {
        String query = "UPDATE post SET " +
                "title_post = ?, " +
                "desc_post = ?, " +
                "img_post = ?, " +
                "updated_at = ? " +
                "WHERE id_post = ? ";

        PostModel post = (PostModel) item;
        ConnectDB db = new ConnectDB();
        try(Connection con = db.getConnetion();
            PreparedStatement stmt = con.prepareStatement(query)){

            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getDescription());
            stmt.setString(3, post.getPostContent());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(4, now);
            stmt.setInt(5, post.getId());

            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            System.err.println("Error updating post: " + e.getMessage());
        }finally {
            db.closeConnection();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM post WHERE id_post = ?";
        ConnectDB db = new ConnectDB();

        try(Connection con = db.getConnetion();
            PreparedStatement stmt = con.prepareStatement(query))
        {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            System.err.println("Error deleting post: " + e.getMessage());
        }finally {
            db.closeConnection();
        }

        return false;
    }

    @Override
    public List<PostModel> listAll(String type) {
        List<PostModel> posts = new ArrayList<>();

        String sql = getQuery(type);
//        MAX(CASE WHEN pt.id_tag IN (1, 2, 3) THEN 1 ELSE 0 END) DESC,

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

    private String getQuery(String type){
        System.out.println(type);
        String query = """
                SELECT
                    p.id_post,
                    p.id_user,
                    u.username,
                    p.img_post,
                    p.title_post,
                    p.desc_post,
                    u.prof_pic,
                    p.created_at,
                    COUNT(DISTINCT l.id_like) AS like_count,
                    COUNT(DISTINCT dl.id_dislike) AS dislike_count,
                    COUNT(DISTINCT c.id_com) AS comment_count,
                    CASE
                        WHEN COUNT(DISTINCT t.nama_tag) = 0 THEN NULL
                        ELSE GROUP_CONCAT(DISTINCT t.nama_tag || '-' || t.type_tag)
                    END AS tags
                FROM post p
                LEFT JOIN like l ON p.id_post = l.id_post
                LEFT JOIN dislike dl ON p.id_post = dl.id_post
                LEFT JOIN comment c ON p.id_post = c.id_post
                JOIN user u ON p.id_user = u.id_user
                LEFT JOIN postTag pt ON p.id_post = pt.id_post
                LEFT JOIN tag t ON pt.id_tag = t.id_tag
                """;
//        System.out.println(query);
        switch (type){
            case "explore":
                query += """
                        GROUP BY p.id_post
                        ORDER BY
                    (COUNT(DISTINCT l.id_like) - COUNT(DISTINCT dl.id_dislike)) DESC,
                    p.id_post DESC; 
                        """;
                break;
            case "beranda":
                List<String> UPM = UserPrefTagModel.listAll(); // user's preferred tags
                boolean isThere= UPM != null && !UPM.isEmpty();
                System.out.println(isThere);
                if(isThere){
                    query += " WHERE ";
                    for (int i = 0; i < UPM.size(); i++){
                        query += " pt.id_tag = '" + UPM.get(i) + "'";
                        if(!(i == UPM.size() - 1)){
                            query += " OR ";
                        }else{
                            query += " OR p.id_user = " + Sessions.getUserId();
                        }
                    }
                }

                query += """
                        GROUP BY p.id_post
                        ORDER BY
                    (COUNT(DISTINCT l.id_like) - COUNT(DISTINCT dl.id_dislike)) DESC,
                    p.id_post DESC;
                        """;
                break;
            case "myProfile":
                query += """
                        WHERE p.id_user = """ + Sessions.getUserId() + """
                         GROUP BY p.id_post
                        ORDER BY
                            p.created_at desc; 
                        """;
                break;
        }
        if(type.startsWith("profile-")){
            String profileId = type.substring(8);
            query += """
                        WHERE p.id_user = """ + profileId + """
                         GROUP BY p.id_post
                        ORDER BY
                            p.created_at desc; 
                        """;
        }
        return query;
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

