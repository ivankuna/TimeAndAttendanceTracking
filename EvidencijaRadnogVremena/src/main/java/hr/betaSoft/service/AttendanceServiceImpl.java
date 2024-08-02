package hr.betaSoft.service;

import hr.betaSoft.constants.EmailTemplates;
import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.AttendanceRepository;
import hr.betaSoft.tools.DateTimeStorage;
import hr.betaSoft.tools.SendMail;
import hr.betaSoft.tools.TimeCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public void processClockInData(Employee employee) {
        Attendance lastAttendanceRecord = findFirstByEmployeeOrderByIdDesc(employee) == null ? new Attendance() : findFirstByEmployeeOrderByIdDesc(employee);

        Attendance clockIn = new Attendance();

        if (Objects.equals(lastAttendanceRecord.getStatus(), 1) || Objects.equals(lastAttendanceRecord.getId(), null)) {
            sendWarningEmail(employee, lastAttendanceRecord, "clockIn");
        }

        clockIn.setEmployee(employee);
        clockIn.setClockInDate(DateTimeStorage.getCurrentDate());
        clockIn.setClockInTime(DateTimeStorage.getCurrentTime());
        clockIn.setStatus(1);

        saveAttendance(clockIn);
    }

    @Override
    public void processClockOutData(Employee employee) {
        Attendance lastAttendanceRecord = findFirstByEmployeeOrderByIdDesc(employee) == null ? new Attendance() : findFirstByEmployeeOrderByIdDesc(employee);

        Attendance clockOut = new Attendance();

        boolean error = false;

        if (Objects.equals(lastAttendanceRecord.getStatus(), 1)) {
            clockOut = lastAttendanceRecord;
        } else {
            sendWarningEmail(employee, lastAttendanceRecord, "clockOut");
            error = true;
        }

        clockOut.setClockOutDate(DateTimeStorage.getCurrentDate());
        clockOut.setClockOutTime(DateTimeStorage.getCurrentTime());

        if (error) {
            clockOut.setEmployee(employee);
            clockOut.setStatus(2);
        } else {
            clockOut.setHoursAtWork(TimeCalculator.returnTimeDifference
                    (clockOut.getClockInDate().toString() + " " + clockOut.getClockInTime(),
                     clockOut.getClockOutDate().toString() + " " + clockOut.getClockOutTime()));
            clockOut.setStatus(0);
        }

        saveAttendance(clockOut);
    }

    @Override
    public void saveAttendance(Attendance attendance) {
        attendanceRepository.save(attendance);
    }

    @Transactional
    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id).orElse(null);

        if (attendance != null) {
            attendanceRepository.delete(attendance);
        }
    }

    @Override
    public Attendance findById(long id) {
        return attendanceRepository.findById(id);
    }

    @Override
    public List<Attendance> findByEmployee(Employee employee) {
        return attendanceRepository.findByEmployee(employee);
    }

    @Override
    public Attendance findFirstByEmployeeOrderByIdDesc(Employee employee) {
        return attendanceRepository.findFirstByEmployeeOrderByIdDesc(employee);
    }

    private void sendWarningEmail(Employee employee, Attendance lastAttendanceRecord, String action) {

        boolean isClockInError = action.equals("clockIn");

        String employeeFirstNameLastName = employee.getFirstName() + " " + employee.getLastName();
        String employeeOIB = employee.getOib();
        String currentDateTime = DateTimeStorage.getCurrentDateTimeAsString();
        String dateOfPreviousRecord = "";
        String company = employee.getUser().getCompany();
        String address = employee.getUser().getAddress();
        String city = employee.getUser().getCity();

        if (lastAttendanceRecord.getId() == null) {
            dateOfPreviousRecord = "";
        } else {
            dateOfPreviousRecord = isClockInError ?
                    " " + DateTimeStorage.DATE_FORMAT.format(lastAttendanceRecord.getClockInDate())  + " " + lastAttendanceRecord.getClockInTime() :
                    " " + DateTimeStorage.DATE_FORMAT.format(lastAttendanceRecord.getClockOutDate()) + " " + lastAttendanceRecord.getClockOutTime();
        }

        String emailSubject = isClockInError ? EmailTemplates.CLOCK_IN_ERROR_EMAIL_SUBJECT : EmailTemplates.CLOCK_OUT_ERROR_EMAIL_SUBJECT;

        String emailBody = isClockInError ? String.format(EmailTemplates.CLOCK_IN_ERROR_EMAIL_BODY, employeeFirstNameLastName,
                                            employeeOIB, currentDateTime, dateOfPreviousRecord, company, address, city) :
                                            String.format(EmailTemplates.CLOCK_OUT_ERROR_EMAIL_BODY, employeeFirstNameLastName,
                                            employeeOIB, currentDateTime, dateOfPreviousRecord, company, address, city);

        SendMail.sendMail(employee.getUser().getEmailAdmin(), emailSubject, emailBody, "");
    }
}
