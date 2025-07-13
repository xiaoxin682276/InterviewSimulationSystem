package com.interview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "evaluation_results")
public class EvaluationResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession interviewSession;
    
    @Column(name = "evaluation_type")
    private String evaluationType; // basic, enhanced, multimodal
    
    @Column(name = "total_score")
    private Double totalScore;
    
    @Column(name = "overall_feedback", columnDefinition = "TEXT")
    private String overallFeedback;
    
    @Column(name = "key_issues", columnDefinition = "TEXT")
    private String keyIssues; // JSON格式存储关键问题
    
    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions; // JSON格式存储改进建议
    
    @Column(name = "competency_scores", columnDefinition = "TEXT")
    private String competencyScores; // JSON格式存储能力评分
    
    @Column(name = "facial_analysis", columnDefinition = "TEXT")
    private String facialAnalysis; // JSON格式存储面部分析结果
    
    @Column(name = "body_language_analysis", columnDefinition = "TEXT")
    private String bodyLanguageAnalysis; // JSON格式存储肢体语言分析结果
    
    @Column(name = "learning_paths", columnDefinition = "TEXT")
    private String learningPaths; // JSON格式存储学习路径
    
    @Column(name = "evaluation_duration_seconds")
    private Integer evaluationDurationSeconds;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public EvaluationResult() {}
    
    public EvaluationResult(InterviewSession interviewSession, String evaluationType) {
        this.interviewSession = interviewSession;
        this.evaluationType = evaluationType;
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
    
    public InterviewSession getInterviewSession() {
        return interviewSession;
    }
    
    public void setInterviewSession(InterviewSession interviewSession) {
        this.interviewSession = interviewSession;
    }
    
    public String getEvaluationType() {
        return evaluationType;
    }
    
    public void setEvaluationType(String evaluationType) {
        this.evaluationType = evaluationType;
    }
    
    public Double getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
    
    public String getOverallFeedback() {
        return overallFeedback;
    }
    
    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
    }
    
    public String getKeyIssues() {
        return keyIssues;
    }
    
    public void setKeyIssues(String keyIssues) {
        this.keyIssues = keyIssues;
    }
    
    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }
    
    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }
    
    public String getCompetencyScores() {
        return competencyScores;
    }
    
    public void setCompetencyScores(String competencyScores) {
        this.competencyScores = competencyScores;
    }
    
    public String getFacialAnalysis() {
        return facialAnalysis;
    }
    
    public void setFacialAnalysis(String facialAnalysis) {
        this.facialAnalysis = facialAnalysis;
    }
    
    public String getBodyLanguageAnalysis() {
        return bodyLanguageAnalysis;
    }
    
    public void setBodyLanguageAnalysis(String bodyLanguageAnalysis) {
        this.bodyLanguageAnalysis = bodyLanguageAnalysis;
    }
    
    public String getLearningPaths() {
        return learningPaths;
    }
    
    public void setLearningPaths(String learningPaths) {
        this.learningPaths = learningPaths;
    }
    
    public Integer getEvaluationDurationSeconds() {
        return evaluationDurationSeconds;
    }
    
    public void setEvaluationDurationSeconds(Integer evaluationDurationSeconds) {
        this.evaluationDurationSeconds = evaluationDurationSeconds;
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
} 