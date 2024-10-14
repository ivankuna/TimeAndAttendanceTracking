package hr.betaSoft.utils;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDataHandler {

    public static List<AttendanceData> getFormattedAttendanceData(List<Attendance> paramAttendanceList, String year, String month) {

        List<AttendanceData> attendanceDataList = new ArrayList<>();
        List<AttendanceData> tempAttendanceDataList = new ArrayList<>();
        List<AttendanceData> resultList = new ArrayList<>();
        List<Integer> daysAccountedFor = new ArrayList<>();

        for (Attendance attendance : paramAttendanceList) {
            Date clockInDate = attendance.getClockInDate();
            Date clockOutDate = attendance.getClockOutDate();
            String clockInTime = attendance.getClockInTime();
            String clockOutTime = attendance.getClockOutTime();
            int reducedClockInDate = DateUtils.reduceDateToDay(attendance.getClockInDate());
            int reducedClockOutDate = DateUtils.reduceDateToDay(attendance.getClockOutDate());
            String tempDay = DateUtils.getDayOfDate(clockInDate);

            if (attendance.getClockOutDate().after(attendance.getClockInDate())) {
                attendanceDataList.add(new AttendanceData(reducedClockInDate, DateUtils.getDayOfDate(clockInDate), attendance.getClockInTime(),
                        "00:00", DateUtils.returnTimeDifference(clockInDate.toString(), clockInTime, clockInDate.toString(), "24:00")));
                daysAccountedFor.add(reducedClockInDate);
                if (reducedClockOutDate - reducedClockInDate > 1) {
                    for (int i = 1; i < (reducedClockOutDate - reducedClockInDate); i++) {
                        tempDay = DateUtils.getDayAfter(tempDay);
                        attendanceDataList.add(new AttendanceData(reducedClockInDate + i, tempDay, "00:00", "00:00", "24:00"));
                        daysAccountedFor.add(reducedClockInDate + i);
                    }
                }
                attendanceDataList.add(new AttendanceData(reducedClockOutDate, DateUtils.getDayOfDate(clockOutDate), "00:00",
                        clockOutTime, DateUtils.returnTimeDifference(clockOutDate.toString(), "00:00", clockOutDate.toString(), clockOutTime)));
                daysAccountedFor.add(reducedClockOutDate);
            } else {
                attendanceDataList.add(new AttendanceData(reducedClockInDate, DateUtils.getDayOfDate(clockInDate), clockInTime, clockOutTime,
                        DateUtils.returnTimeDifference(clockInDate.toString(), clockInTime, clockOutDate.toString(), clockOutTime)));
            }
        }

        int numOfDaysInMonth = DateUtils.getNumOfDaysInMonth(month, year);
        int counter = 0;

        while (counter < 2) {
            for (int i = 1; i <= numOfDaysInMonth; i++) {
                if (counter < 1) {
                    if (!daysAccountedFor.contains(i)) {
                        tempAttendanceDataList.add(new AttendanceData(i, dayFinder(i, month, year), "", "", ""));
                    }
                } else {
                    for (AttendanceData attendanceData : attendanceDataList) {
                        if (attendanceData.getDatum() == i) {
                            resultList.add(attendanceData);
                        }
                    }
                    for (AttendanceData attendanceData : tempAttendanceDataList) {
                        if (attendanceData.getDatum() == i) {
                            resultList.add(attendanceData);
                        }
                    }
                }
            }
            counter++;
        }

        return resultList;
    }

    private static String dayFinder(int dateDayValue, String month, String year) {

        Date date = DateUtils.getFirstDateOfMonth(year, month);
        String day = DateUtils.getDayOfDate(date);

        for (int i = 1; i < dateDayValue; i++) {
            day = DateUtils.getDayAfter(day);
        }

        return  day;
    }
}