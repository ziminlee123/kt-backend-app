package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ZoneType type;

    @Column(nullable = false)
    private Integer capacity; // 수용 인원

    @Column(length = 100)
    private String coordinates; // GPS 좌표 (선택사항)

    @Column(columnDefinition = "TEXT")
    private String notes; // 비고

    // 실시간 운영 데이터 (운영 중일 때만 사용)
    @Column(name = "current_capacity")
    private Integer currentCapacity; // 현재 인원

    @Column(name = "congestion_level")
    private Integer congestionLevel; // 혼잡도 (0-100%)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 혼잡도 계산
    public Integer calculateCongestionLevel() {
        if (currentCapacity == null || capacity == null || capacity == 0) {
            return 0;
        }
        return Math.min(100, (int) ((currentCapacity * 100.0) / capacity));
    }

    // 혼잡도 상태 반환
    public String getCongestionStatus() {
        Integer level = congestionLevel != null ? congestionLevel : calculateCongestionLevel();
        
        if (level >= 80) return "매우 혼잡";
        if (level >= 60) return "혼잡";
        if (level >= 40) return "보통";
        return "여유";
    }

    // 실시간 데이터 업데이트
    public void updateRealTimeData(Integer currentCapacity) {
        this.currentCapacity = currentCapacity;
        this.congestionLevel = calculateCongestionLevel();
    }
}
