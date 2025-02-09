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
import hr.betaSoft.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final UserService userService;

    public EmployeeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping("/show")
    public String showEmployees(Model model, HttpServletRequest request) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Popis radnika");
        attributes.put("addLink", "/employees/new");
        attributes.put("addBtnText", "Unesite radnika");
        attributes.put("updateLink", "/employees/update/{id}");
        attributes.put("deleteLink", "/employees/delete/{id}");
        model.addAttribute("pdfLink", "");
        attributes.put("absencePdfLink", "");
        model.addAttribute("showLink", "");

        defineShowData(model, request, attributes);

        return "table";
    }

    @GetMapping("/show-attendance")
    public String showEmployeeAttendance(Model model, HttpServletRequest request) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Popis radnika - radno vrijeme");
        attributes.put("updateLink", "/attendance/show/{id}");
        attributes.put("deleteLink", "");
        attributes.put("attendancePdf", "dummy");
        attributes.put("absencePdfLink", "");

        attributes.put("addLink", "/fund-hours-html");
        attributes.put("addBtnText", "Mjesečni fond sati");
        attributes.put("addIcon", "fas fa-calculator me-2");
        model.addAttribute("printAttendanceDummy", "dummy");

        defineShowData(model, request, attributes);

        return "table";
    }

    @GetMapping("/show-absence-record")
    public String showEmployeeAbsenceRecord(Model model, HttpServletRequest request) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("title", "Popis radnika - nenazočnost na poslu");
        attributes.put("updateLink", "/absence-record/show/{id}");
        attributes.put("deleteLink", "");
        attributes.put("pdfLink", "");
        attributes.put("absencePdfLink", "dummy");
        model.addAttribute("showLink", "");

        defineShowData(model, request, attributes);

        return "table";
    }

    private void defineShowData(Model model, HttpServletRequest request, Map<String, Object> attributes) {

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("Prezime", "lastName", "id", ""));
            columnList.add(new Column("Ime", "firstName", "id", ""));
            columnList.add(new Column("PIN", "pin", "id", ""));
        } else {
            columnList.add(new Column("Prezime", "lastName", "id", ""));
            columnList.add(new Column("Ime", "firstName", "id", ""));
            columnList.add(new Column("OIB", "oib", "id", ""));
            columnList.add(new Column("Radno mjesto", "employmentPosition", "id", ""));
            columnList.add(new Column("Tjedni sati", "weeklyWorkingHours", "id", ""));
            columnList.add(new Column("PIN", "pin", "id", ""));
        }

        User authenticatedUser = userService.getAuthenticatedUser();
        List<Employee> employeeList = employeeService.totalWorkHoursCalcForEmployees(employeeService.findByUser(authenticatedUser));

        model.addAllAttributes(attributes);
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", employeeList);
        model.addAttribute("path", "/employees");
        model.addAttribute("sendLink", "");
        model.addAttribute("tableName", "employees");
        model.addAttribute("script", "/js/table-employees.js");
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        Employee employee = (Employee) model.getAttribute("employee");

        if (employee != null) {
            model.addAttribute("class", employee);
        } else {
            model.addAttribute("class", new Employee());
        }

        List<Data> dataList = defineDataList();
        List<String> hiddenList = Data.getColumnFieldsNotInDataList(Employee.class, dataList);

        model.addAttribute("dataList", dataList);
        model.addAttribute("hiddenList", hiddenList);
        model.addAttribute("title", "Radnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/employees/save");
        model.addAttribute("path", "/employees/show");
        model.addAttribute("sendLink", "");
        model.addAttribute("script", "/js/form-employees.js");

        return "form";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {

            Employee employee = employeeService.findById(id);

            List<Data> dataList = defineDataList();
            List<String> hiddenList = Data.getColumnFieldsNotInDataList(Employee.class, dataList);

            model.addAttribute("class", employee);
            model.addAttribute("dataList", dataList);
            model.addAttribute("hiddenList", hiddenList);
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

    @PostMapping("/save")
    public String addEmployee(@ModelAttribute("employee") Employee employee, BindingResult result, RedirectAttributes ra) {

        User tempUser = userService.getAuthenticatedUser();

        Employee pinExists = employeeService.findByPinAndUser(employee.getPin(), tempUser);

        boolean error = false;

        int errorNum = 0;

        String errorMessage = "";

        if (!OibHandler.checkOib(employee.getOib())) {
            errorNum = 1;
        } else if (pinExists != null && !Objects.equals(employee.getId(), pinExists.getId())) {
            errorNum = 2;
        } else if (employee.getPin().length() != 4) {
            errorNum = 3;
        }

        if (errorNum != 0) {
            error = true;
        }

        errorMessage = switch (errorNum) {
            case 1 -> "Neispravan unos OIB-a.";
            case 2 -> "Već postoji radnik s tim PIN-om";
            case 3 -> "PIN mora sadržavati 4 broja";
            default -> "";
        };

        if (error) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", errorMessage);
            return redirect(employee);
        }

        List<String> emptyAttributes = employee.checkForEmptyEmployeeAttributes();

        if (!emptyAttributes.isEmpty()) {
            ra.addFlashAttribute("employee", employee);
            ra.addFlashAttribute("message", Employee.defineErrorMessageForEmptyEmployeeAttributes(emptyAttributes));
            return redirect(employee);
        }

        if (!Objects.equals(employee.getSignOutDate(), null)) {
            if (employee.getSignUpDate().after(employee.getSignOutDate())) {
                ra.addFlashAttribute("employee", employee);
                ra.addFlashAttribute("message", "Datum prestanka rada radnika u tvrtci ne može biti prije datuma početka rada radnika u tvrtci!");
                return redirect(employee);
            }
        }

        employee = employeeService.setEmployeeDailyWorkHours(employee);

        employee.setUser(tempUser);
        employeeService.saveEmployee(employee);
        return "redirect:/employees/show";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes ra) {

        try {
            employeeService.deleteEmployee(id);
        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/employees/show";
    }

    @GetMapping("/user/update/{id}")
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

    @PostMapping("/user/save")
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

    private String redirect(Employee employee) {

        if (employee.getId() != null) {
            return "redirect:/employees/update/" + employee.getId();
        }
        return "redirect:/employees/new";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.", "OIB", "oib", "", "", "", "number-input", "true", "", items, "false"));
        ;
        dataList.add(new Data("2.", "Ime", "firstName", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("3.", "Prezime", "lastName", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("4.", "Spol", "gender", "", "", "", "text", "true", "", Employee.GENDER, "false"));
        ;
        dataList.add(new Data("5.", "Datum rođenja", "dateOfBirth", "", "", "", "date-input", "true", "", items, "false"));
        ;
        dataList.add(new Data("6.", "Adresa", "address", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("7.", "Poštanski broj i grad", "city", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("8.", "Radno mjesto", "employmentPosition", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("9.", "Mjesto rada - Grad", "cityOfEmployment", "", "", "", "text", "true", "", items, "false"));
        ;
        dataList.add(new Data("10.", "Neradni dan(i) u tjednu", "nonWorkingDays", "", "", "", "text", "true", "", DateUtils.WEEKDAYS, "true"));
        ;
        dataList.add(new Data("11.", "Raspored prekovremenih sati", "overtimeSchedule", "", "", "", "text", "false", "", Employee.OVERTIME_SCHEDULE, "false"));
        ;
        dataList.add(new Data("12.", "Sati rada za ponedjeljak", "mondayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("13.", "Sati rada za utorak", "tuesdayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("14.", "Sati rada za srijedu", "wednesdayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("15.", "Sati rada za četvrtak", "thursdayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("16.", "Sati rada za petak", "fridayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("17.", "Sati rada za subotu", "saturdayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("18.", "Sati rada za nedjelju", "sundayWorkHours", "", "", "", "number-input", "false", "", items, "false"));
        ;
        dataList.add(new Data("19.", "Vrijeme početka noćnog rada", "nightWorkStart", "", "", "", "text", "true", "true", items, "false"));
        ;
        dataList.add(new Data("20.", "Vrijeme završetka noćnog rada", "nightWorkEnd", "", "", "", "text", "true", "true", items, "false"));
        ;
        dataList.add(new Data("21.", "Datum početka rada u tvrtci", "signUpDate", "", "", "", "date-pick", "false","true", items, "false"));
        ;
        dataList.add(new Data("22.", "Datum prestanka rada u tvrtci", "signOutDate", "", "", "", "date-pick", "false","true", items, "false"));
        ;
        dataList.add(new Data("23.", "PIN", "pin", "", "", "", "number-input", "true", "", items, "false"));
        ;

        return dataList;
    }
}
