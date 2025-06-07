package app.edugram.models;


import java.util.List;

public class LikeModel extends BaseModel implements  Toggleable{
    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean set(String dbTable, List<String> dbField, List<String> dbValue) {
        return false;
    }

    @Override
    public boolean unset(String table, int tableId) {
        return false;
    }

    @Override
    public boolean exists(String table, int tableId) {
        return false;
    }
}
