package hr.betaSoft.security.userdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;

    @NotEmpty(message = "Korisničko ime mora biti upisano!")
    private String username;

    @NotEmpty(message = "OIB mora biti upisan!")
    private String oib;

    @NotEmpty(message = "Naziv firme mora biti upisan!")
    private String company;

    @NotEmpty(message = "Adresa mora biti upisana!")
    private String address;

    @NotEmpty(message = "Naziv grada mora biti upisan!")
    private String city;

    @NotEmpty(message = "Ime mora biti upisano!")
    private String firstName;

    @NotEmpty(message = "Prezime mora biti upisano!")
    private String lastName;

    @NotEmpty(message = "Broj telefona mora biti upisan!")
    private String telephone;

    @NotEmpty(message = "Email mora biti upisan!")
    @Email
    private String email;

    @NotEmpty(message = "Email administratora mora biti upisan!")
    @Email
    private String emailAdmin;

    @NotEmpty(message = "Lozinka mora biti upisana!")
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfUserAccountExpiry;

    private String machineID;
}
