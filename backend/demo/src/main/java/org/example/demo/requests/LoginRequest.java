package org.example.demo.requests;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Email je obavezan")
    @NotEmpty(message = "Email ne sme biti prazan")
    @Email(message = "Email nije u ispravnom formatu")
    private String email;

    @NotNull(message = "Lozinka je obavezna")
    @NotEmpty(message = "Lozinka ne sme biti prazna")
    private String password;
}
