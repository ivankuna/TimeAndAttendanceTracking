package hr.betaSoft.controller;

import hr.betaSoft.exception.AttendanceNotFoundException;
import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DateTimeStorage;
import hr.betaSoft.tools.DeviceDetector;
import hr.betaSoft.utils.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    private final EmployeeService employeeService;

    private final UserService userService;

    private static final String SESSION_EMPLOYEE_ID = "employeeId";

    @Autowired
    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService, UserService userService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping("show/{id}")
    public String showAttendanceOfEmployee(@PathVariable Long id, HttpSession session, Model model, HttpServletRequest request) {

        session.setAttribute(SESSION_EMPLOYEE_ID, id.intValue());

        Employee employee = employeeService.findById(id);

        String title = employee.getFirstName() + " " + employee.getLastName() + " (" + employee.getOib() + ")";

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("Datum dolaska", "clockInDate", "id",""));
            columnList.add(new Column("Dan dolaska", "clockInDay", "id",""));
            columnList.add(new Column("Vrijeme dolaska", "clockInTime", "id",""));
            columnList.add(new Column("Datum odlaska", "clockOutDate", "id",""));
            columnList.add(new Column("Vrijeme odlaska", "clockOutTime", "id",""));
            columnList.add(new Column("Vrijeme na poslu", "hoursAtWork", "id",""));
            columnList.add(new Column("Status", "status", "id",""));
        } else {
            columnList.add(new Column("Datum dolaska", "clockInDate", "id",""));
            columnList.add(new Column("Dan dolaska", "clockInDay", "id",""));
            columnList.add(new Column("Vrijeme dolaska", "clockInTime", "id",""));
            columnList.add(new Column("DOLAZAK", "clockInDataUserUpdate", "id",""));
            columnList.add(new Column("Datum odlaska", "clockOutDate", "id",""));
            columnList.add(new Column("Vrijeme odlaska", "clockOutTime", "id",""));
            columnList.add(new Column("ODLAZAK", "clockOutDataUserUpdate", "id",""));
            columnList.add(new Column("Vrijeme na poslu", "hoursAtWork", "id",""));
            columnList.add(new Column("Status", "status", "id",""));
        }

        List<Attendance> attendanceList = attendanceService.setClockInDaysBasedOnDBData(attendanceService.findByEmployee(employee));

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", attendanceList);
        model.addAttribute("addBtnText", "Novi zapis");
        model.addAttribute("addLink", "/attendance/new");
        model.addAttribute("path", "/employees/show-attendance");
        model.addAttribute("updateLink", "/attendance/update/{id}");
        model.addAttribute("deleteLink", "/attendance/delete/{id}");
        model.addAttribute("locationLink", "/attendance/address-in/{id}");
        model.addAttribute("tableName", "attendence");
        model.addAttribute("script", "/js/table-users.js");

        return "table";
    }

    @GetMapping("/show-current-attendance")
    public String showCurrentAttendance(Model model, HttpServletRequest request) {

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("Radnik", "employee", "id",""));
            columnList.add(new Column("Datum dolaska", "clockInDate", "id",""));
            columnList.add(new Column("Vrijeme dolaska", "clockInTime", "id",""));
        } else {
            columnList.add(new Column("Radnik", "employee", "id",""));
            columnList.add(new Column("Datum dolaska", "clockInDate", "id",""));
            columnList.add(new Column("Vrijeme dolaska", "clockInTime", "id",""));
        }

        List<Employee> employeeList = employeeService.findByUser(userService.getAuthenticatedUser());
        List<Attendance> attendanceList = new ArrayList<>();
        Attendance tempAttendance;

        for (Employee employee : employeeList) {
            tempAttendance = attendanceService.findTopByEmployeeOrderByClockInDateDescClockInTimeDesc(employee);
            if (tempAttendance != null) {
                if (Objects.equals(tempAttendance.getStatus(), 1)) {
                    attendanceList.add(tempAttendance);
                }
            }
        }

        model.addAttribute("title", "Prisutnost na poslu");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", attendanceList);
        model.addAttribute("addLink", "");
        model.addAttribute("path", "/employees");
        model.addAttribute("updateLink", "");
        model.addAttribute("deleteLink", "");
        model.addAttribute("locationLink", "/attendance/address-in/{id}");
        model.addAttribute("tableName", "attendence");
        model.addAttribute("script", "/js/table-users.js");

        return "table";
    }

    @GetMapping("/new")
    public String showAddForm(HttpSession session, Model model) {

        Integer employeeId = (Integer) session.getAttribute(SESSION_EMPLOYEE_ID);

        Employee employee = employeeService.findById(employeeId.longValue());

        Attendance attendance = (Attendance) model.getAttribute("attendance");

        if (attendance != null) {
            model.addAttribute("class", attendance);
        } else {
            model.addAttribute("class", new Attendance());
        }

        List<Data> dataList = defineDataList();
        List<String> hiddenList = Data.getColumnFieldsNotInDataList(Attendance.class, dataList);

        model.addAttribute("dataList", dataList);
        model.addAttribute("hiddenList", hiddenList);
        model.addAttribute("title", "Dolazak/odlazak");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/attendance/save/" + employeeId);
        model.addAttribute("path", "/attendance/show/" + employee.getId());
        model.addAttribute("sendLink", "");
        model.addAttribute("script", "/js/form-users.js");

        return "form";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes ra) {

        try {
            Integer employeeId = (Integer) session.getAttribute(SESSION_EMPLOYEE_ID);

            Employee employee = employeeService.findById(employeeId.longValue());

            Attendance attendance = attendanceService.findById(id);

            List<Data> dataList = defineDataList();
            List<String> hiddenList = Data.getColumnFieldsNotInDataList(Attendance.class, dataList);

            model.addAttribute("class", attendance);
            model.addAttribute("dataList", dataList);
            model.addAttribute("hiddenList", hiddenList);
            model.addAttribute("title", "Dolazak/odlazak");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/attendance/save/" + employeeId);
            model.addAttribute("path", "/attendance/show/" + employee.getId());
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/form-users.js");

            return "form";
        } catch (AttendanceNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/attendance/show/" + id;
        }
    }


    @GetMapping("/address-in/{id}")
    public String showAddressClockInForm(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes ra) {

        try {

            Attendance attendance = attendanceService.findById(id);

            model.addAttribute("latitude", attendance.getClockInLatitude());
            model.addAttribute("longitude", attendance.getClockInLongitude());
            return "pin-address";


        } catch (AttendanceNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/attendance/show/" + id;
        }
    }
    @GetMapping("/address-out/{id}")
    public String showAddressClockOutForm(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes ra) {

        try {

            Attendance attendance = attendanceService.findById(id);

            model.addAttribute("latitude", attendance.getClockOutLatitude());
            model.addAttribute("longitude", attendance.getClockOutLongitude());
            return "pin-address";


        } catch (AttendanceNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/attendance/show/" + id;
        }
    }

    @PostMapping("/save/{id}")
    public String addAttendance(@PathVariable("id") Long id, @ModelAttribute("attendance") Attendance attendance, BindingResult result, RedirectAttributes ra) {

        Employee employee = employeeService.findById(id);

        if (result.hasErrors() && !result.hasFieldErrors("clockInDate") && !result.hasFieldErrors("clockOutDate")) {
            ra.addFlashAttribute("attendance", attendance);
            return redirect(attendance);
        }

        List<String> emptyAttributes = attendance.checkForEmptyAttendanceAttributes();

        if (!emptyAttributes.isEmpty()) {
            ra.addFlashAttribute("attendance", attendance);
            ra.addFlashAttribute("message", Attendance.defineErrorMessageForEmptyAttendanceAttributes(emptyAttributes));
            return redirect(attendance);
        }

        if (!DateUtils.isTimeFormatValid(attendance.getClockInTime()) || !DateUtils.isTimeFormatValid(attendance.getClockOutTime())) {
            ra.addFlashAttribute("attendance", attendance);
            ra.addFlashAttribute("message", "Neispravan unos vremena dolaska i/ili odlaska!");
            return redirect(attendance);
        }

        if (!Objects.equals(attendance.getClockInDate(), null) && !Objects.equals(attendance.getClockOutDate(), null)) {
            if (attendance.getClockInDate().after(attendance.getClockOutDate())) {
                ra.addFlashAttribute("attendance", attendance);
                ra.addFlashAttribute("message", "Datum odlaska ne može biti prije datuma dolaska!");
                return redirect(attendance);
            }
        }

        if (attendance.getClockInDate() != null && attendance.getClockOutDate() != null) {
            List<Attendance> attendanceList = attendanceService.findByEmployee(employee);
            for (Attendance att : attendanceList) {
                if (att.getClockInDate() != null && att.getClockOutDate() != null) {
                    if (attendanceService.dateAndTimeOverlap(attendance, att) && !Objects.equals(attendance.getId(), att.getId())) {
                        ra.addFlashAttribute("attendance", attendance);
                        ra.addFlashAttribute("message",
                                "Nije moguće upisati dolazak/odlazak jer već postoji zapis: " + DateTimeStorage.DATE_FORMAT.format(att.getClockInDate()) + " " +
                                att.getClockInTime() + " - " + DateTimeStorage.DATE_FORMAT.format(att.getClockOutDate()) + " " + att.getClockOutTime());
                        return redirect(attendance);
                    }
                }
            }
        }

        attendanceService.processAttendanceDataFromController(attendance, employee);

        return "redirect:/attendance/show/" + employee.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteAttendance(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {

        Integer employeeId = (Integer) session.getAttribute(SESSION_EMPLOYEE_ID);

        Employee employee = employeeService.findById(employeeId.longValue());

        try {
            attendanceService.deleteAttendance(id);
        } catch (AttendanceNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/attendance/show/" + employee.getId();
    }

    private String redirect(Attendance attendance) {

        if (attendance.getId() != null) {
            return "redirect:/attendance/update/" + attendance.getId();
        }
        return "redirect:/attendance/new";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.", "Datum dolaska", "clockInDate", "", "", "", "date-pick", "false","true", items, "false"));
        ;
        dataList.add(new Data("2.", "Vrijeme dolaska (HH:mm)", "clockInTime", "", "", "", "text", "false", "true", items, "false"));
        ;
        dataList.add(new Data("3.", "Datum odlaska", "clockOutDate", "", "", "", "date-pick", "false", "true", items, "false"));
        ;
        dataList.add(new Data("4.", "Vrijeme odlaska (HH:mm)", "clockOutTime", "", "", "", "text", "false", "true", items, "false"));
        ;

        return dataList;
    }
}