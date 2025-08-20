package com.kt.backendapp.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalStatisticsDTO {
    
    // 전체 통계
    private Long totalFestivals;
    private Long currentlyRunning;
    private Long upcoming;
    private Long ended;
    private Long cancelled;
    
    // 월별 통계
    private List<MonthlyStatistic> monthlyStats;
    
    // 상태별 통계
    private Map<String, Long> statusStats;
    
    // 성과 통계
    private Double averageCompletionRate;
    private Double averageSatisfactionScore;
    private Long totalRevenue;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyStatistic {
        private Integer year;
        private Integer month;
        private Long count;
    }
}
