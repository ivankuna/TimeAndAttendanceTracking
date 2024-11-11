package hr.betaSoft.repository;

import hr.betaSoft.model.AbsenceRecord;
import hr.betaSoft.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface AbsenceRecordRepository extends JpaRepository<AbsenceRecord, Long> {

    AbsenceRecord findById(long id);

    List<AbsenceRecord> findByEmployee(Employee employee);

    List<AbsenceRecord> findByEmployeeAndStartDateBetween(Employee employee, Date startDate, Date endDate);
}