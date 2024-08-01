package hr.betaSoft.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCalculator {
    public static String returnTimeDifference(String startDate, String endDate) {

        long hours = 0;
        long minutes = 0;

        try {
            Date tempStartDate = DateTimeStorage.DATE_TIME_FORMAT.parse(startDate);
            Date tempEndDate = DateTimeStorage.DATE_TIME_FORMAT.parse(endDate);

            long timeDiff = tempEndDate.getTime() - tempStartDate.getTime();

            hours = timeDiff / (1000 * 60 * 60);

            minutes = (timeDiff / (1000 * 60)) % 60;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.format("%02d:%02d", hours, minutes);
    }
}
