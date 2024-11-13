package hr.betaSoft.test;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;
import hr.betaSoft.model.Employee;
import hr.betaSoft.model.Holiday;
import hr.betaSoft.utils.DateUtils;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) {

        Employee employee = new Employee();
        employee.setMondayWorkHours(8);
        employee.setTuesdayWorkHours(8);
        employee.setWednesdayWorkHours(8);
        employee.setThursdayWorkHours(8);
        employee.setFridayWorkHours(8);
        employee.setSaturdayWorkHours(0);
        employee.setSundayWorkHours(0);

        Holiday holiday1 = new Holiday();
        holiday1.setDateOfHoliday(Date.valueOf("2024-11-01"));
        Holiday holiday2 = new Holiday();
        holiday2.setDateOfHoliday(Date.valueOf("2024-11-18"));

        List<Holiday> holidayList = new ArrayList<>();

        holidayList.add(holiday1);
        holidayList.add(holiday2);

        System.out.println(tempGetNonWorkingHours(employee, "2024", "11", holidayList));
    }

    private static Integer tempGetTotalEmployeeFundHours(Employee employee, String year, String month) {

        List<Integer> weekdayCountList = new ArrayList<>();
        int numOfDaysInMonth = DateUtils.getNumOfDaysInMonth(year, month);
        int counter = 0;

        for (String day : DateUtils.WEEKDAYS) {
            for (int i = 1; i <= numOfDaysInMonth; i++) {
                if (Objects.equals(day, DateUtils.returnWeekday(i, month, year))) {
                    counter++;
                }
            }
            weekdayCountList.add(counter);
            counter = 0;
        }

        return employee.getMondayWorkHours() * weekdayCountList.get(0) +
                employee.getTuesdayWorkHours() * weekdayCountList.get(1) +
                employee.getWednesdayWorkHours() * weekdayCountList.get(2) +
                employee.getThursdayWorkHours() * weekdayCountList.get(3) +
                employee.getFridayWorkHours() * weekdayCountList.get(4) +
                employee.getSaturdayWorkHours() * weekdayCountList.get(5) +
                employee.getSundayWorkHours() * weekdayCountList.get(6);
    }

    private static Integer tempGetNonWorkingHours(Employee employee, String year, String month, List<Holiday> holidayList) {

        List<Holiday> relevantHolidays = new ArrayList<>();
        int nonWorkingHours = 0;

        for (Holiday holiday : holidayList) {
            if (Objects.equals(month, DateUtils.reduceDateToMonth(holiday.getDateOfHoliday()))) {
                relevantHolidays.add(holiday);
            }
        }

        for (Holiday holiday : relevantHolidays) {
            nonWorkingHours = nonWorkingHours + tempGetWorkHoursForGivenDayInt(employee, DateUtils.getDayOfDate(holiday.getDateOfHoliday()));
        }

        return nonWorkingHours;
    }


    private static Integer tempGetWorkHoursForGivenDayInt(Employee employee, String day) {

        int intWorkHoursForGivenDay = 0;

        if (Objects.equals(day, DateUtils.WEEKDAYS.get(0))) {
            intWorkHoursForGivenDay = employee.getMondayWorkHours() == null ? 0 : employee.getMondayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(1))) {
            intWorkHoursForGivenDay = employee.getTuesdayWorkHours() == null ? 0 : employee.getTuesdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(2))) {
            intWorkHoursForGivenDay = employee.getWednesdayWorkHours() == null ? 0 : employee.getWednesdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(3))) {
            intWorkHoursForGivenDay = employee.getThursdayWorkHours() == null ? 0 : employee.getThursdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(4))) {
            intWorkHoursForGivenDay = employee.getFridayWorkHours() == null ? 0 : employee.getFridayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(5))) {
            intWorkHoursForGivenDay = employee.getSaturdayWorkHours() == null ? 0 : employee.getSaturdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(6))) {
            intWorkHoursForGivenDay = employee.getSundayWorkHours() == null ? 0 : employee.getSundayWorkHours();
        }

        return intWorkHoursForGivenDay;
    }
}
