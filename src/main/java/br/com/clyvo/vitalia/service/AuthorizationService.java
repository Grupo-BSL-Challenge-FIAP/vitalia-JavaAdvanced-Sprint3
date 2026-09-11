package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final AppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + username));
    }

    public void validatePetOwnership(Pet pet) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser currentUser = repository.findByEmail(email).orElse(null);
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

    public void validateVeterinarian(AppUser veterinarian) {
        boolean isVeterinarian = veterinarian.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VETERINARIAN") || role.getName().equalsIgnoreCase("ROLE_VETERINARIAN"));

        if (!isVeterinarian) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário informado não possui o perfil de Veterinário");
        }
    }
}