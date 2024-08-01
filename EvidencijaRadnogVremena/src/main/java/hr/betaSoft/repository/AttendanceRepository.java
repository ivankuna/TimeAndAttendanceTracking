package hr.betaSoft.repository;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Attendance findById(long id);

    Attendance findByEmployee(Employee employee);

    Attendance findFirstByEmployeeOrderByIdDesc(Employee employee);
}
