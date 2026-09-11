package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.AlertRequest;
import br.com.clyvo.vitalia.dto.response.AlertResponse;
import br.com.clyvo.vitalia.entity.Alert;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.repository.AlertRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository repository;
    private final PetRepository petRepository;
    private final AuthorizationService authorizationService;
    private final SimpMessagingTemplate messagingTemplate;

    public AlertResponse create(AlertRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        authorizationService.validatePetOwnership(pet);

        Alert alert = Alert.builder()
                .pet(pet)
                .alertType(request.alertType().name())
                .message(request.message())
                .severity(request.severity().name())
                .status(request.status() != null ? request.status().name() : "OPEN")
                .createdAt(LocalDateTime.now())
                .build();

        Alert savedAlert = repository.save(alert);
        AlertResponse response = toResponse(savedAlert);
        messagingTemplate.convertAndSend("/topic/alerts", response);

        return response;
    }

    public Page findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    public AlertResponse findById(Long id) {
        Alert alert = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta não encontrado"));

        if (alert.getPet() != null) {
            authorizationService.validatePetOwnership(alert.getPet());
        }

        return toResponse(alert);
    }

    public List findByPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        authorizationService.validatePetOwnership(pet);

        return repository.findByPetIdOrderByCreatedAtDesc(petId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AlertResponse update(Long id, AlertRequest request) {
        Alert alert = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta não encontrado"));

        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        authorizationService.validatePetOwnership(pet);

        alert.setPet(pet);
        alert.setAlertType(request.alertType().name());
        alert.setMessage(request.message());
        alert.setSeverity(request.severity().name());

        if (request.status() != null) {
            String statusName = request.status().name();
            if ("RESOLVED".equals(statusName) && !"RESOLVED".equals(alert.getStatus())) {
                alert.setResolvedAt(LocalDateTime.now());
            }
            alert.setStatus(statusName);
        }

        return toResponse(repository.save(alert));
    }

    public void delete(Long id) {
        Alert alert = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta não encontrado"));
        if (alert.getPet() != null) {
            authorizationService.validatePetOwnership(alert.getPet());
        }
        repository.deleteById(id);
    }

    private AlertResponse toResponse(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getAlertType(),
                alert.getMessage(),
                alert.getSeverity(),
                alert.getStatus(),
                alert.getCreatedAt(),
                alert.getResolvedAt(),
                alert.getPet() != null ? alert.getPet().getId() : null
        );
    }
}