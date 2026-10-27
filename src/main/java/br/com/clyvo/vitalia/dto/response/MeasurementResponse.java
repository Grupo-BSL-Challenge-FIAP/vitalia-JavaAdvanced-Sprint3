package br.com.clyvo.vitalia.dto.response;

public record MeasurementResponse(Long id, Long petId, String aiStatus, boolean aiAnalysisAvailable) {
}