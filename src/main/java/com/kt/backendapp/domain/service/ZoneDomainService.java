package com.kt.backendapp.domain.service;

import com.kt.backendapp.domain.vo.CongestionLevel;
import com.kt.backendapp.domain.vo.Coordinates;
import com.kt.backendapp.entity.Zone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 구역 관련 도메인 비즈니스 로직
 */
@Service
@Slf4j
public class ZoneDomainService {
    
    /**
     * 실시간 혼잡도 업데이트 및 검증
     */
    public CongestionLevel updateCongestionLevel(Zone zone, int currentCapacity) {
        if (currentCapacity < 0) {
            throw new IllegalArgumentException("현재 인원은 0 이상이어야 합니다");
        }
        
        if (currentCapacity > zone.getCapacity()) {
            log.warn("구역 {} 현재 인원({})이 수용인원({})을 초과했습니다", 
                    zone.getName(), currentCapacity, zone.getCapacity());
            // 수용인원을 초과해도 허용 (실제 상황에서 발생 가능)
        }
        
        CongestionLevel congestionLevel = CongestionLevel.calculate(currentCapacity, zone.getCapacity());
        
        // 위험 수준에 따른 로깅
        if (congestionLevel.getRiskLevel() == CongestionLevel.RiskLevel.CRITICAL) {
            log.error("구역 {} 위험 수준 혼잡도: {}%", zone.getName(), congestionLevel.getPercentage());
        } else if (congestionLevel.getRiskLevel() == CongestionLevel.RiskLevel.HIGH) {
            log.warn("구역 {} 높은 혼잡도: {}%", zone.getName(), congestionLevel.getPercentage());
        }
        
        return congestionLevel;
    }
    
    /**
     * 구역 간 거리 계산
     */
    public double calculateDistance(Zone zone1, Zone zone2) {
        if (zone1.getCoordinates() == null || zone2.getCoordinates() == null) {
            throw new IllegalArgumentException("두 구역 모두 좌표 정보가 있어야 거리 계산이 가능합니다");
        }
        
        Coordinates coord1 = Coordinates.fromString(zone1.getCoordinates());
        Coordinates coord2 = Coordinates.fromString(zone2.getCoordinates());
        
        return coord1.distanceTo(coord2);
    }
    
    /**
     * 가장 가까운 구역 찾기
     */
    public Zone findNearestZone(Zone targetZone, List<Zone> zones) {
        if (targetZone.getCoordinates() == null) {
            throw new IllegalArgumentException("대상 구역에 좌표 정보가 없습니다");
        }
        
        return zones.stream()
                .filter(zone -> !zone.getId().equals(targetZone.getId()))
                .filter(zone -> zone.getCoordinates() != null)
                .min((z1, z2) -> {
                    double dist1 = calculateDistance(targetZone, z1);
                    double dist2 = calculateDistance(targetZone, z2);
                    return Double.compare(dist1, dist2);
                })
                .orElse(null);
    }
    
    /**
     * 혼잡도 완화를 위한 추천 구역 찾기
     */
    public List<Zone> recommendAlternativeZones(Zone congestedZone, List<Zone> allZones) {
        if (congestedZone.getCongestionLevel() == null || 
            congestedZone.getCongestionLevel() < 70) {
            return List.of(); // 혼잡하지 않으면 추천 불필요
        }
        
        return allZones.stream()
                .filter(zone -> !zone.getId().equals(congestedZone.getId()))
                .filter(zone -> zone.getType() == congestedZone.getType()) // 같은 타입
                .filter(zone -> zone.getCongestionLevel() != null && zone.getCongestionLevel() < 50) // 여유로운 구역
                .sorted((z1, z2) -> {
                    // 거리와 여유도를 종합 고려
                    double score1 = calculateRecommendationScore(congestedZone, z1);
                    double score2 = calculateRecommendationScore(congestedZone, z2);
                    return Double.compare(score2, score1); // 점수 높은 순
                })
                .limit(3)
                .toList();
    }
    
    private double calculateRecommendationScore(Zone fromZone, Zone toZone) {
        double capacityScore = (double) toZone.getCapacity() / 10000; // 수용인원 점수
        double congestionScore = (100 - toZone.getCongestionLevel()) / 100.0; // 여유도 점수
        
        double distanceScore = 1.0; // 기본 거리 점수
        if (fromZone.getCoordinates() != null && toZone.getCoordinates() != null) {
            double distance = calculateDistance(fromZone, toZone);
            distanceScore = Math.max(0.1, 1.0 - (distance / 1000)); // 1km당 점수 감소
        }
        
        return capacityScore * 0.3 + congestionScore * 0.5 + distanceScore * 0.2;
    }
    
    /**
     * 구역 수용인원 적정성 검증
     */
    public boolean isCapacityAppropriate(Zone zone) {
        // 구역 타입별 적정 수용인원 기준
        return switch (zone.getType()) {
            case MAIN_STAGE -> zone.getCapacity() >= 5000 && zone.getCapacity() <= 100000;
            case FOOD_COURT -> zone.getCapacity() >= 1000 && zone.getCapacity() <= 20000;
            case VIP -> zone.getCapacity() >= 100 && zone.getCapacity() <= 2000;
            case MERCHANDISE -> zone.getCapacity() >= 500 && zone.getCapacity() <= 10000;
            case PARKING -> zone.getCapacity() >= 1000 && zone.getCapacity() <= 50000;
        };
    }
    
    /**
     * 구역 이름 중복 검증
     */
    public boolean isNameUniqueInFestival(String zoneName, Long festivalId, List<Zone> existingZones) {
        return existingZones.stream()
                .filter(zone -> zone.getFestival().getId().equals(festivalId))
                .noneMatch(zone -> zone.getName().equalsIgnoreCase(zoneName.trim()));
    }
    
    /**
     * 전체 축제 수용인원 대비 구역 분배 검증
     */
    public boolean isDistributionBalanced(List<Zone> zones) {
        if (zones.isEmpty()) {
            return true;
        }
        
        int totalCapacity = zones.stream().mapToInt(Zone::getCapacity).sum();
        
        // 메인 스테이지가 전체의 30-70%를 차지해야 함
        int mainStageCapacity = zones.stream()
                .filter(zone -> zone.getType().getCode().equals("main-stage"))
                .mapToInt(Zone::getCapacity)
                .sum();
        
        double mainStageRatio = (double) mainStageCapacity / totalCapacity;
        
        return mainStageRatio >= 0.3 && mainStageRatio <= 0.7;
    }
}
