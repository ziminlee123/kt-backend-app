package com.kt.backendapp.domain.vo;

import lombok.Value;

import java.util.regex.Pattern;

/**
 * 목표 관객 수를 나타내는 값 객체
 */
@Value
public class TargetAudience {
    private static final Pattern TARGET_PATTERN = 
            Pattern.compile("^([0-9,]+)\\s*명?$");
    
    int count;
    String displayText;
    
    public TargetAudience(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("목표 관객 수는 0보다 커야 합니다: " + count);
        }
        if (count > 10_000_000) {
            throw new IllegalArgumentException("목표 관객 수가 너무 큽니다: " + count);
        }
        
        this.count = count;
        this.displayText = formatNumber(count) + "명";
    }
    
    public static TargetAudience fromString(String targetString) {
        if (targetString == null || targetString.trim().isEmpty()) {
            throw new IllegalArgumentException("목표 관객 수가 비어있습니다");
        }
        
        if (!TARGET_PATTERN.matcher(targetString.trim()).matches()) {
            throw new IllegalArgumentException("올바르지 않은 목표 관객 수 형식입니다: " + targetString);
        }
        
        // 숫자만 추출
        String numberOnly = targetString.replaceAll("[^0-9]", "");
        int count = Integer.parseInt(numberOnly);
        
        return new TargetAudience(count);
    }
    
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }
    
    /**
     * 달성률 계산
     */
    public double calculateAchievementRate(int actualAttendees) {
        if (actualAttendees < 0) {
            throw new IllegalArgumentException("실제 참석자 수는 0 이상이어야 합니다");
        }
        return (double) actualAttendees / count * 100.0;
    }
    
    /**
     * 목표 대비 부족/초과 인원
     */
    public int getDifference(int actualAttendees) {
        return actualAttendees - count;
    }
    
    /**
     * 목표 달성 여부
     */
    public boolean isAchieved(int actualAttendees) {
        return actualAttendees >= count;
    }
    
    @Override
    public String toString() {
        return displayText;
    }
}
