package com.kt.backendapp.domain.service;

import com.kt.backendapp.domain.vo.TargetAudience;
import com.kt.backendapp.entity.Festival;
import com.kt.backendapp.entity.FestivalStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 축제 관련 도메인 비즈니스 로직
 */
@Service
@Slf4j
public class FestivalDomainService {
    
    /**
     * 축제 상태를 자동으로 업데이트
     */
    public FestivalStatus determineStatus(Festival festival, LocalDate currentDate) {
        if (festival.getStatus() == FestivalStatus.CANCELLED) {
            return FestivalStatus.CANCELLED;
        }
        
        if (currentDate.isBefore(festival.getStartDate())) {
            return FestivalStatus.BEFORE;
        } else if (currentDate.isAfter(festival.getEndDate())) {
            return FestivalStatus.ENDED;
        } else {
            return FestivalStatus.DURING;
        }
    }
    
    /**
     * 축제 상태 변경 가능 여부 검증
     */
    public boolean canChangeStatusTo(Festival festival, FestivalStatus newStatus, LocalDate currentDate) {
        FestivalStatus currentStatus = festival.getStatus();
        
        // 취소된 축제는 다른 상태로 변경 불가
        if (currentStatus == FestivalStatus.CANCELLED && newStatus != FestivalStatus.CANCELLED) {
            return false;
        }
        
        // 종료된 축제는 다시 운영 중으로 변경 불가
        if (currentStatus == FestivalStatus.ENDED && 
            (newStatus == FestivalStatus.DURING || newStatus == FestivalStatus.BEFORE)) {
            return false;
        }
        
        // 날짜 기반 상태 검증
        switch (newStatus) {
            case BEFORE:
                return currentDate.isBefore(festival.getStartDate());
            case DURING:
                return !currentDate.isBefore(festival.getStartDate()) && 
                       !currentDate.isAfter(festival.getEndDate());
            case ENDED:
                return currentDate.isAfter(festival.getEndDate()) || 
                       currentStatus == FestivalStatus.DURING;
            case CANCELLED:
                return currentStatus != FestivalStatus.ENDED;
            default:
                return false;
        }
    }
    
    /**
     * 축제 성과 평가
     */
    public PerformanceRating evaluatePerformance(Festival festival) {
        if (festival.getActualAttendees() == null || festival.getSatisfactionScore() == null) {
            return PerformanceRating.NOT_EVALUATED;
        }
        
        TargetAudience target = TargetAudience.fromString(festival.getTarget());
        double achievementRate = target.calculateAchievementRate(festival.getActualAttendees());
        int satisfaction = festival.getSatisfactionScore();
        
        // 목표 달성률과 만족도를 종합 평가
        if (achievementRate >= 100 && satisfaction >= 90) {
            return PerformanceRating.EXCELLENT;
        } else if (achievementRate >= 80 && satisfaction >= 80) {
            return PerformanceRating.GOOD;
        } else if (achievementRate >= 60 && satisfaction >= 70) {
            return PerformanceRating.AVERAGE;
        } else if (achievementRate >= 40 && satisfaction >= 60) {
            return PerformanceRating.BELOW_AVERAGE;
        } else {
            return PerformanceRating.POOR;
        }
    }
    
    /**
     * 축제 기간 유효성 검증
     */
    public boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        
        // 시작일이 종료일보다 늦을 수 없음
        if (startDate.isAfter(endDate)) {
            return false;
        }
        
        // 축제 기간이 너무 길면 안됨 (최대 30일)
        if (startDate.plusDays(30).isBefore(endDate)) {
            log.warn("축제 기간이 30일을 초과합니다: {} ~ {}", startDate, endDate);
            return false;
        }
        
        return true;
    }
    
    /**
     * 축제 수익성 분석
     */
    public ProfitabilityAnalysis analyzeProfitability(Festival festival) {
        if (festival.getTotalRevenue() == null || festival.getActualAttendees() == null) {
            return ProfitabilityAnalysis.builder()
                    .isAnalyzable(false)
                    .message("수익 데이터가 충분하지 않습니다")
                    .build();
        }
        
        double revenuePerAttendee = (double) festival.getTotalRevenue() / festival.getActualAttendees();
        
        return ProfitabilityAnalysis.builder()
                .isAnalyzable(true)
                .totalRevenue(festival.getTotalRevenue())
                .attendeeCount(festival.getActualAttendees())
                .revenuePerAttendee(revenuePerAttendee)
                .profitabilityGrade(determineProfitabilityGrade(revenuePerAttendee))
                .build();
    }
    
    private ProfitabilityGrade determineProfitabilityGrade(double revenuePerAttendee) {
        if (revenuePerAttendee >= 50000) return ProfitabilityGrade.EXCELLENT;
        if (revenuePerAttendee >= 30000) return ProfitabilityGrade.GOOD;
        if (revenuePerAttendee >= 20000) return ProfitabilityGrade.AVERAGE;
        if (revenuePerAttendee >= 10000) return ProfitabilityGrade.BELOW_AVERAGE;
        return ProfitabilityGrade.POOR;
    }
    
    public enum PerformanceRating {
        EXCELLENT("우수"),
        GOOD("양호"),
        AVERAGE("보통"),
        BELOW_AVERAGE("미흡"),
        POOR("부족"),
        NOT_EVALUATED("미평가");
        
        private final String description;
        
        PerformanceRating(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum ProfitabilityGrade {
        EXCELLENT("매우 높음"),
        GOOD("높음"),
        AVERAGE("보통"),
        BELOW_AVERAGE("낮음"),
        POOR("매우 낮음");
        
        private final String description;
        
        ProfitabilityGrade(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @lombok.Builder
    @lombok.Data
    public static class ProfitabilityAnalysis {
        private boolean isAnalyzable;
        private String message;
        private Long totalRevenue;
        private Integer attendeeCount;
        private Double revenuePerAttendee;
        private ProfitabilityGrade profitabilityGrade;
    }
}
