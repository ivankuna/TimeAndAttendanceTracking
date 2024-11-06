package hr.betaSoft.test;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;
import hr.betaSoft.model.Employee;
import hr.betaSoft.utils.DateUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) {

        int num = 123;

        String workHoursForGivenDay = String.format("%02d:00", num);

        System.out.println(workHoursForGivenDay);
    }

    public static boolean overlaps(Attendance attendance1, Attendance attendance2) {
        if (attendance1.getClockInDate() == null || attendance1.getClockOutDate() == null ||
                attendance2.getClockInDate() == null || attendance2.getClockOutDate() == null) {
            return false;  // If dates are missing, we can't determine overlap
        }

        // Check if date ranges overlap
        boolean dateOverlap = !attendance1.getClockOutDate().before(attendance2.getClockInDate()) &&
                !attendance1.getClockInDate().after(attendance2.getClockOutDate());

        if (!dateOverlap) {
            return false; // No overlap if date ranges do not intersect
        }

        // Parse time strings to compare
        LocalTime attendance1StartTime = LocalTime.parse(attendance1.getClockInTime());
        LocalTime attendance1EndTime = LocalTime.parse(attendance1.getClockOutTime());
        LocalTime attendance2StartTime = LocalTime.parse(attendance2.getClockInTime());
        LocalTime attendance2EndTime = LocalTime.parse(attendance2.getClockOutTime());

        // If dates are the same, check if time ranges overlap
        if (attendance1.getClockInDate().equals(attendance2.getClockInDate()) &&
                attendance1.getClockOutDate().equals(attendance2.getClockOutDate())) {
            return !(attendance1EndTime.isBefore(attendance2StartTime) ||
                    attendance1StartTime.isAfter(attendance2EndTime));
        }

        // Overlap is valid if time frames intersect within overlapping dates
        return (attendance1.getClockInDate().equals(attendance2.getClockInDate()) &&
                attendance1EndTime.isAfter(attendance2StartTime)) ||
                (attendance1.getClockOutDate().equals(attendance2.getClockOutDate()) &&
                        attendance1StartTime.isBefore(attendance2EndTime));
    }



    private static boolean testIsNightWorkTest(String nightWorkStart, String nightWorkEnd, String time) {

        return (time.compareTo(nightWorkStart) >= 0 && time.compareTo("24:00") <= 0) ||
                (time.compareTo(nightWorkEnd) <= 0 && time.compareTo("00:00") >= 0);
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
