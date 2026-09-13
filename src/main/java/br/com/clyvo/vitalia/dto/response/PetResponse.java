package br.com.clyvo.vitalia.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetResponse(
        Long id,
        String name,
        String sex,
        LocalDate birthDate,
        BigDecimal weightKg,
        String status,
        Long ownerUserId,
        Long breedId,
        String breedName,
        Long speciesId,
        String speciesName
) {
}