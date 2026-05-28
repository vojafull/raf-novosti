package org.example.demo.requests;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull(message = "Ime je obavezno")
    @NotEmpty(message = "Ime ne sme biti prazno")
    private String firstName;

    @NotNull(message = "Prezime je obavezno")
    @NotEmpty(message = "Prezime ne sme biti prazno")
    private String lastName;

    @NotNull(message = "Email je obavezan")
    @NotEmpty(message = "Email ne sme biti prazan")
    @Email(message = "Email nije u ispravnom formatu")
    private String email;

    @NotNull(message = "Tip korisnika je obavezan")
    @NotEmpty(message = "Tip korisnika ne sme biti prazan")
    private String type;

    @NotNull(message = "Lozinka je obavezna")
    @NotEmpty(message = "Lozinka ne sme biti prazna")
    private String password;

    @NotNull(message = "Potvrda lozinke je obavezna")
    @NotEmpty(message = "Potvrda lozinke ne sme biti prazna")
    private String confirmPassword;

}
