package com.kt.backendapp.service.impl;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.service.DashboardService;
import com.kt.backendapp.service.FestivalService;
import com.kt.backendapp.service.ZoneService;
import com.kt.backendapp.service.SNSFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final FestivalService festivalService;
    private final ZoneService zoneService;
    private final SNSFeedbackService snsService;

    @Override
    public OperationalDashboardDTO getOperationalDashboard(String festivalId) {
        log.info("실시간 운영 대시보드 조회: Festival ID={}", festivalId);
        
        FestivalDTO festival = festivalService.getFestivalById(festivalId);
        List<ZoneDTO> zones = zoneService.getZonesByFestivalId(festivalId);
        List<SNSFeedbackDTO> topIssues = snsService.getTopIssuesByMentions(festivalId, 3);
        Map<String, Object> feedbackSummary = snsService.getFeedbackSummary(festivalId);
        
        // 혼잡도별 구역 수 계산
        int lowCongestion = 0, moderate = 0, high = 0, critical = 0;
        for (ZoneDTO zone : zones) {
            if (zone.getCongestionLevel() != null) {
                if (zone.getCongestionLevel() < 40) lowCongestion++;
                else if (zone.getCongestionLevel() < 60) moderate++;
                else if (zone.getCongestionLevel() < 80) high++;
                else critical++;
            }
        }
        
        // 실시간 알림 생성
        List<OperationalDashboardDTO.Alert> alerts = generateMockAlerts();
        
        return OperationalDashboardDTO.builder()
                .festivalId(festivalId)
                .festivalName(festival.getName())
                .status(festival.getStatus())
                .totalZones(zones.size())
                .totalCapacity(zones.stream().mapToLong(ZoneDTO::getCapacity).sum())
                .currentTotalCapacity(zones.stream().mapToLong(z -> z.getCurrentCapacity() != null ? z.getCurrentCapacity() : 0).sum())
                .averageCongestionLevel(zones.stream().mapToInt(z -> z.getCongestionLevel() != null ? z.getCongestionLevel() : 0).average().orElse(0))
                .zones(zones)
                .lowCongestionZones(lowCongestion)
                .moderateCongestionZones(moderate)
                .highCongestionZones(high)
                .criticalCongestionZones(critical)
                .totalMentions((Long) feedbackSummary.get("totalMentions"))
                .unresolvedIssues((Long) feedbackSummary.get("unresolvedCount"))
                .negativeFeedbackPercentage(((Number) feedbackSummary.get("negativeFeedbackPercentage")).doubleValue())
                .topIssues(topIssues)
                .alerts(alerts)
                .build();
    }

    @Override
    public FestivalStatisticsDTO getStatisticsDashboard() {
        return festivalService.getFestivalStatistics();
    }

    @Override
    public Object getRealtimeAlerts(String festivalId) {
        return generateMockAlerts();
    }

    @Override
    public Object getFestivalPerformance(String festivalId) {
        FestivalDTO festival = festivalService.getFestivalById(festivalId);
        
        Map<String, Object> performance = new HashMap<>();
        performance.put("festivalId", festivalId);
        performance.put("festivalName", festival.getName());
        performance.put("status", festival.getStatus());
        performance.put("targetAttendees", festival.getTarget());
        performance.put("actualAttendees", festival.getActualAttendees());
        performance.put("completionRate", festival.getCompletionRate());
        performance.put("satisfactionScore", festival.getSatisfactionScore());
        performance.put("totalRevenue", festival.getTotalRevenue());
        performance.put("durationDays", festival.getDurationDays());
        
        return performance;
    }

    private List<OperationalDashboardDTO.Alert> generateMockAlerts() {
        return Arrays.asList(
                OperationalDashboardDTO.Alert.builder()
                        .type("congestion")
                        .severity("high")
                        .message("메인 스테이지 구역 혼잡도 84% - 주의 필요")
                        .zoneId("1")
                        .timestamp(LocalDateTime.now().minusMinutes(5).toString())
                        .build(),
                
                OperationalDashboardDTO.Alert.builder()
                        .type("feedback")
                        .severity("medium")
                        .message("SNS에서 화장실 청결도 관련 불만 급증")
                        .timestamp(LocalDateTime.now().minusMinutes(15).toString())
                        .build(),
                
                OperationalDashboardDTO.Alert.builder()
                        .type("system")
                        .severity("low")
                        .message("VIP 구역 여유 공간 있음 - 추가 고객 유치 가능")
                        .zoneId("3")
                        .timestamp(LocalDateTime.now().minusMinutes(30).toString())
                        .build()
        );
    }
}
