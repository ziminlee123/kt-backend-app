package com.kt.backendapp.domain.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {

    @Test
    @DisplayName("올바른 좌표 문자열로 객체 생성 성공")
    void createFromValidString() {
        // given
        String coordinateString = "37.5665, 126.9780";
        
        // when
        Coordinates coordinates = Coordinates.fromString(coordinateString);
        
        // then
        assertEquals(37.5665, coordinates.getLatitude(), 0.0001);
        assertEquals(126.9780, coordinates.getLongitude(), 0.0001);
    }

    @Test
    @DisplayName("잘못된 좌표 문자열로 객체 생성 실패")
    void createFromInvalidString() {
        // given
        String invalidString = "invalid coordinates";
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            Coordinates.fromString(invalidString)
        );
    }

    @Test
    @DisplayName("위도 범위 초과 시 예외 발생")
    void invalidLatitudeRange() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            new Coordinates(91.0, 126.9780)
        );
        assertThrows(IllegalArgumentException.class, () -> 
            new Coordinates(-91.0, 126.9780)
        );
    }

    @Test
    @DisplayName("경도 범위 초과 시 예외 발생")
    void invalidLongitudeRange() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            new Coordinates(37.5665, 181.0)
        );
        assertThrows(IllegalArgumentException.class, () -> 
            new Coordinates(37.5665, -181.0)
        );
    }

    @Test
    @DisplayName("두 좌표 간 거리 계산")
    void calculateDistance() {
        // given
        Coordinates seoul = new Coordinates(37.5665, 126.9780);
        Coordinates busan = new Coordinates(35.1796, 129.0756);
        
        // when
        double distance = seoul.distanceTo(busan);
        
        // then
        assertTrue(distance > 320 && distance < 330, 
            "서울-부산 거리는 약 325km이어야 함, 실제: " + distance);
    }

    @Test
    @DisplayName("문자열 변환 형식 검증")
    void toStringFormat() {
        // given
        Coordinates coordinates = new Coordinates(37.566535, 126.978027);
        
        // when
        String result = coordinates.toString();
        
        // then
        assertEquals("37.566535, 126.978027", result);
    }
}
