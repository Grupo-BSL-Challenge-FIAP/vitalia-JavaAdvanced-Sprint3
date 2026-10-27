package br.com.clyvo.vitalia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_VITALIA_HEALTH_MEASUREMENT")
public class MeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEASUREMENT_ID")
    private Long id;

    @Column(name = "PET_ID")
    private Long petId;

    @Column(name = "ACTIVITY_LEVEL", columnDefinition = "NUMBER")
    private Double activityLevel;

    @Column(name = "SLEEP_HOURS", columnDefinition = "NUMBER")
    private Double sleepHours;

    @Column(name = "HEART_RATE_BPM", columnDefinition = "NUMBER")
    private Double heartRate;

    @Column(name = "RESPIRATORY_RATE", columnDefinition = "NUMBER")
    private Double respiratoryRate;

    @Column(name = "TEMPERATURE_C", columnDefinition = "NUMBER")
    private Double temperature;

    @Column(name = "BEHAVIOR_SCORE", columnDefinition = "NUMBER")
    private Double behaviorScore;

    @Column(name = "MEASUREMENT_DATE")
    private LocalDateTime measurementDate;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getMeasurementDate() { return measurementDate; }
    public void setMeasurementDate(LocalDateTime measurementDate) { this.measurementDate = measurementDate; }

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