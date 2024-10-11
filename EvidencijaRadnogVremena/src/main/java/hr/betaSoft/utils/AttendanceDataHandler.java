package hr.betaSoft.utils;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;

import java.util.ArrayList;
import java.util.List;

public class AttendanceDataHandler {

//    public static List<AttendanceData> getFormattedAttendanceData(List<Attendance> paramAttendanceList, String year, String month) {
//
//        List<AttendanceData> attendanceDataList = new ArrayList<>();
//
//        List<AttendanceData> tempAttendanceDataList = new ArrayList<>();
//
//        for (Attendance attendance : paramAttendanceList) {
//
//
//        }
//
//        return null;
//    }

    public static List<AttendanceData> getFormattedAttendanceData(List<Attendance> paramAttendanceList, String year, String month) {

        int numOfDaysInMonth = DateUtils.getNumOfDaysInMonth(month, year);

        List<Attendance> attendanceList = paramAttendanceList;

        List<AttendanceData> attendanceDataList = new ArrayList<>();

        String dayCounter = DateUtils.getDayOfDate(DateUtils.getFirstDateOfMonth(year, month));

        for (int i = 0; i < numOfDaysInMonth; i++) {
            attendanceDataList.add(new AttendanceData((i+1), dayCounter));
            dayCounter = DateUtils.getDayAfter(dayCounter);
        }

        int index;

        AttendanceData attendanceData;

        for (Attendance attendance : attendanceList) {
            index = DateUtils.reduceDateToDay(attendance.getClockInDate()) - 1;
            attendanceData = attendanceDataList.get(index);
            attendanceData.setPocetakRada(attendance.getClockInTime());
            attendanceData.setZavrsetakRada(attendance.getClockOutTime());
            attendanceData.setUkupnoSatiRada(attendance.getHoursAtWork());
        }

        String totalHoursAtWork = "00:00";

        for (Attendance attendance : attendanceList) {
            totalHoursAtWork = DateUtils.timeCalculator(totalHoursAtWork, attendance.getHoursAtWork());
        }

        attendanceDataList.add(new AttendanceData(totalHoursAtWork));

        return attendanceDataList;
    }
}