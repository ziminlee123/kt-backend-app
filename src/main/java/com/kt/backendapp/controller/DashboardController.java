package com.kt.backendapp.controller;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 개발용
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 실시간 운영 대시보드 데이터
     */
    @GetMapping("/festivals/{festivalId}/operational")
    public ResponseEntity<ApiResponseDTO<OperationalDashboardDTO>> getOperationalDashboard(
            @PathVariable String festivalId) {
        
        log.info("실시간 운영 대시보드 조회 - Festival ID: {}", festivalId);
        
        OperationalDashboardDTO dashboard = dashboardService.getOperationalDashboard(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("실시간 대시보드 조회 성공", dashboard));
    }

    /**
     * 전체 축제 통계 대시보드
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponseDTO<FestivalStatisticsDTO>> getStatisticsDashboard() {
        log.info("전체 축제 통계 대시보드 조회");
        
        FestivalStatisticsDTO statistics = dashboardService.getStatisticsDashboard();
        return ResponseEntity.ok(ApiResponseDTO.success("통계 대시보드 조회 성공", statistics));
    }

    /**
     * 실시간 알림 조회
     */
    @GetMapping("/festivals/{festivalId}/alerts")
    public ResponseEntity<ApiResponseDTO<Object>> getRealtimeAlerts(@PathVariable String festivalId) {
        log.info("실시간 알림 조회 - Festival ID: {}", festivalId);
        
        Object alerts = dashboardService.getRealtimeAlerts(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("실시간 알림 조회 성공", alerts));
    }

    /**
     * 축제 성과 요약
     */
    @GetMapping("/festivals/{festivalId}/performance")
    public ResponseEntity<ApiResponseDTO<Object>> getFestivalPerformance(@PathVariable String festivalId) {
        log.info("축제 성과 요약 조회 - Festival ID: {}", festivalId);
        
        Object performance = dashboardService.getFestivalPerformance(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("축제 성과 조회 성공", performance));
    }
}
