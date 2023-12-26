package jp.co.h_t.dto;

/**
 * ユーザー情報を保持するDTOクラス。
 */
public class UserDTO {
    private int id;
    private String username;
    private String password;
    private String realname;

    /**
     * ユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public int getId() {
        return id;
    }

    /**
     * ユーザーIDを設定します。
     *
     * @param id ユーザーID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * ユーザー名を取得します。
     *
     * @return ユーザー名
     */
    public String getUsername() {
        return username;
    }

    /**
     * ユーザー名を設定します。
     *
     * @param username ユーザー名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * パスワードを取得します。
     *
     * @return パスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定します。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 実名を取得します。
     *
     * @return 実名
     */
    public String getRealname() {
        return realname;
    }

    /**
     * 実名を設定します。
     *
     * @param realname 実名
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }
}
