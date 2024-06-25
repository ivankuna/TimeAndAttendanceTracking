package hr.betaSoft.service;

import hr.betaSoft.model.ArrivalDeparture;
import hr.betaSoft.model.Employee;

public interface ArrivalDepartureService {

    ArrivalDeparture findById(long id);

    ArrivalDeparture findByEmployee(Employee employee);
}
