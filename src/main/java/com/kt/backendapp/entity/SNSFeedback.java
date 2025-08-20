package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sns_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SNSFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @Column(nullable = false, length = 300)
    private String issue; // 이슈 내용

    @Column(nullable = false)
    private Integer mentions; // 언급 횟수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SentimentType sentiment; // 감정 분석 결과

    @Column(name = "platform", length = 50)
    private String platform; // SNS 플랫폼 (예: Twitter, Instagram, Facebook)

    @Column(name = "severity_level")
    private Integer severityLevel; // 심각도 (1-5)

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false; // 해결 여부

    @Column(columnDefinition = "TEXT")
    private String resolution; // 해결 방안

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // 이슈 해결 처리
    public void resolveIssue(String resolution) {
        this.isResolved = true;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
    }

    // 심각도에 따른 우선순위 반환
    public String getPriorityLevel() {
        if (severityLevel == null) return "보통";
        
        return switch (severityLevel) {
            case 5 -> "매우 높음";
            case 4 -> "높음";
            case 3 -> "보통";
            case 2 -> "낮음";
            case 1 -> "매우 낮음";
            default -> "보통";
        };
    }
}
