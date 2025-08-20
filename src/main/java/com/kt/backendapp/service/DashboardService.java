package com.kt.backendapp.service;

import com.kt.backendapp.dto.*;

public interface DashboardService {
    
    // 대시보드 데이터
    OperationalDashboardDTO getOperationalDashboard(String festivalId);
    FestivalStatisticsDTO getStatisticsDashboard();
    Object getRealtimeAlerts(String festivalId);
    Object getFestivalPerformance(String festivalId);
}
