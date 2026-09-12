package br.com.clyvo.vitalia.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MobileRegisterRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String fullName,

        @NotBlank
        String phoneNumber,

        String cpf,

        LocalDate dateOfBirth,

        String address
) {}