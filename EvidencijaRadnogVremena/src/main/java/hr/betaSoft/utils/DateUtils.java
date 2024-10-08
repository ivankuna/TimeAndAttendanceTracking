package hr.betaSoft.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class DateUtils {

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

    public static String getDayOfDate(Date date) {

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int val = cal.get(Calendar.DAY_OF_WEEK);

        String day = "";

        switch (val) {
            case 1 -> day = "Ned";
            case 2 -> day = "Pon";
            case 3 -> day = "Uto";
            case 4 -> day = "Sri";
            case 5 -> day = "Čet";
            case 6 -> day = "Pet";
            case 7 -> day = "Sub";
        }

        return day;
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

    public static String getDayBefore(String paramDay) {

        String dayBefore = "";

        List<String> weekdays = new ArrayList<>();

        weekdays.add("Pon");
        weekdays.add("Uto");
        weekdays.add("Sri");
        weekdays.add("Čet");
        weekdays.add("Pet");
        weekdays.add("Sub");
        weekdays.add("Ned");

        int index = 0;

        for (String day : weekdays) {
            if (Objects.equals(paramDay, day)) {
                index = weekdays.indexOf(day) > 0 ? weekdays.indexOf(day) : weekdays.size() - 1;
                dayBefore = weekdays.get(index);
                break;
            }
        }

        return dayBefore;
    }
}
