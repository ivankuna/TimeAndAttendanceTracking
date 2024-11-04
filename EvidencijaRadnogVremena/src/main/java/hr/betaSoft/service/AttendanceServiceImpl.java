package hr.betaSoft.service;

import hr.betaSoft.constants.EmailTemplates;
import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.AttendanceRepository;
import hr.betaSoft.tools.DateTimeStorage;
import hr.betaSoft.tools.SendMail;
import hr.betaSoft.utils.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
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
            clockOut.setHoursAtWork(DateUtils.returnTimeDifference
                    (clockOut.getClockInDate().toString(), clockOut.getClockInTime(),
                     clockOut.getClockOutDate().toString(), clockOut.getClockOutTime()));
            clockOut.setStatus(0);
        }

        saveAttendance(clockOut);
    }

    @Override
    public void processAttendanceDataFromController(Attendance attendance, Employee employee) {

        if (attendance.getClockInDate() != null && attendance.getClockOutDate() == null) {
            attendance.setHoursAtWork("");
            attendance.setStatus(1);
        }
        if (attendance.getClockInDate() == null && attendance.getClockOutDate() != null) {
            attendance.setHoursAtWork("");
            attendance.setStatus(2);
        }
        if (attendance.getClockInDate() != null && attendance.getClockOutDate() != null) {
            attendance.setHoursAtWork(DateUtils.returnTimeDifference
                    (attendance.getClockInDate().toString(), attendance.getClockInTime(),
                     attendance.getClockOutDate().toString(), attendance.getClockOutTime()));
            attendance.setStatus(0);
        }

        if (attendance.getId() != null) {
            Attendance tempAttendance = findById(attendance.getId());
            if (!Objects.equals(tempAttendance.getClockInDate(), attendance.getClockInDate()) ||
                    !Objects.equals(tempAttendance.getClockInTime() == null ? "" : tempAttendance.getClockInTime(),
                            attendance.getClockInTime() == null ?"" : attendance.getClockInTime())) {
                attendance.setClockInDataUserUpdate(true);
            }
            if (!Objects.equals(tempAttendance.getClockOutDate(), attendance.getClockOutDate()) ||
                    !Objects.equals(tempAttendance.getClockOutTime() == null ? "" : tempAttendance.getClockOutTime(),
                            attendance.getClockOutTime() == null ? "" : attendance.getClockOutTime())) {
                attendance.setClockOutDataUserUpdate(true);
            }
        } else {
            if (attendance.getClockInDate() != null) {
                attendance.setClockInDataUserUpdate(true);
            }
            if (attendance.getClockOutDate() != null) {
                attendance.setClockOutDataUserUpdate(true);
            }
        }

        attendance.setEmployee(employee);
        saveAttendance(attendance);
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

    @Transactional
    @Override
    public void deleteAllByEmployee(Employee employee) {
        attendanceRepository.deleteAllByEmployee(employee);
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

    @Override
    public List<Attendance> findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(Employee employee, Date startDate, Date endDate) {
        return attendanceRepository.findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(employee, startDate, endDate);
    }

    @Override
    public List<Attendance> findByEmployeeAndClockInDateBeforeAndClockOutDateAfterOrderByClockInDateAscClockInTimeAsc(Employee employee, Date before, Date after) {
        return attendanceRepository.findByEmployeeAndClockInDateBeforeAndClockOutDateAfterOrderByClockInDateAscClockInTimeAsc(employee, before, after);
    }

    @Override
    public Attendance setClockInDayBasedOnDBData(Attendance attendance) {

        attendance.setClockInDay(
                attendance.getClockInDate() != null ? DateUtils.getDayOfDate(attendance.getClockInDate()) : ""
        );
        return attendance;
    }

    @Override
    public List<Attendance> setClockInDaysBasedOnDBData(List<Attendance> attendanceList) {

        for (Attendance attendance : attendanceList) {
            setClockInDayBasedOnDBData(attendance);
        }
        return attendanceList;
    }

    @Override
    public boolean dateAndTimeOverlap(Attendance firstAttendance, Attendance secondAttendance) {
        LocalDateTime firstAttendanceIn = DateUtils.sqlDateAndTimeToLocalDateTime(firstAttendance.getClockInDate(), firstAttendance.getClockInTime());
        LocalDateTime firstAttendanceOut = DateUtils.sqlDateAndTimeToLocalDateTime(firstAttendance.getClockOutDate(), firstAttendance.getClockOutTime());
        LocalDateTime secondAttendanceIn = DateUtils.sqlDateAndTimeToLocalDateTime(secondAttendance.getClockInDate(), secondAttendance.getClockInTime());
        LocalDateTime secondAttendanceOut = DateUtils.sqlDateAndTimeToLocalDateTime(secondAttendance.getClockOutDate(), secondAttendance.getClockOutTime());

        return firstAttendanceIn.isAfter(secondAttendanceIn) && firstAttendanceIn.isBefore(secondAttendanceOut) ||
                firstAttendanceOut.isAfter(secondAttendanceIn) && firstAttendanceOut.isBefore(secondAttendanceOut) ||
                secondAttendanceIn.isAfter(firstAttendanceIn) && secondAttendanceIn.isBefore(firstAttendanceOut) ||
                secondAttendanceOut.isAfter(firstAttendanceIn) && secondAttendanceOut.isBefore(firstAttendanceOut);
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