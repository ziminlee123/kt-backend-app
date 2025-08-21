package com.kt.backendapp.domain.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CongestionLevelTest {

    @Test
    @DisplayName("혼잡도 계산 정확성")
    void calculateCongestion() {
        // given & when
        CongestionLevel level1 = CongestionLevel.calculate(5000, 10000);
        CongestionLevel level2 = CongestionLevel.calculate(8000, 10000);
        CongestionLevel level3 = CongestionLevel.calculate(12000, 10000);
        
        // then
        assertEquals(50, level1.getPercentage());
        assertEquals(80, level2.getPercentage());
        assertEquals(100, level3.getPercentage()); // 최대 100%로 제한
    }

    @Test
    @DisplayName("혼잡도 상태 문자열 정확성")
    void getStatusString() {
        // given & when & then
        assertEquals("여유", CongestionLevel.calculate(3000, 10000).getStatus());
        assertEquals("보통", CongestionLevel.calculate(5000, 10000).getStatus());
        assertEquals("혼잡", CongestionLevel.calculate(7000, 10000).getStatus());
        assertEquals("매우 혼잡", CongestionLevel.calculate(9000, 10000).getStatus());
    }

    @Test
    @DisplayName("위험 수준 판정")
    void getRiskLevel() {
        // given & when & then
        assertEquals(CongestionLevel.RiskLevel.MINIMAL, 
            CongestionLevel.calculate(2000, 10000).getRiskLevel());
        assertEquals(CongestionLevel.RiskLevel.LOW, 
            CongestionLevel.calculate(5000, 10000).getRiskLevel());
        assertEquals(CongestionLevel.RiskLevel.MEDIUM, 
            CongestionLevel.calculate(7000, 10000).getRiskLevel());
        assertEquals(CongestionLevel.RiskLevel.HIGH, 
            CongestionLevel.calculate(8500, 10000).getRiskLevel());
        assertEquals(CongestionLevel.RiskLevel.CRITICAL, 
            CongestionLevel.calculate(9500, 10000).getRiskLevel());
    }

    @Test
    @DisplayName("혼잡도 높음/낮음 판정")
    void isHighLow() {
        // given
        CongestionLevel low = CongestionLevel.calculate(3000, 10000);
        CongestionLevel high = CongestionLevel.calculate(8500, 10000);
        
        // when & then
        assertTrue(low.isLow());
        assertFalse(low.isHigh());
        assertFalse(high.isLow());
        assertTrue(high.isHigh());
    }

    @Test
    @DisplayName("잘못된 수용인원으로 계산 시 예외")
    void invalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> 
            CongestionLevel.calculate(5000, 0)
        );
        assertThrows(IllegalArgumentException.class, () -> 
            CongestionLevel.calculate(-1000, 10000)
        );
    }
}
