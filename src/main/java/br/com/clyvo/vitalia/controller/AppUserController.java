package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.request.AppUserRequest;
import br.com.clyvo.vitalia.dto.response.AppUserResponse;
import br.com.clyvo.vitalia.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService service;
    
    @GetMapping
    public ResponseEntity<Page<AppUserResponse>> findAll(Pageable pageable) {
        Page<AppUserResponse> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<AppUserResponse> findById(@PathVariable Long id) {
        AppUserResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<AppUserResponse> update(@PathVariable Long id, @RequestBody @Valid AppUserRequest request) {
        AppUserResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}