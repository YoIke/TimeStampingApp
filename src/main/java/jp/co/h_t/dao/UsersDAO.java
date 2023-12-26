package jp.co.h_t.dao;

import jp.co.h_t.dto.UserDTO;

import java.sql.*;

/**
 * データベースに接続してユーザーを作成し、認証するためのDAOクラスです。
 */
public class UsersDAO {
    private String url;
    private String adminID;
    private String adminPW;

    /**
     * UsersDAOクラスのインスタンスを作成するためのコンストラクタです。
     *
     * @param url     データベースのURL
     * @param adminID 管理者のユーザーID
     * @param adminPW 管理者のパスワード
     */
    public UsersDAO(String url, String adminID, String adminPW) {
        this.url = url;
        this.adminID = adminID;
        this.adminPW = adminPW;
    }

    /**
     * ユーザーを作成するためのメソッドです。
     *
     * @param username 作成するユーザーのユーザー名
     * @param password 作成するユーザーのパスワード
     * @param realname 作成するユーザーの本名
     * @return ユーザーのID。既に同じユーザー名が存在する場合は-1を返します。
     */
    public int create(String username, String password, String realname) {
        try (Connection conn = DriverManager.getConnection(url, adminID, adminPW)) {
            // ユーザーを作成するSQL
            String sql = "INSERT INTO users (username, password, realname) VALUES (?, ?, ?) ON CONFLICT (username) DO NOTHING RETURNING id;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, realname);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // ユーザーが作成された場合は、そのIDを返す
                return rs.getInt("id");
            } else {
                // ユーザーが既に存在していた場合は-1を返す
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // エラーが発生した場合は-1を返す
            return -1;
        }
    }

    /**
     * ユーザーを認証するためのメソッドです。
     *
     * @param username 認証するユーザーのユーザー名
     * @param password 認証するユーザーのパスワード
     * @return ユーザー情報を表すUserDTOオブジェクト。認証に失敗した場合はnullを返します。
     */
    public UserDTO auth(String username, String password) {
        try (Connection conn = DriverManager.getConnection(url, adminID, adminPW)) {
            // ユーザーを認証するためのSQL
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // ユーザーが存在する場合は、UserDTOオブジェクトを作成してユーザー情報を設定する
                UserDTO user = new UserDTO();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRealname(rs.getString("realname"));
                return user;
            } else {
                // ユーザーが存在しない場合はnullを返す
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
