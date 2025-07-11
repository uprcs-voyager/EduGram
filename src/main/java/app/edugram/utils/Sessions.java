package app.edugram.utils;

public class Sessions {
    private static int userId;
    private static String username = "HeBoss";
    private static String nama = "BigBoss";
    private static String profilePicture = "hiboss.jpg";
    private static String email;
    private static String password;
    private static String level;
    public static String currentPage = "beranda";

    public static void setUser(int userId,String email, String username, String nama, String profilePicture, String password, String level, String currentPage) {
        Sessions.userId = userId;
        Sessions.username = username;
        Sessions.nama = nama;
        Sessions.email = email;
        Sessions.profilePicture = profilePicture;
        Sessions.password = password;
        Sessions.level = level;
        System.out.println(level);
        Sessions.currentPage = currentPage;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getLevel() {
        return level;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUsername() {
        return username;
    }

    public static String getNama() {
        return nama;
    }

    public static String getEmail() {
        return email;
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

    public static String getCurrentPage() {
        return currentPage;
    }

    public static void setCurrentPage(String currentPage) {
        Sessions.currentPage = currentPage;
    }

}
