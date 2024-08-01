package hr.betaSoft.test;

import hr.betaSoft.model.Employee;
import hr.betaSoft.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    private EmployeeService employeeService;

    public TestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/test123")
    public String test(Model model) {

        Employee employee = employeeService.findByPinAndMachineId("123", "123");

        if (employee != null) {
            model.addAttribute("employee", employee);
        } else {
            model.addAttribute("employee", new Employee());
        }

        return "test";
    }
}
