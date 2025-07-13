package com.interview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession interviewSession;
    
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Column(name = "question_category")
    private String questionCategory; // 技术能力, 项目经验, 沟通能力, 学习能力
    
    @Column(name = "question_type")
    private String questionType; // text, audio, video
    
    @Column(name = "question_order")
    private Integer questionOrder;
    
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;
    
    @Column(name = "audio_url")
    private String audioUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "answer_duration_seconds")
    private Integer answerDurationSeconds;
    
    @Column(name = "score")
    private Double score;
    
    @Column(name = "feedback")
    private String feedback;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public InterviewQuestion() {}
    
    public InterviewQuestion(InterviewSession interviewSession, String questionText, String questionCategory, Integer questionOrder) {
        this.interviewSession = interviewSession;
        this.questionText = questionText;
        this.questionCategory = questionCategory;
        this.questionOrder = questionOrder;
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
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getQuestionCategory() {
        return questionCategory;
    }
    
    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public Integer getQuestionOrder() {
        return questionOrder;
    }
    
    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }
    
    public String getAnswerText() {
        return answerText;
    }
    
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    
    public String getAudioUrl() {
        return audioUrl;
    }
    
    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public Integer getAnswerDurationSeconds() {
        return answerDurationSeconds;
    }
    
    public void setAnswerDurationSeconds(Integer answerDurationSeconds) {
        this.answerDurationSeconds = answerDurationSeconds;
    }
    
    public Double getScore() {
        return score;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
    
    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
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
    
    // 业务方法
    public void answerQuestion(String answerText) {
        this.answerText = answerText;
        this.answeredAt = LocalDateTime.now();
    }
    
    public void answerWithAudio(String audioUrl, Integer durationSeconds) {
        this.audioUrl = audioUrl;
        this.answerDurationSeconds = durationSeconds;
        this.answeredAt = LocalDateTime.now();
    }
    
    public void answerWithVideo(String videoUrl, Integer durationSeconds) {
        this.videoUrl = videoUrl;
        this.answerDurationSeconds = durationSeconds;
        this.answeredAt = LocalDateTime.now();
    }
} 