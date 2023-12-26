package jp.co.h_t;

public class CsvRecord {
    private String date;
    private int userId;
    private String startTime;
    private String endTime;

    public CsvRecord(String date, int userId, String startTime, String endTime) {
        this.date = date;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}

