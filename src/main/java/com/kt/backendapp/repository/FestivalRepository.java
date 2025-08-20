package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Festival;
import com.kt.backendapp.entity.FestivalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FestivalRepository extends JpaRepository<Festival, Long> {

    // 상태별 축제 조회
    List<Festival> findByStatus(FestivalStatus status);

    // 축제명으로 검색 (부분 일치)
    List<Festival> findByNameContainingIgnoreCase(String name);

    // 위치로 검색 (부분 일치)
    List<Festival> findByLocationContainingIgnoreCase(String location);

    // 특정 기간 내 축제 조회
    @Query("SELECT f FROM Festival f WHERE f.startDate <= :endDate AND f.endDate >= :startDate")
    List<Festival> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 진행 중인 축제 조회 (현재 날짜 기준)
    @Query("SELECT f FROM Festival f WHERE f.status = 'DURING' AND :currentDate BETWEEN f.startDate AND f.endDate")
    List<Festival> findCurrentlyRunningFestivals(@Param("currentDate") LocalDate currentDate);

    // 예정된 축제 조회 (시작일 기준 정렬)
    @Query("SELECT f FROM Festival f WHERE f.status = 'BEFORE' AND f.startDate > :currentDate ORDER BY f.startDate ASC")
    List<Festival> findUpcomingFestivals(@Param("currentDate") LocalDate currentDate);

    // 종료된 축제 조회 (최신순)
    @Query("SELECT f FROM Festival f WHERE f.status = 'ENDED' ORDER BY f.endDate DESC")
    List<Festival> findEndedFestivalsOrderByEndDateDesc();

    // 축제와 구역 정보를 함께 조회 (N+1 문제 해결)
    @Query("SELECT f FROM Festival f LEFT JOIN FETCH f.zones WHERE f.id = :id")
    Optional<Festival> findByIdWithZones(@Param("id") Long id);

    // 올해 축제 통계 조회
    @Query("SELECT COUNT(f) FROM Festival f WHERE YEAR(f.startDate) = :year")
    Long countByYear(@Param("year") int year);

    // 월별 축제 수 조회
    @Query("SELECT COUNT(f) FROM Festival f WHERE YEAR(f.startDate) = :year AND MONTH(f.startDate) = :month")
    Long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // 상태별 축제 수 조회
    @Query("SELECT f.status, COUNT(f) FROM Festival f GROUP BY f.status")
    List<Object[]> countByStatus();

    // 성과가 좋은 축제 조회 (목표 달성률 기준)
    @Query("SELECT f FROM Festival f WHERE f.actualAttendees IS NOT NULL AND f.target IS NOT NULL " +
           "AND (CAST(f.actualAttendees AS double) / CAST(REGEXP_REPLACE(f.target, '[^0-9]', '') AS double)) >= :achievementRate")
    List<Festival> findHighPerformingFestivals(@Param("achievementRate") double achievementRate);

    // 특정 축제의 존재 여부 확인 (이름과 날짜 중복 체크)
    boolean existsByNameAndStartDateAndEndDate(String name, LocalDate startDate, LocalDate endDate);
}
