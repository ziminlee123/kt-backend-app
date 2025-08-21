package com.kt.backendapp.domain.service;

import com.kt.backendapp.entity.Festival;
import com.kt.backendapp.entity.FestivalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class FestivalDomainServiceTest {

    private FestivalDomainService festivalDomainService;
    private Festival festival;

    @BeforeEach
    void setUp() {
        festivalDomainService = new FestivalDomainService();
        festival = Festival.builder()
                .id(1L)
                .name("테스트 축제")
                .startDate(LocalDate.of(2024, 8, 15))
                .endDate(LocalDate.of(2024, 8, 17))
                .target("10,000명")
                .status(FestivalStatus.BEFORE)
                .build();
    }

    @Test
    @DisplayName("날짜 기반 상태 자동 결정")
    void determineStatusByDate() {
        // given
        LocalDate beforeStart = LocalDate.of(2024, 8, 10);
        LocalDate during = LocalDate.of(2024, 8, 16);
        LocalDate afterEnd = LocalDate.of(2024, 8, 20);

        // when & then
        assertEquals(FestivalStatus.BEFORE, 
            festivalDomainService.determineStatus(festival, beforeStart));
        assertEquals(FestivalStatus.DURING, 
            festivalDomainService.determineStatus(festival, during));
        assertEquals(FestivalStatus.ENDED, 
            festivalDomainService.determineStatus(festival, afterEnd));
    }

    @Test
    @DisplayName("취소된 축제는 상태 변경 불가")
    void cancelledFestivalStatusChange() {
        // given
        festival.setStatus(FestivalStatus.CANCELLED);
        LocalDate testDate = LocalDate.of(2024, 8, 16);

        // when & then
        assertFalse(festivalDomainService.canChangeStatusTo(festival, FestivalStatus.DURING, testDate));
        assertFalse(festivalDomainService.canChangeStatusTo(festival, FestivalStatus.BEFORE, testDate));
        assertTrue(festivalDomainService.canChangeStatusTo(festival, FestivalStatus.CANCELLED, testDate));
    }

    @Test
    @DisplayName("종료된 축제는 이전 상태로 변경 불가")
    void endedFestivalStatusChange() {
        // given
        festival.setStatus(FestivalStatus.ENDED);
        LocalDate testDate = LocalDate.of(2024, 8, 20);

        // when & then
        assertFalse(festivalDomainService.canChangeStatusTo(festival, FestivalStatus.DURING, testDate));
        assertFalse(festivalDomainService.canChangeStatusTo(festival, FestivalStatus.BEFORE, testDate));
        assertTrue(festivalDomainService.canChangeStatusTo(festival, FestivalStatus.ENDED, testDate));
    }

    @Test
    @DisplayName("축제 성과 평가 - 우수")
    void evaluateExcellentPerformance() {
        // given
        festival.setActualAttendees(12000); // 120% 달성
        festival.setSatisfactionScore(95);

        // when
        FestivalDomainService.PerformanceRating rating = 
            festivalDomainService.evaluatePerformance(festival);

        // then
        assertEquals(FestivalDomainService.PerformanceRating.EXCELLENT, rating);
    }

    @Test
    @DisplayName("축제 성과 평가 - 미평가")
    void evaluateNotEvaluated() {
        // given
        festival.setActualAttendees(null);
        festival.setSatisfactionScore(null);

        // when
        FestivalDomainService.PerformanceRating rating = 
            festivalDomainService.evaluatePerformance(festival);

        // then
        assertEquals(FestivalDomainService.PerformanceRating.NOT_EVALUATED, rating);
    }

    @Test
    @DisplayName("유효한 날짜 범위 검증")
    void validateDateRange() {
        // given
        LocalDate validStart = LocalDate.of(2024, 8, 15);
        LocalDate validEnd = LocalDate.of(2024, 8, 17);
        LocalDate invalidEnd = LocalDate.of(2024, 8, 10); // 시작일보다 이전
        LocalDate tooLongEnd = LocalDate.of(2024, 10, 15); // 30일 초과

        // when & then
        assertTrue(festivalDomainService.isValidDateRange(validStart, validEnd));
        assertFalse(festivalDomainService.isValidDateRange(validStart, invalidEnd));
        assertFalse(festivalDomainService.isValidDateRange(validStart, tooLongEnd));
    }

    @Test
    @DisplayName("수익성 분석")
    void analyzeProfitability() {
        // given
        festival.setActualAttendees(10000);
        festival.setTotalRevenue(500_000_000L); // 5억원

        // when
        FestivalDomainService.ProfitabilityAnalysis analysis = 
            festivalDomainService.analyzeProfitability(festival);

        // then
        assertTrue(analysis.isAnalyzable());
        assertEquals(50000.0, analysis.getRevenuePerAttendee());
        assertEquals(FestivalDomainService.ProfitabilityGrade.EXCELLENT, 
            analysis.getProfitabilityGrade());
    }
}
