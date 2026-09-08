package br.com.clyvo.vitalia;

import br.com.clyvo.vitalia.dto.request.AppointmentRequest;
import br.com.clyvo.vitalia.entity.AppUser;
import br.com.clyvo.vitalia.entity.Role;
import br.com.clyvo.vitalia.repository.AppointmentRepository;
import br.com.clyvo.vitalia.repository.AppUserRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import br.com.clyvo.vitalia.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForVeterinario() {
        AppUser tutor = new AppUser();
        tutor.setId(1L);

        Role roleTutor = new Role();
        roleTutor.setName("TUTOR");
        tutor.setRoles(Set.of(roleTutor));

        when(petRepository.existsById(1L)).thenReturn(true);
        when(petRepository.findById(1L)).thenReturn(Optional.of(new br.com.clyvo.vitalia.entity.Pet()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(tutor));

        AppointmentRequest requestDTO = new AppointmentRequest(
                LocalDateTime.now(),
                null,
                "Consulta de rotina",
                1L,
                1L
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            appointmentService.create(requestDTO);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("O usuário informado não possui o perfil de Veterinário", exception.getReason());
    }
}