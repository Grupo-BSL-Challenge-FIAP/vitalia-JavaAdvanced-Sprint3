package br.com.clyvo.vitalia;

import br.com.clyvo.vitalia.dto.request.ClinicalHistoryRequest;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Pet;
import br.com.clyvo.vitalia.entity.Role;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import br.com.clyvo.vitalia.repository.ClinicalHistoryRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import br.com.clyvo.vitalia.service.ClinicalHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClinicalHistoryServiceTest {

    @Mock
    private ClinicalHistoryRepository clinicalHistoryRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private ClinicalHistoryService clinicalHistoryService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForVeterinarioAoCriarHistorico() {
        AppUser tutor = new AppUser();
        tutor.setId(1L);
        tutor.setEmail("tutor@email.com");

        Role roleTutor = new Role();
        roleTutor.setName("TUTOR");
        tutor.setRoles(Set.of(roleTutor));

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setOwner(tutor); // ou pet.setOwnerUserId(1L) dependendo da sua entidade

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(userRepository.findByEmail("tutor@email.com")).thenReturn(Optional.of(tutor));

        org.springframework.security.core.Authentication auth = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        org.mockito.Mockito.when(auth.getName()).thenReturn("tutor@email.com");
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        ClinicalHistoryRequest requestDTO = new ClinicalHistoryRequest(
                1L,
                1L,
                1L,
                "Diagnóstico de rotina",
                "Observações gerais",
                "Tratamento aplicado"
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            clinicalHistoryService.create(requestDTO);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("O usuário informado não possui o perfil de Veterinário", exception.getReason());
    }
}