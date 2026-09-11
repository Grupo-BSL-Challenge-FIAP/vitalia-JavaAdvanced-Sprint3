package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.request.ClinicalHistoryRequest;
import br.com.clyvo.vitalia.dto.response.ClinicalHistoryResponse;
import br.com.clyvo.vitalia.service.ClinicalHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clinical-histories")
@RequiredArgsConstructor
public class ClinicalHistoryController {

    private final ClinicalHistoryService service;

    @PostMapping
    public ResponseEntity<ClinicalHistoryResponse> create(@RequestBody @Valid ClinicalHistoryRequest request) {
        ClinicalHistoryResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    public ResponseEntity<Page<ClinicalHistoryResponse>> getAll(Pageable pageable) {
        Page<ClinicalHistoryResponse> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicalHistoryResponse> getById(@PathVariable Long id) {
        ClinicalHistoryResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<ClinicalHistoryResponse>> getByPet(@PathVariable Long petId) {
        List<ClinicalHistoryResponse> response = service.findByPetId(petId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicalHistoryResponse> update(@PathVariable Long id, @RequestBody @Valid ClinicalHistoryRequest request) {
        ClinicalHistoryResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}