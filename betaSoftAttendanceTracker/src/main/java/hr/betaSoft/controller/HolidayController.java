package hr.betaSoft.controller;

import hr.betaSoft.exception.HolidayNotFoundException;
import hr.betaSoft.model.Holiday;
import hr.betaSoft.service.HolidayService;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DeviceDetector;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/show")
    public String showHolidays(Model model, HttpServletRequest request) {

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("ID", "id", "id",""));
            columnList.add(new Column("Datum", "dateOfHoliday", "id",""));
            columnList.add(new Column("Državni praznik", "nameOfHoliday", "id",""));
        } else {
            columnList.add(new Column("ID", "id", "id",""));
            columnList.add(new Column("Datum", "dateOfHoliday", "id",""));
            columnList.add(new Column("Državni praznik", "nameOfHoliday", "id",""));
        }

        List<Holiday> holidayList = holidayService.findAll();

        model.addAttribute("title", "Popis državnih praznika");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", holidayList);
        model.addAttribute("addBtnText", "Dodaj");
        model.addAttribute("path", "/users");
        model.addAttribute("addLink", "/holidays/new");
        model.addAttribute("sendLink", "");
        model.addAttribute("pdfLink", "");
        model.addAttribute("absencePdfLink", "");
        model.addAttribute("updateLink", "/holidays/update/{id}");
        model.addAttribute("deleteLink", "/holidays/delete/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("tableName", "holidays");
        model.addAttribute("script", "/js/table-users.js");

        return "table";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        Holiday holiday = (Holiday) model.getAttribute("holiday");

        if (holiday != null) {
            model.addAttribute("class", holiday);
        } else {
            model.addAttribute("class", new Holiday());
        }

        List<Data> dataList = defineDataList();
        List<String> hiddenList = new ArrayList<>();

        model.addAttribute("dataList", dataList);
        model.addAttribute("hiddenList", hiddenList);
        model.addAttribute("title", "Državni praznik");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/holidays/save");
        model.addAttribute("path", "/holidays/show");
        model.addAttribute("sendLink", "");

        return "form";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {

            Holiday holiday = holidayService.findById(id);

            List<Data> dataList = defineDataList();
            List<String> hiddenList = new ArrayList<>();

            model.addAttribute("class", holiday);
            model.addAttribute("dataList", dataList);
            model.addAttribute("hiddenList", hiddenList);
            model.addAttribute("title", "Državni praznik");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/holidays/save");
            model.addAttribute("path", "/holidays/show");
            model.addAttribute("sendLink", "");

            return "form";
        } catch (HolidayNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/holidays/show";
        }
    }

    @PostMapping("/save")
    public String addHoliday(@ModelAttribute("holiday") Holiday holiday) {

        holidayService.saveHoliday(holiday);
        return "redirect:/holidays/show";
    }

    @GetMapping("/delete/{id}")
    public String deleteHoliday(@PathVariable Long id, RedirectAttributes ra) {

        try {
            holidayService.deleteHoliday(id);
        } catch (HolidayNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/holidays/show";
    }

    private List<Data> defineDataList() {

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.", "Datum *", "dateOfHoliday", "", "", "", "date-input", "true", "true", items, "false"));
        ;
        dataList.add(new Data("2.", "Državni praznik *", "nameOfHoliday", "", "", "", "text", "true", "true", items, "false"));
        ;

        return dataList;
    }
}