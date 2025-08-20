package com.kt.backendapp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneRealTimeUpdateDTO {
    
    @NotNull(message = "구역 ID는 필수입니다")
    private String zoneId;
    
    @NotNull(message = "현재 인원은 필수입니다")
    @Min(value = 0, message = "현재 인원은 0명 이상이어야 합니다")
    private Integer currentCapacity;
}
