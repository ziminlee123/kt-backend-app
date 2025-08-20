package com.kt.backendapp.service;

import com.kt.backendapp.dto.SNSFeedbackDTO;

import java.util.List;
import java.util.Map;

public interface SNSFeedbackService {
    
    // 기본 조회
    List<SNSFeedbackDTO> getFeedbackByFestival(String festivalId, String sentiment, Boolean resolved);
    List<SNSFeedbackDTO> getTopIssuesByMentions(String festivalId, int limit);
    List<SNSFeedbackDTO> getUnresolvedIssues(String festivalId);
    List<SNSFeedbackDTO> getHighSeverityIssues(String festivalId, int severityLevel);
    
    // 이슈 관리
    SNSFeedbackDTO resolveIssue(String feedbackId, String resolution);
    
    // 통계 및 분석
    Map<String, Object> getSentimentStatistics(String festivalId);
    Map<String, Object> getPlatformStatistics(String festivalId);
    List<SNSFeedbackDTO> getRecentFeedback(String festivalId, int hours);
    List<SNSFeedbackDTO> searchFeedbackByKeyword(String festivalId, String keyword);
    Map<String, Object> getFeedbackSummary(String festivalId);
}
