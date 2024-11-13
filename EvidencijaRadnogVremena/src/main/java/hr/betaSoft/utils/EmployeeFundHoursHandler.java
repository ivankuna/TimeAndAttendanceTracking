package hr.betaSoft.utils;

import hr.betaSoft.model.Employee;
import hr.betaSoft.model.EmployeeFundHours;
import hr.betaSoft.model.Holiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeFundHoursHandler {

    public static List<EmployeeFundHours> getFormattedEmployeeFundHours(
            List<Employee> employees,
            List<Holiday> holidayList,
            String year, String month) {

        List<EmployeeFundHours> employeeFundHoursList = new ArrayList<>();
        int serialNumber = 1;

        for (Employee employee : employees) {

            employeeFundHoursList.add(new EmployeeFundHours(
                    serialNumber,
                    employee.getFirstName() + " " + employee.getLastName(),
                    employee.getMondayWorkHours(),
                    employee.getTuesdayWorkHours(),
                    employee.getWednesdayWorkHours(),
                    employee.getThursdayWorkHours(),
                    employee.getFridayWorkHours(),
                    employee.getSaturdayWorkHours(),
                    employee.getSundayWorkHours(),
                    getTotalWeeklyWorkHours(employee),
                    getTotalEmployeeFundHours(employee, year, month),
                    getTotalEmployeeFundHours(employee, year, month) - getNonWorkingHours(employee, year, month, holidayList),
                    getNonWorkingHours(employee, year, month, holidayList)
            ));
            serialNumber ++;
        }

        return employeeFundHoursList;
    }

    private static Integer getTotalWeeklyWorkHours(Employee employee) {

        return employee.getMondayWorkHours() +
                employee.getTuesdayWorkHours() +
                employee.getWednesdayWorkHours() +
                employee.getThursdayWorkHours() +
                employee.getFridayWorkHours() +
                employee.getSaturdayWorkHours() +
                employee.getSundayWorkHours();
    }

    private static Integer getTotalEmployeeFundHours(Employee employee, String year, String month) {

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

    private static Integer getNonWorkingHours(Employee employee, String year, String month, List<Holiday> holidayList) {

        List<Holiday> relevantHolidays = new ArrayList<>();
        int nonWorkingHours = 0;

        for (Holiday holiday : holidayList) {
            if (Objects.equals(month, DateUtils.reduceDateToMonth(holiday.getDateOfHoliday()))) {
                relevantHolidays.add(holiday);
            }
        }

        for (Holiday holiday : relevantHolidays) {
            nonWorkingHours = nonWorkingHours + getWorkHoursForGivenDayInt(employee, DateUtils.getDayOfDate(holiday.getDateOfHoliday()));
        }

        return nonWorkingHours;
    }


    private static Integer getWorkHoursForGivenDayInt(Employee employee, String day) {

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
