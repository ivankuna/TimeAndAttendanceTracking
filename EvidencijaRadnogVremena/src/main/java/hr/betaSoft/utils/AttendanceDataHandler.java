package hr.betaSoft.utils;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;

import java.util.ArrayList;
import java.util.List;

public class AttendanceDataHandler {

    public static List<AttendanceData> getFormattedAttendanceData(List<Attendance> paramAttendanceList, String year, String month) {

        int numOfDaysInMonth = 0;

        List<Attendance> attendanceList = paramAttendanceList;

        List<AttendanceData> tempAttendanceDataList = new ArrayList<>();
        List<AttendanceData> attendanceDataList = new ArrayList<>();

        for (Attendance attendance : attendanceList) {
            tempAttendanceDataList.add(
                    new AttendanceData(
                            DateUtils.reduceDateToDay(attendance.getClockInDate()), DateUtils.getDayOfDate(attendance.getClockInDate()),
                            attendance.getClockInTime(), attendance.getClockOutTime(), attendance.getHoursAtWork(),
                            "", "","","","","","",""
                            )
            );
        }

        int counter = 0;

        for (AttendanceData attendanceData : tempAttendanceDataList) {
            if ((attendanceData.getDatum() - counter) > 1) {
                attendanceDataList.add(
                        new AttendanceData((counter + 1), DateUtils.getDayBefore(attendanceData.getDan()), "", "", "",
                                "", "", "", "", "", "", "", "")
                );
            }
            attendanceDataList.add(attendanceData);
            counter = attendanceData.getDatum();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return attendanceDataList;
    }
}