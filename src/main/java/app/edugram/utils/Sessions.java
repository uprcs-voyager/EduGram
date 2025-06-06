package app.edugram.utils;

public class Sessions {
    private static int userId;
    private static String username = "HeBoss";
    private static String nama = "BigBoss";

    public static void setUser(int userId, String username, String nama) {
        Sessions.userId = userId;
        Sessions.username = username;
        Sessions.nama = nama;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static String getNama() {
        return nama;
    }

    public static void clear(){
        Sessions.userId = 0;
        Sessions.username = null;
        Sessions.nama = null;
    }
}
