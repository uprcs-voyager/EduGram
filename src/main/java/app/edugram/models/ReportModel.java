package app.edugram.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ReportModel extends BaseModel implements CRUDable{
    private String postId;
    private String userId;
    private String keluhan;

    public void setData(String id_post, String id_user, String keluhan) {
        this.postId = id_post;
        this.userId = id_user;
        this.keluhan = keluhan;
    }

    @Override
    public boolean validate() {
        String query = "SELECT id_report FROM report WHERE id_post = ? AND id_user = ?";
        ConnectDB db = new ConnectDB();

        try(
            Connection con = db.getConnetion();
            PreparedStatement ps = con.prepareStatement(query)
        )
        {
            ps.setString(1, postId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.closeConnection();
        }
        return false;
    }

    @Override
    public boolean create(Object item) {
        String sql = "INSERT INTO report (id_post, id_user, desc, created_at, isNoticed) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";
        ConnectDB db = new ConnectDB();

        try(Connection con = db.getConnetion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, postId);
            ps.setString(2, userId);
            ps.setString(3, keluhan);
            ps.setInt(4, 0);

            ps.execute();
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.closeConnection();
        }

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
    public List listAll(String type) {
        return List.of();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }
}
