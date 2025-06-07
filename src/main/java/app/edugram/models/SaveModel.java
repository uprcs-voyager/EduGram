package app.edugram.models;


import app.edugram.utils.Sessions;

import java.util.List;

public class SaveModel extends BaseModel implements  Toggleable{

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(List<String> dbValue) {
        String query = "INSERT INTO save (id_user, id_post) VALUES (";
        for(int i = 0; i < dbValue.size(); i++){
            query += dbValue.get(i);
            if(i != dbValue.size()-1){query += ",";}
        }
        query += ")";
        return ConnectDB.startQueryExecution(query, true);
    }

    @Override
    public boolean unset(int tableId) {
        String sql = "DELETE FROM save WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();

        return ConnectDB.startQueryExecution(sql, true);
    }

    @Override
    public boolean exists(int tableId) {
        String sql = "SELECT * FROM save  WHERE id_post = " + tableId + " AND id_user = " + Sessions.getUserId();

        return ConnectDB.startQueryExecution(sql, false);
    }
}
