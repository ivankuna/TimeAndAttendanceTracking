package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfHoliday;

    @Column
    private String nameOfHoliday;

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", dateOfHoliday=" + dateOfHoliday +
                ", nameOfHoliday='" + nameOfHoliday + '\'' +
                '}';
    }
}