package hr.betaSoft.controller;

import hr.betaSoft.security.service.UserService;
import hr.betaSoft.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PinController {

    private UserService userService;

    private EmployeeService employeeService;

    public PinController(UserService userService, EmployeeService employeeService) {
        this.userService = userService;
        this.employeeService = employeeService;
    }

    @GetMapping("/pin")
    public String enterPinTest() {
        return "enter-pin";
    }
}