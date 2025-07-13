package com.interview.service;

import com.interview.entity.InterviewSession;
import com.interview.entity.User;
import com.interview.repository.InterviewSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InterviewSessionService {
    
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;
    
    /**
     * 创建新的面试会话
     */
    public InterviewSession createSession(User user, String position, String sessionType, String evaluationMode) {
        InterviewSession session = new InterviewSession(user, position, sessionType);
        session.setEvaluationMode(evaluationMode);
        session.setQuestionsCount(0);
        session.setAnswersCount(0);
        return interviewSessionRepository.save(session);
    }
    
    /**
     * 根据ID查找面试会话
     */
    public Optional<InterviewSession> findSessionById(Long id) {
        return interviewSessionRepository.findById(id);
    }
    
    /**
     * 查找用户的面试会话
     */
    public List<InterviewSession> findSessionsByUser(User user) {
        return interviewSessionRepository.findByUser(user);
    }
    
    /**
     * 查找用户最近的面试会话
     */
    public List<InterviewSession> findRecentSessionsByUser(User user) {
        return interviewSessionRepository.findRecentSessionsByUser(user);
    }
    
    /**
     * 查找用户完成的面试会话
     */
    public List<InterviewSession> findCompletedSessionsByUser(User user) {
        return interviewSessionRepository.findCompletedSessionsByUser(user);
    }
    
    /**
     * 查找用户进行中的面试会话
     */
    public Optional<InterviewSession> findActiveSessionByUser(User user) {
        return interviewSessionRepository.findActiveSessionByUser(user);
    }
    
    /**
     * 根据岗位查找面试会话
     */
    public List<InterviewSession> findSessionsByPosition(String position) {
        return interviewSessionRepository.findByPosition(position);
    }
    
    /**
     * 根据会话类型查找面试会话
     */
    public List<InterviewSession> findSessionsByType(String sessionType) {
        return interviewSessionRepository.findBySessionType(sessionType);
    }
    
    /**
     * 根据评估模式查找面试会话
     */
    public List<InterviewSession> findSessionsByEvaluationMode(String evaluationMode) {
        return interviewSessionRepository.findByEvaluationMode(evaluationMode);
    }
    
    /**
     * 根据状态查找面试会话
     */
    public List<InterviewSession> findSessionsByStatus(String status) {
        return interviewSessionRepository.findByStatus(status);
    }
    
    /**
     * 根据用户和岗位查找面试会话
     */
    public List<InterviewSession> findSessionsByUserAndPosition(User user, String position) {
        return interviewSessionRepository.findByUserAndPosition(user, position);
    }
    
    /**
     * 根据时间范围查找面试会话
     */
    public List<InterviewSession> findSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return interviewSessionRepository.findSessionsByDateRange(startDate, endDate);
    }
    
    /**
     * 根据分数范围查找面试会话
     */
    public List<InterviewSession> findSessionsByScoreRange(Double minScore, Double maxScore) {
        return interviewSessionRepository.findSessionsByScoreRange(minScore, maxScore);
    }
    
    /**
     * 完成面试会话
     */
    public InterviewSession completeSession(Long sessionId, Double totalScore) {
        Optional<InterviewSession> sessionOpt = interviewSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            InterviewSession session = sessionOpt.get();
            session.completeSession();
            session.setTotalScore(totalScore);
            return interviewSessionRepository.save(session);
        }
        throw new RuntimeException("面试会话不存在");
    }
    
    /**
     * 取消面试会话
     */
    public InterviewSession cancelSession(Long sessionId) {
        Optional<InterviewSession> sessionOpt = interviewSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            InterviewSession session = sessionOpt.get();
            session.cancelSession();
            return interviewSessionRepository.save(session);
        }
        throw new RuntimeException("面试会话不存在");
    }
    
    /**
     * 更新面试会话信息
     */
    public InterviewSession updateSession(Long sessionId, Integer questionsCount, Integer answersCount) {
        Optional<InterviewSession> sessionOpt = interviewSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            InterviewSession session = sessionOpt.get();
            session.setQuestionsCount(questionsCount);
            session.setAnswersCount(answersCount);
            session.setUpdatedAt(LocalDateTime.now());
            return interviewSessionRepository.save(session);
        }
        throw new RuntimeException("面试会话不存在");
    }
    
    /**
     * 统计用户面试会话总数
     */
    public long countSessionsByUser(User user) {
        return interviewSessionRepository.countSessionsByUser(user);
    }
    
    /**
     * 统计用户完成的面试会话数
     */
    public long countCompletedSessionsByUser(User user) {
        return interviewSessionRepository.countCompletedSessionsByUser(user);
    }
    
    /**
     * 统计岗位面试会话数
     */
    public long countSessionsByPosition(String position) {
        return interviewSessionRepository.countSessionsByPosition(position);
    }
    
    /**
     * 统计会话类型数量
     */
    public long countSessionsByType(String sessionType) {
        return interviewSessionRepository.countSessionsByType(sessionType);
    }
    
    /**
     * 查找平均分数最高的面试会话
     */
    public List<InterviewSession> findTopScoringSessions() {
        return interviewSessionRepository.findTopScoringSessions();
    }
    
    /**
     * 查找用户平均分数
     */
    public Double findAverageScoreByUser(User user) {
        return interviewSessionRepository.findAverageScoreByUser(user);
    }
    
    /**
     * 查找岗位平均分数
     */
    public Double findAverageScoreByPosition(String position) {
        return interviewSessionRepository.findAverageScoreByPosition(position);
    }
    
    /**
     * 查找用户最近的面试会话（限制数量）
     */
    public List<InterviewSession> findRecentSessionsByUserLimit(User user, int limit) {
        return interviewSessionRepository.findRecentSessionsByUserLimit(user, 
            org.springframework.data.domain.PageRequest.of(0, limit));
    }
    
    /**
     * 查找用户最佳成绩的面试会话
     */
    public Optional<InterviewSession> findBestSessionByUser(User user) {
        return interviewSessionRepository.findBestSessionByUser(user);
    }
    
    /**
     * 查找用户进步趋势
     */
    public List<Double> findScoreTrendByUser(User user) {
        return interviewSessionRepository.findScoreTrendByUser(user);
    }
    
    /**
     * 检查用户是否有进行中的面试会话
     */
    public boolean hasActiveSession(User user) {
        return interviewSessionRepository.findActiveSessionByUser(user).isPresent();
    }
    
    /**
     * 获取用户面试统计信息
     */
    public InterviewStatistics getInterviewStatistics(User user) {
        long totalSessions = countSessionsByUser(user);
        long completedSessions = countCompletedSessionsByUser(user);
        Double averageScore = findAverageScoreByUser(user);
        Optional<InterviewSession> bestSession = findBestSessionByUser(user);
        
        return new InterviewStatistics(
            totalSessions,
            completedSessions,
            averageScore != null ? averageScore : 0.0,
            bestSession.map(InterviewSession::getTotalScore).orElse(0.0)
        );
    }
    
    /**
     * 面试统计信息内部类
     */
    public static class InterviewStatistics {
        private final long totalSessions;
        private final long completedSessions;
        private final double averageScore;
        private final double bestScore;
        
        public InterviewStatistics(long totalSessions, long completedSessions, double averageScore, double bestScore) {
            this.totalSessions = totalSessions;
            this.completedSessions = completedSessions;
            this.averageScore = averageScore;
            this.bestScore = bestScore;
        }
        
        public long getTotalSessions() {
            return totalSessions;
        }
        
        public long getCompletedSessions() {
            return completedSessions;
        }
        
        public double getAverageScore() {
            return averageScore;
        }
        
        public double getBestScore() {
            return bestScore;
        }
    }
} 