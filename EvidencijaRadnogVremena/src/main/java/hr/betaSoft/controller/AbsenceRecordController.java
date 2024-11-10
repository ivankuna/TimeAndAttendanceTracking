package hr.betaSoft.controller;

import hr.betaSoft.exception.AbsenceRecordNotFoundException;
import hr.betaSoft.model.AbsenceRecord;
import hr.betaSoft.model.Employee;
import hr.betaSoft.service.AbsenceRecordService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DateTimeStorage;
import hr.betaSoft.tools.DeviceDetector;
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

@Controller
@RequestMapping("/absence-record")
public class AbsenceRecordController {

    private final AbsenceRecordService absenceRecordService;

    private final EmployeeService employeeService;

    private static final String SESSION_EMPLOYEE_ID = "employeeId";

    @Autowired
    public AbsenceRecordController(AbsenceRecordService absenceRecordService, EmployeeService employeeService) {
        this.absenceRecordService = absenceRecordService;
        this.employeeService = employeeService;
    }

    @GetMapping("show/{id}")
    public String showAbsenceRecordOfEmployee(@PathVariable Long id, HttpSession session, Model model, HttpServletRequest request) {

        session.setAttribute(SESSION_EMPLOYEE_ID, id.intValue());

        Employee employee = employeeService.findById(id);

        String title = employee.getFirstName() + " " + employee.getLastName() + " (" + employee.getOib() + ")";

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("Od datuma", "startDate", "id",""));
            columnList.add(new Column("Do datuma", "endDate", "id",""));
            columnList.add(new Column("Vrsta", "typeOfAbsence", "id",""));
            columnList.add(new Column("Napomena", "note", "id",""));
        } else {
            columnList.add(new Column("Od datuma", "startDate", "id",""));
            columnList.add(new Column("Do datuma", "endDate", "id",""));
            columnList.add(new Column("Vrsta", "typeOfAbsence", "id",""));
            columnList.add(new Column("Napomena", "note", "id",""));
        }

        List<AbsenceRecord> absenceRecordList = absenceRecordService.findByEmployee(employee);

        model.addAttribute("title", title);
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", absenceRecordList);
        model.addAttribute("addBtnText", "Novi zapis");
        model.addAttribute("addLink", "/absence-record/new");
        model.addAttribute("path", "/employees/show-absence-record");
        model.addAttribute("updateLink", "/absence-record/update/{id}");
        model.addAttribute("deleteLink", "/absence-record/delete/{id}");
        model.addAttribute("tableName", "absence_record");
        model.addAttribute("script", "/js/table-users.js");

        return "table";
    }

    @GetMapping("/new")
    public String showAddForm(HttpSession session, Model model) {

        Integer employeeId = (Integer) session.getAttribute(SESSION_EMPLOYEE_ID);

        Employee employee = employeeService.findById(employeeId.longValue());

        AbsenceRecord absenceRecord = (AbsenceRecord) model.getAttribute("absenceRecord");

        if (absenceRecord != null) {
            model.addAttribute("class", absenceRecord);
        } else {
            model.addAttribute("class", new AbsenceRecord());
        }

        List<Data> dataList = defineDataList();
        List<String> hiddenList = new ArrayList<>();

        model.addAttribute("dataList", dataList);
        model.addAttribute("hiddenList", hiddenList);
        model.addAttribute("title", "Evidencija nenazočnosti na poslu");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/absence-record/save/" + employeeId);
        model.addAttribute("path", "/absence-record/show/" + employee.getId());
        model.addAttribute("sendLink", "");
        model.addAttribute("script", "/js/form-users.js");

        return "form";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes ra) {

        try {
            Integer employeeId = (Integer) session.getAttribute(SESSION_EMPLOYEE_ID);

            Employee employee = employeeService.findById(employeeId.longValue());

            AbsenceRecord absenceRecord = absenceRecordService.findById(id);

            List<Data> dataList = defineDataList();
            List<String> hiddenList = new ArrayList<>();

            model.addAttribute("class", absenceRecord);
            model.addAttribute("dataList", dataList);
            model.addAttribute("hiddenList", hiddenList);
            model.addAttribute("title", "Evidencija nenazočnosti na poslu");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/absence-record/save/" + employeeId);
            model.addAttribute("path", "/absence-record/show/" + employee.getId());
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/form-users.js");

            return "form";
        } catch (AbsenceRecordNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/absence-record/show/" + id;
        }
    }

    @PostMapping("/save/{id}")
    public String addAbsenceRecord(@PathVariable("id") Long id, @ModelAttribute("absenceRecord") AbsenceRecord absenceRecord, BindingResult result, RedirectAttributes ra) {

        Employee employee = employeeService.findById(id);

        List<String> emptyAttributes = absenceRecord.checkForEmptyAbsenceRecordAttributes();

        if (!emptyAttributes.isEmpty()) {
            ra.addFlashAttribute("absenceRecord", absenceRecord);
            ra.addFlashAttribute("message", AbsenceRecord.defineErrorMessageForEmptyAbsenceRecordAttributes(emptyAttributes));
            return redirect(absenceRecord);
        }

        if (absenceRecord.getEndDate().before(absenceRecord.getStartDate())) {
            ra.addFlashAttribute("absenceRecord", absenceRecord);
            ra.addFlashAttribute("message", "Datum kraja nenazočnosti ne može biti prije datuma početka nenazočnosti!");
            return redirect(absenceRecord);
        }

        List<AbsenceRecord> absenceRecordList = absenceRecordService.findByEmployee(employee);

        for (AbsenceRecord ar : absenceRecordList) {
            if (absenceRecordService.dateOverlap(absenceRecord, ar)) {
                ra.addFlashAttribute("absenceRecord", absenceRecord);
                ra.addFlashAttribute("message",
                        "Nije moguće upisati nenazočnost na poslu jer već postoji zapis: " + DateTimeStorage.DATE_FORMAT.format(ar.getStartDate()) +
                        " - " + DateTimeStorage.DATE_FORMAT.format(ar.getEndDate()));
                return redirect(absenceRecord);
            }
        }

        absenceRecord.setEmployee(employee);

        absenceRecordService.saveAbsenceRecord(absenceRecord);

        return "redirect:/absence-record/show/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteAbsenceRecord(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {

        Integer employeeId = (Integer) session.getAttribute(SESSION_EMPLOYEE_ID);

        Employee employee = employeeService.findById(employeeId.longValue());

        try {
            absenceRecordService.deleteAbsenceRecord(id);
        } catch (AbsenceRecordNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/absence-record/show/" + employee.getId();
    }

    private String redirect(AbsenceRecord absenceRecord) {

        if (absenceRecord.getId() != null) {
            return "redirect:/absence-record/update/" + absenceRecord.getId();
        }
        return "redirect:/absence-record/new";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.", "Vrsta", "typeOfAbsence", "", "", "", "text", "false", "", AbsenceRecord.TYPES_OF_ABSENCE, "false"));
        ;
        dataList.add(new Data("2.", "Od datuma", "startDate", "", "", "", "date-pick", "false", "true", items, "false"));
        ;
        dataList.add(new Data("3.", "Do datuma", "endDate", "", "", "", "date-pick", "false", "true", items, "false"));
        ;
        dataList.add(new Data("4.", "Napomena", "note", "", "", "", "text", "false", "true", items, "false"));
        ;

        return dataList;
    }
}
