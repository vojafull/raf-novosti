package org.example.demo.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotNull(message = "Naziv kategorije je obavezan")
    @NotEmpty(message = "Naziv kategorije ne sme biti prazan")
    private String name;

    @NotNull(message = "Opis kategorije je obavezan")
    @NotEmpty(message = "Opis kategorije ne sme biti prazan")
    private String description;
}
