package jp.co.h_t;

import jp.co.h_t.dao.TimeRecordsDAO;
import jp.co.h_t.dao.UsersDAO;

import java.time.LocalDateTime;

// Shift を 2 回押して 'どこでも検索' ダイアログを開き、`show whitespaces` と入力して
// Enter キーを押します。これでコードに空白文字が表示されます。
public class Main {
    public static void main(String[] args) {
        // データベース接続情報を設定します
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String adminID = "postgres";
        String adminPW = "1105";

        // UsersDAO インスタンスを作成します
        UsersDAO usersDAO = new UsersDAO(url, adminID, adminPW);
        TimeRecordsDAO timeRecordsDAO = new TimeRecordsDAO(url, adminID, adminPW);
        var hoge = LocalDateTime.now();
        timeRecordsDAO.create(hoge, 1, 1);
    }
}