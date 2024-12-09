package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/pin/record")
@CrossOrigin(origins = "*")
public class PinValidationController {

    private final EmployeeService employeeService;

    private final AttendanceService attendanceService;

    @Autowired
    public PinValidationController(EmployeeService employeeService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleRecordRequest(@RequestBody Map<String, String> requestData) {
        String pin = requestData.get("pin");
        String machineId = requestData.get("machineId");
        String action = requestData.get("action");
        String lat = requestData.get("latitude");
        String lon = requestData.get("longitude");
        BigDecimal latitude = new BigDecimal(lat);
        BigDecimal longitude = new BigDecimal(lon);



        Employee employee = employeeService.findByPinAndMachineId(pin, machineId);

        boolean error = false;
        String entry_message = "";

        if (employee != null) {
            if (action.equals("clockIn")) {
                attendanceService.processClockInData(employee, latitude, longitude);
                entry_message = employee.getFirstName() + ",<br><br>Vaš dolazak<br>je evidentiran.";
            } else if (action.equals("clockOut")){
                attendanceService.processClockOutData(employee, latitude, longitude);
                entry_message = employee.getFirstName() + ",<br><br>Vaš odlazak<br>je evidentiran.";
            }

        } else {
            error = true;
        }

        String message = error ? "Unijeli ste nepostojeći PIN!" : entry_message;
        Map<String, String> response = Map.of("message", message);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }
}