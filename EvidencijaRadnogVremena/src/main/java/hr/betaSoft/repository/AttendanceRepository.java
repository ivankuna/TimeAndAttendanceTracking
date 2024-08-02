package hr.betaSoft.repository;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findById(long id);

    List<Attendance> findByEmployee(Employee employee);

    Attendance findFirstByEmployeeOrderByIdDesc(Employee employee);
}
