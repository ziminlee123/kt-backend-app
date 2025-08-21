package com.kt.backendapp.service.impl;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.entity.*;
import com.kt.backendapp.repository.FestivalRepository;
import com.kt.backendapp.service.FestivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FestivalServiceImpl implements FestivalService {

    private final FestivalRepository festivalRepository;
    
    // Entity를 DTO로 변환하는 메서드
    private FestivalDTO convertToDTO(Festival festival) {
        return FestivalDTO.builder()
                .id(festival.getId())
                .name(festival.getName())
                .location(festival.getLocation())
                .startDate(festival.getStartDate())
                .endDate(festival.getEndDate())
                .target(festival.getTarget())
                .description(festival.getDescription())
                .status(festival.getStatus().name().toLowerCase())
                .durationDays(festival.getDurationDays())
                .createdAt(festival.getCreatedAt())
                .updatedAt(festival.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FestivalDTO> getAllFestivals(Pageable pageable, String status, String search) {
        // Mock 데이터로 빠른 구현
        List<FestivalDTO> mockFestivals = createMockFestivals();
        
        // 필터링 적용
        if (status != null) {
            mockFestivals = mockFestivals.stream()
                    .filter(f -> status.equals(f.getStatus()))
                    .toList();
        }
        
        if (search != null && !search.isEmpty()) {
            mockFestivals = mockFestivals.stream()
                    .filter(f -> f.getName().toLowerCase().contains(search.toLowerCase()) ||
                                f.getLocation().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
        
        return new PageImpl<>(mockFestivals, pageable, mockFestivals.size());
    }

    @Override
    @Transactional(readOnly = true)
    public FestivalDTO getFestivalById(Long id) {
        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("축제를 찾을 수 없습니다: " + id));
        return convertToDTO(festival);
    }

    @Override
    public FestivalDTO createFestival(FestivalRequestDTO requestDTO) {
        log.info("축제 생성: {}", requestDTO.getName());
        
        // Entity 생성
        Festival festival = Festival.builder()
                .name(requestDTO.getName())
                .location(requestDTO.getLocation())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .target(requestDTO.getTarget())
                .description(requestDTO.getDescription())
                .status(FestivalStatus.valueOf(requestDTO.getStatus().toUpperCase()))
                .zones(new ArrayList<>())
                .build();
        
        // 데이터베이스에 저장
        Festival savedFestival = festivalRepository.save(festival);
        
        // DTO로 변환하여 반환
        return convertToDTO(savedFestival);
    }

    @Override
    public FestivalDTO updateFestival(Long id, FestivalRequestDTO requestDTO) {
        log.info("축제 수정: ID={}, Name={}", id, requestDTO.getName());
        
        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("축제를 찾을 수 없습니다: " + id));
        
        festival.setName(requestDTO.getName());
        festival.setLocation(requestDTO.getLocation());
        festival.setStartDate(requestDTO.getStartDate());
        festival.setEndDate(requestDTO.getEndDate());
        festival.setTarget(requestDTO.getTarget());
        festival.setDescription(requestDTO.getDescription());
        festival.setStatus(FestivalStatus.valueOf(requestDTO.getStatus().toUpperCase()));
        
        Festival savedFestival = festivalRepository.save(festival);
        return convertToDTO(savedFestival);
    }

    @Override
    public void deleteFestival(Long id) {
        log.info("축제 삭제: ID={}", id);
        if (!festivalRepository.existsById(id)) {
            throw new RuntimeException("축제를 찾을 수 없습니다: " + id);
        }
        festivalRepository.deleteById(id);
    }

    @Override
    public FestivalDTO updateFestivalStatus(Long id, String status) {
        log.info("축제 상태 변경: ID={}, Status={}", id, status);
        
        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("축제를 찾을 수 없습니다: " + id));
        
        festival.setStatus(FestivalStatus.valueOf(status.toUpperCase()));
        Festival savedFestival = festivalRepository.save(festival);
        
        return convertToDTO(savedFestival);
    }

    @Override
    public FestivalDTO updateFestivalResults(Long id, FestivalResultsDTO resultsDTO) {
        log.info("축제 결과 업데이트: ID={}", id);
        
        FestivalDTO festival = getFestivalById(id);
        festival.setActualAttendees(resultsDTO.getActualAttendees());
        festival.setSatisfactionScore(resultsDTO.getSatisfactionScore());
        festival.setTotalRevenue(resultsDTO.getTotalRevenue());
        festival.setUpdatedAt(LocalDateTime.now());
        
        // 목표 달성률 계산
        if (festival.getTarget() != null && festival.getActualAttendees() != null) {
            String targetNumber = festival.getTarget().replaceAll("[^0-9]", "");
            if (!targetNumber.isEmpty()) {
                int target = Integer.parseInt(targetNumber);
                int completionRate = (int) ((festival.getActualAttendees() * 100.0) / target);
                festival.setCompletionRate(completionRate);
            }
        }
        
        return festival;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FestivalDTO> getCurrentlyRunningFestivals() {
        return createMockFestivals().stream()
                .filter(f -> "during".equals(f.getStatus()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FestivalDTO> getUpcomingFestivals() {
        return createMockFestivals().stream()
                .filter(f -> "before".equals(f.getStatus()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FestivalStatisticsDTO getFestivalStatistics() {
        List<FestivalDTO> festivals = createMockFestivals();
        
        return FestivalStatisticsDTO.builder()
                .totalFestivals((long) festivals.size())
                .currentlyRunning(festivals.stream().filter(f -> "during".equals(f.getStatus())).count())
                .upcoming(festivals.stream().filter(f -> "before".equals(f.getStatus())).count())
                .ended(festivals.stream().filter(f -> "ended".equals(f.getStatus())).count())
                .cancelled(festivals.stream().filter(f -> "cancelled".equals(f.getStatus())).count())
                .averageCompletionRate(85.6)
                .averageSatisfactionScore(88.4)
                .totalRevenue(1250000000L)
                .statusStats(Map.of(
                        "before", 2L,
                        "during", 1L,
                        "ended", 1L,
                        "cancelled", 1L
                ))
                .build();
    }

    // Mock 데이터 생성 메서드들
    private List<FestivalDTO> createMockFestivals() {
        return Arrays.asList(
                FestivalDTO.builder()
                        .id(1L)
                        .name("2024 여름 음악 페스티벌")
                        .location("서울특별시 한강공원")
                        .startDate(LocalDate.of(2024, 7, 15))
                        .endDate(LocalDate.of(2024, 7, 17))
                        .target("50,000명")
                        .description("국내외 최고의 아티스트들이 함께하는 3일간의 음악 축제입니다.")
                        .status("during")
                        .durationDays(3)
                        .createdAt(LocalDateTime.now().minusDays(30))
                        .updatedAt(LocalDateTime.now().minusHours(2))
                        .build(),
                
                FestivalDTO.builder()
                        .id(2L)
                        .name("예술문화 축제")
                        .location("부산광역시 해운대구")
                        .startDate(LocalDate.of(2024, 8, 20))
                        .endDate(LocalDate.of(2024, 8, 22))
                        .target("25,000명")
                        .description("지역 예술과 문화유산을 기념하는 축제입니다.")
                        .status("before")
                        .durationDays(3)
                        .createdAt(LocalDateTime.now().minusDays(15))
                        .updatedAt(LocalDateTime.now().minusDays(1))
                        .build(),
                
                FestivalDTO.builder()
                        .id(3L)
                        .name("음식 & 와인 체험전")
                        .location("인천광역시 송도국제도시")
                        .startDate(LocalDate.of(2024, 6, 10))
                        .endDate(LocalDate.of(2024, 6, 12))
                        .target("15,000명")
                        .description("프리미엄 음식과 와인을 체험할 수 있는 행사입니다.")
                        .status("ended")
                        .actualAttendees(16800)
                        .satisfactionScore(92)
                        .totalRevenue(485000000L)
                        .completionRate(112)
                        .durationDays(3)
                        .createdAt(LocalDateTime.now().minusDays(60))
                        .updatedAt(LocalDateTime.now().minusDays(10))
                        .build(),
                
                FestivalDTO.builder()
                        .id(4L)
                        .name("취소된 겨울 축제")
                        .location("대구광역시 달성군")
                        .startDate(LocalDate.of(2024, 12, 15))
                        .endDate(LocalDate.of(2024, 12, 17))
                        .target("30,000명")
                        .description("날씨 악화로 인해 취소된 겨울 축제입니다.")
                        .status("cancelled")
                        .durationDays(3)
                        .createdAt(LocalDateTime.now().minusDays(45))
                        .updatedAt(LocalDateTime.now().minusDays(5))
                        .build()
        );
    }

    private FestivalDTO createMockFestivalDetail(String id) {
        FestivalDTO festival = createMockFestivals().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElse(createMockFestivals().get(0));
        
        // 구역 정보 추가
        if ("1".equals(id)) {
            festival.setZones(Arrays.asList(
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
                            .build()
            ));
        }
        
        return festival;
    }
}
