package hr.betaSoft.repository;

import hr.betaSoft.model.ArrivalDeparture;
import hr.betaSoft.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalDepartureRepository  extends JpaRepository<ArrivalDeparture, Long> {

    ArrivalDeparture findById(long id);

    ArrivalDeparture findByEmployee(Employee employee);
}
