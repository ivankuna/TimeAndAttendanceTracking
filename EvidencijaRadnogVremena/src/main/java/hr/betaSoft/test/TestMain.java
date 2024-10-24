package hr.betaSoft.test;

import hr.betaSoft.model.AttendanceData;
import hr.betaSoft.utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) {

        String res = asd("08:00", "", "");

        System.out.println(res);
    }

    public static String asd(String timeA, String timeB, String location) {

        timeA = timeA == null || timeA.trim().isEmpty() ? "00:00" : timeA;
        timeB = timeB == null || timeB.trim().isEmpty() ? "00:00" : timeA;

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

    private static boolean tempCheckForNegativeTime(String time) {

        boolean isNegative = false;

        for (char c : time.toCharArray()) {
            if (Objects.equals(c, '-')) {
                isNegative = true;
                break;
            }
        }

        return isNegative;
    }

    public static String getMonthBeforeTest(String paramMonth) {

        int monthInt = Integer.parseInt(paramMonth);
        int monthBeforeInt = monthInt - 1 == 0 ? 12 : monthInt - 1;

        return monthBeforeInt < 10 ? "0" + monthBeforeInt : String.valueOf(monthBeforeInt);
    }

    private void referenceCode() {

        String timeA = "06:00";
        String timeB = "07:00";

        int rezultat = timeA.compareTo(timeB);

        if (rezultat < 0) {
            System.out.println(timeA + " je manji od " + timeB);
        } else if (rezultat > 0) {
            System.out.println(timeA + " je veći od " + timeB);
        } else {
            System.out.println(timeA + " i " + timeB + " su jednaki.");
        }
    }
}
