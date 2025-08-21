package com.kt.backendapp.domain.event;

import com.kt.backendapp.domain.vo.CongestionLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 높은 혼잡도 감지 이벤트
 */
@Getter
@RequiredArgsConstructor
public class HighCongestionDetectedEvent {
    
    private final Long festivalId;
    private final String festivalName;
    private final Long zoneId;
    private final String zoneName;
    private final CongestionLevel congestionLevel;
    private final int currentCapacity;
    private final int totalCapacity;
    private final LocalDateTime detectedAt;
    
    public static HighCongestionDetectedEvent of(Long festivalId, String festivalName,
                                               Long zoneId, String zoneName,
                                               CongestionLevel congestionLevel,
                                               int currentCapacity, int totalCapacity) {
        return new HighCongestionDetectedEvent(
                festivalId,
                festivalName,
                zoneId,
                zoneName,
                congestionLevel,
                currentCapacity,
                totalCapacity,
                LocalDateTime.now()
        );
    }
    
    public boolean isCritical() {
        return congestionLevel.getRiskLevel() == CongestionLevel.RiskLevel.CRITICAL;
    }
    
    public String getAlertMessage() {
        return String.format("구역 '%s'에서 %s 혼잡도 감지: %s (현재 %d명 / 수용 %d명)",
                zoneName,
                congestionLevel.getRiskLevel().getDescription(),
                congestionLevel.getStatus(),
                currentCapacity,
                totalCapacity
        );
    }
}
