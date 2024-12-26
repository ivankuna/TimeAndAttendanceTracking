package hr.betaSoft.service;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface AttendanceService {

    void processClockInData(Employee employee, BigDecimal latitude, BigDecimal longitude);

    void processClockOutData(Employee employee, BigDecimal latitude, BigDecimal longitude);

    void processAttendanceDataFromController(Attendance attendance, Employee employee);

    void saveAttendance(Attendance attendance);

    void deleteAttendance(Long id);

    void deleteAllByEmployee(Employee employee);

    Attendance findById(long id);

    List<Attendance> findByEmployee(Employee employee);

    Attendance findFirstByEmployeeOrderByIdDesc(Employee employee);

    Attendance findTopByEmployeeOrderByClockInDateDescClockInTimeDesc(Employee employee);

    Attendance findFirstByEmployeeOrderByClockInDateDescClockInTimeDescIdDesc(Employee employee);

    List<Attendance> findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(Employee employee, Date startDate, Date endDate);

    List<Attendance> findByEmployeeAndClockInDateBeforeAndClockOutDateAfterOrderByClockInDateAscClockInTimeAsc(Employee employee, Date before, Date after);

    Attendance setClockInDayBasedOnDBData(Attendance attendance);

    List<Attendance> setClockInDaysBasedOnDBData(List<Attendance> attendances);

    boolean dateAndTimeOverlap(Attendance firstAttendance, Attendance secondAttendance);
}