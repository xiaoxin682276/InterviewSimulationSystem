package com.interview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interview_sessions")
public class InterviewSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "position", nullable = false)
    private String position;
    
    @Column(name = "session_type")
    private String sessionType; // basic, enhanced, multimodal
    
    @Column(name = "status")
    private String status; // in_progress, completed, cancelled
    
    @Column(name = "total_score")
    private Double totalScore;
    
    @Column(name = "evaluation_mode")
    private String evaluationMode; // basic, enhanced
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "questions_count")
    private Integer questionsCount;
    
    @Column(name = "answers_count")
    private Integer answersCount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联的面试问题
    @OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InterviewQuestion> questions;
    
    // 关联的评估结果
    @OneToMany(mappedBy = "interviewSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EvaluationResult> evaluationResults;
    
    // 构造函数
    public InterviewSession() {}
    
    public InterviewSession(User user, String position, String sessionType) {
        this.user = user;
        this.position = position;
        this.sessionType = sessionType;
        this.status = "in_progress";
        this.startedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getSessionType() {
        return sessionType;
    }
    
    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Double getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
    
    public String getEvaluationMode() {
        return evaluationMode;
    }
    
    public void setEvaluationMode(String evaluationMode) {
        this.evaluationMode = evaluationMode;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public Integer getQuestionsCount() {
        return questionsCount;
    }
    
    public void setQuestionsCount(Integer questionsCount) {
        this.questionsCount = questionsCount;
    }
    
    public Integer getAnswersCount() {
        return answersCount;
    }
    
    public void setAnswersCount(Integer answersCount) {
        this.answersCount = answersCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<InterviewQuestion> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<InterviewQuestion> questions) {
        this.questions = questions;
    }
    
    public List<EvaluationResult> getEvaluationResults() {
        return evaluationResults;
    }
    
    public void setEvaluationResults(List<EvaluationResult> evaluationResults) {
        this.evaluationResults = evaluationResults;
    }
    
    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 业务方法
    public void completeSession() {
        this.status = "completed";
        this.completedAt = LocalDateTime.now();
        if (this.startedAt != null) {
            this.durationMinutes = (int) java.time.Duration.between(this.startedAt, this.completedAt).toMinutes();
        }
    }
    
    public void cancelSession() {
        this.status = "cancelled";
        this.completedAt = LocalDateTime.now();
    }
} 