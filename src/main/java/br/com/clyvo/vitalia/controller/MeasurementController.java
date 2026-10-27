package br.com.clyvo.vitalia.controller;

import br.com.clyvo.vitalia.dto.request.MeasurementRequest;
import br.com.clyvo.vitalia.dto.response.MeasurementResponse;
import br.com.clyvo.vitalia.service.MeasurementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    @Autowired
    private MeasurementService measurementService;

    @PostMapping
    public ResponseEntity<MeasurementResponse> registrarMedicao(@Valid @RequestBody MeasurementRequest request) {
        MeasurementResponse response = measurementService.salvarEProcessarMedicao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}