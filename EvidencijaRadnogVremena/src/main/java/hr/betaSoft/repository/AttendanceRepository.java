package hr.betaSoft.repository;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findById(long id);

    List<Attendance> findByEmployee(Employee employee);

    Attendance findFirstByEmployeeOrderByIdDesc(Employee employee);

    Attendance findTopByEmployeeOrderByClockInDateDescClockInTimeDesc(Employee employee);

    List<Attendance> findByEmployeeAndClockOutDateIsNullOrderByClockInDateDescClockInTimeDesc(Employee employee);

    List<Attendance> findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(Employee employee, Date startDate, Date endDate);

    List<Attendance> findByEmployeeAndClockInDateBeforeAndClockOutDateAfterOrderByClockInDateAscClockInTimeAsc(Employee employee, Date before, Date after);

    void deleteAllByEmployee(Employee employee);
}
