package hr.betaSoft.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

    public static void main(String[] args) {
        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        System.out.println(sdf.format(currentTimeMillis));
    }
}
