package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import hr.betaSoft.security.model.User;
import hr.betaSoft.utils.DateUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    public static final List<String> GENDER = Arrays.asList("Muško", "Žensko");

    public static final List<String> OVERTIME_SCHEDULE = Arrays.asList("Tjedno", "Dnevno");

    private static final String[] ATTRIBUTE_VALUES_FOR_CHECKING = new String[]{"Datum početka rada u tvrtci", "Datum prestanka rada u tvrtci"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String gender;

    @Column
    private String oib;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String employmentPosition;

    @Column
    private String cityOfEmployment;

    @Column
    private List<String> nonWorkingDays;

    @Column
    private String overtimeSchedule;

    @Column
    private Integer mondayWorkHours;

    @Column
    private Integer tuesdayWorkHours;

    @Column
    private Integer wednesdayWorkHours;

    @Column
    private Integer thursdayWorkHours;

    @Column
    private Integer fridayWorkHours;

    @Column
    private Integer saturdayWorkHours;

    @Column
    private Integer sundayWorkHours;

    private Integer weeklyWorkingHours;

    @Column
    private String nightWorkStart;

    @Column
    private String nightWorkEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date signUpDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date signOutDate;

    @Column(nullable = false)
    private String pin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public List<String> checkForEmptyEmployeeAttributes() {

        List<String> emptyAttributes = new ArrayList<>();

        Stream.of(ATTRIBUTE_VALUES_FOR_CHECKING)
                .filter(attributeName -> {
                    Object value = getValueByNameForEmployee(attributeName);
                    if (value instanceof String) {
                        return ((String) value).isEmpty();
                    } else if (value instanceof List<?>) {
                        return ((List<?>) value).isEmpty();
                    }
                    return value == null;
                })
                .forEach(emptyAttributes::add);

        if (!emptyAttributes.isEmpty()) {
            emptyAttributes.clear();
            if (signUpDate == null) {
                emptyAttributes.add("Datum početka rada u tvrtci");
            }
        }

        return emptyAttributes;
    }

    private Object getValueByNameForEmployee(String attributeName) {
        switch (attributeName) {
            case "Datum početka rada u tvrtci":
                return signUpDate;
            case "Datum prestanka rada u tvrtci":
                return signOutDate;
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
        }
    }

    public static String defineErrorMessageForEmptyEmployeeAttributes(List<String> emptyAttributes) {

        return !emptyAttributes.isEmpty() ? "Obavezno definirati datum početka rada radnika u tvrtci" : null;
    }

    public static Integer getWorkHoursForGivenDayInt(Employee employee, String day) {

        int intWorkHoursForGivenDay = 0;

        if (Objects.equals(day, DateUtils.WEEKDAYS.get(0))) {
            intWorkHoursForGivenDay = employee.getMondayWorkHours() == null ? 0 : employee.getMondayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(1))) {
            intWorkHoursForGivenDay = employee.getTuesdayWorkHours() == null ? 0 : employee.getTuesdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(2))) {
            intWorkHoursForGivenDay = employee.getWednesdayWorkHours() == null ? 0 : employee.getWednesdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(3))) {
            intWorkHoursForGivenDay = employee.getThursdayWorkHours() == null ? 0 : employee.getThursdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(4))) {
            intWorkHoursForGivenDay = employee.getFridayWorkHours() == null ? 0 : employee.getFridayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(5))) {
            intWorkHoursForGivenDay = employee.getSaturdayWorkHours() == null ? 0 : employee.getSaturdayWorkHours();
        } else if (Objects.equals(day, DateUtils.WEEKDAYS.get(6))) {
            intWorkHoursForGivenDay = employee.getSundayWorkHours() == null ? 0 : employee.getSundayWorkHours();
        }

        return intWorkHoursForGivenDay;
    }

    public static String  getWorkHoursForGivenDayStr(Employee employee, String day) {

        return String.format("%02d:00", getWorkHoursForGivenDayInt(employee, day));
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}