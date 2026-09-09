package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.PetRequest;
import br.com.clyvo.vitalia.dto.response.PetResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;

    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse create(PetRequest request, AppUser owner) {
        Pet pet = Pet.builder()
                .owner(owner)
                .breedId(request.breedId())
                .name(request.name())
                .sex(request.sex())
                .birthDate(request.birthDate())
                .weightKg(request.weightKg())
                .status(request.status()!=null?request.status().name()
                        :"NORMAL"
                )
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(repository.save(pet));
    }

    @Cacheable("pets")
    public Page<PetResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    public PetResponse findById(Long id, AppUser user) {
        Pet pet = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        validateAccess(pet, user);
        return toResponse(pet);
    }

    public Page<PetResponse> findMyPets(Long ownerUserId, Pageable pageable) {
        return repository.findByOwnerUserId(ownerUserId, pageable)
                .map(this::toResponse);
    }

    public Page<PetResponse> findByName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable).map(this::toResponse);
    }

    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse update(Long id, PetRequest request, AppUser user) {
        Pet pet = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        validateAccess(pet, user);

        pet.setName(request.name());
        pet.setBreedId(request.breedId());
        pet.setSex(request.sex());
        pet.setBirthDate(request.birthDate());
        pet.setWeightKg(request.weightKg());
        if (request.status() != null) {
            pet.setStatus(request.status().name());
        }

        return toResponse(repository.save(pet));
    }

    @CacheEvict(value = "pets", allEntries = true)
    public void delete(Long id, AppUser user) {
        Pet pet = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));

        validateAccess(pet, user);
        repository.delete(pet);
    }

    private void validateAccess(Pet pet, AppUser user) {
        boolean isAdminOrVet = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("VETERINARIAN"));

        if (!isAdminOrVet && (pet.getOwner() == null || !pet.getOwner().getId().equals(user.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar ou modificar este pet");
        }
    }

    private PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSex(),
                pet.getBirthDate(),
                pet.getWeightKg(),
                pet.getStatus(),
                pet.getOwner() != null ? pet.getOwner().getId() : null,
                pet.getBreedId()
        );
    }
}