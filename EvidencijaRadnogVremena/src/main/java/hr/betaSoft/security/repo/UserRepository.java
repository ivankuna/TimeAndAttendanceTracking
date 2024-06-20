package hr.betaSoft.security.repo;

import hr.betaSoft.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findById(long id);

    User findByOib(String oib);

    User findByMachineID(String machineID);
}