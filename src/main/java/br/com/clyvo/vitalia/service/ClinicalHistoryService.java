package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.ClinicalHistoryRequest;
import br.com.clyvo.vitalia.dto.response.ClinicalHistoryResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Appointment;
import br.com.clyvo.vitalia.entity.ClinicalHistory;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.repository.AppointmentRepository;
import br.com.clyvo.vitalia.repository.ClinicalHistoryRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicalHistoryService {

    private final ClinicalHistoryRepository repository;
    private final PetRepository petRepository;
    private final AppUserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuthorizationService authorizationService;

    public ClinicalHistoryResponse create(ClinicalHistoryRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        authorizationService.validatePetOwnership(pet);

        AppUser veterinarian = userRepository.findById(request.veterinarianId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinário não encontrado"));

        authorizationService.validateVeterinarian(veterinarian);

        Appointment appointment = null;
        if (request.appointmentId() != null) {
            appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
        }

        ClinicalHistory history = ClinicalHistory.builder()
                .pet(pet)
                .veterinarian(veterinarian)
                .appointment(appointment)
                .recordDate(LocalDateTime.now())
                .diagnosis(request.diagnosis())
                .observations(request.observations())
                .treatment(request.treatment())
                .build();
        return toResponse(repository.save(history));
    }

    public Page findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    public ClinicalHistoryResponse findById(Long id) {
        ClinicalHistory history = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro clínico não encontrado"));

        if (history.getPet() != null) {
            authorizationService.validatePetOwnership(history.getPet());
        }

        return toResponse(history);
    }

    public List findByPetId(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        authorizationService.validatePetOwnership(pet);

        return repository.findByPetIdOrderByRecordDateDesc(petId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClinicalHistoryResponse update(Long id, ClinicalHistoryRequest request) {
        ClinicalHistory history = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro clínico não encontrado"));

        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        authorizationService.validatePetOwnership(pet);

        AppUser veterinarian = userRepository.findById(request.veterinarianId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinário não encontrado"));

        authorizationService.validateVeterinarian(veterinarian);

        Appointment appointment = null;
        if (request.appointmentId() != null) {
            appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
        }

        history.setPet(pet);
        history.setVeterinarian(veterinarian);
        history.setAppointment(appointment);
        history.setDiagnosis(request.diagnosis());
        history.setObservations(request.observations());
        history.setTreatment(request.treatment());

        return toResponse(repository.save(history));
    }

    public void delete(Long id) {
        ClinicalHistory history = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro clínico não encontrado"));
        if (history.getPet() != null) {
            authorizationService.validatePetOwnership(history.getPet());
        }
        repository.deleteById(id);
    }

    private ClinicalHistoryResponse toResponse(ClinicalHistory history) {
        return new ClinicalHistoryResponse(
                history.getId(),
                history.getPet() != null ? history.getPet().getId() : null,
                history.getVeterinarian() != null ? history.getVeterinarian().getId() : null,
                history.getAppointment() != null ? history.getAppointment().getId() : null,
                history.getRecordDate(),
                history.getDiagnosis(),
                history.getObservations(),
                history.getTreatment()
        );
    }
}