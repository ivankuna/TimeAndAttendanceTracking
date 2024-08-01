package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.EmployeeRepository;
import hr.betaSoft.security.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Transactional
    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee != null) {
            employeeRepository.delete(employee);
        }
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> findByUser(User user) {
        return employeeRepository.findByUser(user);
    }

    @Override
    public Employee findByOib(String oib) {
        return employeeRepository.findByOib(oib);
    }

    @Override
    public Employee findFirstByOib(String oib) {
        return employeeRepository.findFirstByOib(oib);
    }

    @Override
    public Employee findFirstByOibAndUser(String oib, User user) {
        return employeeRepository.findFirstByOibAndUser(oib, user);
    }

    @Override
    public Employee findByPinAndMachineId(String pin, String machineId) {
        return employeeRepository.findByPinAndMachineId(pin, machineId);
    }

    @Override
    public Employee findByPinAndUser(String pin, User user) {
        return employeeRepository.findByPinAndUser(pin, user);
    }
}