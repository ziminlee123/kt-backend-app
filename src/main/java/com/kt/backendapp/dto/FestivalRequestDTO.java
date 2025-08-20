package com.kt.backendapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalRequestDTO {
    
    @NotBlank(message = "축제명은 필수입니다")
    @Size(max = 200, message = "축제명은 200자를 초과할 수 없습니다")
    private String name;
    
    @NotBlank(message = "장소는 필수입니다")
    @Size(max = 300, message = "장소는 300자를 초과할 수 없습니다")
    private String location;
    
    @NotNull(message = "시작일은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @NotNull(message = "종료일은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @NotBlank(message = "목표 관객 수는 필수입니다")
    @Size(max = 100, message = "목표 관객 수는 100자를 초과할 수 없습니다")
    private String target;
    
    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    private String description;
    
    @NotBlank(message = "상태는 필수입니다")
    @Pattern(regexp = "before|during|ended|cancelled", message = "올바른 상태 값을 입력해주세요")
    private String status;
    
    // 유효성 검증: 종료일이 시작일보다 늦은지 확인
    @AssertTrue(message = "종료일은 시작일보다 늦어야 합니다")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // null 체크는 @NotNull에서 처리
        }
        return !endDate.isBefore(startDate);
    }
}
