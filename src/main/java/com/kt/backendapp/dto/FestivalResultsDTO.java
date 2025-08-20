package com.kt.backendapp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalResultsDTO {
    
    @NotNull(message = "실제 참석자 수는 필수입니다")
    @Min(value = 0, message = "참석자 수는 0명 이상이어야 합니다")
    private Integer actualAttendees;
    
    @Min(value = 0, message = "만족도 점수는 0점 이상이어야 합니다")
    @Max(value = 100, message = "만족도 점수는 100점 이하여야 합니다")
    private Integer satisfactionScore;
    
    @Min(value = 0, message = "총 수익은 0원 이상이어야 합니다")
    private Long totalRevenue;
    
    @Size(max = 1000, message = "운영 평가는 1000자를 초과할 수 없습니다")
    private String operationalNotes; // 운영 평가 및 특이사항
}
