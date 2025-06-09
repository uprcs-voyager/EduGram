package app.edugram.models;

import java.util.List;

public interface Toggleable {
    public boolean set(List<String> dbValue) ;
    public boolean unset(int tableId);
    public boolean exists(int tableId);
    static boolean LoopValues(List<String> dbValue, String query) {
        StringBuilder queryBuilder = new StringBuilder(query);
        for(int i = 0; i < dbValue.size(); i++){
            queryBuilder.append(dbValue.get(i));
            if(i != dbValue.size()-1){
                queryBuilder.append(",");}
        }
        query = queryBuilder.toString();
        query += ")";
        return ConnectDB.startQueryExecution(query, true);
    }
}
