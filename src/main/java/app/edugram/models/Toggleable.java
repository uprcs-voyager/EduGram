package app.edugram.models;

import java.util.List;

public interface Toggleable {
    public boolean set(String dbTable, List<String> dbField, List<String> dbValue) ;
    public boolean unset(String table, int tableId);
    public boolean exists(String table, int tableId);
}
