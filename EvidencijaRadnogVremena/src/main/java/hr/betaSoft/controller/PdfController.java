package hr.betaSoft.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import hr.betaSoft.exception.EmployeeNotFoundException;
import hr.betaSoft.model.*;
import hr.betaSoft.security.model.User;
import hr.betaSoft.security.service.UserService;
import hr.betaSoft.service.AbsenceRecordService;
import hr.betaSoft.service.AttendanceService;
import hr.betaSoft.service.EmployeeService;
import hr.betaSoft.service.HolidayService;
import hr.betaSoft.tools.DateTimeStorage;
import hr.betaSoft.utils.AttendanceDataHandler;
import hr.betaSoft.utils.DateUtils;
import hr.betaSoft.utils.EmployeeFundHoursHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.io.IOUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final String SESSION_OVERTIME_SCHEDULE = "overtimeSchedule";

    private final UserService userService;

    private final EmployeeService employeeService;

    private final AttendanceService attendanceService;

    private final HolidayService holidayService;

    private final AbsenceRecordService absenceRecordService;

    public PdfController(UserService userService, EmployeeService employeeService, AttendanceService attendanceService, HolidayService holidayService, AbsenceRecordService absenceRecordService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
        this.holidayService = holidayService;
        this.absenceRecordService = absenceRecordService;
    }

    private void setSessionAttributes(HttpSession session, Long employeeId, String year, String month, String overtimeSchedule) {
        session.setAttribute(SESSION_EMPLOYEE_ID, employeeId);
        session.setAttribute(SESSION_YEAR, year);
        session.setAttribute(SESSION_MONTH, month);
        session.setAttribute(SESSION_OVERTIME_SCHEDULE, overtimeSchedule);
    }

    @PostMapping("/pdf")
    public void showPdfControllerMethod(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("month") String month,
            @RequestParam("year") String year,
            HttpSession session,
            Model model, RedirectAttributes ra,
            HttpServletResponse response) throws IOException {

        if (checkGlobalVariables(employeeId, year, month,  employeeService.findById(employeeId).getOvertimeSchedule())) {
            ra.addFlashAttribute("message", "Invalid input values");
            response.sendRedirect("/employees/show-attendance");
            return;
        }

        setSessionAttributes(session, employeeId, year, month, employeeService.findById(employeeId).getOvertimeSchedule());
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

        Employee employee = employeeService.findById(employeeId);

        if (checkGlobalVariables(employeeId, year, month,  employeeService.findById(employeeId).getOvertimeSchedule())) {
            return "redirect:/employees/show-attendance";
        }

        setSessionAttributes(session, employeeId, year, month, employeeService.findById(employeeId).getOvertimeSchedule());
        List<AttendanceData> attendanceDataList = getAttendanceDataForAttendanceRecord(session, employeeId);

        if (attendanceDataList.isEmpty()) {
            ra.addFlashAttribute("message", "Nepotpuni podaci za radnika/cu " +
                    employee.getFirstName() + " " + employee.getLastName() +
                    " za " + month + ". mjesec " + year + ". godine!");
            return "redirect:/employees/show-attendance";
        }

        model.addAttribute("pageTitle", "Šihterica");
        model.addAttribute("title", "Šihterica");
        model.addAttribute("employeeName", employee.getFirstName() + " " + employee.getLastName());
        model.addAttribute("year", year);
        model.addAttribute("month", DateUtils.MONTHS.get(Integer.parseInt(month) - 1));

        model.addAttribute("dataList", attendanceDataList);

        return "attendance-data-template";
    }

    @PostMapping("/attendance-html")
    public String showAttHtmlControllerMethod(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("month") String month,
            @RequestParam("year") String year,
            HttpSession session,
            RedirectAttributes ra,
            Model model) {

        if (checkGlobalVariables(employeeId, year, month,  employeeService.findById(employeeId).getOvertimeSchedule())) {
            return "redirect:/employees/show-attendance";
        }

        setSessionAttributes(session, employeeId, year, month, employeeService.findById(employeeId).getOvertimeSchedule());
        List<Attendance> attendanceList = attendanceService.setClockInDaysBasedOnDBData(getAttendanceForAttendanceRecord(session, employeeId));

        model.addAttribute("pageTitle", "Prikaz dolazaka/odlazaka");
        model.addAttribute("title", "Prikaz dolazaka/odlazaka");
        model.addAttribute("dataList", attendanceList);

        return "attendance-template";
    }

    @PostMapping("/absence-html")
    public String showAbsHtmlControllerMethod(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("year") String year,
            HttpSession session,
            RedirectAttributes ra,
            Model model) {

        Date firstDayOfYear = DateUtils.getFirstDateOfMonth(year, "01");
        Date lastDayOfYear = DateUtils.getFirstDateOfMonth(year, "12");

        List<AbsenceRecord> absenceRecordList = absenceRecordService.findByEmployeeAndStartDateBetween(employeeService.findById(employeeId), firstDayOfYear, lastDayOfYear);

        model.addAttribute("pageTitle", "Prikaz nenazočnosti na poslu");
        model.addAttribute("title", "Prikaz nenazočnosti na poslu");
        model.addAttribute("dataList", absenceRecordList);

        return "absence-record-template";
    }

    @GetMapping("/fund-hours-html")
    public String showFundHrsHtmlControllerMethod(Model model) {

        // TESTNI PODACI
        String year = "2024";
        String month = "11";

        User user = userService.getAuthenticatedUser();

        List<EmployeeFundHours> employeeFundHoursList = EmployeeFundHoursHandler.getFormattedEmployeeFundHours(
                employeeService.findByUser(user),
                holidayService.findAll(),
                year, month
        );

        StringBuilder sbHoliday = new StringBuilder();
        List<String> strHolidayList = new ArrayList<>();

        for (Holiday holiday : holidayService.findAll()) {
            if (Objects.equals(month, DateUtils.reduceDateToMonth(holiday.getDateOfHoliday()))) {
                sbHoliday
                        .append(DateTimeStorage.DATE_FORMAT.format(holiday.getDateOfHoliday())).append(" - ")
                        .append(DateUtils.getDayOfDate(holiday.getDateOfHoliday()));
                strHolidayList.add(sbHoliday.toString());
                sbHoliday.setLength(0);
            }
        }

        model.addAttribute("pageTitle", "Izračun mjesečnog fonda sati");
        model.addAttribute("title", "Izračun mjesečnog fonda sati");
        model.addAttribute("dataList", employeeFundHoursList);
        model.addAttribute("month", DateUtils.MONTHS.get(Integer.parseInt(month) - 1));
        model.addAttribute("holidayList", strHolidayList);

        return "employee-fund-hours-template";
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
            List<AttendanceData> attendanceDataList = getAttendanceDataForAttendanceRecord(session, employeeId);

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
        return templateEngine.process("attendance-data-template", context);
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

    private List<AttendanceData> getAttendanceDataForAttendanceRecord(HttpSession session, Long id) {

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

    private List<Attendance> getAttendanceForAttendanceRecord(HttpSession session, Long employeeId) {

        Employee employee = employeeService.findById(employeeId);
        String year = (String) session.getAttribute(SESSION_YEAR);
        String month = (String) session.getAttribute(SESSION_MONTH);

        Date firstDateOfMonth = DateUtils.getFirstDateOfMonth(year, month);
        Date lastDateOfMonth = DateUtils.getLastDateOfMonth(year, month);

        return attendanceService.findByEmployeeAndClockInDateBetweenOrderByClockInDateAscClockInTimeAsc(
                employee, firstDateOfMonth, lastDateOfMonth);
    }

    private boolean checkGlobalVariables(Long employeeId, String year, String month, String overtimeSchedule) {
        return employeeId == null || (Objects.equals(year, "") || year.isEmpty()) ||
                (Objects.equals(month, "") || month.isEmpty()) || (Objects.equals(overtimeSchedule, "") || overtimeSchedule.isEmpty());
    }
}