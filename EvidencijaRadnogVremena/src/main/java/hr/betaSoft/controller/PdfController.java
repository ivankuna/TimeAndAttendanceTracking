package hr.betaSoft.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;
import hr.betaSoft.model.Employee;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.utils.AttendanceDataHandler;
import hr.betaSoft.utils.DateUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.io.IOUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

@Controller
public class PdfController {

    private EmployeeService employeeService;

    private AttendanceService attendanceService;

    public PdfController(EmployeeService employeeService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    public static void convertHtmlContentToPdf(String htmlContent, String pdfFilePath) throws IOException, DocumentException {

        String modifiedHtmlContent = htmlContent.replace("true", "Da")
                .replace("false", "Ne")
                .replace("<br>", "<br/>");

        org.jsoup.nodes.Document doc = Jsoup.parse(modifiedHtmlContent);
        org.jsoup.select.Elements dateElements = doc.select("td:matches(\\d{4}-\\d{2}-\\d{2})");

        for (org.jsoup.nodes.Element element : dateElements) {
            String originalText = element.text();
            String formattedDate = formatDate(originalText);
            element.text(formattedDate);
        }

        org.jsoup.select.Elements metaElements = doc.select("meta");

        for (org.jsoup.nodes.Element element : metaElements) {
            String newMeta = "<meta http-equiv=\"content-type\" content=\"application/xhtml+xml; charset=UTF-8\"/>";
            element.text(newMeta);
        }

        Document pdfDocument = new Document(PageSize.A4);

        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));

        pdfDocument.open();

        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

        BaseFont bf = BaseFont.createFont("font/Calibri-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 11);

        try (InputStream inputStream = new ByteArrayInputStream(doc.outerHtml().getBytes(StandardCharsets.UTF_8))) {
            worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8, new FontFactoryImp() {
                @Override
                public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
                    return font;
                }
            });
        }

        pdfDocument.close();
    }

    private static String formatDate(String originalDate) {

        String[] parts = originalDate.split("-");
        if (parts.length == 3) {
            return parts[2] + "." + parts[1] + "." + parts[0] + ".";
        }
        return originalDate;
    }


    public String createPdf(Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        try {
            List<AttendanceData> attendanceDataList = getSampleAttendanceData(id);

            model.addAttribute("pageTitle", "Šihterica");
            model.addAttribute("title", "Šihterica");
            model.addAttribute("dataList", attendanceDataList);

            String htmlContent = renderHtml(model);
            String pdfFilePath = "pdf/sihterica.pdf";
            convertHtmlContentToPdf(htmlContent, pdfFilePath);

            return pdfFilePath;

        } catch (EmployeeNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "ERROR";
    }

    private List<AttendanceData> getSampleAttendanceData(Long id) {

        Employee employee = employeeService.findById(id);
        String year = "2024";
        String month = "09";
        Date firstDateOfMonth = DateUtils.getFirstDateOfMonth(year, month);
        Date lastDateOfMonth = DateUtils.getLastDateOfMonth(year, month);

        List<Attendance> attendanceList = attendanceService.findByEmployeeAndClockInDateBetween(employee, firstDateOfMonth, lastDateOfMonth);
        List<AttendanceData> attendanceDataList = AttendanceDataHandler.getFormattedAttendanceData(attendanceList);

        return attendanceDataList;
    }

    @Autowired
    private TemplateEngine templateEngine;

    private String renderHtml(Model model) {
        Context context = new Context();
        context.setVariables(model.asMap());
        return templateEngine.process("attendance-template", context);
    }

    public void showPdf(Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        String message = "";

        try {

            File pdfDir = new File("pdf");

            if (!pdfDir.exists()) {
                boolean dirCreated = pdfDir.mkdir();
                if (!dirCreated) {
                    message = "ERROR: PdfController.java -> showPdf()";
                }
            }

            String pdfFilePath = createPdf(id, model, ra, response);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + pdfFilePath);

            File pdfFile = new File(pdfFilePath);
            FileInputStream fileInputStream = new FileInputStream(pdfFile);

            IOUtils.copy(fileInputStream, response.getOutputStream());

            fileInputStream.close();
            response.getOutputStream().flush();

        } catch (Exception e) {
            ra.addFlashAttribute("message", message.isEmpty() ? e.getMessage() : message);
        }
    }

    @GetMapping("/pdf/{id}")
    public void showPdfControllerMethod(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {
        showPdf(id, model, ra, response);
    }

    @GetMapping("/html/{id}")
    public String showHtmlControllerMethod(@PathVariable("id") Long id, Model model, RedirectAttributes ra, HttpServletResponse response) {

        List<AttendanceData> attendanceDataList = getSampleAttendanceData(id);

        model.addAttribute("pageTitle", "Šihterica");
        model.addAttribute("title", "Šihterica");
        model.addAttribute("dataList", attendanceDataList);

        return "attendance-template";
    }
}
