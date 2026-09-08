package br.com.clyvo.vitalia;

import br.com.clyvo.vitalia.dto.request.AlertRequest;
import br.com.clyvo.vitalia.enums.AlertStatus;
import br.com.clyvo.vitalia.enums.AlertType;
import br.com.clyvo.vitalia.enums.RiskLevel;
import br.com.clyvo.vitalia.repository.AlertRepository;
import br.com.clyvo.vitalia.repository.PetRepository;
import br.com.clyvo.vitalia.service.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AlertService alertService;

    @Test
    void deveLancarExcecaoQuandoPetNaoForEncontradoAoCriarAlerta() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        AlertRequest requestDTO = new AlertRequest(
                AlertType.HEART_RATE,
                "Alerta de teste",
                RiskLevel.HIGH,
                AlertStatus.OPEN,
                1L
        );

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            alertService.create(requestDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Pet não encontrado", exception.getReason());
    }
}