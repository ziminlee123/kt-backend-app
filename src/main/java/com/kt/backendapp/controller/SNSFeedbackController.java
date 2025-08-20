package com.kt.backendapp.controller;

import com.kt.backendapp.dto.*;
import com.kt.backendapp.service.SNSFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/festivals/{festivalId}/feedback")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // 개발용
public class SNSFeedbackController {

    private final SNSFeedbackService snsService;

    /**
     * 특정 축제의 모든 SNS 피드백 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SNSFeedbackDTO>>> getFeedbackByFestival(
            @PathVariable String festivalId,
            @RequestParam(required = false) String sentiment,
            @RequestParam(required = false) Boolean resolved) {
        
        log.info("SNS 피드백 조회 - Festival ID: {}, sentiment: {}, resolved: {}", 
                festivalId, sentiment, resolved);
        
        List<SNSFeedbackDTO> feedback = snsService.getFeedbackByFestival(festivalId, sentiment, resolved);
        return ResponseEntity.ok(ApiResponseDTO.success("SNS 피드백 조회 성공", feedback));
    }

    /**
     * 상위 이슈 조회 (언급 횟수 기준)
     */
    @GetMapping("/top-issues")
    public ResponseEntity<ApiResponseDTO<List<SNSFeedbackDTO>>> getTopIssues(
            @PathVariable String festivalId,
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("상위 이슈 조회 - Festival ID: {}, limit: {}", festivalId, limit);
        
        List<SNSFeedbackDTO> topIssues = snsService.getTopIssuesByMentions(festivalId, limit);
        return ResponseEntity.ok(ApiResponseDTO.success("상위 이슈 조회 성공", topIssues));
    }

    /**
     * 미해결 이슈 조회
     */
    @GetMapping("/unresolved")
    public ResponseEntity<ApiResponseDTO<List<SNSFeedbackDTO>>> getUnresolvedIssues(
            @PathVariable String festivalId) {
        
        log.info("미해결 이슈 조회 - Festival ID: {}", festivalId);
        
        List<SNSFeedbackDTO> unresolvedIssues = snsService.getUnresolvedIssues(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("미해결 이슈 조회 성공", unresolvedIssues));
    }

    /**
     * 높은 심각도 이슈 조회
     */
    @GetMapping("/high-severity")
    public ResponseEntity<ApiResponseDTO<List<SNSFeedbackDTO>>> getHighSeverityIssues(
            @PathVariable String festivalId,
            @RequestParam(defaultValue = "4") int severityLevel) {
        
        log.info("높은 심각도 이슈 조회 - Festival ID: {}, severity: {}", festivalId, severityLevel);
        
        List<SNSFeedbackDTO> highSeverityIssues = snsService.getHighSeverityIssues(festivalId, severityLevel);
        return ResponseEntity.ok(ApiResponseDTO.success("높은 심각도 이슈 조회 성공", highSeverityIssues));
    }

    /**
     * 이슈 해결 처리
     */
    @PatchMapping("/{feedbackId}/resolve")
    public ResponseEntity<ApiResponseDTO<SNSFeedbackDTO>> resolveIssue(
            @PathVariable String festivalId,
            @PathVariable String feedbackId,
            @RequestBody Map<String, String> resolution) {
        
        log.info("이슈 해결 처리 - Festival ID: {}, Feedback ID: {}", festivalId, feedbackId);
        
        SNSFeedbackDTO resolvedFeedback = snsService.resolveIssue(feedbackId, resolution.get("resolution"));
        return ResponseEntity.ok(ApiResponseDTO.success("이슈가 해결 처리되었습니다", resolvedFeedback));
    }

    /**
     * 감정별 피드백 통계
     */
    @GetMapping("/statistics/sentiment")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getSentimentStatistics(
            @PathVariable String festivalId) {
        
        log.info("감정별 피드백 통계 조회 - Festival ID: {}", festivalId);
        
        Map<String, Object> statistics = snsService.getSentimentStatistics(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("감정별 통계 조회 성공", statistics));
    }

    /**
     * 플랫폼별 피드백 통계
     */
    @GetMapping("/statistics/platform")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getPlatformStatistics(
            @PathVariable String festivalId) {
        
        log.info("플랫폼별 피드백 통계 조회 - Festival ID: {}", festivalId);
        
        Map<String, Object> statistics = snsService.getPlatformStatistics(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("플랫폼별 통계 조회 성공", statistics));
    }

    /**
     * 실시간 피드백 트렌드 (최근 N시간)
     */
    @GetMapping("/trend/recent")
    public ResponseEntity<ApiResponseDTO<List<SNSFeedbackDTO>>> getRecentFeedbackTrend(
            @PathVariable String festivalId,
            @RequestParam(defaultValue = "24") int hours) {
        
        log.info("실시간 피드백 트렌드 조회 - Festival ID: {}, hours: {}", festivalId, hours);
        
        List<SNSFeedbackDTO> recentFeedback = snsService.getRecentFeedback(festivalId, hours);
        return ResponseEntity.ok(ApiResponseDTO.success("실시간 트렌드 조회 성공", recentFeedback));
    }

    /**
     * 키워드로 피드백 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<SNSFeedbackDTO>>> searchFeedback(
            @PathVariable String festivalId,
            @RequestParam String keyword) {
        
        log.info("피드백 키워드 검색 - Festival ID: {}, keyword: {}", festivalId, keyword);
        
        List<SNSFeedbackDTO> searchResults = snsService.searchFeedbackByKeyword(festivalId, keyword);
        return ResponseEntity.ok(ApiResponseDTO.success("키워드 검색 결과", searchResults));
    }

    /**
     * SNS 피드백 요약 정보
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getFeedbackSummary(
            @PathVariable String festivalId) {
        
        log.info("SNS 피드백 요약 조회 - Festival ID: {}", festivalId);
        
        Map<String, Object> summary = snsService.getFeedbackSummary(festivalId);
        return ResponseEntity.ok(ApiResponseDTO.success("피드백 요약 조회 성공", summary));
    }
}
