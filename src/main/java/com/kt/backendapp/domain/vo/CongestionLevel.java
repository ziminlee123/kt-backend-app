package com.kt.backendapp.domain.vo;

import lombok.Value;

/**
 * 혼잡도를 나타내는 값 객체
 */
@Value
public class CongestionLevel {
    int percentage; // 0-100%
    
    public CongestionLevel(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("혼잡도는 0-100% 사이여야 합니다: " + percentage);
        }
        this.percentage = percentage;
    }
    
    public static CongestionLevel calculate(int currentCapacity, int totalCapacity) {
        if (totalCapacity <= 0) {
            throw new IllegalArgumentException("총 수용인원은 0보다 커야 합니다");
        }
        if (currentCapacity < 0) {
            throw new IllegalArgumentException("현재 인원은 0 이상이어야 합니다");
        }
        
        int congestion = Math.min(100, (int) ((currentCapacity * 100.0) / totalCapacity));
        return new CongestionLevel(congestion);
    }
    
    public static CongestionLevel empty() {
        return new CongestionLevel(0);
    }
    
    public static CongestionLevel full() {
        return new CongestionLevel(100);
    }
    
    /**
     * 혼잡도 상태 반환
     */
    public String getStatus() {
        if (percentage >= 80) return "매우 혼잡";
        if (percentage >= 60) return "혼잡";
        if (percentage >= 40) return "보통";
        return "여유";
    }
    
    /**
     * 혼잡도 색상 코드 (UI용)
     */
    public String getColorCode() {
        if (percentage >= 80) return "#dc3545"; // 빨간색
        if (percentage >= 60) return "#fd7e14"; // 주황색
        if (percentage >= 40) return "#ffc107"; // 노란색
        return "#28a745"; // 초록색
    }
    
    /**
     * 위험 수준
     */
    public RiskLevel getRiskLevel() {
        if (percentage >= 90) return RiskLevel.CRITICAL;
        if (percentage >= 80) return RiskLevel.HIGH;
        if (percentage >= 60) return RiskLevel.MEDIUM;
        if (percentage >= 40) return RiskLevel.LOW;
        return RiskLevel.MINIMAL;
    }
    
    /**
     * 혼잡도가 높은지 확인
     */
    public boolean isHigh() {
        return percentage >= 80;
    }
    
    /**
     * 혼잡도가 낮은지 확인
     */
    public boolean isLow() {
        return percentage < 40;
    }
    
    @Override
    public String toString() {
        return percentage + "%";
    }
    
    public enum RiskLevel {
        MINIMAL("최소"),
        LOW("낮음"),
        MEDIUM("보통"),
        HIGH("높음"),
        CRITICAL("위험");
        
        private final String description;
        
        RiskLevel(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
