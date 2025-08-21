package com.kt.backendapp.controller;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.service.FestivalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 개발용
public class FestivalController {

    private final FestivalService festivalService;

    /**
     * 모든 축제 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<FestivalDTO>>> getAllFestivals(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        log.info("축제 목록 조회 요청 - status: {}, search: {}, page: {}", status, search, pageable.getPageNumber());
        
        Page<FestivalDTO> festivals = festivalService.getAllFestivals(pageable, status, search);
        return ResponseEntity.ok(ApiResponseDTO.success("축제 목록 조회 성공", festivals));
    }

    /**
     * 축제 상세 조회 (구역 포함)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<FestivalDTO>> getFestivalById(@PathVariable Long id) {
        log.info("축제 상세 조회 요청 - ID: {}", id);
        
        FestivalDTO festival = festivalService.getFestivalById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("축제 상세 조회 성공", festival));
    }

    /**
     * 새 축제 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<FestivalDTO>> createFestival(
            @Valid @RequestBody FestivalRequestDTO requestDTO) {
        
        log.info("축제 생성 요청 - name: {}", requestDTO.getName());
        
        FestivalDTO createdFestival = festivalService.createFestival(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("축제가 성공적으로 생성되었습니다", createdFestival));
    }

    /**
     * 축제 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<FestivalDTO>> updateFestival(
            @PathVariable Long id,
            @Valid @RequestBody FestivalRequestDTO requestDTO) {
        
        log.info("축제 수정 요청 - ID: {}, name: {}", id, requestDTO.getName());
        
        FestivalDTO updatedFestival = festivalService.updateFestival(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("축제 정보가 성공적으로 수정되었습니다", updatedFestival));
    }

    /**
     * 축제 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteFestival(@PathVariable Long id) {
        log.info("축제 삭제 요청 - ID: {}", id);
        
        festivalService.deleteFestival(id);
        return ResponseEntity.ok(ApiResponseDTO.success("축제가 성공적으로 삭제되었습니다", null));
    }

    /**
     * 축제 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<FestivalDTO>> updateFestivalStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        log.info("축제 상태 변경 요청 - ID: {}, status: {}", id, status);
        
        FestivalDTO updatedFestival = festivalService.updateFestivalStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success("축제 상태가 변경되었습니다", updatedFestival));
    }

    /**
     * 현재 운영 중인 축제 조회
     */
    @GetMapping("/running")
    public ResponseEntity<ApiResponseDTO<List<FestivalDTO>>> getRunningFestivals() {
        log.info("운영 중인 축제 조회 요청");
        
        List<FestivalDTO> runningFestivals = festivalService.getCurrentlyRunningFestivals();
        return ResponseEntity.ok(ApiResponseDTO.success("운영 중인 축제 조회 성공", runningFestivals));
    }

    /**
     * 예정된 축제 조회
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponseDTO<List<FestivalDTO>>> getUpcomingFestivals() {
        log.info("예정된 축제 조회 요청");
        
        List<FestivalDTO> upcomingFestivals = festivalService.getUpcomingFestivals();
        return ResponseEntity.ok(ApiResponseDTO.success("예정된 축제 조회 성공", upcomingFestivals));
    }

    /**
     * 축제 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponseDTO<FestivalStatisticsDTO>> getFestivalStatistics() {
        log.info("축제 통계 조회 요청");
        
        FestivalStatisticsDTO statistics = festivalService.getFestivalStatistics();
        return ResponseEntity.ok(ApiResponseDTO.success("축제 통계 조회 성공", statistics));
    }

    /**
     * 축제 결과 업데이트 (축제 종료 후)
     */
    @PatchMapping("/{id}/results")
    public ResponseEntity<ApiResponseDTO<FestivalDTO>> updateFestivalResults(
            @PathVariable Long id,
            @RequestBody FestivalResultsDTO resultsDTO) {
        
        log.info("축제 결과 업데이트 요청 - ID: {}", id);
        
        FestivalDTO updatedFestival = festivalService.updateFestivalResults(id, resultsDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("축제 결과가 업데이트되었습니다", updatedFestival));
    }
}
