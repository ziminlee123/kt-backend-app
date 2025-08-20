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
public class ApiResponseDTO<T> {
    
    private String status; // SUCCESS, ERROR
    
    private String message;
    
    private T data;
    
    private String errorCode;
    
    private String path;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // 성공 응답 생성 메서드
    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .status("SUCCESS")
                .data(data)
                .build();
    }
    
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }
    
    // 에러 응답 생성 메서드
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .status("ERROR")
                .message(message)
                .build();
    }
    
    public static <T> ApiResponseDTO<T> error(String message, String errorCode) {
        return ApiResponseDTO.<T>builder()
                .status("ERROR")
                .message(message)
                .errorCode(errorCode)
                .build();
    }
    
    public static <T> ApiResponseDTO<T> error(String message, String errorCode, String path) {
        return ApiResponseDTO.<T>builder()
                .status("ERROR")
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }
}
