package hr.betaSoft.controller;

import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.Employee;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.tools.DateTimeStorage;
import hr.betaSoft.tools.TimeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pin/record")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final EmployeeService employeeService;

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(EmployeeService employeeService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleRecordRequest(@RequestBody Map<String, String> requestData) {
        String pin = requestData.get("pin");
        String machineId = requestData.get("machineId");
        String action = requestData.get("action");

        Employee employee = employeeService.findByPinAndMachineId(pin, machineId);

        boolean error = false;

        if (employee != null) {
            if (action.equals("clockIn")) {
                attendanceService.processClockInData(employee);
            } else if (action.equals("clockOut")){
                attendanceService.processClockOutData(employee);
            }
        } else {
            error = true;
        }

        String message = error ? "Unešeni PIN nije valjan!" : "Podaci uspješno primljeni!";
        Map<String, String> response = Map.of("message", message);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }
}

