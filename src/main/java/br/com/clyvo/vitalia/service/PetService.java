package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.PetRequest;
import br.com.clyvo.vitalia.dto.response.PetResponse;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.repository.BreedRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import br.com.clyvo.vitalia.repository.SpeciesRepository;
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
    private final BreedRepository breedRepository;
    private final SpeciesRepository speciesRepository;

    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse create(
            PetRequest request,
            AppUser owner
    ) {
        validateBreed(request.breedId());

        Pet pet = Pet.builder()
                .owner(owner)
                .breedId(request.breedId())
                .name(request.name())
                .sex(request.sex())
                .birthDate(request.birthDate())
                .weightKg(request.weightKg())
                .status(
                        request.status() != null
                                ? request.status().name()
                                : "NORMAL"
                )
                .createdAt(LocalDateTime.now())
                .build();

        Pet savedPet = repository.save(pet);

        return toResponse(savedPet);
    }

    @Cacheable("pets")
    public Page<PetResponse> findAll(
            Pageable pageable
    ) {
        return repository
                .findAll(pageable)
                .map(this::toResponse);
    }

    public PetResponse findById(
            Long id,
            AppUser user
    ) {
        Pet pet = repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pet não encontrado"
                        )
                );

        validateAccess(pet, user);

        return toResponse(pet);
    }

    public Page<PetResponse> findMyPets(
            Long ownerUserId,
            Pageable pageable
    ) {
        return repository
                .findByOwnerUserId(
                        ownerUserId,
                        pageable
                )
                .map(this::toResponse);
    }

    public Page<PetResponse> findByName(
            String name,
            Pageable pageable
    ) {
        return repository
                .findByNameContainingIgnoreCase(
                        name,
                        pageable
                )
                .map(this::toResponse);
    }

    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse update(
            Long id,
            PetRequest request,
            AppUser user
    ) {
        Pet pet = repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pet não encontrado"
                        )
                );

        validateAccess(pet, user);

        /*
         * Se breedId for informado,
         * confirmamos que a raça realmente existe.
         */
        validateBreed(request.breedId());

        pet.setName(request.name());
        pet.setBreedId(request.breedId());
        pet.setSex(request.sex());
        pet.setBirthDate(request.birthDate());
        pet.setWeightKg(request.weightKg());

        if (request.status() != null) {
            pet.setStatus(
                    request.status().name()
            );
        }

        Pet updatedPet =
                repository.save(pet);

        return toResponse(updatedPet);
    }

    @CacheEvict(value = "pets", allEntries = true)
    public void delete(
            Long id,
            AppUser user
    ) {
        Pet pet = repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pet não encontrado"
                        )
                );

        validateAccess(pet, user);

        repository.delete(pet);
    }

    /**
     * Valida se o breedId recebido realmente existe.
     *
     * breedId continua opcional por enquanto.
     */
    private void validateBreed(
            Long breedId
    ) {
        if (breedId == null) {
            return;
        }

        if (!breedRepository.existsById(breedId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Raça não encontrada"
            );
        }
    }

    /**
     * Garante que TUTOR só consiga acessar
     * pets pertencentes à própria conta.
     *
     * ADMIN e VETERINARIAN continuam
     * com permissão conforme regra atual.
     */
    private void validateAccess(
            Pet pet,
            AppUser user
    ) {
        boolean isAdminOrVet =
                user.getRoles()
                        .stream()
                        .anyMatch(role ->
                                role.getName().equals("ADMIN")
                                        ||
                                        role.getName().equals("VETERINARIAN")
                        );

        if (
                !isAdminOrVet &&
                        (
                                pet.getOwner() == null ||
                                        !pet.getOwner()
                                                .getId()
                                                .equals(user.getId())
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar ou modificar este pet"
            );
        }
    }

    /**
     * Converte Pet para PetResponse e resolve:
     *
     * breedId -> breedName
     * breedId -> speciesId -> speciesName
     */
    private PetResponse toResponse(
            Pet pet
    ) {
        String breedName = null;
        Long speciesId = null;
        String speciesName = null;

        if (pet.getBreedId() != null) {

            var breedOptional =
                    breedRepository.findById(
                            pet.getBreedId()
                    );

            if (breedOptional.isPresent()) {
                var breed =
                        breedOptional.get();

                breedName =
                        breed.getName();

                speciesId =
                        breed.getSpeciesId();

                if (speciesId != null) {
                    speciesName =
                            speciesRepository
                                    .findById(speciesId)
                                    .map(species ->
                                            species.getName()
                                    )
                                    .orElse(null);
                }
            }
        }

        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSex(),
                pet.getBirthDate(),
                pet.getWeightKg(),
                pet.getStatus(),

                pet.getOwner() != null
                        ? pet.getOwner().getId()
                        : null,

                pet.getBreedId(),
                breedName,
                speciesId,
                speciesName
        );
    }
}