package app.edugram.utils;

public class Sessions {
    private static int userId;
    private static String username = "HeBoss";
    private static String nama = "BigBoss";
    private static String profilePicture = "hiboss.jpg";

    public static void setUser(int userId, String username, String nama, String profilePicture) {
        Sessions.userId = userId;
        Sessions.username = username;
        Sessions.nama = nama;
        Sessions.profilePicture = profilePicture;
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

    public static String getProfilePicture() {
        return profilePicture;
    }

    public static void setProfilePicture(String profilePicture) {
        Sessions.profilePicture = profilePicture;
    }

    public static void clear(){
        Sessions.userId = 0;
        Sessions.username = null;
        Sessions.nama = null;
        Sessions.profilePicture = null;
    }
}
