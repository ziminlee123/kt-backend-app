package com.kt.backendapp.service;

import com.kt.backendapp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FestivalService {
    
    // 기본 CRUD
    Page<FestivalDTO> getAllFestivals(Pageable pageable, String status, String search);
    FestivalDTO getFestivalById(String id);
    FestivalDTO createFestival(FestivalRequestDTO requestDTO);
    FestivalDTO updateFestival(String id, FestivalRequestDTO requestDTO);
    void deleteFestival(String id);
    
    // 상태 관리
    FestivalDTO updateFestivalStatus(String id, String status);
    FestivalDTO updateFestivalResults(String id, FestivalResultsDTO resultsDTO);
    
    // 조회 기능
    List<FestivalDTO> getCurrentlyRunningFestivals();
    List<FestivalDTO> getUpcomingFestivals();
    
    // 통계
    FestivalStatisticsDTO getFestivalStatistics();
}
