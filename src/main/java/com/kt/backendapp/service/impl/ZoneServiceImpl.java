package com.kt.backendapp.service.impl;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.entity.Festival;
import com.kt.backendapp.entity.Zone;
import com.kt.backendapp.entity.ZoneType;
import com.kt.backendapp.repository.FestivalRepository;
import com.kt.backendapp.repository.ZoneRepository;
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

    private final ZoneRepository zoneRepository;
    private final FestivalRepository festivalRepository;

    @Override
    public List<ZoneDTO> getZonesByFestivalId(String festivalId) {
        log.info("축제 구역 목록 조회: Festival ID={}", festivalId);
        
        // 1. Festival 존재 확인
        Long festivalIdLong = Long.parseLong(festivalId);
        if (!festivalRepository.existsById(festivalIdLong)) {
            throw new RuntimeException("축제를 찾을 수 없습니다: " + festivalId);
        }
        
        // 2. 해당 축제의 모든 구역 조회
        List<Zone> zones = zoneRepository.findByFestivalId(festivalIdLong);
        
        // 3. Entity를 DTO로 변환
        List<ZoneDTO> zoneDTOs = zones.stream()
                .map(this::convertToDTO)
                .toList();
        
        log.info("축제 구역 조회 완료: Festival ID={}, 구역 수={}", festivalId, zoneDTOs.size());
        return zoneDTOs;
    }

    @Override
    public ZoneDTO getZoneById(String zoneId) {
        log.info("구역 상세 조회: Zone ID={}", zoneId);
        
        Zone zone = zoneRepository.findById(Long.parseLong(zoneId))
                .orElseThrow(() -> new RuntimeException("구역을 찾을 수 없습니다: " + zoneId));
        
        return convertToDTO(zone);
    }

    @Override
    public ZoneDTO createZone(String festivalId, ZoneRequestDTO requestDTO) {
        log.info("구역 생성: Festival={}, Zone={}", festivalId, requestDTO.getName());
        
        // 1. Festival 존재 확인
        Festival festival = festivalRepository.findById(Long.parseLong(festivalId))
                .orElseThrow(() -> new RuntimeException("축제를 찾을 수 없습니다: " + festivalId));
        
        // 2. 동일한 축제에서 구역명 중복 체크
        if (zoneRepository.existsByFestivalIdAndName(Long.parseLong(festivalId), requestDTO.getName())) {
            throw new RuntimeException("동일한 축제에 이미 존재하는 구역명입니다: " + requestDTO.getName());
        }
        
        // 3. DTO를 Entity로 변환
        Zone zone = Zone.builder()
                .festival(festival)
                .name(requestDTO.getName())
                .type(ZoneType.fromCode(requestDTO.getType()))
                .capacity(requestDTO.getCapacity())
                .coordinates(requestDTO.getCoordinates())
                .notes(requestDTO.getNotes())
                .currentCapacity(0)  // 초기값
                .congestionLevel(0)  // 초기값
                .build();
        
        // 4. DB에 저장
        Zone savedZone = zoneRepository.save(zone);
        log.info("구역 생성 완료: ID={}, Name={}", savedZone.getId(), savedZone.getName());
        
        // 5. Entity를 DTO로 변환하여 반환
        return convertToDTO(savedZone);
    }

    @Override
    public ZoneDTO updateZone(String zoneId, ZoneRequestDTO requestDTO) {
        log.info("구역 수정: ID={}", zoneId);
        
        Zone zone = zoneRepository.findById(Long.parseLong(zoneId))
                .orElseThrow(() -> new RuntimeException("구역을 찾을 수 없습니다: " + zoneId));
        
        // 구역명 중복 체크 (동일한 축제 내에서, 현재 구역 제외)
        if (zoneRepository.existsByFestivalIdAndName(zone.getFestival().getId(), requestDTO.getName()) 
            && !zone.getName().equals(requestDTO.getName())) {
            throw new RuntimeException("동일한 축제에 이미 존재하는 구역명입니다: " + requestDTO.getName());
        }
        
        // 구역 정보 업데이트
        zone.setName(requestDTO.getName());
        zone.setType(ZoneType.fromCode(requestDTO.getType()));
        zone.setCapacity(requestDTO.getCapacity());
        zone.setCoordinates(requestDTO.getCoordinates());
        zone.setNotes(requestDTO.getNotes());
        
        Zone savedZone = zoneRepository.save(zone);
        log.info("구역 수정 완료: ID={}, Name={}", savedZone.getId(), savedZone.getName());
        
        return convertToDTO(savedZone);
    }

    @Override
    public void deleteZone(String zoneId) {
        log.info("구역 삭제: ID={}", zoneId);
        
        if (!zoneRepository.existsById(Long.parseLong(zoneId))) {
            throw new RuntimeException("구역을 찾을 수 없습니다: " + zoneId);
        }
        
        zoneRepository.deleteById(Long.parseLong(zoneId));
        log.info("구역 삭제 완료: ID={}", zoneId);
    }

    @Override
    public ZoneDTO updateRealTimeData(String zoneId, ZoneRealTimeUpdateDTO updateDTO) {
        log.info("실시간 데이터 업데이트: Zone={}, Capacity={}", zoneId, updateDTO.getCurrentCapacity());
        
        Zone zone = zoneRepository.findById(Long.parseLong(zoneId))
                .orElseThrow(() -> new RuntimeException("구역을 찾을 수 없습니다: " + zoneId));
        
        // 실시간 데이터 업데이트
        zone.updateRealTimeData(updateDTO.getCurrentCapacity());
        
        Zone savedZone = zoneRepository.save(zone);
        log.info("실시간 데이터 업데이트 완료: Zone={}, Capacity={}, Congestion={}%", 
                zoneId, savedZone.getCurrentCapacity(), savedZone.getCongestionLevel());
        
        return convertToDTO(savedZone);
    }

    @Override
    public List<ZoneDTO> getHighCongestionZones(String festivalId, int threshold) {
        log.info("혼잡 구역 조회: Festival ID={}, Threshold={}%", festivalId, threshold);
        
        Long festivalIdLong = Long.parseLong(festivalId);
        List<Zone> zones = zoneRepository.findHighCongestionZones(festivalIdLong, threshold);
        
        return zones.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ZoneDTO> getLowCongestionZones(String festivalId, int threshold) {
        log.info("여유 구역 조회: Festival ID={}, Threshold={}%", festivalId, threshold);
        
        Long festivalIdLong = Long.parseLong(festivalId);
        List<Zone> zones = zoneRepository.findLowCongestionZones(festivalIdLong, threshold);
        
        return zones.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ZoneDTO> getZonesByType(String festivalId, String type) {
        log.info("타입별 구역 조회: Festival ID={}, Type={}", festivalId, type);
        
        Long festivalIdLong = Long.parseLong(festivalId);
        ZoneType zoneType = ZoneType.fromCode(type);
        List<Zone> zones = zoneRepository.findByFestivalIdAndType(festivalIdLong, zoneType);
        
        return zones.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public Object getZoneStatistics(String festivalId) {
        log.info("구역 통계 조회: Festival ID={}", festivalId);
        
        Long festivalIdLong = Long.parseLong(festivalId);
        
        // Repository 메서드들을 사용한 통계 계산
        Long totalZones = zoneRepository.countByFestivalId(festivalIdLong);
        Long totalCapacity = zoneRepository.getTotalCapacityByFestivalId(festivalIdLong);
        Long currentCapacity = zoneRepository.getCurrentTotalCapacityByFestivalId(festivalIdLong);
        Double averageCongestion = zoneRepository.getAverageCongestionLevel(festivalIdLong);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalZones", totalZones != null ? totalZones : 0);
        stats.put("totalCapacity", totalCapacity != null ? totalCapacity : 0);
        stats.put("currentCapacity", currentCapacity != null ? currentCapacity : 0);
        stats.put("averageCongestion", averageCongestion != null ? Math.round(averageCongestion * 100.0) / 100.0 : 0);
        
        // 타입별 통계
        List<Object[]> typeStats = zoneRepository.getZoneTypeStatistics(festivalIdLong);
        Map<String, Long> typeStatistics = new HashMap<>();
        for (Object[] stat : typeStats) {
            ZoneType type = (ZoneType) stat[0];
            Long count = (Long) stat[1];
            typeStatistics.put(type.getCode(), count);
        }
        stats.put("typeStatistics", typeStatistics);
        
        log.info("구역 통계 조회 완료: Festival ID={}, Total Zones={}", festivalId, totalZones);
        return stats;
    }

    private List<ZoneDTO> createMockZones(String festivalId) {
        return Arrays.asList(
                ZoneDTO.builder()
                        .id(1L)
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
                        .id(2L)
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
                        .id(3L)
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
    
    /**
     * Zone Entity를 ZoneDTO로 변환
     */
    private ZoneDTO convertToDTO(Zone zone) {
        return ZoneDTO.builder()
                .id(zone.getId())
                .name(zone.getName())
                .type(zone.getType().getCode())
                .capacity(zone.getCapacity())
                .currentCapacity(zone.getCurrentCapacity())
                .congestionLevel(zone.getCongestionLevel())
                .congestionStatus(zone.getCongestionStatus())
                .coordinates(zone.getCoordinates())
                .notes(zone.getNotes())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }
}
