package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Zone;
import com.kt.backendapp.entity.ZoneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    // 특정 축제의 모든 구역 조회
    List<Zone> findByFestivalId(Long festivalId);

    // 특정 축제의 구역을 타입별로 조회
    List<Zone> findByFestivalIdAndType(Long festivalId, ZoneType type);

    // 축제별 구역 수 조회
    @Query("SELECT COUNT(z) FROM Zone z WHERE z.festival.id = :festivalId")
    Long countByFestivalId(@Param("festivalId") Long festivalId);

    // 축제별 총 수용인원 계산
    @Query("SELECT SUM(z.capacity) FROM Zone z WHERE z.festival.id = :festivalId")
    Long getTotalCapacityByFestivalId(@Param("festivalId") Long festivalId);

    // 특정 축제의 현재 총 인원 계산 (실시간 운영 중)
    @Query("SELECT SUM(z.currentCapacity) FROM Zone z WHERE z.festival.id = :festivalId AND z.currentCapacity IS NOT NULL")
    Long getCurrentTotalCapacityByFestivalId(@Param("festivalId") Long festivalId);

    // 혼잡도가 높은 구역 조회 (임계값 이상)
    @Query("SELECT z FROM Zone z WHERE z.festival.id = :festivalId AND z.congestionLevel >= :threshold ORDER BY z.congestionLevel DESC")
    List<Zone> findHighCongestionZones(@Param("festivalId") Long festivalId, @Param("threshold") int threshold);

    // 타입별 구역 조회 (전체 축제)
    List<Zone> findByType(ZoneType type);

    // 특정 축제의 구역명으로 검색
    Optional<Zone> findByFestivalIdAndName(Long festivalId, String name);

    // 좌표 정보가 있는 구역 조회
    @Query("SELECT z FROM Zone z WHERE z.festival.id = :festivalId AND z.coordinates IS NOT NULL")
    List<Zone> findZonesWithCoordinates(@Param("festivalId") Long festivalId);

    // 실시간 데이터 업데이트 (현재 인원)
    @Modifying
    @Transactional
    @Query("UPDATE Zone z SET z.currentCapacity = :currentCapacity, z.congestionLevel = :congestionLevel WHERE z.id = :zoneId")
    void updateRealTimeData(@Param("zoneId") Long zoneId, 
                           @Param("currentCapacity") Integer currentCapacity, 
                           @Param("congestionLevel") Integer congestionLevel);

    // 축제별 구역 타입 통계
    @Query("SELECT z.type, COUNT(z) FROM Zone z WHERE z.festival.id = :festivalId GROUP BY z.type")
    List<Object[]> getZoneTypeStatistics(@Param("festivalId") Long festivalId);

    // 평균 혼잡도 계산 (축제별)
    @Query("SELECT AVG(z.congestionLevel) FROM Zone z WHERE z.festival.id = :festivalId AND z.congestionLevel IS NOT NULL")
    Double getAverageCongestionLevel(@Param("festivalId") Long festivalId);

    // 수용인원 기준 상위 구역 조회
    @Query("SELECT z FROM Zone z WHERE z.festival.id = :festivalId ORDER BY z.capacity DESC")
    List<Zone> findTopCapacityZones(@Param("festivalId") Long festivalId);

    // 특정 축제에서 구역명 중복 체크
    boolean existsByFestivalIdAndName(Long festivalId, String name);

    // 축제 삭제 시 관련 구역들 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Zone z WHERE z.festival.id = :festivalId")
    void deleteByFestivalId(@Param("festivalId") Long festivalId);

    // 여유 있는 구역 조회 (혼잡도 낮은 순)
    @Query("SELECT z FROM Zone z WHERE z.festival.id = :festivalId AND z.congestionLevel < :threshold ORDER BY z.congestionLevel ASC")
    List<Zone> findLowCongestionZones(@Param("festivalId") Long festivalId, @Param("threshold") int threshold);
}
