package com.kt.backendapp.controller;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals/{festivalId}/zones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 개발용
public class ZoneController {

    private final ZoneService zoneService;

    /**
     * 특정 축제의 모든 구역 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ZoneDTO>>> getZonesByFestival(
            @PathVariable String festivalId) {
        
        log.info("축제 구역 목록 조회 요청 - Festival ID: {}", festivalId);
        
        List<ZoneDTO> zones = zoneService.getZonesByFestivalId(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("구역 목록 조회 성공", zones));
    }

    /**
     * 구역 상세 조회
     */
    @GetMapping("/{zoneId}")
    public ResponseEntity<ApiResponseDTO<ZoneDTO>> getZoneById(
            @PathVariable String festivalId,
            @PathVariable String zoneId) {
        
        log.info("구역 상세 조회 요청 - Festival ID: {}, Zone ID: {}", festivalId, zoneId);
        
        ZoneDTO zone = zoneService.getZoneById(zoneId);
        return ResponseEntity.ok(ApiResponseDTO.success("구역 상세 조회 성공", zone));
    }

    /**
     * 새 구역 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ZoneDTO>> createZone(
            @PathVariable String festivalId,
            @Valid @RequestBody ZoneRequestDTO requestDTO) {
        
        log.info("구역 생성 요청 - Festival ID: {}, Zone name: {}", festivalId, requestDTO.getName());
        
        ZoneDTO createdZone = zoneService.createZone(festivalId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("구역이 성공적으로 생성되었습니다", createdZone));
    }

    /**
     * 구역 정보 수정
     */
    @PutMapping("/{zoneId}")
    public ResponseEntity<ApiResponseDTO<ZoneDTO>> updateZone(
            @PathVariable String festivalId,
            @PathVariable String zoneId,
            @Valid @RequestBody ZoneRequestDTO requestDTO) {
        
        log.info("구역 수정 요청 - Festival ID: {}, Zone ID: {}", festivalId, zoneId);
        
        ZoneDTO updatedZone = zoneService.updateZone(zoneId, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("구역 정보가 성공적으로 수정되었습니다", updatedZone));
    }

    /**
     * 구역 삭제
     */
    @DeleteMapping("/{zoneId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteZone(
            @PathVariable String festivalId,
            @PathVariable String zoneId) {
        
        log.info("구역 삭제 요청 - Festival ID: {}, Zone ID: {}", festivalId, zoneId);
        
        zoneService.deleteZone(zoneId);
        return ResponseEntity.ok(ApiResponseDTO.success("구역이 성공적으로 삭제되었습니다", null));
    }

    /**
     * 실시간 구역 데이터 업데이트 (현재 인원)
     */
    @PatchMapping("/{zoneId}/realtime")
    public ResponseEntity<ApiResponseDTO<ZoneDTO>> updateZoneRealTimeData(
            @PathVariable String festivalId,
            @PathVariable String zoneId,
            @Valid @RequestBody ZoneRealTimeUpdateDTO updateDTO) {
        
        log.info("구역 실시간 데이터 업데이트 - Zone ID: {}, Current Capacity: {}", 
                zoneId, updateDTO.getCurrentCapacity());
        
        ZoneDTO updatedZone = zoneService.updateRealTimeData(zoneId, updateDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("실시간 데이터가 업데이트되었습니다", updatedZone));
    }

    /**
     * 혼잡도 높은 구역 조회
     */
    @GetMapping("/congestion/high")
    public ResponseEntity<ApiResponseDTO<List<ZoneDTO>>> getHighCongestionZones(
            @PathVariable String festivalId,
            @RequestParam(defaultValue = "80") int threshold) {
        
        log.info("혼잡도 높은 구역 조회 - Festival ID: {}, Threshold: {}%", festivalId, threshold);
        
        List<ZoneDTO> highCongestionZones = zoneService.getHighCongestionZones(festivalId, threshold);
        return ResponseEntity.ok(ApiResponseDTO.success("혼잡 구역 조회 성공", highCongestionZones));
    }

    /**
     * 여유 있는 구역 조회
     */
    @GetMapping("/congestion/low")
    public ResponseEntity<ApiResponseDTO<List<ZoneDTO>>> getLowCongestionZones(
            @PathVariable String festivalId,
            @RequestParam(defaultValue = "40") int threshold) {
        
        log.info("여유 구역 조회 - Festival ID: {}, Threshold: {}%", festivalId, threshold);
        
        List<ZoneDTO> lowCongestionZones = zoneService.getLowCongestionZones(festivalId, threshold);
        return ResponseEntity.ok(ApiResponseDTO.success("여유 구역 조회 성공", lowCongestionZones));
    }

    /**
     * 구역 타입별 조회
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponseDTO<List<ZoneDTO>>> getZonesByType(
            @PathVariable String festivalId,
            @PathVariable String type) {
        
        log.info("구역 타입별 조회 - Festival ID: {}, Type: {}", festivalId, type);
        
        List<ZoneDTO> zones = zoneService.getZonesByType(festivalId, type);
        return ResponseEntity.ok(ApiResponseDTO.success("타입별 구역 조회 성공", zones));
    }

    /**
     * 구역 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponseDTO<Object>> getZoneStatistics(@PathVariable String festivalId) {
        log.info("구역 통계 조회 - Festival ID: {}", festivalId);
        
        Object statistics = zoneService.getZoneStatistics(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("구역 통계 조회 성공", statistics));
    }
}
