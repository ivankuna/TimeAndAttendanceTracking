package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import hr.betaSoft.security.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    public static final List<String> GENDER = Arrays.asList("Muško", "Žensko");

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

    @Column(nullable = false)
    private String pin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", oib='" + oib + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", employmentPosition='" + employmentPosition + '\'' +
                ", cityOfEmployment='" + cityOfEmployment + '\'' +
                ", nonWorkingDays=" + nonWorkingDays +
                ", weeklyWorkingHours=" + weeklyWorkingHours +
                ", pin='" + pin + '\'' +
                ", user=" + user +
                '}';
    }
}