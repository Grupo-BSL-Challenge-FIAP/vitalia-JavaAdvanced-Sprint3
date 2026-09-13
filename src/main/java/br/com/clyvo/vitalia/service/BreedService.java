package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.response.BreedResponse;
import br.com.clyvo.vitalia.repository.BreedRepository;
import br.com.clyvo.vitalia.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;
    private final SpeciesRepository speciesRepository;

    public List<BreedResponse> findBySpeciesId(Long speciesId) {

        if (!speciesRepository.existsById(speciesId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Espécie não encontrada"
            );
        }

        return breedRepository
                .findBySpeciesIdOrderByNameAsc(speciesId)
                .stream()
                .map(breed ->
                        new BreedResponse(
                                breed.getId(),
                                breed.getName(),
                                breed.getSpeciesId()
                        )
                )
                .toList();
    }
}