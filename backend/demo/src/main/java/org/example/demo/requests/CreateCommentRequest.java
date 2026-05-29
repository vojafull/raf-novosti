package org.example.demo.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
public class CreateCommentRequest {
    @NotNull(message = "Ime autora je obavezno")
    @NotEmpty(message = "Ime autora ne sme biti prazno")
    private String authorName;

    @NotNull(message = "Tekst komentara je obavezan")
    @NotEmpty(message = "Tekst komentara ne sme biti prazan")
    private String content;
}
