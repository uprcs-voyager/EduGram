package app.edugram.models;

import java.util.List;

public interface Toggleable {
    public boolean set(List<String> dbValue) ;
    public boolean unset(int tableId);
    public boolean exists(int tableId);
}
