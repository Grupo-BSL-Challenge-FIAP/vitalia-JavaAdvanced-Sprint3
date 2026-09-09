package br.com.clyvo.vitalia.dto.request;

import br.com.clyvo.vitalia.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank(message = "O nome completo é obrigatório")
        String fullName,

        @NotBlank(message = "O e-mail é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {}
