package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.request.PetRequest;
import br.com.clyvo.vitalia.dto.response.PetResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Gerenciamento dos pets cadastrados no sistema")
public class PetController {

    private final PetService service;

    @PostMapping
    @Operation(summary = "Cadastra um novo pet vinculado ao tutor autenticado")
    public ResponseEntity<PetResponse> create(
            @RequestBody @Valid PetRequest request,
            @AuthenticationPrincipal AppUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, user));
    }

    @GetMapping
    @Operation(summary = "Lista todos os pets com paginação e ordenação")
    public ResponseEntity<Page<PetResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pet pelo ID")
    public ResponseEntity<PetResponse> findById(@PathVariable Long id, @AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(service.findById(id, user));
    }

    @GetMapping("/search/name")
    @Operation(summary = "Busca pets por nome (parcial, sem distinção de maiúsculas)")
    public ResponseEntity<Page<PetResponse>> findByName(
            @RequestParam String name, Pageable pageable) {
        return ResponseEntity.ok(service.findByName(name, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de um pet")
    public ResponseEntity<PetResponse> update(
            @PathVariable Long id, @RequestBody @Valid PetRequest request, @AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(service.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um pet do sistema")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal AppUser user) {
        service.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-pets")
    @Operation(summary = "Lista todos os pets do tutor autenticado")
    public ResponseEntity<Page<PetResponse>> findMyPets(
            @AuthenticationPrincipal AppUser account,
            Pageable pageable) {
        return ResponseEntity.ok(service.findMyPets(account.getId(), pageable));
    }
}