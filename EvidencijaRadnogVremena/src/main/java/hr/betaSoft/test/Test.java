package hr.betaSoft.test;

import hr.betaSoft.tools.TimeCalculator;

import java.sql.Date;
import java.util.Calendar;

public class Test {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2024, Calendar.JUNE, 15); // Months are zero-based in Calendar
        Date specificDate1 = new Date(calendar.getTimeInMillis());

        String horsAtWork = TimeCalculator.returnTimeDifference(specificDate1 + " 08:00", specificDate1 + " 14:00");

        System.out.println(horsAtWork);
    }
}
