package jp.co.h_t;

import java.time.LocalDate;
import java.time.LocalTime;

public class GroupedTimeRecord {
    private LocalDate date;
    private LocalTime arrivalTime;
    private LocalTime leaveTime;
    private String workingHours;
    private boolean weekend;
    private boolean incomplete;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(LocalTime leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isIncomplete() {
        return incomplete;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public String getRowClass() {
        if (isWeekend()) {
            return "weekend";
        } else if (isIncomplete()) {
            return "incomplete";
        }
        return "";
    }
}