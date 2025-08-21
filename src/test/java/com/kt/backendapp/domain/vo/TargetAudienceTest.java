package com.kt.backendapp.domain.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TargetAudienceTest {

    @Test
    @DisplayName("목표 관객 수 문자열 파싱 성공")
    void parseFromString() {
        // given
        String targetString = "50,000명";
        
        // when
        TargetAudience target = TargetAudience.fromString(targetString);
        
        // then
        assertEquals(50000, target.getCount());
        assertEquals("50,000명", target.getDisplayText());
    }

    @Test
    @DisplayName("달성률 계산 정확성")
    void calculateAchievementRate() {
        // given
        TargetAudience target = new TargetAudience(10000);
        
        // when & then
        assertEquals(100.0, target.calculateAchievementRate(10000), 0.01);
        assertEquals(120.0, target.calculateAchievementRate(12000), 0.01);
        assertEquals(80.0, target.calculateAchievementRate(8000), 0.01);
    }

    @Test
    @DisplayName("목표 달성 여부 판정")
    void isAchieved() {
        // given
        TargetAudience target = new TargetAudience(10000);
        
        // when & then
        assertTrue(target.isAchieved(10000));
        assertTrue(target.isAchieved(12000));
        assertFalse(target.isAchieved(8000));
    }

    @Test
    @DisplayName("0 이하 목표 관객 수 예외")
    void invalidTargetCount() {
        assertThrows(IllegalArgumentException.class, () -> 
            new TargetAudience(0)
        );
        assertThrows(IllegalArgumentException.class, () -> 
            new TargetAudience(-1000)
        );
    }
}
