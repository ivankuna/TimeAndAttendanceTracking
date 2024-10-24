package hr.betaSoft.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceData {

    private int date;
    private String day;
    private String startOfWork;
    private String endOfWork;
    private String totalHoursOfWork;
    private String nightWork;
    private String sundayWork;
    private String holidayWork;
    private String overtimeWork;
    private String shiftWork;
    private String splitShiftWork;
    private String fieldWork;
    private String standByHours;
    private String offDaysAndHolidays;
    private String annualLeave;
    private String sickLeave;
    private String paidLeave;
    private String unpaidLeave;
    private String excusedAbsence;

    public AttendanceData(int date, String day, String startOfWork, String endOfWork, String totalHoursOfWork) {
        this.date = date;
        this.day = day;
        this.startOfWork = startOfWork;
        this.endOfWork = endOfWork;
        this.totalHoursOfWork = totalHoursOfWork;
    }

    public AttendanceData(
            String totalHoursOfWork,
            String nightWork,
            String sundayWork,
            String holidayWork,
            String overtimeWork) {
        this.totalHoursOfWork = totalHoursOfWork;
        this.nightWork = nightWork;
        this.sundayWork = sundayWork;
        this.holidayWork = holidayWork;
        this.overtimeWork = overtimeWork;
    }

    // TEST
    public AttendanceData(String totalHoursOfWork, String overtimeWork) {
        this.totalHoursOfWork = totalHoursOfWork;
        this.overtimeWork = overtimeWork;
    }
}