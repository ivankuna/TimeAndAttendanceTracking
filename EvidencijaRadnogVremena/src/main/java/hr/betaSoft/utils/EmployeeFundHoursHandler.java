package hr.betaSoft.utils;

import hr.betaSoft.model.Employee;
import hr.betaSoft.model.EmployeeFundHours;
import hr.betaSoft.model.Holiday;

import java.sql.Date;
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
        int tempDateDayStart = 1;
        int tempDateDayEnd = numOfDaysInMonth;
        int counter = 0;
        Date tempSignUpDate = employee.getSignUpDate();
        Date tempSignOutDate = employee.getSignOutDate();

        // Ako je signUpDate radnika prije trenutnog mjeseca, tempSignUpDate mora biti prvi dan u trenutnom mjesecu

        if (Objects.equals(tempSignOutDate, null)) {
            if (Objects.equals(DateUtils.reduceDateToMonth(tempSignUpDate), month) && Objects.equals(DateUtils.reduceDateToYear(tempSignUpDate), year)) {
                tempDateDayStart = DateUtils.reduceDateToDay(tempSignUpDate);
            }
        } else {
            if (Objects.equals(DateUtils.reduceDateToMonth(tempSignUpDate), month) && Objects.equals(DateUtils.reduceDateToYear(tempSignUpDate), year) &&
                    Objects.equals(DateUtils.reduceDateToMonth(tempSignOutDate), month) && Objects.equals(DateUtils.reduceDateToYear(tempSignOutDate), year)) {
                tempDateDayEnd = DateUtils.reduceDateToDay(tempSignOutDate);
            }
        }

        for (String day : DateUtils.WEEKDAYS) {
            for (int i = tempDateDayStart; i <= tempDateDayEnd; i++) {
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
        Date tempSignUpDate = employee.getSignUpDate();
        Date tempSignOutDate = employee.getSignOutDate();

        for (Holiday holiday : holidayList) {
            if (Objects.equals(month, DateUtils.reduceDateToMonth(holiday.getDateOfHoliday())) &&
                    Objects.equals(year, DateUtils.reduceDateToYear(holiday.getDateOfHoliday()))) {
                if (Objects.equals(tempSignOutDate, null)) {
                    if (Objects.equals(tempSignUpDate, holiday.getDateOfHoliday()) || holiday.getDateOfHoliday().after(tempSignUpDate)) {
                        relevantHolidays.add(holiday);
                    }
                } else {
                    if ((Objects.equals(tempSignUpDate, holiday.getDateOfHoliday()) || holiday.getDateOfHoliday().after(tempSignUpDate)) &&
                            (Objects.equals(tempSignOutDate, holiday.getDateOfHoliday()) || holiday.getDateOfHoliday().before(tempSignOutDate))) {
                        relevantHolidays.add(holiday);
                    }
                }
            }
        }

        for (Holiday holiday : relevantHolidays) {
            nonWorkingHours = nonWorkingHours + Employee.getWorkHoursForGivenDayInt(employee, DateUtils.getDayOfDate(holiday.getDateOfHoliday()));
        }

        return nonWorkingHours;
    }
}
