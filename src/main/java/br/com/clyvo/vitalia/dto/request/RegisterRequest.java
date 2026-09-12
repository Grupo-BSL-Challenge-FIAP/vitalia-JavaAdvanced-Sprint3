package br.com.clyvo.vitalia.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(

        @NotBlank(message = "O nome completo é obrigatório")
        @Size(
                min = 3,
                max = 100,
                message = "O nome deve ter entre 3 e 100 caracteres"
        )
        String fullName,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(
                min = 8,
                max = 20,
                message = "A senha deve ter entre 8 e 20 caracteres"
        )
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "A senha deve conter pelo menos uma letra e um número"
        )
        String password,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(
                regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
                message = "O telefone deve seguir o formato (11) 99999-9999"
        )
        String phoneNumber,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "O CPF deve seguir o formato 000.000.000-00"
        )
        String cpf,

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve estar no passado")
        LocalDate dateOfBirth,

        @NotBlank(message = "O endereço é obrigatório")
        @Size(
                max = 255,
                message = "O endereço deve possuir no máximo 255 caracteres"
        )
        String address

) {}