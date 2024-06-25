package hr.betaSoft.service;

import hr.betaSoft.model.ArrivalDeparture;
import hr.betaSoft.model.Employee;
import hr.betaSoft.repository.ArrivalDepartureRepository;
import org.springframework.stereotype.Service;

@Service
public class ArrivalDepartureServiceImpl implements ArrivalDepartureService {

    private ArrivalDepartureRepository arrivalDepartureRepository;

    public ArrivalDepartureServiceImpl(ArrivalDepartureRepository arrivalDepartureRepository) {
        this.arrivalDepartureRepository = arrivalDepartureRepository;
    }

    @Override
    public ArrivalDeparture findById(long id) {
        return arrivalDepartureRepository.findById(id);
    }

    @Override
    public ArrivalDeparture findByEmployee(Employee employee) {
        return arrivalDepartureRepository.findByEmployee(employee);
    }
}
