package hr.betaSoft.repository;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findById(long id);

    List<Employee> findByUser(User user);

    Employee findByOib(String oib);

    Employee findFirstByOib(String oib);

    Employee findFirstByOibAndUser(String oib, User user);
}