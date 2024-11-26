package hr.betaSoft.service;

import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.EmployeeRepository;
import hr.betaSoft.security.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final AttendanceService attendanceService;

    private final AbsenceRecordService absenceRecordService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, AttendanceService attendanceService, AbsenceRecordService absenceRecordService) {
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
        this.absenceRecordService = absenceRecordService;
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
            attendanceService.deleteAllByEmployee(employee);
            absenceRecordService.deleteAllByEmployee(employee);
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

    @Override
    public List<Employee> totalWorkHoursCalcForEmployees(List<Employee> employees) {

        for (Employee employee : employees) {
            totalWorkHoursCalcForEmployee(employee);
        }
        return employees;
    }

    @Override
    public Employee totalWorkHoursCalcForEmployee(Employee employee) {

        int mondayWorkHours = employee.getMondayWorkHours() != null ? employee.getMondayWorkHours() : 0;
        int tuesdayWorkHours = employee.getTuesdayWorkHours() != null ? employee.getTuesdayWorkHours() : 0;
        int wednesdayWorkHours = employee.getWednesdayWorkHours() != null ? employee.getWednesdayWorkHours() : 0;
        int thursdayWorkHours = employee.getThursdayWorkHours() != null ? employee.getThursdayWorkHours() : 0;
        int fridayWorkHours = employee.getFridayWorkHours() != null ? employee.getFridayWorkHours() : 0;
        int saturdayWorkHours = employee.getSaturdayWorkHours() != null ? employee.getSaturdayWorkHours() : 0;
        int sundayWorkHours = employee.getSundayWorkHours() != null ? employee.getSundayWorkHours() : 0;

        int weeklyWorkingHours = mondayWorkHours +
                tuesdayWorkHours +
                wednesdayWorkHours +
                thursdayWorkHours +
                fridayWorkHours +
                saturdayWorkHours +
                sundayWorkHours;

        employee.setWeeklyWorkingHours(weeklyWorkingHours);

        return employee;
    }

    @Override
    public Employee setEmployeeDailyWorkHours(Employee employee) {
        employee.setMondayWorkHours(employee.getMondayWorkHours() == null ? 0 : employee.getMondayWorkHours());
        employee.setTuesdayWorkHours(employee.getTuesdayWorkHours() == null ? 0 : employee.getTuesdayWorkHours());
        employee.setWednesdayWorkHours(employee.getWednesdayWorkHours() == null ? 0 : employee.getWednesdayWorkHours());
        employee.setThursdayWorkHours(employee.getThursdayWorkHours() == null ? 0 : employee.getThursdayWorkHours());
        employee.setFridayWorkHours(employee.getFridayWorkHours() == null ? 0 : employee.getFridayWorkHours());
        employee.setSaturdayWorkHours(employee.getSaturdayWorkHours() == null ? 0 : employee.getSaturdayWorkHours());
        employee.setSundayWorkHours(employee.getSundayWorkHours() == null ? 0 : employee.getSundayWorkHours());

        return employee;
    }

    @Override
    public String getEmployeeDetailsForHtml(Long id) {
        Employee employee = findById(id);

        return employee.getFirstName() + " " +
                employee.getLastName() + ", OIB: " +
                employee.getOib();
    }
}