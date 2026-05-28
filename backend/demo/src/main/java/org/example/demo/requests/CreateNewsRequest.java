package org.example.demo.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@NoArgsConstructor
public class CreateNewsRequest {
    @NotNull(message = "Naslov je obavezan")
    @NotEmpty(message = "Naslov ne sme biti prazan")
    private String title;

    @NotNull(message = "Sadrzaj je obavezan")
    @NotEmpty(message = "Sadrzaj ne sme biti prazan")
    private String content;

    @NotNull(message = "Kategorija je obavezna")
    private Integer categoryId;

    private List<String> tagNames;
}
