package hr.betaSoft.service;

import hr.betaSoft.model.AbsenceRecord;
import hr.betaSoft.model.Employee;

import java.sql.Date;
import java.util.List;

public interface AbsenceRecordService {

    void saveAbsenceRecord(AbsenceRecord absenceRecord);

    void deleteAbsenceRecord(Long id);

    AbsenceRecord findById(long id);

    List<AbsenceRecord> findByEmployee(Employee employee);

    boolean dateOverlap(AbsenceRecord firstAbsenceRecord, AbsenceRecord secondAbsenceRecord);

    List<AbsenceRecord> findByEmployeeAndStartDateBetweenOrderByStartDateAsc(Employee employee, Date startDate, Date endDate);

    List<AbsenceRecord> findByEmployeeAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(Employee employee, Date before, Date after);
}
