package hr.betaSoft.repository;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findById(long id);

    List<Employee> findByUser(User user);

    Employee findByOib(String oib);

    Employee findFirstByOib(String oib);

    Employee findFirstByOibAndUser(String oib, User user);

    @Query("SELECT e FROM Employee e JOIN e.user u WHERE e.pin = :pin AND u.machineID = :machineId")
    Employee findByPinAndMachineId(@Param("pin") String pin, @Param("machineId") String machineId);

    Employee findByPinAndUser(String pin, User user);
}