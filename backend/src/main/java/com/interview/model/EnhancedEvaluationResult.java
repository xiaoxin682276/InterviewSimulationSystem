package com.interview.model;

import java.util.List;
import java.util.Map;

public class EnhancedEvaluationResult {
    private double totalScore;
    private Map<String, Double> coreCompetencies; // 5项核心能力指标
    private Map<String, Double> detailedAnalysis; // 详细分析结果
    private List<String> keyIssues; // 关键问题定位
    private List<String> improvementSuggestions; // 改进建议
    private String overallFeedback;
    private Map<String, Object> multimodalInsights; // 多模态洞察
    private List<LearningPath> learningPaths; // 个性化学习路径
    private List<QuestionAnalysis> questionAnalyses;

    // 构造函数
    public EnhancedEvaluationResult() {}

    public EnhancedEvaluationResult(double totalScore, Map<String, Double> coreCompetencies,
                                  Map<String, Double> detailedAnalysis, List<String> keyIssues,
                                  List<String> improvementSuggestions, String overallFeedback) {
        this.totalScore = totalScore;
        this.coreCompetencies = coreCompetencies;
        this.detailedAnalysis = detailedAnalysis;
        this.keyIssues = keyIssues;
        this.improvementSuggestions = improvementSuggestions;
        this.overallFeedback = overallFeedback;
    }

    // Getter和Setter方法
    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public Map<String, Double> getCoreCompetencies() {
        return coreCompetencies;
    }

    public void setCoreCompetencies(Map<String, Double> coreCompetencies) {
        this.coreCompetencies = coreCompetencies;
    }

    public Map<String, Double> getDetailedAnalysis() {
        return detailedAnalysis;
    }

    public void setDetailedAnalysis(Map<String, Double> detailedAnalysis) {
        this.detailedAnalysis = detailedAnalysis;
    }

    public List<String> getKeyIssues() {
        return keyIssues;
    }

    public void setKeyIssues(List<String> keyIssues) {
        this.keyIssues = keyIssues;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public String getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
    }

    public Map<String, Object> getMultimodalInsights() {
        return multimodalInsights;
    }

    public void setMultimodalInsights(Map<String, Object> multimodalInsights) {
        this.multimodalInsights = multimodalInsights;
    }

    public List<LearningPath> getLearningPaths() {
        return learningPaths;
    }

    public void setLearningPaths(List<LearningPath> learningPaths) {
        this.learningPaths = learningPaths;
    }

    public List<QuestionAnalysis> getQuestionAnalyses() {
        return questionAnalyses;
    }
    public void setQuestionAnalyses(List<QuestionAnalysis> questionAnalyses) {
        this.questionAnalyses = questionAnalyses;
    }

    // 学习路径内部类
    public static class LearningPath {
        private String category;
        private String title;
        private String description;
        private String resourceUrl;
        private String resourceType; // "video", "course", "practice", "book"
        private int priority; // 优先级 1-5
        private double estimatedTime; // 预计学习时间（小时）

        public LearningPath() {}

        public LearningPath(String category, String title, String description, 
                          String resourceUrl, String resourceType, int priority, double estimatedTime) {
            this.category = category;
            this.title = title;
            this.description = description;
            this.resourceUrl = resourceUrl;
            this.resourceType = resourceType;
            this.priority = priority;
            this.estimatedTime = estimatedTime;
        }

        // Getter和Setter
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getResourceUrl() { return resourceUrl; }
        public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }

        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }

        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }

        public double getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(double estimatedTime) { this.estimatedTime = estimatedTime; }
    }

    // 每题分析内部类
    public static class QuestionAnalysis {
        private String question;
        private String userAnswer;
        private String analysis;
        private Double score;

        public QuestionAnalysis() {}

        public QuestionAnalysis(String question, String userAnswer, String analysis, Double score) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.analysis = analysis;
            this.score = score;
        }

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }

        public String getUserAnswer() { return userAnswer; }
        public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

        public String getAnalysis() { return analysis; }
        public void setAnalysis(String analysis) { this.analysis = analysis; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
    }

    // 核心能力指标常量
    public static final String PROFESSIONAL_KNOWLEDGE = "专业知识水平";
    public static final String SKILL_MATCH = "技能匹配度";
    public static final String LANGUAGE_EXPRESSION = "语言表达能力";
    public static final String LOGICAL_THINKING = "逻辑思维能力";
    public static final String INNOVATION_CAPABILITY = "创新能力";
    public static final String STRESS_RESISTANCE = "应变抗压能力";
} 