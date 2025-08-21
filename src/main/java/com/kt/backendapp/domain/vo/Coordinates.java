package com.kt.backendapp.domain.vo;

import lombok.Value;

import java.util.regex.Pattern;

/**
 * GPS 좌표를 나타내는 값 객체
 */
@Value
public class Coordinates {
    private static final Pattern COORDINATE_PATTERN = 
            Pattern.compile("^-?\\d+\\.\\d+,\\s*-?\\d+\\.\\d+$");
    
    double latitude;   // 위도
    double longitude;  // 경도
    
    public Coordinates(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public static Coordinates fromString(String coordinateString) {
        if (coordinateString == null || coordinateString.trim().isEmpty()) {
            throw new IllegalArgumentException("좌표 문자열이 비어있습니다");
        }
        
        if (!COORDINATE_PATTERN.matcher(coordinateString.trim()).matches()) {
            throw new IllegalArgumentException("올바르지 않은 좌표 형식입니다: " + coordinateString);
        }
        
        String[] parts = coordinateString.trim().split(",");
        double lat = Double.parseDouble(parts[0].trim());
        double lng = Double.parseDouble(parts[1].trim());
        
        return new Coordinates(lat, lng);
    }
    
    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90도에서 90도 사이여야 합니다: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180도에서 180도 사이여야 합니다: " + longitude);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
    
    /**
     * 두 좌표 사이의 거리 계산 (km)
     */
    public double distanceTo(Coordinates other) {
        double earthRadius = 6371; // km
        
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLatRad = Math.toRadians(other.latitude - this.latitude);
        double deltaLngRad = Math.toRadians(other.longitude - this.longitude);
        
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
}
