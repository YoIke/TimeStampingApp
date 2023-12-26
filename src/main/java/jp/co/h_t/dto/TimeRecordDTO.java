package jp.co.h_t.dto;

import java.time.LocalDateTime;

/**
 * タイムレコードデータを表すクラス。
 */
public class TimeRecordDTO {
    private int id;
    private LocalDateTime record;
    private int type;
    private int userId;

    /**
     * コンストラクタでタイムレコードデータを初期化します。
     *
     * @param id     タイムレコードのID
     * @param record タイムレコードの日時
     * @param type   タイムレコードの種類 (出勤・退勤)
     * @param userId ユーザーID
     */
    public TimeRecordDTO(int id, LocalDateTime record, int type, int userId) {
        this.id = id;
        this.record = record;
        this.type = type;
        this.userId = userId;
    }

    /**
     * タイムレコードのIDを取得します。
     *
     * @return タイムレコードのID
     */
    public int getId() {
        return id;
    }

    /**
     * タイムレコードのIDを設定します。
     *
     * @param id タイムレコードのID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * タイムレコードの日時を取得します。
     *
     * @return タイムレコードの日時
     */
    public LocalDateTime getRecord() {
        return record;
    }

    /**
     * タイムレコードの日時を設定します。
     *
     * @param record タイムレコードの日時
     */
    public void setRecord(LocalDateTime record) {
        this.record = record;
    }

    /**
     * タイムレコードの種類 (出勤・退勤) を取得します。
     *
     * @return タイムレコードの種類
     */
    public int getType() {
        return type;
    }

    /**
     * タイムレコードの種類 (出勤・退勤) を設定します。
     *
     * @param type タイムレコードの種類
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * ユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * ユーザーIDを設定します。
     *
     * @param userId ユーザーID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
