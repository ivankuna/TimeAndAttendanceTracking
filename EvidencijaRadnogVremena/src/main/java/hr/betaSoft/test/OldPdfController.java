package hr.betaSoft.test;

import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OldPdfController {

    private final AttendanceService attendanceService;

    private final EmployeeService employeeService;

    @Autowired
    public OldPdfController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping("/old-generate-pdf/{id}")
//    public void generatePdf(@PathVariable Long id, HttpServletResponse response, Model model, RedirectAttributes ra) throws Exception {
    public String generatePdf(@PathVariable Long id, HttpServletResponse response, Model model, RedirectAttributes ra) throws Exception {
        System.out.println(id);
        // Prepare data for Thymeleaf
        List<AttendanceData> attendanceDataList = getSampleAttendanceData();
        model.addAttribute("attendanceDataList", attendanceDataList);
        model.addAttribute("title", "Šihterica");

//        // Render Thymeleaf template to HTML string
//        String htmlContent = renderThymeleafTemplate(model);
//
//        // Set up the font provider
//        DefaultFontProvider fontProvider = new DefaultFontProvider(false, false, false);
//        fontProvider.addFont("src/main/resources/font/Calibri-Regular.ttf"); // Add your font path here
//
//        ConverterProperties converterProperties = new ConverterProperties();
//        converterProperties.setFontProvider(fontProvider);
//
//        // Convert HTML to PDF with the proper font
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        HtmlConverter.convertToPdf(htmlContent, baos, converterProperties);
//
//        // Save the PDF file to the directory
//        String pdfFilePath = savePdfToFileSystem(baos);
//
//        // Set response headers to display the PDF in the browser
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "inline; filename=sihterica.pdf");
//
//        // Write PDF to response
//        OutputStream os = response.getOutputStream();
//        baos.writeTo(os);
//        os.flush();
//
//        // Add a flash attribute to confirm file saving (optional)
//        ra.addFlashAttribute("message", "PDF successfully saved at: " + pdfFilePath);

        return "attendance_template";
    }

    private List<AttendanceData> getSampleAttendanceData() {

        // Sample data
        List<AttendanceData> attendanceDataList = new ArrayList<>();
        attendanceDataList.add(new AttendanceData(1, "Ponedjeljak", "08:00", "16:00", "08:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00", "00:00"));

        return attendanceDataList;
    }

    // The method that renders Thymeleaf template
    private String renderThymeleafTemplate(Model model) {
        Context context = new Context();
        model.asMap().forEach(context::setVariable); // Pass the model variables into the Thymeleaf context

        // Process the template and return HTML as a string
        return templateEngine.process("attendance_template", context);
    }

    // Method to save the generated PDF to a file
    private String savePdfToFileSystem(ByteArrayOutputStream baos) throws Exception {
        String pdfDirectory = "pdf";  // Directory where the PDF will be saved
        File pdfDir = new File(pdfDirectory);

        // Create directory if it doesn't exist
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }

        // Define the PDF file path
        String pdfFileName = "sihterica.pdf";
        String pdfFilePath = pdfDirectory + File.separator + pdfFileName;

        // Save the PDF to the file system
        try (FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
            baos.writeTo(fos);
        }

        return pdfFilePath;  // Return the path of the saved file
    }
}
