package com.kt.backendapp.service;

import com.kt.backendapp.dto.*;

import java.util.List;

public interface ZoneService {
    
    // 기본 CRUD
    List<ZoneDTO> getZonesByFestivalId(String festivalId);
    ZoneDTO getZoneById(String zoneId);
    ZoneDTO createZone(String festivalId, ZoneRequestDTO requestDTO);
    ZoneDTO updateZone(String zoneId, ZoneRequestDTO requestDTO);
    void deleteZone(String zoneId);
    
    // 실시간 데이터
    ZoneDTO updateRealTimeData(String zoneId, ZoneRealTimeUpdateDTO updateDTO);
    
    // 혼잡도 관련
    List<ZoneDTO> getHighCongestionZones(String festivalId, int threshold);
    List<ZoneDTO> getLowCongestionZones(String festivalId, int threshold);
    
    // 분류 및 통계
    List<ZoneDTO> getZonesByType(String festivalId, String type);
    Object getZoneStatistics(String festivalId);
}
