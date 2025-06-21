package app.edugram.models;

import app.edugram.utils.Sessions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportModel extends BaseModel implements CRUDable{
    private String postId;
    private String userId;
    private String keluhan;

    private String notice;


    public void setData(String id_post, String id_user, String keluhan) {
        this.postId = id_post;
        this.userId = id_user;
        this.keluhan = keluhan;
    }

    @Override
    public boolean validate() {
        String query = "SELECT id_report FROM report WHERE id_post = ? AND id_user = ?";
        ConnectDB db = new ConnectDB();

        try(
            Connection con = db.getConnetion();
            PreparedStatement ps = con.prepareStatement(query)
        )
        {
            ps.setString(1, postId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // True if a record exists (already reported)

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.closeConnection();
        }
//        return false;
    }

    @Override
    public Object read(int id) {
        return null;
    }

    @Override
    public boolean update(Object item) {
        String sql = "UPDATE report SET isNoticed = ? WHERE id_post = ?";
        ConnectDB db = new ConnectDB();
        System.out.println();
        Connection con = db.getConnetion();
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, notice);
            ps.setString(2, postId);
            ps.executeUpdate();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.closeConnection();
        }
        return false;
    }

    public String getNotice() {return notice;}

    public void setNotice(String notice) {this.notice = notice;}

    @Override
    public boolean delete(int id) {
        String query = "SELECT * FROM like  WHERE id_post = " + id;
        return ConnectDB.startQueryExecution(query, false);
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    //
    @Override
    public List <ReportModel> listAll(String type) {
        return new ArrayList<>();
    }

    public static Map<Integer, List<ReportData>> getAllGroupedReportsFromDatabase() {
        Map<Integer, List<ReportData>> groupedReports = new HashMap<>();
        String query = """
                SELECT
                    r.id_report,
                    r.id_post,
                    r.id_user,
                    r.desc,
                    r.created_at,
                    r.isNoticed,
                    u.username
                FROM report r
                JOIN user u ON r.id_user = u.id_user
                WHERE r.isNoticed = 0 -- Hanya ambil laporan yang belum diperhatikan (Current Report)
                ORDER BY r.created_at DESC;
                """;
        ConnectDB db = new ConnectDB();

        try (Connection con = db.getConnetion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int reportId = rs.getInt("id_report");
                int postId = rs.getInt("id_post");
                int reporterUserId = rs.getInt("id_user");
                String reason = rs.getString("desc");
                LocalDateTime timestamp = rs.getTimestamp("created_at").toLocalDateTime();
                boolean isNoticed = rs.getInt("isNoticed") == 1; // Konversi int ke boolean
                String reporterUsername = rs.getString("username"); // Ambil username

                ReportData reportData = new ReportData(reportId, postId, reporterUserId, reason, timestamp, isNoticed);

                // Tambahkan username ke ReportData (jika Anda ingin menyimpannya di ReportData)
                // Atau cukup gunakan di ReportReasonViewController
                // Untuk kesederhanaan, kita akan mengembalikannya di sini dan menggunakannya di ReportController.

                groupedReports.computeIfAbsent(postId, k -> new ArrayList<>()).add(reportData);
            }
        } catch (Exception e) {
            System.err.println("Error fetching grouped reports from database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return groupedReports;
    }




    /**
     * Mengambil semua laporan yang sudah ditandai 'noticed' dari database.
     *
     * @return Map<Integer, List<ReportData>> di mana key adalah postId, dan value adalah list ReportData.
     */
    public static Map<Integer, List<ReportData>> getAllNoticedReportsFromDatabase() {
        Map<Integer, List<ReportData>> noticedReports = new HashMap<>();
        String query = """
                SELECT
                    r.id_report,
                    r.id_post,
                    r.id_user,
                    r.desc,
                    r.created_at,
                    r.isNoticed,
                    u.username
                FROM report r
                JOIN user u ON r.id_user = u.id_user
                WHERE r.isNoticed = 1 -- Hanya ambil laporan yang sudah diperhatikan (Noticed Report)
                ORDER BY r.created_at DESC;
                """;
        ConnectDB db = new ConnectDB();

        try (Connection con = db.getConnetion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int reportId = rs.getInt("id_report");
                int postId = rs.getInt("id_post");
                int reporterUserId = rs.getInt("id_user");
                String reason = rs.getString("desc");
                LocalDateTime timestamp = rs.getTimestamp("created_at").toLocalDateTime();
                boolean isNoticed = rs.getInt("isNoticed") == 1;
                String reporterUsername = rs.getString("username");

                ReportData reportData = new ReportData(reportId, postId, reporterUserId, reason, timestamp, isNoticed);
                noticedReports.computeIfAbsent(postId, k -> new ArrayList<>()).add(reportData);
            }
        } catch (Exception e) {
            System.err.println("Error fetching noticed reports from database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return noticedReports;
    }

    // Inner class untuk merepresentasikan satu entri laporan yang diambil dari DB
    public static class ReportData {
        private int reportId;
        private int postId;
        private int reporterUserId;
        private String reason;
        private LocalDateTime timestamp;
        private boolean isNoticed;

        public ReportData(int reportId, int postId, int reporterUserId, String reason, LocalDateTime timestamp, boolean isNoticed) {
            this.reportId = reportId;
            this.postId = postId;
            this.reporterUserId = reporterUserId;
            this.reason = reason;
            this.timestamp = timestamp;
            this.isNoticed = isNoticed;
        }

        // Getters
        public int getReportId() { return reportId; }
        public int getPostId() { return postId; }
        public int getReporterUserId() { return reporterUserId; }
        public String getReason() { return reason; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isNoticed() { return isNoticed; }
    }



    @Override
    public boolean create(Object item) {
        String sql = "INSERT INTO report (id_post, id_user, desc, created_at, isNoticed) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";
        ConnectDB db = new ConnectDB();
        ReportModel reportItem = (ReportModel) item; // cast item ke report model ( idk what these meant)

        try(Connection con = db.getConnetion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reportItem.getPostId());
            ps.setString(2, reportItem.getUserId());
            ps.setString(3, reportItem.getKeluhan());
            ps.setInt(4, 0); // is noticed ( default false )

            return ps.executeUpdate() > 0; // Return true if row was inserted

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            db.closeConnection();
        }

//        return false;
    }













}
