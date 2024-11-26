package hr.betaSoft.security.service;

import hr.betaSoft.security.model.User;
import hr.betaSoft.security.userdto.UserDto;

import java.util.List;

public interface UserService {

    void saveUser(UserDto userDto);

    UserDto convertEntityToDto(User user);

    void deleteUser(Long id);

    List<User> findAll();

    User findById(long id);

    User findByUsername(String username);

    User findByOib(String oib);

    long countUsers();

    User getAuthenticatedUser();

    boolean checkIfEmployeeUnderUserExist(long userId);

    User findByMachineID(String machineID);

    // For ERV APP
    String getAuthenticatedUserDetailsForHtml();
}