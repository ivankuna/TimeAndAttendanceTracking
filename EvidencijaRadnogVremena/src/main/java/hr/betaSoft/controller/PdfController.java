package hr.betaSoft.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.Attendance;
import hr.betaSoft.model.AttendanceData;
import hr.betaSoft.model.Employee;
import hr.betaSoft.model.Holiday;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.service.HolidayService;
import hr.betaSoft.utils.AttendanceDataHandler;
import hr.betaSoft.utils.DateUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.io.IOUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class PdfController {

    private static final String SESSION_EMPLOYEE_ID = "employeeId";

    private static final String SESSION_YEAR = "year";

    private static final String SESSION_MONTH = "month";

    private final EmployeeService employeeService;

    private final AttendanceService attendanceService;

    private final HolidayService holidayService;

    public PdfController(EmployeeService employeeService, AttendanceService attendanceService, HolidayService holidayService) {
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
        this.holidayService = holidayService;
    }

    private void setSessionAttributes(HttpSession session, Long employeeId, String year, String month) {
        session.setAttribute(SESSION_EMPLOYEE_ID, employeeId);
        session.setAttribute(SESSION_YEAR, year);
        session.setAttribute(SESSION_MONTH, month);
    }

    @PostMapping("/pdf")
    public void showPdfControllerMethod(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("month") String month,
            @RequestParam("year") String year,
            HttpSession session,
            Model model, RedirectAttributes ra,
            HttpServletResponse response) throws IOException {

        if (checkGlobalVariables(employeeId, year, month)) {
            ra.addFlashAttribute("message", "Invalid input values");
            response.sendRedirect("/employees/show-attendance");
            return;
        }

        setSessionAttributes(session, employeeId, year, month);
        showPdf(session, model, ra, response);
    }

    @PostMapping("/html")
    public String showHtmlControllerMethod(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("month") String month,
            @RequestParam("year") String year,
            HttpSession session,
            RedirectAttributes ra,
            Model model) {

        if (checkGlobalVariables(employeeId, year, month)) {
            return "redirect:/employees/show-attendance";
        }

        setSessionAttributes(session, employeeId, year, month);
        List<AttendanceData> attendanceDataList = getSampleAttendanceData(session, employeeId);

        if (attendanceDataList.isEmpty()) {
            ra.addFlashAttribute("message", "Nepotpuni podaci za radnika/cu " +
                    employeeService.findById(employeeId).getFirstName() + " " + employeeService.findById(employeeId).getLastName() +
                    " za " + month + ". mjesec " + year + ". godine!");
            return "redirect:/employees/show-attendance";
        }

        model.addAttribute("pageTitle", "Šihterica");
        model.addAttribute("title", "Šihterica");
        model.addAttribute("dataList", attendanceDataList);

        return "attendance-template";
    }

    private void showPdf(HttpSession session, Model model, RedirectAttributes ra, HttpServletResponse response) {

        String message = "";

        try {

            File pdfDir = new File("pdf");

            if (!pdfDir.exists()) {
                boolean dirCreated = pdfDir.mkdir();
                if (!dirCreated) {
                    message = "ERROR: PdfController.java -> showPdf()";
                }
            }

            String pdfFilePath = createPdf(session, model, ra, response);

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

    private String createPdf(HttpSession session, Model model, RedirectAttributes ra, HttpServletResponse response) {

        try {
            Long employeeId = (Long) session.getAttribute(SESSION_EMPLOYEE_ID);
            List<AttendanceData> attendanceDataList = getSampleAttendanceData(session, employeeId);

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

    @Autowired
    private TemplateEngine templateEngine;

    private String renderHtml(Model model) {
        Context context = new Context();
        context.setVariables(model.asMap());
        return templateEngine.process("attendance-template", context);
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

    private List<AttendanceData> getSampleAttendanceData(HttpSession session, Long id) {

        Employee employee = employeeService.totalWorkHoursCalcForEmployee(employeeService.findById(id));
        String year = (String) session.getAttribute(SESSION_YEAR);
        String month = (String) session.getAttribute(SESSION_MONTH);

        Date firstDateOfMonth = DateUtils.getFirstDateOfMonth(year, month);
        Date lastDateOfMonth = DateUtils.getLastDateOfMonth(year, month);
        Date firstDateOfPreviousMonth = DateUtils.getFirstDateOfMonth(year, DateUtils.getMonthBefore(month));
        Date lastDateOfPreviousMonth = DateUtils.getLastDateOfMonth(year, DateUtils.getMonthBefore(month));

        List<Attendance> attendanceList = new ArrayList<>();

        attendanceList.addAll(attendanceService.findByEmployeeAndClockInDateBeforeAndClockOutDateAfterOrderByClockInDateAscClockInTimeAsc(
                employee, firstDateOfMonth, lastDateOfPreviousMonth));

        attendanceList.addAll(attendanceService.findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(
                employee, firstDateOfMonth, lastDateOfMonth));

        List<Attendance> attendanceListForOvertimeCalc = attendanceService.findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(
                employee, firstDateOfPreviousMonth, lastDateOfPreviousMonth);

        List<Holiday> holidayList = holidayService.findAll();

        return AttendanceDataHandler.getFormattedAttendanceData(employee, attendanceList, attendanceListForOvertimeCalc, holidayList, year, month);
    }

    private boolean checkGlobalVariables(Long employeeId, String year, String month) {
        return employeeId == null || (Objects.equals(year, "") || year.isEmpty()) || (Objects.equals(month, "") || month.isEmpty());
    }
}