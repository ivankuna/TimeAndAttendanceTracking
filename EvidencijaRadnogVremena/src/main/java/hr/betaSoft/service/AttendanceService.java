package hr.betaSoft.service;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;

public interface AttendanceService {

    void processClockInData(Employee employee);

    void processClockOutData(Employee employee);

    void saveAttendance(Attendance attendance);

    void deleteAttendance(Long id);

    Attendance findById(long id);

    Attendance findByEmployee(Employee employee);

    Attendance findFirstByEmployeeOrderByIdDesc(Employee employee);
}
