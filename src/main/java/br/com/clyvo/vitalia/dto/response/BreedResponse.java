package br.com.clyvo.vitalia.dto.response;

public record BreedResponse(
        Long id,
        String name,
        Long speciesId
) {
}