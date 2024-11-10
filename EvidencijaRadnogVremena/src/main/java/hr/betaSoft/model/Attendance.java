package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendance")
public class Attendance {

    private static final String[] ATTRIBUTE_VALUES_FOR_CHECKING = new String[]{"Datum dolaska", "Vrijeme dolaska", "Datum odlaska", "Vrijeme odlaska"};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date clockInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date clockOutDate;

    @Column
    private String clockInTime;

    @Column
    private String clockOutTime;

    @Column
    private String hoursAtWork;

    @Column
    private Integer status;

    @Column
    private boolean clockInDataUserUpdate;

    @Column
    private boolean clockOutDataUserUpdate;

    private String clockInDay;

    public List<String> checkForEmptyAttendanceAttributes() {

        List<String> emptyAttributes = new ArrayList<>();

        Stream.of(ATTRIBUTE_VALUES_FOR_CHECKING)
                .filter(attributeName -> {
                    Object value = getValueByNameForAttendance(attributeName);
                    if (value instanceof String) {
                        return ((String) value).isEmpty();
                    } else if (value instanceof List<?>) {
                        return ((List<?>) value).isEmpty();
                    }
                    return value == null;
                })
                .forEach(emptyAttributes::add);

        if (emptyAttributes.size() != ATTRIBUTE_VALUES_FOR_CHECKING.length) {
            emptyAttributes.clear();
            if (clockInDate != null && clockInTime.isEmpty()) {
                emptyAttributes.add("Vrijeme dolaska");
            }
            if (clockInDate == null && !clockInTime.isEmpty()) {
                emptyAttributes.add("Datum dolaska");
            }
            if (clockOutDate != null && clockOutTime.isEmpty()) {
                emptyAttributes.add("Vrijeme odlaska");
            }
            if (clockOutDate == null && !clockOutTime.isEmpty()) {
                emptyAttributes.add("Datum odlaska");
            }
        }

        return emptyAttributes;
    }

    private Object getValueByNameForAttendance(String attributeName) {
        switch (attributeName) {
            case "Datum dolaska":
                return clockInDate;
            case "Vrijeme dolaska":
                return clockInTime;
            case "Datum odlaska":
                return clockOutDate;
            case "Vrijeme odlaska":
                return clockOutTime;
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
        }
    }

    public static String defineErrorMessageForEmptyAttendanceAttributes(List<String> emptyAttributes) {

        return emptyAttributes.size() == ATTRIBUTE_VALUES_FOR_CHECKING.length ? "Ne možete spremiti praznu formu!" :
                                       "Popunite sva obavezna polja: " + String.join(", ", emptyAttributes);
    }
}