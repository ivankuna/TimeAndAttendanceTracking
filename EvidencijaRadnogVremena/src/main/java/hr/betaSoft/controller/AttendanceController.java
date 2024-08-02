package hr.betaSoft.controller;

import hr.betaSoft.exception.AttendanceNotFoundException;
import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DeviceDetector;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    private final EmployeeService employeeService;

    private Employee currentEmployee;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    @GetMapping("show/{id}")
    public String showAttendanceOfEmployee(@PathVariable Long id, Model model, HttpServletRequest request) {

        this.currentEmployee = employeeService.findById(id);

        String title = currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + " (" + currentEmployee.getOib() + ")";

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("Datum dolaska", "clockInDate", "id",""));
            columnList.add(new Column("Vrijeme dolaska", "clockInTime", "id",""));
            columnList.add(new Column("Datum odlaska", "clockOutDate", "id",""));
            columnList.add(new Column("Vrijeme odlaska", "clockOutTime", "id",""));
            columnList.add(new Column("Vrijeme na poslu", "hoursAtWork", "id",""));
            columnList.add(new Column("Status", "status", "id",""));
        } else {
            columnList.add(new Column("Datum dolaska", "clockInDate", "id",""));
            columnList.add(new Column("Vrijeme dolaska", "clockInTime", "id",""));
            columnList.add(new Column("Datum odlaska", "clockOutDate", "id",""));
            columnList.add(new Column("Vrijeme odlaska", "clockOutTime", "id",""));
            columnList.add(new Column("Vrijeme na poslu", "hoursAtWork", "id",""));
            columnList.add(new Column("Status", "status", "id",""));
        }

        List<Attendance> attendanceList = attendanceService.findByEmployee(currentEmployee);

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", attendanceList);
        model.addAttribute("addBtnText", "Novi zapis");
        model.addAttribute("addLink", "/attendance/new");
        model.addAttribute("path", "/employees/show-attendance");
        model.addAttribute("updateLink", "/attendance/update/{id}");
        model.addAttribute("deleteLink", "/attendance/delete/{id}");
        model.addAttribute("tableName", "attendence");
        model.addAttribute("script", "/js/table-users.js");

        return "table";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        Attendance attendance = (Attendance) model.getAttribute("attendance");

        if (attendance != null) {
            model.addAttribute("class", attendance);
        } else {
            model.addAttribute("class", new Attendance());
        }

        List<Data> dataList = defineDataList();

        model.addAttribute("dataList", dataList);
        model.addAttribute("title", "Dolazak/odlazak");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/attendance/save");
        model.addAttribute("path", "/attendance/show/" + currentEmployee.getId());
        model.addAttribute("sendLink", "");
        model.addAttribute("script", "/js/form-employees.js");

        return "form";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            Attendance attendance = attendanceService.findById(id);

            List<String> dummyHidden = new ArrayList<>();

            model.addAttribute("class", attendance);
            model.addAttribute("dataList", defineDataList());
            model.addAttribute("hiddenList", dummyHidden);
            model.addAttribute("title", "Dolazak/odlazak");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/attendance/save");
            model.addAttribute("path", "/attendance/show/" + currentEmployee.getId());
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/form-users.js");

            return "form";
        } catch (AttendanceNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/employees/show";
        }
    }

    @PostMapping("/save")
    public String addAttendance(@ModelAttribute("attendance") Attendance attendance) {

        attendance.setEmployee(currentEmployee);

        attendanceService.saveAttendance(attendance);

        return "redirect:/attendance/show/" + currentEmployee.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteAttendance(@PathVariable Long id, RedirectAttributes ra) {

        try {
            attendanceService.deleteAttendance(id);
        } catch (AttendanceNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/attendance/show/" + currentEmployee.getId();
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.", "Datum dolaska *", "clockInDate", "", "", "", "date-pick", "false", "true", items, "false"));
        ;
        dataList.add(new Data("2.", "Vrijeme dolaska *", "clockInTime", "", "", "", "text", "false", "true", items, "false"));
        ;
        dataList.add(new Data("3.", "Datum odlaska *", "clockOutDate", "", "", "", "date-pick", "false", "true", items, "false"));
        ;
        dataList.add(new Data("4.", "Vrijeme dolaska *", "clockOutTime", "", "", "", "text", "false", "true", items, "false"));
        ;
        dataList.add(new Data("5.", "Vrijeme na poslu *", "hoursAtWork", "", "", "", "text", "false", "true", items, "false"));
        ;
        dataList.add(new Data("6.", "Status *", "status", "", "", "", "text", "false", "true", items, "false"));
        ;

        return dataList;
    }
}
