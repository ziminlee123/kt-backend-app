package com.kt.backendapp.service.impl;

import com.kt.backendapp.dto.SNSFeedbackDTO;
import com.kt.backendapp.service.SNSFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SNSFeedbackServiceImpl implements SNSFeedbackService {

    @Override
    public List<SNSFeedbackDTO> getFeedbackByFestival(String festivalId, String sentiment, Boolean resolved) {
        List<SNSFeedbackDTO> feedback = createMockFeedback(festivalId);
        
        if (sentiment != null) {
            feedback = feedback.stream().filter(f -> sentiment.equals(f.getSentiment())).toList();
        }
        
        if (resolved != null) {
            feedback = feedback.stream().filter(f -> resolved.equals(f.getIsResolved())).toList();
        }
        
        return feedback;
    }

    @Override
    public List<SNSFeedbackDTO> getTopIssuesByMentions(String festivalId, int limit) {
        return createMockFeedback(festivalId).stream()
                .sorted((a, b) -> Integer.compare(b.getMentions(), a.getMentions()))
                .limit(limit)
                .toList();
    }

    @Override
    public List<SNSFeedbackDTO> getUnresolvedIssues(String festivalId) {
        return createMockFeedback(festivalId).stream()
                .filter(f -> !f.getIsResolved())
                .toList();
    }

    @Override
    public List<SNSFeedbackDTO> getHighSeverityIssues(String festivalId, int severityLevel) {
        return createMockFeedback(festivalId).stream()
                .filter(f -> f.getSeverityLevel() != null && f.getSeverityLevel() >= severityLevel)
                .toList();
    }

    @Override
    public SNSFeedbackDTO resolveIssue(String feedbackId, String resolution) {
        log.info("이슈 해결 처리: ID={}", feedbackId);
        
        SNSFeedbackDTO feedback = createMockFeedback("1").stream()
                .filter(f -> f.getId().equals(feedbackId))
                .findFirst()
                .orElse(createMockFeedback("1").get(0));
        
        feedback.setIsResolved(true);
        feedback.setResolution(resolution);
        feedback.setResolvedAt(LocalDateTime.now());
        
        return feedback;
    }

    @Override
    public Map<String, Object> getSentimentStatistics(String festivalId) {
        List<SNSFeedbackDTO> feedback = createMockFeedback(festivalId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", feedback.size());
        stats.put("positive", feedback.stream().filter(f -> "positive".equals(f.getSentiment())).count());
        stats.put("neutral", feedback.stream().filter(f -> "neutral".equals(f.getSentiment())).count());
        stats.put("negative", feedback.stream().filter(f -> "negative".equals(f.getSentiment())).count());
        stats.put("totalMentions", feedback.stream().mapToInt(SNSFeedbackDTO::getMentions).sum());
        
        return stats;
    }

    @Override
    public Map<String, Object> getPlatformStatistics(String festivalId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("Twitter", Map.of("count", 45, "mentions", 892));
        stats.put("Instagram", Map.of("count", 38, "mentions", 567));
        stats.put("Facebook", Map.of("count", 22, "mentions", 334));
        
        return stats;
    }

    @Override
    public List<SNSFeedbackDTO> getRecentFeedback(String festivalId, int hours) {
        return createMockFeedback(festivalId).stream()
                .filter(f -> f.getCreatedAt().isAfter(LocalDateTime.now().minusHours(hours)))
                .toList();
    }

    @Override
    public List<SNSFeedbackDTO> searchFeedbackByKeyword(String festivalId, String keyword) {
        return createMockFeedback(festivalId).stream()
                .filter(f -> f.getIssue().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public Map<String, Object> getFeedbackSummary(String festivalId) {
        List<SNSFeedbackDTO> feedback = createMockFeedback(festivalId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalFeedback", feedback.size());
        summary.put("totalMentions", feedback.stream().mapToInt(SNSFeedbackDTO::getMentions).sum());
        summary.put("unresolvedCount", feedback.stream().filter(f -> !f.getIsResolved()).count());
        summary.put("negativeFeedbackPercentage", 
                Math.round(feedback.stream().filter(f -> "negative".equals(f.getSentiment())).count() * 100.0 / feedback.size()));
        summary.put("averageSeverity", 
                feedback.stream().mapToInt(f -> f.getSeverityLevel() != null ? f.getSeverityLevel() : 1).average().orElse(1.0));
        
        return summary;
    }

    private List<SNSFeedbackDTO> createMockFeedback(String festivalId) {
        return Arrays.asList(
                SNSFeedbackDTO.builder()
                        .id("1")
                        .festivalId(festivalId)
                        .issue("메인 스테이지 대기줄")
                        .mentions(127)
                        .sentiment("negative")
                        .platform("Twitter")
                        .severityLevel(4)
                        .isResolved(false)
                        .priorityLevel("높음")
                        .createdAt(LocalDateTime.now().minusHours(2))
                        .build(),
                
                SNSFeedbackDTO.builder()
                        .id("2")
                        .festivalId(festivalId)
                        .issue("푸드코트 화장실 청결도")
                        .mentions(89)
                        .sentiment("negative")
                        .platform("Instagram")
                        .severityLevel(3)
                        .isResolved(false)
                        .priorityLevel("보통")
                        .createdAt(LocalDateTime.now().minusHours(4))
                        .build(),
                
                SNSFeedbackDTO.builder()
                        .id("3")
                        .festivalId(festivalId)
                        .issue("음향 시설 품질")
                        .mentions(76)
                        .sentiment("positive")
                        .platform("Facebook")
                        .severityLevel(2)
                        .isResolved(true)
                        .resolution("음향 엔지니어 추가 배치로 해결")
                        .priorityLevel("낮음")
                        .createdAt(LocalDateTime.now().minusHours(6))
                        .resolvedAt(LocalDateTime.now().minusHours(1))
                        .build()
        );
    }
}
