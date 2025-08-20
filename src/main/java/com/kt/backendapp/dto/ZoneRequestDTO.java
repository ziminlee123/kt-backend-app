package com.kt.backendapp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneRequestDTO {
    
    @NotBlank(message = "구역명은 필수입니다")
    @Size(max = 200, message = "구역명은 200자를 초과할 수 없습니다")
    private String name;
    
    @NotBlank(message = "구역 타입은 필수입니다")
    @Pattern(regexp = "main-stage|food-court|merchandise|vip|parking", 
             message = "올바른 구역 타입을 선택해주세요")
    private String type;
    
    @NotNull(message = "수용인원은 필수입니다")
    @Min(value = 1, message = "수용인원은 1명 이상이어야 합니다")
    @Max(value = 1000000, message = "수용인원은 1,000,000명을 초과할 수 없습니다")
    private Integer capacity;
    
    @Size(max = 100, message = "좌표는 100자를 초과할 수 없습니다")
    @Pattern(regexp = "^$|^-?\\d+\\.\\d+,\\s*-?\\d+\\.\\d+$", 
             message = "좌표는 '위도, 경도' 형식이어야 합니다 (예: 37.5665, 126.9780)")
    private String coordinates;
    
    @Size(max = 1000, message = "비고는 1000자를 초과할 수 없습니다")
    private String notes;
}
