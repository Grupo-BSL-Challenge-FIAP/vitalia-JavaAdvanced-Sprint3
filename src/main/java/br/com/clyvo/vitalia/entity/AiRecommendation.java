package br.com.clyvo.vitalia.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_VITALIA_AI_RECOMMENDATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "recommendation_type", nullable = false, length = 100)
    private String recommendationType;

    @Column(name = "content", nullable = false, length = 4000)
    private String content;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "status", length = 50)
    private String status;
}