package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import hr.betaSoft.security.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private static final String[] ATTRIBUTE_VALUES_FOR_CHECKING = new String[]{"Datum početka rada u tvrtci", "Datum kraja rada u tvrtci"};

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
            case "Datum kraja rada u tvrtci":
                return signOutDate;
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
        }
    }

    public static String defineErrorMessageForEmptyEmployeeAttributes(List<String> emptyAttributes) {

        return !emptyAttributes.isEmpty() ? "Obavezno definirati datum početka rada radnika u tvrtci" : null;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}