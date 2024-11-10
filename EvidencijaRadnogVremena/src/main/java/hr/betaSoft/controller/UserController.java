package hr.betaSoft.controller;

import hr.betaSoft.security.exception.UserNotFoundException;
import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.security.userdto.UserDto;
import hr.betaSoft.tools.Column;
import hr.betaSoft.tools.Data;
import hr.betaSoft.tools.DeviceDetector;
import hr.betaSoft.tools.OibHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/show")
    public String showUsers(Model model, HttpServletRequest request) {

        List<Column> columnList = new ArrayList<>();

        if (DeviceDetector.isMobileDevice(request)) {
            columnList.add(new Column("Tvrtka", "company", "id",""));
            columnList.add(new Column("Korisnik", "username", "id",""));
            columnList.add(new Column("Licenca", "dateOfUserAccountExpiry", "id",""));
        } else {
            columnList.add(new Column("ID", "id", "id",""));
            columnList.add(new Column("Korisničko ime", "username", "id",""));
            columnList.add(new Column("OIB", "oib", "id",""));
            columnList.add(new Column("Naziv tvrtke", "company", "id",""));
            columnList.add(new Column("Adresa", "address", "id",""));
            columnList.add(new Column("Grad", "city", "id",""));
            columnList.add(new Column("Osoba", "name", "id",""));
            columnList.add(new Column("Telefon", "telephone", "id",""));
            columnList.add(new Column("e-mail korisnika", "email", "id",""));
            columnList.add(new Column("e-mail administratora", "emailAdmin", "id",""));
            columnList.add(new Column("Datum licence", "dateOfUserAccountExpiry", "id",""));
            columnList.add(new Column("Machine ID", "machineID", "id",""));
        }

        List<User> userList = userService.findAll();

        model.addAttribute("title", "Popis korisnika");
        model.addAttribute("columnList", columnList);
        model.addAttribute("dataList", userList);
        model.addAttribute("addBtnText", "Novi korisnik");
        model.addAttribute("path", "/users");
        model.addAttribute("addLink", "/users/new");
        model.addAttribute("sendLink", "");
        model.addAttribute("pdfLink", "");
        model.addAttribute("absencePdfLink", "");
        model.addAttribute("updateLink", "/users/update/{id}");
        model.addAttribute("deleteLink", "/users/delete/{id}");
        model.addAttribute("showLink", "");
        model.addAttribute("tableName", "users");
        model.addAttribute("script", "/js/table-users.js");

        return "table";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        UserDto userDto = (UserDto) model.getAttribute("userDto");

        if (userDto != null) {
            model.addAttribute("class", userDto);
        } else {
            model.addAttribute("class", new UserDto());
        }
        List<String> dummyHidden = new ArrayList<>();

        model.addAttribute("dataList", defineDataList(false, false));
        model.addAttribute("hiddenList", dummyHidden);
        model.addAttribute("title", "Korisnik");
        model.addAttribute("dataId", "id");
        model.addAttribute("pathSave", "/users/save");
        model.addAttribute("path", "/users/show");
        model.addAttribute("sendLink", "");
        model.addAttribute("pathSaveSend", "");
        model.addAttribute("script", "/js/form-users.js");
        return "form";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            UserDto user = userService.convertEntityToDto(userService.findById(id));
            List<String> dummyHidden = new ArrayList<>();

            model.addAttribute("class", user);
            model.addAttribute("dataList", defineDataList(true, false));
            model.addAttribute("hiddenList", dummyHidden);
            model.addAttribute("title", "Korisnik");
            model.addAttribute("dataId", "id");
            model.addAttribute("pathSave", "/users/save");
            model.addAttribute("path", "/users/show");
            model.addAttribute("sendLink", "");
            model.addAttribute("pathSaveSend", "");
            model.addAttribute("script", "/js/form-users.js");
            return "form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/users/show";
        }
    }

    @PostMapping("/save")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, BindingResult result, RedirectAttributes ra) {

        User usernameExists = userService.findByUsername(userDto.getUsername());

        User machineIdExists = userService.findByMachineID(userDto.getMachineID());

        boolean error = false;

        int errorNum = 0;

        String errorMessage = "";

        if (usernameExists != null) {
            errorNum = 1;
        } else if (!OibHandler.checkOib(userDto.getOib())) {
            errorNum = 2;
        } else if (userDto.getDateOfUserAccountExpiry() == null) {
            errorNum = 3;
        } else if (userDto.getId() == null && machineIdExists != null) {
            errorNum = 4;
        }

        if (errorNum != 0) {
            error = true;
        }

        errorMessage = switch (errorNum) {
            case 1 -> "Već postoji račun registriran s tim korisničkim imenom.";
            case 2 -> "Neispravan unos OIB-a.";
            case 3 -> "Neispravan unos datuma isteka roka korisničkog računa.";
            case 4 -> "Već postoji račun registriran s tim MachineID-om.";
            default -> "";
        };

        if (error) {
            ra.addFlashAttribute("userDto", userDto);
            ra.addFlashAttribute("message", errorMessage);

            if (userDto.getId() != null) {
                return "redirect:/users/update/" + userDto.getId();
            }
            return "redirect:/users/new";
        }

        if (result.hasErrors()) {
            ra.addFlashAttribute("userDto", userDto);

            if (userDto.getId() != null) {
                return "redirect:/users/update/" + userDto.getId();
            }
            return "redirect:/users/new";
        }

        if (userDto.getId() != null) {
            User tempUser = userService.findById(userDto.getId());
            userDto.setUsername(tempUser.getUsername());
            userDto.setPassword(tempUser.getPassword());
        }

        userService.saveUser(userDto);
        return "redirect:/users/show";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {

        try {
            if (id == 1) {
                ra.addFlashAttribute("message", "Nije moguće obrisati administratora!");
            } else if (userService.checkIfEmployeeUnderUserExist(id)) {
                ra.addFlashAttribute("message", "Nije moguće obrisati korisnika s unešenim radnicima!");
            } else {
                userService.deleteUser(id);
            }
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/users/show";
    }

    public static List<Data> defineDataList(boolean update, boolean emplUpdate) {

        String fieldStatus = emplUpdate ? "false" : "true";

        List<Data> dataList = new ArrayList<>();

        List<String> items = new ArrayList<>();

        dataList.add(new Data("1.","Naziv tvrtke *", "company", "", "", "", "text", "true", fieldStatus, items,"false"));
        ;
        dataList.add(new Data("2.","OIB *", "oib", "", "", "", "text", "true", fieldStatus, items,"false"));
        ;
        dataList.add(new Data("3.","Adresa *", "address", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("4.","Poštanski broj i grad *", "city", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("5.","Ime *", "firstName", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("6.","Prezime *", "lastName", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("7.","Telefon *", "telephone", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("8.","e-mail korisnika *", "email", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("9.","e-mail administratora *", "emailAdmin", "", "", "", "text", "true", "", items,"false"));
        ;
        dataList.add(new Data("10.","Datum isteka roka korisničkog računa", "dateOfUserAccountExpiry", "", "", "", "date-pick", "true", fieldStatus, items,"false"));
        ;
        dataList.add(new Data("11.","Machine ID *", "machineID", "", "", "", "text", "true", fieldStatus, items,"false"));
        ;
        if (!update) {
            dataList.add(new Data("12.","Korisničko ime *", "username", "", "", "", "text", "true", "", items,"false"));
            ;
            dataList.add(new Data("13.","Lozinka *", "password", "", "", "", "text", "true", "", items,"false"));
            ;
        }
        return dataList;
    }

    @GetMapping("/register-machine")
    public String setMachineID(Model model) {

        model.addAttribute("title", "Prijava preglednika");
        model.addAttribute("path", "/users");

        return "machine-id";
    }
}