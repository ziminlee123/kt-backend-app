package com.kt.backendapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FestivalDTO {
    
    private String id; // 프론트엔드 호환성을 위해 String 타입 사용
    
    private String name;
    
    private String location;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String target;
    
    private String description;
    
    private String status; // before, during, ended, cancelled
    
    private List<ZoneDTO> zones;
    
    // 결과 데이터 (축제 종료 후)
    private Integer actualAttendees;
    
    private Integer satisfactionScore;
    
    private Long totalRevenue;
    
    // 계산된 필드들
    private Integer completionRate; // 목표 달성률
    
    private Integer durationDays; // 축제 기간
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
