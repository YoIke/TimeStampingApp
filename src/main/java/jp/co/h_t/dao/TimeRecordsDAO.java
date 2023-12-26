package jp.co.h_t.dao;

import jp.co.h_t.dto.TimeRecordDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeRecordsDAO {
    private String url;
    private String adminID;
    private String adminPW;

    public TimeRecordsDAO(String url, String adminID, String adminPW) {
        this.url = url;
        this.adminID = adminID;
        this.adminPW = adminPW;
    }

    public int create(LocalDateTime record, int type, int userId) {
        String sql = "INSERT INTO timerecords (record, type, user_id) VALUES (?, ?, ?) RETURNING id;";
        int generatedId = -1;

        try (Connection conn = DriverManager.getConnection(url, adminID, adminPW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(record));
            pstmt.setInt(2, type);
            pstmt.setInt(3, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                generatedId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    /**
     * 指定されたユーザーIDと日付に該当するタイムレコードのリストを取得します。
     *
     * @param userId ユーザーID
     * @param ymd    日付
     * @return タイムレコードのリスト
     */
    public List<TimeRecordDTO> getByUserDate(int userId, LocalDateTime ymd) {
        String sql = "SELECT * FROM timerecords WHERE user_id = ? AND DATE(record) = ?;";
        List<TimeRecordDTO> timeRecords = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, adminID, adminPW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(ymd.toLocalDate()));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TimeRecordDTO record = new TimeRecordDTO(
                        rs.getInt("id"),
                        rs.getTimestamp("record").toLocalDateTime(),
                        rs.getInt("type"),
                        rs.getInt("user_id")
                );
                timeRecords.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeRecords;
    }

    /**
     * 指定されたユーザーIDと年月に該当するタイムレコードのリストを取得します。
     *
     * @param userId ユーザーID
     * @param ym     年月
     * @return タイムレコードのリスト
     */
    public List<TimeRecordDTO> getByUserMonth(int userId, LocalDateTime ym) {
        String sql = "SELECT * FROM timerecords WHERE user_id = ? AND EXTRACT(YEAR FROM record) = ? AND EXTRACT(MONTH FROM record) = ?;";
        List<TimeRecordDTO> timeRecords = new ArrayList<>();

        // データベースへの接続とSQL文の実行
        try (Connection conn = DriverManager.getConnection(url, adminID, adminPW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, ym.getYear());
            pstmt.setInt(3, ym.getMonthValue());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TimeRecordDTO record = new TimeRecordDTO(
                        rs.getInt("id"),
                        rs.getTimestamp("record").toLocalDateTime(),
                        rs.getInt("type"),
                        rs.getInt("user_id")
                );
                timeRecords.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeRecords;
    }

    /**
     * 指定されたユーザーID、年、および月に該当するタイムレコードのリストを取得します。
     *
     * @param userId ユーザーID
     * @param year   年
     * @param month  月
     * @return タイムレコードのリスト
     */
    public List<TimeRecordDTO> getByUserMonth(int userId, int year, int month) {
        String sql = "SELECT * FROM timerecords WHERE user_id = ? AND EXTRACT(YEAR FROM record) = ? AND EXTRACT(MONTH FROM record) = ? ORDER BY record;";
        List<TimeRecordDTO> timeRecords = new ArrayList<>();

        // データベースへの接続とSQL文の実行
        try (Connection conn = DriverManager.getConnection(url, adminID, adminPW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TimeRecordDTO record = new TimeRecordDTO(
                        rs.getInt("id"),
                        rs.getTimestamp("record").toLocalDateTime(),
                        rs.getInt("type"),
                        rs.getInt("user_id")
                );
                timeRecords.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeRecords;
    }

}


