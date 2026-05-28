package org.example.demo.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
public class ReactionRequest {
    @NotNull(message = "Reakcija je obavezna")
    @NotEmpty(message = "Reakcija ne sme biti prazna")
    private String reaction;
}
