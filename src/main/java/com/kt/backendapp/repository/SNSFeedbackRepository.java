package com.kt.backendapp.repository;

import com.kt.backendapp.entity.SNSFeedback;
import com.kt.backendapp.entity.SentimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SNSFeedbackRepository extends JpaRepository<SNSFeedback, Long> {

    // 특정 축제의 모든 피드백 조회
    List<SNSFeedback> findByFestivalId(Long festivalId);

    // 특정 축제의 미해결 이슈 조회 (언급 횟수 내림차순)
    @Query("SELECT s FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.isResolved = false ORDER BY s.mentions DESC")
    List<SNSFeedback> findUnresolvedIssuesByFestivalId(@Param("festivalId") Long festivalId);

    // 특정 축제의 상위 N개 이슈 조회 (언급 횟수 기준)
    @Query("SELECT s FROM SNSFeedback s WHERE s.festival.id = :festivalId ORDER BY s.mentions DESC LIMIT :limit")
    List<SNSFeedback> findTopIssuesByMentions(@Param("festivalId") Long festivalId, @Param("limit") int limit);

    // 감정별 피드백 조회
    List<SNSFeedback> findByFestivalIdAndSentiment(Long festivalId, SentimentType sentiment);

    // 심각도별 이슈 조회
    @Query("SELECT s FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.severityLevel >= :severityLevel ORDER BY s.severityLevel DESC, s.mentions DESC")
    List<SNSFeedback> findHighSeverityIssues(@Param("festivalId") Long festivalId, @Param("severityLevel") int severityLevel);

    // 플랫폼별 피드백 조회
    List<SNSFeedback> findByFestivalIdAndPlatform(Long festivalId, String platform);

    // 특정 기간 내 피드백 조회
    @Query("SELECT s FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.createdAt BETWEEN :startTime AND :endTime ORDER BY s.createdAt DESC")
    List<SNSFeedback> findByFestivalIdAndTimeRange(@Param("festivalId") Long festivalId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    // 축제별 총 언급 수 계산
    @Query("SELECT SUM(s.mentions) FROM SNSFeedback s WHERE s.festival.id = :festivalId")
    Long getTotalMentionsByFestivalId(@Param("festivalId") Long festivalId);

    // 감정별 통계 조회
    @Query("SELECT s.sentiment, COUNT(s), SUM(s.mentions) FROM SNSFeedback s WHERE s.festival.id = :festivalId GROUP BY s.sentiment")
    List<Object[]> getSentimentStatistics(@Param("festivalId") Long festivalId);

    // 해결된 이슈 조회
    List<SNSFeedback> findByFestivalIdAndIsResolvedTrue(Long festivalId);

    // 미해결 이슈 수 조회
    @Query("SELECT COUNT(s) FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.isResolved = false")
    Long countUnresolvedIssues(@Param("festivalId") Long festivalId);

    // 평균 심각도 계산
    @Query("SELECT AVG(s.severityLevel) FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.severityLevel IS NOT NULL")
    Double getAverageSeverityLevel(@Param("festivalId") Long festivalId);

    // 부정적 피드백 비율 계산
    @Query("SELECT " +
           "(SELECT COUNT(s1) FROM SNSFeedback s1 WHERE s1.festival.id = :festivalId AND s1.sentiment = 'NEGATIVE') * 100.0 / " +
           "(SELECT COUNT(s2) FROM SNSFeedback s2 WHERE s2.festival.id = :festivalId)")
    Double getNegativeFeedbackPercentage(@Param("festivalId") Long festivalId);

    // 최근 생성된 피드백 조회 (실시간 모니터링용)
    @Query("SELECT s FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<SNSFeedback> findRecentFeedback(@Param("festivalId") Long festivalId, @Param("since") LocalDateTime since);

    // 키워드로 이슈 검색
    @Query("SELECT s FROM SNSFeedback s WHERE s.festival.id = :festivalId AND LOWER(s.issue) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SNSFeedback> findByFestivalIdAndIssueContaining(@Param("festivalId") Long festivalId, @Param("keyword") String keyword);

    // 플랫폼별 통계
    @Query("SELECT s.platform, COUNT(s), SUM(s.mentions) FROM SNSFeedback s WHERE s.festival.id = :festivalId GROUP BY s.platform")
    List<Object[]> getPlatformStatistics(@Param("festivalId") Long festivalId);

    // 시간대별 피드백 트렌드 (시간별 그룹화)
    @Query("SELECT HOUR(s.createdAt), COUNT(s), SUM(s.mentions) FROM SNSFeedback s WHERE s.festival.id = :festivalId AND DATE(s.createdAt) = DATE(:date) GROUP BY HOUR(s.createdAt) ORDER BY HOUR(s.createdAt)")
    List<Object[]> getHourlyFeedbackTrend(@Param("festivalId") Long festivalId, @Param("date") LocalDateTime date);

    // 해결 시간 평균 계산 (해결된 이슈만)
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, s.createdAt, s.resolvedAt)) FROM SNSFeedback s WHERE s.festival.id = :festivalId AND s.isResolved = true AND s.resolvedAt IS NOT NULL")
    Double getAverageResolutionTimeInHours(@Param("festivalId") Long festivalId);
}
