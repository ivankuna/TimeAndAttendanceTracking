package hr.betaSoft.tools;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateTimeStorage {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String getCurrentTime() {

        ZoneId zoneId = ZoneId.of("Europe/Zagreb");

        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return currentTime.format(formatter);
    }

    public static Date getCurrentDate() {

        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();

        return new Date(currentTimeMillis);
    }

    public static String getCurrentDateAsString() {

        return DATE_FORMAT.format(getCurrentDate());
    }

    public static String getCurrentDateTimeAsString() {

        return DATE_FORMAT.format(getCurrentDate()) + " " + getCurrentTime();
    }
}
