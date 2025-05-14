package hr.betaSoft.controller;

import hr.betaSoft.model.Employee;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // Generisanje trenutnog datuma i vremena
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        boolean error = false;
        String entry_message = "";

        try {
            // Writing raw request data to a file
            Path path = Paths.get("tmp/log");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            try (FileWriter writer = new FileWriter("tmp/log/post_request.log", true)) {
                writer.write("Raw Request Data:\n");
                writer.write("Date and Time: " + formattedDateTime + "\n");
                writer.write("PIN: " + pin + "\n");
                writer.write("Machine ID: " + machineId + "\n");
                writer.write("Action: " + action + "\n");
                writer.write("Latitude: " + lat + "\n");
                writer.write("Longitude: " + lon + "\n");
                writer.write("--------\n");
            }

            BigDecimal latitude = (lat != null && !lat.isEmpty()) ? new BigDecimal(lat) : BigDecimal.ZERO;
            BigDecimal longitude = (lon != null && !lon.isEmpty()) ? new BigDecimal(lon) : BigDecimal.ZERO;


            Employee employee = employeeService.findByPinAndMachineId(pin, machineId);

            if (employee != null) {
                if (action.equals("clockIn")) {
                    attendanceService.processClockInData(employee, latitude, longitude);
                    entry_message = employee.getFirstName() + ",<br><br>Vaš dolazak<br>je evidentiran.";
                } else if (action.equals("clockOut")) {
                    attendanceService.processClockOutData(employee, latitude, longitude);
                    entry_message = employee.getFirstName() + ",<br><br>Vaš odlazak<br>je evidentiran.";
                }
            } else {
                error = true;
                entry_message = "Unijeli ste nepostojeći PIN!";
            }

            // Generisanje trenutnog datuma i vremena za zapisivanje odgovora
            now = LocalDateTime.now();
            formattedDateTime = now.format(formatter);

            // Writing the response to a file
            try (FileWriter writer = new FileWriter("tmp/log/post_request.log", true)) {
                writer.write("Date and Time: " + formattedDateTime + "\n");
                writer.write("Response: " + entry_message + "\n");
                writer.write("--------\n");
            }


        } catch (Exception e) {
            // Catch any other general exceptions
            entry_message = "Unexpected error occurred: " + e.getMessage();
            error = true;
        }

        // Prepare the response message based on whether there was an error
        String message = error ? entry_message : entry_message;
        Map<String, String> response = Map.of("message", message);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }

}