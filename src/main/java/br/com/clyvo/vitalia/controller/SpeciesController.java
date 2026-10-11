package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.response.BreedResponse;
import br.com.clyvo.vitalia.dto.response.SpeciesResponse;
import br.com.clyvo.vitalia.service.BreedService;
import br.com.clyvo.vitalia.service.DatabaseProcedureService;
import br.com.clyvo.vitalia.service.SpeciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/species")
@RequiredArgsConstructor
@Tag(
        name = "Species",
        description = "Espécies e raças disponíveis"
)
public class SpeciesController {

    private final SpeciesService speciesService;
    private final BreedService breedService;
    private final DatabaseProcedureService databaseProcedureService;

    @GetMapping
    @Operation(
            summary = "Lista todas as espécies"
    )
    public ResponseEntity<List<SpeciesResponse>>
    findAll() {

        return ResponseEntity.ok(
                speciesService.findAll()
        );
    }

    @GetMapping("/{speciesId}/breeds")
    @Operation(
            summary = "Lista as raças de uma espécie"
    )
    public ResponseEntity<List<BreedResponse>>
    findBreedsBySpecies(
            @PathVariable Long speciesId
    ) {

        return ResponseEntity.ok(
                breedService.findBySpeciesId(
                        speciesId
                )
        );
    }

    @PostMapping("/procedure")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cadastra uma espécie utilizando package PL/SQL"
    )
    public ResponseEntity<Void> insertSpeciesByProcedure(
            @RequestParam String name
    ) {

        databaseProcedureService.insertSpecies(name);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}