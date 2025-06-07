package app.edugram.utils.cookies;

import app.edugram.utils.cookies.UserCookie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class CookieUtil {
    private static final String COOKIE_PATH = "user_cookie.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static UserCookie loadCookie() {
        try (Reader reader = new FileReader(COOKIE_PATH)) {
            return gson.fromJson(reader, UserCookie.class);
        } catch (IOException e) {
            return new UserCookie(); // Return default if file not found
        }
    }

    public static void saveCookie(UserCookie cookie) {
        try (Writer writer = new FileWriter(COOKIE_PATH)) {
            gson.toJson(cookie, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
