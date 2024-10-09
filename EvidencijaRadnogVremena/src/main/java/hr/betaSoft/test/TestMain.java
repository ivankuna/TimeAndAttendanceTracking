package hr.betaSoft.test;

import hr.betaSoft.tools.DateTimeStorage;

import java.sql.Date;
import java.util.Calendar;

public class TestMain {

    private static String tempGetDayOfDate(Date date) {

        Calendar cal = Calendar.getInstance();
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

    public static boolean tempIsLeapYear (String paramYear) {

        // This extra leap day occurs in each year that is a multiple of 4, except for years evenly divisible by 100 but not by 400.

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

    public static void main(String[] args) {

        // 1988, 1992, and 1996

        System.out.println(tempIsLeapYear("1992"));
    }
}
