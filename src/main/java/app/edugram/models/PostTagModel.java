package app.edugram.models;

import app.edugram.utils.Sessions;

import java.util.Arrays;
import java.util.List;

public class PostTagModel extends BaseModel implements Toggleable{
    private String idTag;
    private String idPost;

    public void setData(String idTag, String idPost) {
        this.idTag = idTag;
        this.idPost = idPost;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(List<String> dbValue) {
        String query = "INSERT INTO postTag (id_post, id_tag) VALUES (";
        return Toggleable.LoopValues(dbValue, query);
    }

    @Override
    public boolean unset(int tableId) {
        String sql = "DELETE FROM postTag WHERE id_post = " + getIdPost() + " AND id_tag = " + getIdTag();

        return ConnectDB.startQueryExecution(sql, true);
    }

    @Override
    public boolean exists(int tableId) {
        String query = "SELECT * FROM postTag WHERE id_post = " + getIdPost() + " AND id_tag = " + getIdTag();
        return ConnectDB.startQueryExecution(query, false);
    }

    public String getIdTag() {
        return idTag;
    }

    public String getIdPost() {
        return idPost;
    }
}
