package app.edugram.models;


import app.edugram.utils.Sessions;

import java.util.List;

public class FollowModel extends BaseModel implements  Toggleable{
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(List<String> dbValue) {
//        List<String> value = List.of(String.valueOf(Sessions.getUserId()), String.valueOf(currentPost.getUserId()));
//        likeModel.set(value);
        String query = "INSERT INTO follow (follower, following) VALUES (";
        return Toggleable.LoopValues(dbValue, query);
    }

    @Override
    public boolean unset(int tableId) {
        String sql = "DELETE FROM follow WHERE following = " + tableId + " AND follower = " + Sessions.getUserId();

        return ConnectDB.startQueryExecution(sql, true);
    }

    @Override
    public boolean exists(int tableId) {
        String query = "SELECT * FROM follow  WHERE following = " + tableId + " AND follower = " + Sessions.getUserId();
        return ConnectDB.startQueryExecution(query, false);
    }
}
