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
@Table(name = "arrivalDeparture")
public class ArrivalDeparture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private boolean arrival;

    @Column(nullable=false)
    private boolean departure;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfInput;

    @Column(nullable=false)
    private String timeOfInput;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
