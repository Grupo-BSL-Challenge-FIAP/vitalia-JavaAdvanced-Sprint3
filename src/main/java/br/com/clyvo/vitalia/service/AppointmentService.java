package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.AppointmentRequest;
import br.com.clyvo.vitalia.dto.response.AppointmentResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Appointment;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import br.com.clyvo.vitalia.repository.AppointmentRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final PetRepository petRepository;
    private final AppUserRepository userRepository;

    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        AppUser veterinarian = userRepository.findById(request.veterinarianId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinário não encontrado"));

        boolean isVeterinarian = veterinarian.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VETERINARIAN") || role.getName().equalsIgnoreCase("ROLE_VETERINARIAN"));

        if (!isVeterinarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário informado não possui o perfil de Veterinário");
        }

        Appointment appointment = Appointment.builder()
                .pet(pet)
                .veterinarian(veterinarian)
                .appointmentDate(request.appointmentDate())
                .status(request.status() != null ? request.status().name() : "SCHEDULED")
                .notes(request.notes())
                .build();

        return toResponse(repository.save(appointment));
    }

    public Page<AppointmentResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        AppUser veterinarian = userRepository.findById(request.veterinarianId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinário não encontrado"));

        boolean isVeterinarian = veterinarian.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VETERINARIAN") || role.getName().equalsIgnoreCase("ROLE_VETERINARIAN"));

        if (!isVeterinarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário informado não possui o perfil de Veterinário");
        }

        appointment.setPet(pet);
        appointment.setVeterinarian(veterinarian);
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setNotes(request.notes());
        if (request.status() != null) {
            appointment.setStatus(request.status().name());
        }

        return toResponse(repository.save(appointment));
    }

    public AppointmentResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
    }

    public List<AppointmentResponse> findByPetId(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado");
        }

        return repository.findByPetIdOrderByAppointmentDateDesc(petId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada");
        repository.deleteById(id);
    }

    private AppointmentResponse toResponse(Appointment app) {
        return new AppointmentResponse(
                app.getId(),
                app.getAppointmentDate(),
                app.getStatus(),
                app.getNotes(),
                app.getPet() != null ? app.getPet().getId() : null,
                app.getVeterinarian() != null ? app.getVeterinarian().getId() : null
        );
    }
}