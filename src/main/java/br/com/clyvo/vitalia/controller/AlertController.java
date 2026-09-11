package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.request.AlertRequest;
import br.com.clyvo.vitalia.dto.response.AlertResponse;
import br.com.clyvo.vitalia.service.AlertService;
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
@RequestMapping("alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;

    @PostMapping
    public ResponseEntity<AlertResponse> create(@RequestBody @Valid AlertRequest request) {
        AlertResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    public ResponseEntity<Page<AlertResponse>> getAll(Pageable pageable) {
        Page<AlertResponse> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getById(@PathVariable Long id) {
        AlertResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AlertResponse>> getByPet(@PathVariable Long petId) {
        List<AlertResponse> response = service.findByPet(petId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertResponse> update(@PathVariable Long id, @RequestBody @Valid AlertRequest request) {
        AlertResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}