package com.kt.backendapp.service.impl;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.service.ZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneServiceImpl implements ZoneService {

    @Override
    public List<ZoneDTO> getZonesByFestivalId(String festivalId) {
        return createMockZones(festivalId);
    }

    @Override
    public ZoneDTO getZoneById(String zoneId) {
        return createMockZones("1").stream()
                .filter(z -> z.getId().equals(zoneId))
                .findFirst()
                .orElse(createMockZones("1").get(0));
    }

    @Override
    public ZoneDTO createZone(String festivalId, ZoneRequestDTO requestDTO) {
        log.info("구역 생성: Festival={}, Zone={}", festivalId, requestDTO.getName());
        
        return ZoneDTO.builder()
                .id(UUID.randomUUID().toString())
                .name(requestDTO.getName())
                .type(requestDTO.getType())
                .capacity(requestDTO.getCapacity())
                .coordinates(requestDTO.getCoordinates())
                .notes(requestDTO.getNotes())
                .currentCapacity(0)
                .congestionLevel(0)
                .congestionStatus("여유")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public ZoneDTO updateZone(String zoneId, ZoneRequestDTO requestDTO) {
        log.info("구역 수정: ID={}", zoneId);
        
        ZoneDTO zone = getZoneById(zoneId);
        zone.setName(requestDTO.getName());
        zone.setType(requestDTO.getType());
        zone.setCapacity(requestDTO.getCapacity());
        zone.setCoordinates(requestDTO.getCoordinates());
        zone.setNotes(requestDTO.getNotes());
        zone.setUpdatedAt(LocalDateTime.now());
        
        return zone;
    }

    @Override
    public void deleteZone(String zoneId) {
        log.info("구역 삭제: ID={}", zoneId);
    }

    @Override
    public ZoneDTO updateRealTimeData(String zoneId, ZoneRealTimeUpdateDTO updateDTO) {
        log.info("실시간 데이터 업데이트: Zone={}, Capacity={}", zoneId, updateDTO.getCurrentCapacity());
        
        ZoneDTO zone = getZoneById(zoneId);
        zone.setCurrentCapacity(updateDTO.getCurrentCapacity());
        
        // 혼잡도 계산
        if (zone.getCapacity() != null && zone.getCapacity() > 0) {
            int congestionLevel = (int) ((updateDTO.getCurrentCapacity() * 100.0) / zone.getCapacity());
            zone.setCongestionLevel(congestionLevel);
            
            if (congestionLevel >= 80) zone.setCongestionStatus("매우 혼잡");
            else if (congestionLevel >= 60) zone.setCongestionStatus("혼잡");
            else if (congestionLevel >= 40) zone.setCongestionStatus("보통");
            else zone.setCongestionStatus("여유");
        }
        
        zone.setUpdatedAt(LocalDateTime.now());
        return zone;
    }

    @Override
    public List<ZoneDTO> getHighCongestionZones(String festivalId, int threshold) {
        return createMockZones(festivalId).stream()
                .filter(z -> z.getCongestionLevel() != null && z.getCongestionLevel() >= threshold)
                .toList();
    }

    @Override
    public List<ZoneDTO> getLowCongestionZones(String festivalId, int threshold) {
        return createMockZones(festivalId).stream()
                .filter(z -> z.getCongestionLevel() != null && z.getCongestionLevel() < threshold)
                .toList();
    }

    @Override
    public List<ZoneDTO> getZonesByType(String festivalId, String type) {
        return createMockZones(festivalId).stream()
                .filter(z -> type.equals(z.getType()))
                .toList();
    }

    @Override
    public Object getZoneStatistics(String festivalId) {
        List<ZoneDTO> zones = createMockZones(festivalId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalZones", zones.size());
        stats.put("totalCapacity", zones.stream().mapToInt(ZoneDTO::getCapacity).sum());
        stats.put("currentCapacity", zones.stream().mapToInt(z -> z.getCurrentCapacity() != null ? z.getCurrentCapacity() : 0).sum());
        stats.put("averageCongestion", zones.stream().mapToInt(z -> z.getCongestionLevel() != null ? z.getCongestionLevel() : 0).average().orElse(0));
        
        return stats;
    }

    private List<ZoneDTO> createMockZones(String festivalId) {
        return Arrays.asList(
                ZoneDTO.builder()
                        .id("1")
                        .name("메인 스테이지")
                        .type("main-stage")
                        .capacity(20000)
                        .currentCapacity(16800)
                        .congestionLevel(84)
                        .congestionStatus("매우 혼잡")
                        .notes("주요 공연 무대")
                        .createdAt(LocalDateTime.now().minusDays(20))
                        .updatedAt(LocalDateTime.now().minusMinutes(5))
                        .build(),
                
                ZoneDTO.builder()
                        .id("2")
                        .name("푸드 플라자")
                        .type("food-court")
                        .capacity(5000)
                        .currentCapacity(2800)
                        .congestionLevel(56)
                        .congestionStatus("보통")
                        .coordinates("37.5665, 126.9780")
                        .createdAt(LocalDateTime.now().minusDays(20))
                        .updatedAt(LocalDateTime.now().minusMinutes(2))
                        .build(),
                
                ZoneDTO.builder()
                        .id("3")
                        .name("VIP 라운지")
                        .type("vip")
                        .capacity(500)
                        .currentCapacity(120)
                        .congestionLevel(24)
                        .congestionStatus("여유")
                        .notes("프리미엄 고객 전용")
                        .createdAt(LocalDateTime.now().minusDays(15))
                        .updatedAt(LocalDateTime.now().minusMinutes(10))
                        .build()
        );
    }
}
