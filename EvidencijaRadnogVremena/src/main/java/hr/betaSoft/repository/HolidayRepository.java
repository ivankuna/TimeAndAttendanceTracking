package hr.betaSoft.repository;

import hr.betaSoft.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Holiday findById(long id);

    Holiday findByDateOfHoliday(Date date);
}