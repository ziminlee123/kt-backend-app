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
public class SNSFeedbackDTO {
    
    private String id;
    
    private String festivalId;
    
    private String issue;
    
    private Integer mentions;
    
    private String sentiment; // positive, neutral, negative
    
    private String platform; // Twitter, Instagram, Facebook 등
    
    private Integer severityLevel; // 1-5
    
    private Boolean isResolved;
    
    private String resolution;
    
    // 계산된 필드들
    private String priorityLevel; // 매우 높음, 높음, 보통, 낮음, 매우 낮음
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resolvedAt;
}
