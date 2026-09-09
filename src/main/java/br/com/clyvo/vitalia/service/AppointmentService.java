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
import org.springframework.security.core.context.SecurityContextHolder;
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

        validatePetOwnership(pet);

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

        validatePetOwnership(pet);

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
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));

        if (appointment.getPet() != null) {
            validatePetOwnership(appointment.getPet());
        }

        return toResponse(appointment);
    }

    public List<AppointmentResponse> findByPetId(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        validatePetOwnership(pet);

        return repository.findByPetIdOrderByAppointmentDateDesc(petId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada"));
        if (appointment.getPet() != null) {
            validatePetOwnership(appointment.getPet());
        }
        repository.deleteById(id);
    }

    private void validatePetOwnership(Pet pet) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser != null) {
            boolean isTutor = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equalsIgnoreCase("TUTOR") || r.getName().equalsIgnoreCase("ROLE_TUTOR"));
            boolean isAdminOrVet = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN") || r.getName().equalsIgnoreCase("ROLE_ADMIN") ||
                            r.getName().equalsIgnoreCase("VETERINARIAN") || r.getName().equalsIgnoreCase("ROLE_VETERINARIAN"));

            if (isTutor && !isAdminOrVet) {
                boolean isOwner = pet.getOwner() != null && pet.getOwner().getId().equals(currentUser.getId());
                if (!isOwner) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado: este pet não pertence ao seu usuário");
                }
            }
        }
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