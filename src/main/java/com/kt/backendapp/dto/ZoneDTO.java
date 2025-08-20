package com.kt.backendapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZoneDTO {
    
    private String id; // 프론트엔드 호환성을 위해 String 타입 사용
    
    private String name;
    
    private String type; // main-stage, food-court, merchandise, vip, parking
    
    private Integer capacity;
    
    private String coordinates;
    
    private String notes;
    
    // 실시간 운영 데이터 (운영 중일 때만)
    private Integer currentCapacity;
    
    private Integer congestionLevel; // 0-100%
    
    // 계산된 필드들
    private String congestionStatus; // 여유, 보통, 혼잡, 매우 혼잡
    
    private Double occupancyRate; // 점유율 (currentCapacity / capacity * 100)
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
