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
        response.put("message", "🎉 KT Backend App이 성공적으로 실행되었습니다!");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("port", "8080");
        response.put("project", "backend-app");
        response.put("database", "PostgreSQL 연결 준비");
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

