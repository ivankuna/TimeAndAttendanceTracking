package hr.betaSoft.service;

import hr.betaSoft.model.Holiday;

import java.sql.Date;
import java.util.List;

public interface HolidayService {

    void saveHoliday(Holiday holiday);

    void deleteHoliday(Long id);

    List<Holiday> findAll();

    Holiday findById(long id);

    Holiday findByDateOfHoliday(Date date);
}