package hr.betaSoft.service;

import hr.betaSoft.model.Holiday;
import hr.betaSoft.repository.HolidayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService{

    private HolidayRepository holidayRepository;

    public HolidayServiceImpl(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    @Override
    public void saveHoliday(Holiday holiday) {
        holidayRepository.save(holiday);
    }

    @Transactional
    @Override
    public void deleteHoliday(Long id) {
        Holiday holiday = holidayRepository.findById(id).orElse(null);

        if (holiday != null) {
            holidayRepository.delete(holiday);
        }
    }

    @Override
    public List<Holiday> findAll() {
        return holidayRepository.findAll();
    }

    @Override
    public Holiday findById(long id) {
        return holidayRepository.findById(id);
    }
}
