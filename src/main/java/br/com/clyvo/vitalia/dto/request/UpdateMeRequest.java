package br.com.clyvo.vitalia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateMeRequest(

        @NotBlank
        @Size(min = 2, max = 150)
        String fullName,

        @NotBlank
        String phoneNumber,

        @NotBlank
        @Pattern(regexp = "\\d{11}")
        String cpf,

        @NotNull
        LocalDate dateOfBirth,

        @NotBlank
        @Size(max = 255)
        String address

) {
}