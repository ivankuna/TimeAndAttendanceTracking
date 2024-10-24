package hr.betaSoft.utils;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;
import hr.betaSoft.model.Employee;
import hr.betaSoft.model.Holiday;
import hr.betaSoft.tools.DateTimeStorage;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttendanceDataHandler {

    public static List<AttendanceData> getFormattedAttendanceData(
            Employee employee,
            List<Attendance> paramAttendanceList,
            List<Attendance> paramAttendanceListForOvertimeCalc,
            List<Holiday> paramHolidayList,
            String year, String month) {

        List<AttendanceData> resultList = new ArrayList<>();
        List<AttendanceData> attendanceDataListForOvertimeCalc;

        for (Attendance attendance : paramAttendanceList) {
            if (attendance.getStatus() != 0) {
                return  resultList;
            }
        }

        resultList = prepareAttendanceDataWithDateTimeAttributesPopulated(paramAttendanceList, year, month);

        attendanceDataListForOvertimeCalc = prepareAttendanceDataWithDateTimeAttributesPopulated(
                paramAttendanceListForOvertimeCalc, year, DateUtils.getMonthBefore(month));

        if (Objects.equals(attendanceDataListForOvertimeCalc.get(attendanceDataListForOvertimeCalc.size() - 1).getDay(), DateUtils.WEEKDAYS.get(6)) &&
                Objects.equals(attendanceDataListForOvertimeCalc.get(attendanceDataListForOvertimeCalc.size() - 1).getDate(), DateUtils.reduceDateToDay(
                        DateUtils.getLastDateOfMonth(year, DateUtils.getMonthBefore(month))))) {
            resultList = populateMiscTypeWorkHourAttributes(resultList, null, paramHolidayList, year, month, employee);
        } else {
            resultList = populateMiscTypeWorkHourAttributes(resultList, attendanceDataListForOvertimeCalc, paramHolidayList, year, month, employee);
        }

        resultList = displayTotalHoursOfWork(resultList);

        return resultList;
    }

    private static List<AttendanceData> prepareAttendanceDataWithDateTimeAttributesPopulated(
            List<Attendance> paramAttendanceList,
            String year, String month) {

        List<AttendanceData> attendanceDataList = new ArrayList<>();
        List<AttendanceData> tempAttendanceDataList = new ArrayList<>();
        List<AttendanceData> resultList = new ArrayList<>();
        List<Integer> daysAccountedFor = new ArrayList<>();

        for (Attendance attendance : paramAttendanceList) {
            Date clockInDate;
            String clockInTime;
            Date clockOutDate;
            String clockOutTime;

            if (!Objects.equals(DateUtils.reduceDateToMonth(attendance.getClockInDate()), month)) {
                clockInDate = DateUtils.getFirstDateOfMonth(year, month);
                clockInTime = "00:00";
            } else {
                clockInDate = attendance.getClockInDate();
                clockInTime = attendance.getClockInTime();
            }

            if (!Objects.equals(DateUtils.reduceDateToMonth(attendance.getClockOutDate()), month)) {
                clockOutDate = DateUtils.getLastDateOfMonth(year, month);
                clockOutTime = "24:00";
            } else {
                clockOutDate = attendance.getClockOutDate();
                clockOutTime = attendance.getClockOutTime();
            }

            int reducedClockInDate = DateUtils.reduceDateToDay(clockInDate);
            int reducedClockOutDate = DateUtils.reduceDateToDay(clockOutDate);
            String tempDay = DateUtils.getDayOfDate(clockInDate);

            if (clockOutDate.after(clockInDate)) {
                attendanceDataList.add(new AttendanceData(reducedClockInDate, DateUtils.getDayOfDate(clockInDate), clockInTime,
                    "24:00", DateUtils.returnTimeDifference(clockInDate.toString(), clockInTime, clockInDate.toString(), "24:00")));
                daysAccountedFor.add(reducedClockInDate);
                if (reducedClockOutDate - reducedClockInDate > 1) {
                    for (int i = 1; i < (reducedClockOutDate - reducedClockInDate); i++) {
                        tempDay = DateUtils.getDayAfter(tempDay);
                        attendanceDataList.add(new AttendanceData(reducedClockInDate + i, tempDay, "00:00","24:00", "24:00"));
                        daysAccountedFor.add(reducedClockInDate + i);
                    }
                }
                attendanceDataList.add(new AttendanceData(reducedClockOutDate, DateUtils.getDayOfDate(clockOutDate), "00:00",
                        clockOutTime, DateUtils.returnTimeDifference(clockOutDate.toString(), "00:00", clockOutDate.toString(), clockOutTime)));
                daysAccountedFor.add(reducedClockOutDate);
            } else {
                attendanceDataList.add(new AttendanceData(reducedClockInDate, DateUtils.getDayOfDate(clockInDate), clockInTime, clockOutTime,
                        DateUtils.returnTimeDifference(clockInDate.toString(), clockInTime, clockOutDate.toString(), clockOutTime)));
                daysAccountedFor.add(reducedClockInDate);
            }
        }

        int numOfDaysInMonth = DateUtils.getNumOfDaysInMonth(month, year);

        for (int i = 1; i <= numOfDaysInMonth; i++) {
            if (!daysAccountedFor.contains(i)) {
                tempAttendanceDataList.add(new AttendanceData(i, returnWeekday(i, month, year), "", "", ""));
            }
        }
        for (int i = 1; i <= numOfDaysInMonth; i++) {
            for (AttendanceData attendanceData : attendanceDataList) {
                if (attendanceData.getDate() == i) {
                    resultList.add(attendanceData);
                }
            }
            for (AttendanceData attendanceData : tempAttendanceDataList) {
                if (attendanceData.getDate() == i) {
                    resultList.add(attendanceData);
                }
            }
        }

        return resultList;
    }

    private static List<AttendanceData> populateMiscTypeWorkHourAttributes(
            List<AttendanceData> attendanceDataList,
            List<AttendanceData> attendanceDataListForOvertimeCalc,
            List<Holiday> paramHolidayList,
            String year, String month,
            Employee employee) {

        List<Integer> listOfSundayIndex = new ArrayList<>();

        for (AttendanceData attendanceData : attendanceDataList) {
            attendanceData.setNightWork(returnTotalHoursOfNightWork(attendanceData, employee, year, month));
            if (attendanceData.getDay().equals(DateUtils.WEEKDAYS.get(6))) {
                if (!isHoliday(paramHolidayList, returnSqlDate(attendanceData.getDate(), month, year))) {
                    attendanceData.setSundayWork(Objects.equals(attendanceData.getTotalHoursOfWork(), "00:00") ? "" : attendanceData.getTotalHoursOfWork());
                }
                listOfSundayIndex.add(attendanceDataList.indexOf(attendanceData));
            }
            if (isHoliday(paramHolidayList, returnSqlDate(attendanceData.getDate(), month, year))) {
                attendanceData.setHolidayWork(attendanceData.getTotalHoursOfWork());
            }
            if (!listOfSundayIndex.contains(attendanceDataList.indexOf(attendanceData)) &&
                    attendanceDataList.indexOf(attendanceData) == (attendanceDataList.size() - 1)) {
                listOfSundayIndex.add(attendanceDataList.indexOf(attendanceData));
            }
        }

        for (Integer sundayIndex : listOfSundayIndex) {
            attendanceDataList = overtimeCalc(employee, attendanceDataList, attendanceDataListForOvertimeCalc, sundayIndex);
        }

        return attendanceDataList;
    }

    private static String returnTotalHoursOfNightWork(AttendanceData attendanceData, Employee employee, String year, String month) {

        String timeA = attendanceData.getStartOfWork();
        String timeB = attendanceData.getEndOfWork();
        String nightWork = "";
        String dateInQuestion = String.format(year + "-" + month + "-%02d", attendanceData.getDate());

        if (isNightWork(employee, timeA)) {
            if (isNightWork(employee, timeB)) {
                if (attendanceData.getStartOfWork().compareTo(employee.getNightWorkEnd()) <= 0 &&
                        attendanceData.getEndOfWork().compareTo(employee.getNightWorkStart()) >= 0) {
                    String nightWorkMorning = DateUtils.returnTimeDifference(dateInQuestion, attendanceData.getStartOfWork(), dateInQuestion, employee.getNightWorkEnd());
                    String nightWorkEvening = DateUtils.returnTimeDifference(dateInQuestion, employee.getNightWorkStart(), dateInQuestion, attendanceData.getEndOfWork());
                    nightWork = DateUtils.timeAddition(nightWorkMorning, nightWorkEvening);
                } else {
                    nightWork = DateUtils.returnTimeDifference(dateInQuestion, attendanceData.getStartOfWork(), dateInQuestion, attendanceData.getEndOfWork());
                }
            } else {
                nightWork = DateUtils.returnTimeDifference(dateInQuestion, attendanceData.getStartOfWork(), dateInQuestion,
                        attendanceData.getStartOfWork().compareTo(employee.getNightWorkEnd()) <= 0 ? employee.getNightWorkEnd() : "24:00");
            }
        } else {
            if (isNightWork(employee, timeB)) {
                nightWork = DateUtils.returnTimeDifference(dateInQuestion, employee.getNightWorkStart(), dateInQuestion, attendanceData.getEndOfWork());
            }
        }

        return nightWork;
    }

    private static List<AttendanceData> overtimeCalc(
            Employee employee,
            List<AttendanceData> attendanceDataList,
            List<AttendanceData> attendanceDataListForOvertimeCalc,
            Integer sundayIndex) {

        String weeklyWorkingHours = String.format("%02d:00", employee.getWeeklyWorkingHours());
        String actualTotalHoursOfWork = "00:00";
        String overtime = "00:00";
        boolean isOvertime = false;

        int startIndex = 0;
        int endIndex = sundayIndex;

        if (attendanceDataListForOvertimeCalc != null && sundayIndex < 6) {
            int tempMonIndex = 0;
            for (AttendanceData attendanceDataOT : attendanceDataListForOvertimeCalc) {
                if (Objects.equals(attendanceDataOT.getDay(), DateUtils.WEEKDAYS.get(0))) {
                    tempMonIndex = attendanceDataListForOvertimeCalc.indexOf(attendanceDataOT);
                }
            }
            for (int i = tempMonIndex; i <= attendanceDataListForOvertimeCalc.size() - 1; i++) {
                String tempTime = attendanceDataListForOvertimeCalc.get(i).getTotalHoursOfWork();
                actualTotalHoursOfWork = DateUtils.timeAddition(
                        actualTotalHoursOfWork.trim().isEmpty() ? "00:00" : actualTotalHoursOfWork,
                        tempTime.trim().isEmpty() ? "00:00" : tempTime);
            }
            if (checkForNegativeTime(DateUtils.timeSubtraction(weeklyWorkingHours, actualTotalHoursOfWork))) {
                overtime = DateUtils.timeSubtraction(actualTotalHoursOfWork, weeklyWorkingHours);
            }
        }

        for (int i = sundayIndex; i >= 0; i--) {
            if (Objects.equals(attendanceDataList.get(i).getDay(), DateUtils.WEEKDAYS.get(0))) {
                startIndex = i;
                break;
            }
        }

        for (int i = startIndex; i <= endIndex; i++) {
            String tempTime = attendanceDataList.get(i).getTotalHoursOfWork().trim().isEmpty() ?
                    "00:00" : attendanceDataList.get(i).getTotalHoursOfWork();
            actualTotalHoursOfWork = DateUtils.timeAddition(actualTotalHoursOfWork, tempTime);
        }

        if (checkForNegativeTime(DateUtils.timeSubtraction(weeklyWorkingHours, actualTotalHoursOfWork))) {
            isOvertime = true;
            overtime = attendanceDataListForOvertimeCalc == null ? DateUtils.timeSubtraction(actualTotalHoursOfWork, weeklyWorkingHours) :
            DateUtils.timeAddition(overtime, DateUtils.timeSubtraction(actualTotalHoursOfWork, weeklyWorkingHours));
        }

        if (isOvertime) {
            for (int i = endIndex; i >= startIndex; i--) {
                String totalHoursOfWork = attendanceDataList.get(i).getTotalHoursOfWork().trim().isEmpty() ?
                        "00:00" : attendanceDataList.get(i).getTotalHoursOfWork();
                if (checkForNegativeTime(DateUtils.timeSubtraction(overtime, totalHoursOfWork))) {
                    attendanceDataList.get(i).setOvertimeWork(Objects.equals(overtime, "00:00") ? "" : overtime);
                    break;
                } else {
                    attendanceDataList.get(i).setOvertimeWork(Objects.equals(totalHoursOfWork, "00:00") ? "" : totalHoursOfWork);
                    overtime = DateUtils.timeSubtraction(overtime, totalHoursOfWork);
                }
            }
        }

        return attendanceDataList;
    }

    private static boolean isNightWork(Employee employee, String time) {

        return (time.compareTo(employee.getNightWorkStart()) >= 0 && time.compareTo("24:00") <= 0) ||
                (time.compareTo(employee.getNightWorkEnd()) <= 0 && time.compareTo("00:00") >= 0);
    }

    private static String returnWeekday(int dateDayValue, String month, String year) {

        Date date = DateUtils.getFirstDateOfMonth(year, month);
        String day = DateUtils.getDayOfDate(date);

        for (int i = 1; i < dateDayValue; i++) {
            day = DateUtils.getDayAfter(day);
        }

        return  day;
    }

    private static Date returnSqlDate(Integer day, String month, String year) {

        java.util.Date utilDate;

        try {
            String date = String.format("%02d." + month + "." + year, day);
            utilDate = DateTimeStorage.DATE_FORMAT.parse(date);
        } catch (Exception e) {
            utilDate = null;
        }

        return utilDate != null ? new java.sql.Date(utilDate.getTime()) : null;
    }

    private static boolean isHoliday(List<Holiday> holidayList, Date date) {

        boolean isHoliday = false;

        for (Holiday holiday : holidayList) {
            if (Objects.equals(holiday.getDateOfHoliday(), date)) {
                isHoliday = true;
                break;
            }
        }

        return isHoliday;
    }

    private static boolean checkForNegativeTime(String time) {

        boolean isNegative = false;

        for (char c : time.toCharArray()) {
            if (Objects.equals(c, '-')) {
                isNegative = true;
                break;
            }
        }

        return isNegative;
    }

    private static List<AttendanceData> displayTotalHoursOfWork(List<AttendanceData> attendanceDataList) {

        String totalHoursOfWork = "00:00";
        String totalHoursOfNightWork = "00:00";
        String totalHoursOfSundayWork = "00:00";
        String totalHoursOfHolidayWork = "00:00";
        String totalHoursOfOvertimeWork = "00:00";

        for (AttendanceData attendanceData : attendanceDataList) {

            if (attendanceData.getDate() == 31) {
                System.out.println("test");
            }

            if (!Objects.equals(attendanceData.getTotalHoursOfWork(), "")) {
                totalHoursOfWork = DateUtils.timeAddition(totalHoursOfWork, attendanceData.getTotalHoursOfWork());
            }
            if (!Objects.equals(attendanceData.getNightWork(), "")) {
                totalHoursOfNightWork = DateUtils.timeAddition(totalHoursOfNightWork, attendanceData.getNightWork());
            }
            if (!Objects.equals(attendanceData.getSundayWork(), "")) {
                totalHoursOfSundayWork = DateUtils.timeAddition(totalHoursOfSundayWork, attendanceData.getSundayWork());
            }
            if (!Objects.equals(attendanceData.getHolidayWork(), "")) {
                totalHoursOfHolidayWork = DateUtils.timeAddition(totalHoursOfHolidayWork, attendanceData.getHolidayWork());
            }
            if (!Objects.equals(attendanceData.getOvertimeWork(), "")) {
                totalHoursOfOvertimeWork = DateUtils.timeAddition(totalHoursOfOvertimeWork, attendanceData.getOvertimeWork());
            }
        }

        attendanceDataList.add(new AttendanceData(
                                totalHoursOfWork,
                                totalHoursOfNightWork,
                                totalHoursOfSundayWork,
                                totalHoursOfHolidayWork,
                                totalHoursOfOvertimeWork));

        return attendanceDataList;
    }
}