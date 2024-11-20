package hr.betaSoft.service;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;

import java.sql.Date;
import java.util.List;

public interface AttendanceService {

    void processClockInData(Employee employee);

    void processClockOutData(Employee employee);

    void processAttendanceDataFromController(Attendance attendance, Employee employee);

    void saveAttendance(Attendance attendance);

    void deleteAttendance(Long id);

    void deleteAllByEmployee(Employee employee);

    Attendance findById(long id);

    List<Attendance> findByEmployee(Employee employee);

    Attendance findFirstByEmployeeOrderByIdDesc(Employee employee);

    Attendance findTopByEmployeeOrderByClockInDateDescClockInTimeDesc(Employee employee);

    List<Attendance> findByEmployeeAndClockOutDateIsNullOrderByClockInDateDesc(Employee employee);

    List<Attendance> findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(Employee employee, Date startDate, Date endDate);

    List<Attendance> findByEmployeeAndClockInDateBeforeAndClockOutDateAfterOrderByClockInDateAscClockInTimeAsc(Employee employee, Date before, Date after);

    Attendance setClockInDayBasedOnDBData(Attendance attendance);

    List<Attendance> setClockInDaysBasedOnDBData(List<Attendance> attendances);

    boolean dateAndTimeOverlap(Attendance firstAttendance, Attendance secondAttendance);
}