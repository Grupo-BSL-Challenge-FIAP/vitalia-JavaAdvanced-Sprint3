package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.response.SpeciesResponse;
import br.com.clyvo.vitalia.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeciesService {

    private final SpeciesRepository speciesRepository;

    public List<SpeciesResponse> findAll() {
        return speciesRepository
                .findAll(Sort.by("name").ascending())
                .stream()
                .map(species ->
                        new SpeciesResponse(
                                species.getId(),
                                species.getName()
                        )
                )
                .toList();
    }
}