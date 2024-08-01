package hr.betaSoft.controller;

import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Employee;
import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DeviceDetector;
import hr.betaSoft.tools.OibHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    private final UserService userService;

    public EmployeeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping("/employees/show")
    public String showEmployees(Model model, HttpServletRequest request) {

        DeviceDetector deviceDetector = new DeviceDetector();
        boolean isMobile = deviceDetector.isMobileDevice(request);

        List<Column> columnList = new ArrayList<>();

        if (isMobile) {
            columnList.add(new Column("Ime", "firstName", "id", ""));
            columnList.add(new Column("Prezime", "lastName", "id", ""));
            columnList.add(new Column("OIB", "oib", "id", ""));

        } else {
            columnList.add(new Column("Ime", "firstName", "id", ""));
            columnList.add(new Column("Prezime", "lastName", "id", ""));
            columnList.add(new Column("OIB", "oib", "id", ""));
            columnList.add(new Column("Radno mjesto", "employmentPosition", "id", ""));
            columnList.add(new Column("PIN", "pin", "id", ""));
        }

        User authenticatedUser = userService.getAuthenticatedUser();
        List<Employee> employeeList = employeeService.findByUser(authenticatedUser);

        model.addAttribute("title", "Popis radnika");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", employeeList);
        model.addAttribute("addLink", "/employees/new");
        model.addAttribute("addBtnText", "Unesi radnika");
        model.addAttribute("path", "/employees");
        model.addAttribute("sendLink", "");
        model.addAttribute("pdfLink", "");
        model.addAttribute("updateLink", "/employees/update/{id}");
        model.addAttribute("deleteLink", "/employees/delete/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-employees.js");

        return "table";
    }

    @GetMapping("/employees/new")
    public String showAddForm(Model model) {

        Employee employee = (Employee) model.getAttribute("employee");

        if (employee != null) {
            model.addAttribute("class", employee);
        } else {
            model.addAttribute("class", new Employee());
        }

        List<Data> dataList = defineDataList();

        model.addAttribute("dataList", dataList);
        model.addAttribute("title", "Radnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/employees/save");
        model.addAttribute("path", "/employees/show");
        model.addAttribute("sendLink", "");
        model.addAttribute("script", "/js/form-employees.js");

        return "form";
    }

    @GetMapping("employees/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {

            Employee employee = employeeService.findById(id);

            List<String> dummyHidden = new ArrayList<>();

            model.addAttribute("class", employee);
            model.addAttribute("dataList", defineDataList());
            model.addAttribute("hiddenList", dummyHidden);
            model.addAttribute("title", "Radnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/employees/save");
            model.addAttribute("path", "/employees/show");
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/form-employees.js");

            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        }
    }

    @PostMapping("/employees/save")
    public String addEmployee(@ModelAttribute("employee") Employee employee, RedirectAttributes ra) {

        User tempUser = userService.getAuthenticatedUser();

        Employee pinExists = employeeService.findByPinAndUser(employee.getPin(), tempUser);

        boolean error = false;

        int errorNum = 0;

        String errorMessage = "";

        if (!OibHandler.checkOib(employee.getOib())) {
            errorNum = 1;
        } else if (pinExists != null && !Objects.equals(employee.getId(), pinExists.getId())) {
            errorNum = 2;
        }

        if (errorNum != 0) {
            error = true;
        }

        errorMessage = switch (errorNum) {
            case 1 -> "Neispravan unos OIB-a.";
            case 2 -> "Već postoji radnik s tim PIN-om";
            default -> "";
        };

        if (error) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", errorMessage);

            if (employee.getId() != null) {
                return "redirect:/employees/update/" + employee.getId();
            }
            return "redirect:/employees/new";
        }

        employee.setUser(tempUser);
        employeeService.saveEmployee(employee);
        return "redirect:/employees/show";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {

        try {
            employeeService.deleteEmployee(id);
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees/show";
    }

    @GetMapping("employees/user/update/{id}")
    public String showUpdateUser(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            UserDto userDto = userService.convertEntityToDto(userService.findById(id));

            model.addAttribute("class", userDto);
            model.addAttribute("dataList", UserController.defineDataList(true, true));
            model.addAttribute("title", "Postavke");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/employees/user/save");
            model.addAttribute("path", "/employees");
            model.addAttribute("sendLink", "");
            model.addAttribute("script", "/js/form-users.js");
            return "form";
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees";
        }
    }

    @PostMapping("/employees/user/save")
    public String updateUser(@ModelAttribute("userDto") UserDto userDto, BindingResult result, RedirectAttributes ra) {

        if (result.hasErrors()) {
            ra.addFlashAttribute("userDto", userDto);
            ra.addFlashAttribute("message", "Greška prilikom spremanja promjene podataka. Kontaktirajte podršku.");

            if (userDto.getId() != null) {
                return "redirect:/employees/user/update/" + userDto.getId();
            }
            return "redirect:/employees";
        }

        if (userDto.getId() != null) {
            User tempUser = userService.findById(userDto.getId());
            userDto.setUsername(tempUser.getUsername());
            userDto.setPassword(tempUser.getPassword());
            userDto.setCompany(tempUser.getCompany());
            userDto.setOib(tempUser.getOib());
            userDto.setDateOfUserAccountExpiry(tempUser.getDateOfUserAccountExpiry());
            userDto.setMachineID(tempUser.getMachineID());
        }

        userService.saveUser(userDto);
        return "redirect:/employees";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.", "OIB *", "oib", "", "", "", "number-input", "true", "", items, "false"));
        ;
        dataList.add(new Data("2.", "Ime *", "firstName", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("3.", "Prezime *", "lastName", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("4.", "Spol *", "gender", "", "", "", "text", "true", "", Employee.GENDER, "false"));
        ;
        dataList.add(new Data("5.", "Datum rođenja *", "dateOfBirth", "", "", "", "date-input", "true", "", items, "false"));
        ;
        dataList.add(new Data("6.", "Adresa *", "address", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("7.", "Poštanski broj i grad *", "city", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("8.", "Radno mjesto *", "employmentPosition", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("9.", "Mjesto rada - Grad *", "cityOfEmployment", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("10.", "PIN *", "pin", "", "", "", "number-input", "true", "", items, "false"));
        ;

        return dataList;
    }
}
