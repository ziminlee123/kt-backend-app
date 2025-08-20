package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "festivals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 300)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 100)
    private String target; // 목표 관객 수 (예: "50,000명")

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FestivalStatus status;

    // 축제와 연관된 구역들
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Zone> zones = new ArrayList<>();

    // 실제 참석자 수 (축제 종료 후 업데이트)
    @Column(name = "actual_attendees")
    private Integer actualAttendees;

    // 만족도 점수 (축제 종료 후 업데이트)
    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;

    // 총 수익 (축제 종료 후 업데이트)
    @Column(name = "total_revenue")
    private Long totalRevenue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 연관관계 편의 메서드
    public void addZone(Zone zone) {
        zones.add(zone);
        zone.setFestival(this);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
        zone.setFestival(null);
    }

    // 축제 기간 계산
    public int getDurationDays() {
        return (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
    }

    // 목표 달성률 계산 (축제 종료 후)
    public Integer getCompletionRate() {
        if (actualAttendees == null || target == null) {
            return null;
        }
        
        // target에서 숫자만 추출 (예: "50,000명" → 50000)
        String targetNumber = target.replaceAll("[^0-9]", "");
        if (targetNumber.isEmpty()) {
            return null;
        }
        
        int targetCount = Integer.parseInt(targetNumber);
        return (int) ((actualAttendees * 100.0) / targetCount);
    }
}
