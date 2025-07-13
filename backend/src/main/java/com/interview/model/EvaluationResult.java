package com.interview.model;

import java.util.List;
import java.util.Map;

public class EvaluationResult {
    private double totalScore;
    private Map<String, Double> categoryScores;
    private List<String> recommendations;
    private String overallFeedback;

    // 构造函数
    public EvaluationResult() {}

    public EvaluationResult(double totalScore, Map<String, Double> categoryScores, 
                          List<String> recommendations, String overallFeedback) {
        this.totalScore = totalScore;
        this.categoryScores = categoryScores;
        this.recommendations = recommendations;
        this.overallFeedback = overallFeedback;
    }

    // Getter和Setter方法
    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public Map<String, Double> getCategoryScores() {
        return categoryScores;
    }

    public void setCategoryScores(Map<String, Double> categoryScores) {
        this.categoryScores = categoryScores;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
    }
} 