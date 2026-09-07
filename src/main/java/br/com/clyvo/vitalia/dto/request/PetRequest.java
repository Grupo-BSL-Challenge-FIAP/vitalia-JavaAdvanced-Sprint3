package br.com.clyvo.vitalia.dto.request;

import br.com.clyvo.vitalia.enums.CurrentStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PetRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O sexo é obrigatório")
        String sex,

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate birthDate,

        @Positive(message = "O peso deve ser um valor positivo")
        BigDecimal weightKg,

        CurrentStatus status,

        Long breedId
) {}
