package com.interview.repository;

import com.interview.entity.InterviewSession;
import com.interview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    
    /**
     * 根据用户查找面试会话
     */
    List<InterviewSession> findByUser(User user);
    
    /**
     * 根据用户和状态查找面试会话
     */
    List<InterviewSession> findByUserAndStatus(User user, String status);
    
    /**
     * 根据岗位查找面试会话
     */
    List<InterviewSession> findByPosition(String position);
    
    /**
     * 根据会话类型查找面试会话
     */
    List<InterviewSession> findBySessionType(String sessionType);
    
    /**
     * 根据评估模式查找面试会话
     */
    List<InterviewSession> findByEvaluationMode(String evaluationMode);
    
    /**
     * 根据状态查找面试会话
     */
    List<InterviewSession> findByStatus(String status);
    
    /**
     * 根据用户和岗位查找面试会话
     */
    List<InterviewSession> findByUserAndPosition(User user, String position);
    
    /**
     * 查找用户最近的面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user ORDER BY s.createdAt DESC")
    List<InterviewSession> findRecentSessionsByUser(@Param("user") User user);
    
    /**
     * 查找用户完成的面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user AND s.status = 'completed' ORDER BY s.completedAt DESC")
    List<InterviewSession> findCompletedSessionsByUser(@Param("user") User user);
    
    /**
     * 查找用户进行中的面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user AND s.status = 'in_progress'")
    Optional<InterviewSession> findActiveSessionByUser(@Param("user") User user);
    
    /**
     * 根据时间范围查找面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<InterviewSession> findSessionsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * 根据分数范围查找面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.totalScore BETWEEN :minScore AND :maxScore")
    List<InterviewSession> findSessionsByScoreRange(@Param("minScore") Double minScore, 
                                                   @Param("maxScore") Double maxScore);
    
    /**
     * 统计用户面试会话总数
     */
    @Query("SELECT COUNT(s) FROM InterviewSession s WHERE s.user = :user")
    long countSessionsByUser(@Param("user") User user);
    
    /**
     * 统计用户完成的面试会话数
     */
    @Query("SELECT COUNT(s) FROM InterviewSession s WHERE s.user = :user AND s.status = 'completed'")
    long countCompletedSessionsByUser(@Param("user") User user);
    
    /**
     * 统计岗位面试会话数
     */
    @Query("SELECT COUNT(s) FROM InterviewSession s WHERE s.position = :position")
    long countSessionsByPosition(@Param("position") String position);
    
    /**
     * 统计会话类型数量
     */
    @Query("SELECT COUNT(s) FROM InterviewSession s WHERE s.sessionType = :sessionType")
    long countSessionsByType(@Param("sessionType") String sessionType);
    
    /**
     * 查找平均分数最高的面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.status = 'completed' ORDER BY s.totalScore DESC")
    List<InterviewSession> findTopScoringSessions();
    
    /**
     * 查找用户平均分数
     */
    @Query("SELECT AVG(s.totalScore) FROM InterviewSession s WHERE s.user = :user AND s.status = 'completed'")
    Double findAverageScoreByUser(@Param("user") User user);
    
    /**
     * 查找岗位平均分数
     */
    @Query("SELECT AVG(s.totalScore) FROM InterviewSession s WHERE s.position = :position AND s.status = 'completed'")
    Double findAverageScoreByPosition(@Param("position") String position);
    
    /**
     * 查找用户最近的面试会话（限制数量）
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user ORDER BY s.createdAt DESC")
    List<InterviewSession> findRecentSessionsByUserLimit(@Param("user") User user, 
                                                        org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查找用户最佳成绩的面试会话
     */
    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user AND s.status = 'completed' ORDER BY s.totalScore DESC")
    Optional<InterviewSession> findBestSessionByUser(@Param("user") User user);
    
    /**
     * 查找用户进步趋势（按时间排序的分数）
     */
    @Query("SELECT s.totalScore FROM InterviewSession s WHERE s.user = :user AND s.status = 'completed' ORDER BY s.completedAt ASC")
    List<Double> findScoreTrendByUser(@Param("user") User user);
} 