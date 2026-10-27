package br.com.clyvo.vitalia.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class MeasurementRequest {

    @NotNull
    private Long petId;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private Double activityLevel;

    @NotNull @DecimalMin("0") @DecimalMax("24")
    private Double sleepHours;

    @NotNull @DecimalMin("20") @DecimalMax("300")
    private Double heartRate;

    @NotNull @DecimalMin("3") @DecimalMax("120")
    private Double respiratoryRate;

    @NotNull @DecimalMin("30") @DecimalMax("45")
    private Double temperature;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private Double behaviorScore;

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public Double getActivityLevel() { return activityLevel; }
    public void setActivityLevel(Double activityLevel) { this.activityLevel = activityLevel; }
    public Double getSleepHours() { return sleepHours; }
    public void setSleepHours(Double sleepHours) { this.sleepHours = sleepHours; }
    public Double getHeartRate() { return heartRate; }
    public void setHeartRate(Double heartRate) { this.heartRate = heartRate; }
    public Double getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Double respiratoryRate) { this.respiratoryRate = respiratoryRate; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getBehaviorScore() { return behaviorScore; }
    public void setBehaviorScore(Double behaviorScore) { this.behaviorScore = behaviorScore; }
}