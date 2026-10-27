package br.com.clyvo.vitalia.service;

import br.com.clyvo.vitalia.dto.request.MeasurementRequest;
import br.com.clyvo.vitalia.dto.response.MeasurementResponse;
import br.com.clyvo.vitalia.entity.MeasurementEntity;
import br.com.clyvo.vitalia.repository.MeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import br.com.clyvo.vitalia.dto.request.AlertRequest;
import br.com.clyvo.vitalia.enums.AlertType;
import br.com.clyvo.vitalia.enums.RiskLevel;
import br.com.clyvo.vitalia.enums.AlertStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
public class MeasurementService {

    private static final Logger log = LoggerFactory.getLogger(MeasurementService.class);
    private static final Set<String> STATUS_VALIDOS = Set.of("NORMAL", "ATTENTION", "RISK");

    @Autowired
    private AlertService alertService;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Value("${vitalia.ai.base-url}")
    private String aiBaseUrl;

    @Value("${vitalia.ai.token}")
    private String aiToken;

    private final RestTemplate restTemplate;

    public MeasurementService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    public MeasurementResponse salvarEProcessarMedicao(MeasurementRequest dto) {
        MeasurementEntity entity = new MeasurementEntity();
        entity.setPetId(dto.getPetId());
        entity.setActivityLevel(dto.getActivityLevel());
        entity.setSleepHours(dto.getSleepHours());
        entity.setHeartRate(dto.getHeartRate());
        entity.setRespiratoryRate(dto.getRespiratoryRate());
        entity.setTemperature(dto.getTemperature());
        entity.setBehaviorScore(dto.getBehaviorScore());
        entity.setMeasurementDate(LocalDateTime.now());

        MeasurementEntity salva = measurementRepository.save(entity);

        String statusIa = consultarIa(salva.getPetId(), dto);

        if ("RISK".equalsIgnoreCase(statusIa)) {
            AlertRequest alertRequest = new AlertRequest(
                    AlertType.GENERAL,
                    "Alerta crítico: A IA identificou alterações de alto risco nas medições do pet.",
                    RiskLevel.HIGH,
                    AlertStatus.OPEN,
                    salva.getPetId()
            );

            alertService.create(alertRequest);

        } else if ("ATTENTION".equalsIgnoreCase(statusIa)) {
            AlertRequest alertRequest = new AlertRequest(
                    AlertType.GENERAL,
                    "Atenção necessária: A IA detetou variações atípicas no comportamento do pet.",
                    RiskLevel.MEDIUM,
                    AlertStatus.OPEN,
                    salva.getPetId()
            );

            alertService.create(alertRequest);
        }
        return new MeasurementResponse(salva.getId(), salva.getPetId(), statusIa, statusIa != null);
    }

    private String consultarIa(Long petId, MeasurementRequest dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(aiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map<String, Object>> resposta = restTemplate.exchange(
                    aiBaseUrl + "/api/ai/pets/{petId}/measurements/analyze",
                    HttpMethod.POST,
                    new HttpEntity<>(dto, headers),
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    petId);

            Object status = resposta.getBody() == null ? null : resposta.getBody().get("status");
            if (status instanceof String s && STATUS_VALIDOS.contains(s)) {
                return s;
            }
            log.warn("API de IA retornou status fora do contrato: {}", status);
            return null;
        } catch (RestClientException e) {
            log.warn("API de IA indisponível para o pet {}: {}", petId, e.getMessage());
            return null;
        }
    }
}