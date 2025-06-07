package app.edugram.models;

import app.edugram.utils.Sessions;
import java.sql.*;
import java.util.List;

public class UserModel extends BaseModel implements CRUDable<UserModel> {
//    ========================================
//    ===== DIGUNAKAN UNTUK USER PROFILE =====
//    ========================================
    private String username;
    private String password;
    private String nama;
    private String email;
    private String profilePic;

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
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public List<UserModel> listAll() {
        return List.of();
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

                    Sessions.setUser(sesIdUser, sesUsername, sesName, sesProfilePic, "beranda.fxml");
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
//    ========================================
//    ============ AUTHENTICATION ============
//    ========================================
}
