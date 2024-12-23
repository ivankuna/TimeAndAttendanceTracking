package hr.betaSoft.service;

import hr.betaSoft.model.AbsenceRecord;
import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.AbsenceRecordRepository;
import hr.betaSoft.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class AbsenceRecordServiceImpl implements AbsenceRecordService{

    private final AbsenceRecordRepository absenceRecordRepository;

    public AbsenceRecordServiceImpl(AbsenceRecordRepository absenceRecordRepository) {
        this.absenceRecordRepository = absenceRecordRepository;
    }


    @Override
    public void saveAbsenceRecord(AbsenceRecord absenceRecord) {

        int totalHoursOfAbsence = 0;
        Date tempStartDate = absenceRecord.getStartDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempStartDate);

        while(!tempStartDate.after(absenceRecord.getEndDate())) {
            totalHoursOfAbsence = totalHoursOfAbsence + Employee.getWorkHoursForGivenDayInt(absenceRecord.getEmployee(), DateUtils.getDayOfDate(tempStartDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            tempStartDate = new Date(calendar.getTimeInMillis());
        }

        absenceRecord.setTotalHoursOfAbsence(totalHoursOfAbsence);

        absenceRecordRepository.save(absenceRecord);
    }

    @Override
    public void deleteAbsenceRecord(Long id) {
        AbsenceRecord absenceRecord = absenceRecordRepository.findById(id).orElse(null);

        if (absenceRecord != null) {
            absenceRecordRepository.delete(absenceRecord);
        }
    }

    @Override
    public AbsenceRecord findById(long id) {
        return absenceRecordRepository.findById(id);
    }

    @Override
    public List<AbsenceRecord> findByEmployee(Employee employee) {
        return absenceRecordRepository.findByEmployee(employee);
    }

    @Override
    public boolean dateOverlap(AbsenceRecord firstAbsenceRecord, AbsenceRecord secondAbsenceRecord) {
        return firstAbsenceRecord.getStartDate().after(secondAbsenceRecord.getStartDate()) && firstAbsenceRecord.getStartDate().before(secondAbsenceRecord.getEndDate()) ||
                firstAbsenceRecord.getEndDate().after(secondAbsenceRecord.getStartDate()) && firstAbsenceRecord.getEndDate().before(secondAbsenceRecord.getEndDate()) ||
                secondAbsenceRecord.getStartDate().after(firstAbsenceRecord.getStartDate()) && secondAbsenceRecord.getStartDate().before(firstAbsenceRecord.getEndDate()) ||
                secondAbsenceRecord.getEndDate().after(firstAbsenceRecord.getStartDate()) && secondAbsenceRecord.getEndDate().before(firstAbsenceRecord.getEndDate()) ||
                firstAbsenceRecord.getStartDate().toLocalDate().equals(secondAbsenceRecord.getStartDate().toLocalDate()) && firstAbsenceRecord.getEndDate().toLocalDate().equals(secondAbsenceRecord.getEndDate().toLocalDate());
    }

    @Override
    public List<AbsenceRecord> findByEmployeeAndStartDateBetweenOrderByStartDateAsc(Employee employee, Date startDate, Date endDate) {
        return absenceRecordRepository.findByEmployeeAndStartDateBetweenOrderByStartDateAsc(employee, startDate, endDate);
    }

    @Override
    public List<AbsenceRecord> findByEmployeeAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(Employee employee, Date before, Date after) {
        return absenceRecordRepository.findByEmployeeAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(employee, before, after);
    }

    @Override
    public void deleteAllByEmployee(Employee employee) {
        absenceRecordRepository.deleteAllByEmployee(employee);
    }
}