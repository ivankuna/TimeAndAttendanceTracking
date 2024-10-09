package hr.betaSoft.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController {

    @GetMapping("/test")
    public String showTestPage() {
        return "test"; // Points to test.html
    }

    @PostMapping("/test/example")
    public String handleFormSubmission(
            @RequestParam("employeeId") Long employeeId, // Add this line
            @RequestParam("month") String month,
            @RequestParam("year") String year,
            Model model) {

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("month", month);
        model.addAttribute("year", year);

        return "result"; // Redirect to the result page
    }
}
