package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "absence_record")
public class AbsenceRecord {

    private static final String[] ATTRIBUTE_VALUES_FOR_CHECKING = new String[]{"Od datuma", "Do datuma"};

    public static final List<String> TYPES_OF_ABSENCE = Arrays.asList(
            "Godišnji odmor",
            "Bolovanje",
            "Plaćeni dopust",
            "Neplaćeni dopust",
            "Opravdani izostanak",
            "Neopravdani izostanak"
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column
    private String typeOfAbsence;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column
    private String note;

    @Column
    private Integer totalHoursOfAbsence;

    public List<String> checkForEmptyAbsenceRecordAttributes() {

        List<String> emptyAttributes = new ArrayList<>();

        Stream.of(ATTRIBUTE_VALUES_FOR_CHECKING)
                .filter(attributeName -> {
                    Object value = getValueByNameForAbsenceRecord(attributeName);
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
            if (startDate == null) {
                emptyAttributes.add("Od datuma");
            }
            if (endDate == null) {
                emptyAttributes.add("Do datuma");
            }
        }

        return emptyAttributes;
    }

    private Object getValueByNameForAbsenceRecord(String attributeName) {
        switch (attributeName) {
            case "Od datuma":
                return startDate;
            case "Do datuma":
                return endDate;
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
        }
    }

    public static String defineErrorMessageForEmptyAbsenceRecordAttributes(List<String> emptyAttributes) {

        return emptyAttributes.size() == ATTRIBUTE_VALUES_FOR_CHECKING.length ? "Obavezno popuniti polja za datume početka i kraja nenazočnosti!" :
                "Popunite sva obavezna polja: " + String.join(", ", emptyAttributes);
    }
}