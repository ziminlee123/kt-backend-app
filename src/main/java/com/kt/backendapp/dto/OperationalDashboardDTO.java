package com.kt.backendapp.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationalDashboardDTO {
    
    // 축제 기본 정보
    private String festivalId;
    private String festivalName;
    private String status;
    
    // 전체 통계
    private Integer totalZones;
    private Long totalCapacity;
    private Long currentTotalCapacity;
    private Double averageCongestionLevel;
    
    // 구역별 실시간 데이터
    private List<ZoneDTO> zones;
    
    // 혼잡도별 구역 수
    private Integer lowCongestionZones;    // 0-40%
    private Integer moderateCongestionZones; // 40-60%
    private Integer highCongestionZones;   // 60-80%
    private Integer criticalCongestionZones; // 80-100%
    
    // SNS 피드백 요약
    private Long totalMentions;
    private Long unresolvedIssues;
    private Double negativeFeedbackPercentage;
    private List<SNSFeedbackDTO> topIssues; // 상위 3개 이슈
    
    // 실시간 알림
    private List<Alert> alerts;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Alert {
        private String type; // congestion, feedback, system
        private String severity; // low, medium, high, critical
        private String message;
        private String zoneId; // 구역 관련 알림인 경우
        private String timestamp;
    }
}
