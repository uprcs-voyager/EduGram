package app.edugram.models;


import app.edugram.utils.Sessions;

import java.util.List;

public class LikeModel extends BaseModel implements  Toggleable{
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(List<String> dbValue) {
        String query = "INSERT INTO like (id_user, id_post) VALUES (";
        return Toggleable.LoopValues(dbValue, query);
    }

    @Override
    public boolean unset(int tableId) {
        String sql = "DELETE FROM like WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();

        return ConnectDB.startQueryExecution(sql, true);
    }

    @Override
    public boolean exists(int tableId) {
        String query = "SELECT * FROM like  WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();
        return ConnectDB.startQueryExecution(query, false);
    }
}
