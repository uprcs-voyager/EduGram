package app.edugram.models;

import app.edugram.utils.Sessions;

import java.util.List;

public class UserPrefTagModel extends BaseModel implements Toggleable{
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(List<String> dbValue) {
        return false;
    }

    @Override
    public boolean unset(int tableId) {
        return false;
    }

    @Override
    public boolean exists(int tableId) {
        String query = "SELECT * FROM userPrefTag WHERE id_user = " + tableId;
        return ConnectDB.startQueryExecution(query, false);
    }
}
