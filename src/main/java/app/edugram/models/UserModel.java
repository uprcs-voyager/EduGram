package app.edugram.models;

import app.edugram.utils.Sessions;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserModel extends BaseModel implements CRUDable<UserModel> {
//    ========================================
//    ===== DIGUNAKAN UNTUK USER PROFILE =====
//    ========================================
    private int id;
    private String username;
    private String password;
    private String nama;
    private String email;
    private String profilePic;

    public UserModel() {
        // Default constructor
    }

    public UserModel(String username, String password, String nama, String email, String profilePic) {
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.email = email;
        this.profilePic = profilePic;
    }

    @Override
    public boolean validate() {
        return username != null && !username.isEmpty()
                && password != null && !password.isEmpty()
                && nama != null && !nama.isEmpty()
                && email != null && !email.isEmpty();
    }
    // Getters
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getNama() {return nama;}
    public String getEmail() {return email;}
    public String getProfilePic() {return profilePic;}

    // Setters
    public void setId(int id) { this.id = id; } // Setter untuk ID
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setNama(String nama) { this.nama = nama; }
    public void setEmail(String email) { this.email = email; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }


//    ========================================
//    ===== DIGUNAKAN UNTUK USER PROFILE =====
//    ========================================

    @Override
    public boolean create(UserModel item) {
        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();
        if (con == null) {
            System.out.println("UserModel.create: Can't connect to db");
            return false;
        }

        String query = "INSERT INTO user (username, password, nama, email, prof_pic, created_at, updated_at) VALUES (?, ?, ?, ?, ?, datetime('now', 'localtime'), datetime('now', 'localtime'))";
        try (PreparedStatement pstmt = con.prepareStatement(query)){
            System.out.println("UserModel.create: Inserting into database");
            pstmt.setString(1, item.username);
            pstmt.setString(2, item.password);
            pstmt.setString(3, item.nama);
            pstmt.setString(4, item.email);
            pstmt.setString(5, item.profilePic);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("UserModel.create: Affected rows: " + affectedRows);
            return affectedRows > 0;
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("UserModel.create: Can't insert into database");
            return false;
        }finally {
            db.closeConnection();
        }
    }

    @Override
    public UserModel read(int id) {
        return null;
    }

    @Override
    public boolean update(UserModel item) {
        String sql = "UPDATE user SET password = ?, nama = ?, email = ?, prof_pic = ? WHERE username = ?";
        ConnectDB db = new ConnectDB();
        Connection conn = db.getConnetion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getPassword());
            stmt.setString(2, item.getNama());
            stmt.setString(3, item.getEmail());
            stmt.setString(4, item.getProfilePic());
            stmt.setString(5, item.getUsername());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        ConnectDB db = new ConnectDB();
        Connection conn = db.getConnetion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<UserModel> listAll(String type) {
        List<UserModel> users = new ArrayList<>();
        String sql = "SELECT username FROM user WHERE username LIKE ? LIMIT 10";

        ConnectDB db = new ConnectDB();
        Connection conn = db.getConnetion();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + type + "%"); // safely use LIKE

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserModel user = new UserModel();
                user.setUsername(rs.getString("username"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }



    public static Map<String, String> findUser(String id) {
        String sql = """
            SELECT
               u.username, u.password, u.nama, u.email, u.prof_pic,
               COALESCE(follower.follower_count, 0) AS follower,
               COALESCE(following.following_count, 0) AS following,
               CASE
                   WHEN f.follower IS NOT NULL THEN 1
                   ELSE 0
               END AS isFollow
           FROM user u
                LEFT JOIN (
                   SELECT following AS id_user, COUNT(*) AS follower_count
                   FROM follow
                   GROUP BY following
               ) follower ON follower.id_user = u.id_user
                LEFT JOIN (
                   SELECT follower AS id_user, COUNT(*) AS following_count
                   FROM follow
                   GROUP BY follower
               ) following ON following.id_user = u.id_user
               LEFT JOIN follow f ON f.follower = ? AND f.following = ?
           WHERE u.id_user = ?
           GROUP BY u.id_user;
        """;

        ConnectDB db = new ConnectDB();
        try (
                Connection conn = db.getConnetion();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, String.valueOf(Sessions.getUserId()));
            stmt.setString(2, id);
            stmt.setString(3, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, String> user = new HashMap<>();
                user.put("username", rs.getString("username"));
                user.put("password", rs.getString("password"));
                user.put("nama", rs.getString("nama"));
                user.put("email", rs.getString("email"));
                user.put("prof_pic", rs.getString("prof_pic"));
                user.put("follower", String.valueOf(rs.getInt("follower")));
                user.put("following", String.valueOf(rs.getInt("following")));
                user.put("isFollow", String.valueOf(rs.getInt("isFollow")));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // or empty map if preferred
    }




    //    ========================================
//    ============ AUTHENTICATION ============
//    ========================================
    public static boolean ValidateUser(String usernameVal, String passwordVal, boolean enableSession) {
        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();

        if (con == null) return false;

        String query = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, usernameVal);
            pstmt.setString(2, passwordVal);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (enableSession) {
                    int sesIdUser = rs.getInt("id_user");
                    String sesUsername = rs.getString("username");
                    String sesName = rs.getString("nama");
                    String sesProfilePic = rs.getString("prof_pic");
                    String sesEmail = rs.getString("email");

                    Sessions.setUser(sesIdUser, sesEmail,sesUsername, sesName, sesProfilePic, "beranda.fxml");
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }
    }

    public static boolean ValidateRegistration(String usernameVal, String passwordVal, String nama, String email) {
        boolean isValid = ValidateUser(usernameVal, passwordVal, false);
        if(!isValid){

            return true;
        }
        return false;
    }

    public static boolean exists(String username, String password){
        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();
        if(con == null) return false;
        System.out.println(username);
        System.out.println(password);

        String query = "SELECT * FROM user  WHERE username = '" + username + "' AND password = '" + password + "'";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Check if there's at least one record
            if(rs.next()) {
                System.out.println("Record found!");
                return true; // User exists
            } else {
                System.out.println("No record found!");
                return false; // User doesn't exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        ConnectDB db = new ConnectDB();
        Connection con = db.getConnetion();
        if (con == null) {
            System.out.println("UserModel.getAllUsernames: Can't connect to db");
            return usernames;
        }

        String query = "SELECT * FROM user ORDER BY username ASC";
        try (PreparedStatement prepare = con.prepareStatement(query);
              ResultSet rs = prepare.executeQuery()) {
            while(rs.next()) {
                usernames.add(rs.getString("username"));
            } } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("UserModel.getAllUsernames: error fetching usernames");
            } finally {
            db.closeConnection();
         }
        return usernames;
    }




//    ========================================
//    ============ AUTHENTICATION ============
//    ========================================
}
