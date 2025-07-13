package com.interview.repository;

import com.interview.entity.EvaluationResult;
import com.interview.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Long> {
    
    /**
     * 根据面试会话查找评估结果
     */
    List<EvaluationResult> findByInterviewSession(InterviewSession interviewSession);
    
    /**
     * 根据面试会话和评估类型查找评估结果
     */
    Optional<EvaluationResult> findByInterviewSessionAndEvaluationType(InterviewSession interviewSession, String evaluationType);
    
    /**
     * 根据评估类型查找评估结果
     */
    List<EvaluationResult> findByEvaluationType(String evaluationType);
    
    /**
     * 根据分数范围查找评估结果
     */
    @Query("SELECT e FROM EvaluationResult e WHERE e.totalScore BETWEEN :minScore AND :maxScore")
    List<EvaluationResult> findByScoreRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
    
    /**
     * 根据时间范围查找评估结果
     */
    @Query("SELECT e FROM EvaluationResult e WHERE e.createdAt BETWEEN :startDate AND :endDate")
    List<EvaluationResult> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查找用户的所有评估结果
     */
    @Query("SELECT e FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId")
    List<EvaluationResult> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户最近的评估结果
     */
    @Query("SELECT e FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId ORDER BY e.createdAt DESC")
    List<EvaluationResult> findRecentByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户最佳评估结果
     */
    @Query("SELECT e FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId ORDER BY e.totalScore DESC")
    Optional<EvaluationResult> findBestByUserId(@Param("userId") Long userId);
    
    /**
     * 查找岗位的所有评估结果
     */
    @Query("SELECT e FROM EvaluationResult e JOIN e.interviewSession s WHERE s.position = :position")
    List<EvaluationResult> findByPosition(@Param("position") String position);
    
    /**
     * 查找岗位平均分数
     */
    @Query("SELECT AVG(e.totalScore) FROM EvaluationResult e JOIN e.interviewSession s WHERE s.position = :position")
    Double findAverageScoreByPosition(@Param("position") String position);
    
    /**
     * 查找用户平均分数
     */
    @Query("SELECT AVG(e.totalScore) FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户评估结果数量
     */
    @Query("SELECT COUNT(e) FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计岗位评估结果数量
     */
    @Query("SELECT COUNT(e) FROM EvaluationResult e JOIN e.interviewSession s WHERE s.position = :position")
    long countByPosition(@Param("position") String position);
    
    /**
     * 统计评估类型数量
     */
    @Query("SELECT COUNT(e) FROM EvaluationResult e WHERE e.evaluationType = :evaluationType")
    long countByEvaluationType(@Param("evaluationType") String evaluationType);
    
    /**
     * 查找高分评估结果（前10名）
     */
    @Query("SELECT e FROM EvaluationResult e ORDER BY e.totalScore DESC")
    List<EvaluationResult> findTopScoringResults(org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查找用户进步趋势
     */
    @Query("SELECT e.totalScore FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId ORDER BY e.createdAt ASC")
    List<Double> findScoreTrendByUserId(@Param("userId") Long userId);
    
    /**
     * 查找包含特定关键词的评估结果
     */
    @Query("SELECT e FROM EvaluationResult e WHERE e.overallFeedback LIKE %:keyword% OR e.keyIssues LIKE %:keyword% OR e.improvementSuggestions LIKE %:keyword%")
    List<EvaluationResult> findByKeyword(@Param("keyword") String keyword);
    
    /**
     * 查找多模态评估结果
     */
    @Query("SELECT e FROM EvaluationResult e WHERE e.facialAnalysis IS NOT NULL OR e.bodyLanguageAnalysis IS NOT NULL")
    List<EvaluationResult> findMultimodalResults();
    
    /**
     * 查找基础评估结果
     */
    @Query("SELECT e FROM EvaluationResult e WHERE e.facialAnalysis IS NULL AND e.bodyLanguageAnalysis IS NULL")
    List<EvaluationResult> findBasicResults();
    
    /**
     * 查找用户多模态评估结果
     */
    @Query("SELECT e FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId AND (e.facialAnalysis IS NOT NULL OR e.bodyLanguageAnalysis IS NOT NULL)")
    List<EvaluationResult> findMultimodalResultsByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户基础评估结果
     */
    @Query("SELECT e FROM EvaluationResult e JOIN e.interviewSession s WHERE s.user.id = :userId AND e.facialAnalysis IS NULL AND e.bodyLanguageAnalysis IS NULL")
    List<EvaluationResult> findBasicResultsByUserId(@Param("userId") Long userId);
} 