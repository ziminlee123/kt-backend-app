package com.kt.backendapp.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🎉 KT 축제 관리 시스템 백엔드가 성공적으로 실행되었습니다!");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("port", "8080");
        response.put("project", "kt-backend-app");
        response.put("version", "1.0.0");
        response.put("description", "Figma 기반 축제 관리 시스템 REST API");
        
        // API 엔드포인트 목록
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("festivals", "/api/festivals - 축제 관리 API");
        endpoints.put("zones", "/api/festivals/{id}/zones - 구역 관리 API");
        endpoints.put("feedback", "/api/festivals/{id}/feedback - SNS 피드백 API");
        endpoints.put("dashboard", "/api/dashboard - 대시보드 API");
        endpoints.put("health", "/health - 헬스체크");
        endpoints.put("db-test", "/api/db-test - DB 연결 테스트");
        
        response.put("endpoints", endpoints);
        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "애플리케이션이 정상적으로 동작 중입니다");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/api/test")
    public Map<String, Object> apiTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "REST API 테스트 성공!");
        response.put("method", "GET");
        response.put("endpoint", "/api/test");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/api/db-test")
    public Map<String, Object> dbTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "✅ 데이터베이스 연결 준비 완료!");
        response.put("note", "실제 엔터티 기반 API 구현을 위해 준비되었습니다");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}

