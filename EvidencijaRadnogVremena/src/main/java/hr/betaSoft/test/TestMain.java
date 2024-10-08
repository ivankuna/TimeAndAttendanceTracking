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

    public static void main(String[] args) {

        String day = tempGetDayOfDate(DateTimeStorage.getCurrentDate());

        System.out.println(day);
    }
}
