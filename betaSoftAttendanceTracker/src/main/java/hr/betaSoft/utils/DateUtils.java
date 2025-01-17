package hr.betaSoft.utils;

import hr.betaSoft.tools.DateTimeStorage;

import java.sql.Date;
import java.text.ParseException;
import java.time.*;
import java.util.*;

public class DateUtils {

    public static final List<String> WEEKDAYS = Arrays.asList(
            "Pon",
            "Uto",
            "Sri",
            "Čet",
            "Pet",
            "Sub",
            "Ned"
    );

    public static final List<String> MONTHS = Arrays.asList(
            "siječanj",
            "veljača",
            "oOžujak",
            "travanj",
            "svibanj",
            "lipanj",
            "srpanj",
            "kolovoz",
            "rujan",
            "listopad",
            "studeni",
            "prosinac"
    );

    public static Date getFirstDateOfMonth(String year, String month) {

        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month);

        YearMonth yearMonth = YearMonth.of(yearInt, monthInt);

        LocalDate firstDate = yearMonth.atDay(1);

        return Date.valueOf(firstDate);
    }

    public static Date getLastDateOfMonth(String year, String month) {

        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month);

        YearMonth yearMonth = YearMonth.of(yearInt, monthInt);

        LocalDate lastDate = yearMonth.atEndOfMonth();

        return Date.valueOf(lastDate);
    }

    public static boolean isLeapYear (String paramYear) {

        boolean leapYear = false;

        int year = Integer.parseInt(paramYear);

        if (year % 4 == 0) {
            leapYear = true;
            if (year % 100 == 0 && year % 400 != 0) {
                leapYear = false;
            }
        }

        return leapYear;
    }

    public static String getDayOfDate(Date date) {

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int val = cal.get(Calendar.DAY_OF_WEEK);

        String day = "";

        switch (val) {
            case 1 -> day = WEEKDAYS.get(6);
            case 2 -> day = WEEKDAYS.get(0);
            case 3 -> day = WEEKDAYS.get(1);
            case 4 -> day = WEEKDAYS.get(2);
            case 5 -> day = WEEKDAYS.get(3);
            case 6 -> day = WEEKDAYS.get(4);
            case 7 -> day = WEEKDAYS.get(5);
        }

        return day;
    }

    public static int getNumOfDaysInMonth(String paramYear, String paramMonth) {

        boolean leapYear = isLeapYear(paramYear);

        int numOfDays = 0;

        int month = Integer.parseInt(paramMonth);

        switch (month) {
            case 1, 3, 5, 7, 8, 10, 12:
                numOfDays = 31;
                break;
            case 2:
                numOfDays = leapYear ? 29 : 28;
                break;
            case 4, 6, 9, 11:
                numOfDays = 30;
                break;
            default:
                break;
        }
        return numOfDays;
    }

    public static int reduceDateToDay(Date paramDate) {

        char[] dateCharArray = paramDate.toString().toCharArray();

        String tempDateFirstNum = String.valueOf(dateCharArray[dateCharArray.length - 2]);

        String dateFirstNum = tempDateFirstNum.equals("0") ?
                "" : tempDateFirstNum;

        String dateSecondNum = String.valueOf(dateCharArray[dateCharArray.length - 1]);

        String date = dateFirstNum + dateSecondNum;

        return Integer.parseInt(date);
    }

    public static String reduceDateToMonth(Date date) {

        char[] dateCharArray = date.toString().toCharArray();

        String month = String.valueOf(dateCharArray[5]) + dateCharArray[6];

        return month;
    }

    public static String reduceDateToYear(Date date) {

        char[] dateCharArray = date.toString().toCharArray();

        String year = String.valueOf(dateCharArray[0]) + dateCharArray[1] + dateCharArray[2] + dateCharArray[3];

        return year;
    }

    public static String getDayBefore(String paramDay) {

        String dayBefore = "";

        int index = 0;

        for (String day : WEEKDAYS) {
            if (Objects.equals(paramDay, day)) {
                index = WEEKDAYS.indexOf(day) > 0 ? WEEKDAYS.indexOf(day) : WEEKDAYS.size();
                dayBefore = WEEKDAYS.get(index - 1);
                break;
            }
        }
        return dayBefore;
    }

    public static String getDayAfter(String paramDay) {

        String dayBefore = "";

        int index = 0;

        for (String day : WEEKDAYS) {
            if (Objects.equals(paramDay, day)) {
                index = WEEKDAYS.indexOf(day) < 6 ? WEEKDAYS.indexOf(day) : -1;
                dayBefore = WEEKDAYS.get(index + 1);
                break;
            }
        }
        return dayBefore;
    }

    public static String getMonthBefore(String paramMonth) {

        int monthInt = Integer.parseInt(paramMonth);
        int monthBeforeInt = monthInt - 1 == 0 ? 12 : monthInt - 1;

        return monthBeforeInt < 10 ? "0" + monthBeforeInt : String.valueOf(monthBeforeInt);
    }

    public static String returnTimeDifference(String clockInDate, String clockInTime, String clockOutDate, String clockOutTime) {

        String startDate = clockInDate + " " + clockInTime;
        String endDate = clockOutDate + " " + clockOutTime;

        long hours = 0;
        long minutes = 0;

        try {
            java.util.Date tempStartDate = DateTimeStorage.DATE_TIME_FORMAT.parse(startDate);
            java.util.Date tempEndDate = DateTimeStorage.DATE_TIME_FORMAT.parse(endDate);

            long timeDiff = tempEndDate.getTime() - tempStartDate.getTime();

            hours = timeDiff / (1000 * 60 * 60);

            minutes = (timeDiff / (1000 * 60)) % 60;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format("%02d:%02d", hours, minutes);
    }

    public static String timeAddition(String timeA, String timeB) {

        timeA = timeA == null || timeA.trim().isEmpty() ? "00:00" : timeA;
        timeB = timeB == null || timeB.trim().isEmpty() ? "00:00" : timeB;

        String[] timeASplit = timeA.split(":");
        String[] timeBSplit = timeB.split(":");

        int hoursA = Integer.parseInt(timeASplit[0]);
        int minutesA = Integer.parseInt(timeASplit[1]);
        int hoursB = Integer.parseInt(timeBSplit[0]);
        int minutesB = Integer.parseInt(timeBSplit[1]);

        int totalMinutes = minutesA + minutesB;
        int totalHours = hoursA + hoursB + totalMinutes / 60;
        totalMinutes = totalMinutes % 60;

        return String.format("%02d:%02d", totalHours, totalMinutes);
    }

    public static String timeSubtraction(String timeA, String timeB) {

        timeA = timeA == null || timeA.trim().isEmpty() ? "00:00" : timeA;
        timeB = timeB == null || timeB.trim().isEmpty() ? "00:00" : timeB;

        String[] timeASplit = timeA.split(":");
        String[] timeBSplit = timeB.split(":");

        int hoursA = Integer.parseInt(timeASplit[0]);
        int minutesA = Integer.parseInt(timeASplit[1]);
        int hoursB = Integer.parseInt(timeBSplit[0]);
        int minutesB = Integer.parseInt(timeBSplit[1]);

        boolean surplus = false;

        int totalMinutes;
        int totalHours;

        if (minutesA - minutesB < 0 && hoursA - hoursB > 0) {
            totalMinutes = 60 - (minutesB - minutesA);
            surplus = true;
        } else {
            totalMinutes = minutesA - minutesB;
        }

        totalHours = surplus ? hoursA - hoursB - 1 : hoursA - hoursB;

        return String.format("%02d:%02d", totalHours, totalMinutes);
    }

    public static boolean isTimeFormatValid(String time) {

        List<String> timeStringList = new ArrayList<>();

        if (!time.trim().isEmpty()) {
            for (char c : time.toCharArray()) {
                timeStringList.add(String.valueOf(c));
            }
        } else {
            return true;
        }

        if (!timeStringList.contains(":") || !Objects.equals(timeStringList.get(2), ":")) {
            return false;
        }

        if (timeStringList.size() != 5) {
            return false;
        }

        try {
            String[] timeArray = time.split(":");
            int hours = Integer.parseInt(timeArray[0]);
            int minutes = Integer.parseInt(timeArray[1]);

            if (hours > 23 || hours < 0) {
                return false;
            }
            if (minutes > 59 || minutes < 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static LocalDateTime sqlDateAndTimeToLocalDateTime (Date sqlDate, String time) {

        LocalDate localDate = sqlDate.toLocalDate();
        LocalTime localTime = LocalTime.parse(time);

        return LocalDateTime.of(localDate, localTime);
    }

    public static String returnWeekday(int dateDayValue, String month, String year) {

        Date date = DateUtils.getFirstDateOfMonth(year, month);
        String day = DateUtils.getDayOfDate(date);

        for (int i = 1; i < dateDayValue; i++) {
            day = DateUtils.getDayAfter(day);
        }

        return  day;
    }

    public static String getCurrentMonth() {

        Date currentDate = DateTimeStorage.getCurrentDate();

        return String.valueOf(currentDate.toString().toCharArray()[5]) + currentDate.toString().toCharArray()[6];
    }

    public static String getCurrentYear() {

        Date currentDate = DateTimeStorage.getCurrentDate();

        return String.valueOf(currentDate.toString().toCharArray()[0])
                + currentDate.toString().toCharArray()[1]
                + currentDate.toString().toCharArray()[2]
                + currentDate.toString().toCharArray()[3];
    }

    public static Date returnSqlDate(Integer day, String month, String year) {

        java.util.Date utilDate;

        try {
            String date = String.format("%02d." + month + "." + year, day);
            utilDate = DateTimeStorage.DATE_FORMAT.parse(date);
        } catch (Exception e) {
            utilDate = null;
        }

        return utilDate != null ? new java.sql.Date(utilDate.getTime()) : null;
    }
}