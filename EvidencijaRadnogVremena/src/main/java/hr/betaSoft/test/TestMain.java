package hr.betaSoft.test;

import java.sql.Date;
import java.util.*;

public class TestMain {

    public static void main(String[] args) {

        Date originalDate = Date.valueOf("2024-11-22");
        // Add 1 day to the date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);

        for (int i = 1; i < 10; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Get the new java.sql.Date
        Date newDate = new Date(calendar.getTimeInMillis());

        System.out.println("Original Date: " + originalDate);
        System.out.println("New Date: " + newDate);
    }
}
