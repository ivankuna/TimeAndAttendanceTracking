package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;

import java.util.List;

public interface EmployeeService {

    void saveEmployee(Employee employee);

    void deleteEmployee(Long id);

    List<Employee> findAll();

    Employee findById(long id);

    List<Employee> findByUser(User user);

    Employee findByOib(String oib);

    Employee findFirstByOib(String oib);

    Employee findFirstByOibAndUser(String oib, User user);

    Employee findByPinAndMachineId(String pin, String machineId);

    Employee findByPinAndUser(String pin, User user);

    List<Employee> totalWorkHoursCalcForEmployees(List<Employee> employees);

    Employee totalWorkHoursCalcForEmployee(Employee employee);

    Employee setEmployeeDailyWorkHours(Employee employee);

    String getEmployeeDetailsForHtml(Long id);
}