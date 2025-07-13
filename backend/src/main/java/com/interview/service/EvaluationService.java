package com.interview.service;

import com.interview.model.EvaluationResult;
import com.interview.model.InterviewRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvaluationService {
    
    // 不同岗位的评分权重
    private final Map<String, Map<String, Double>> positionWeights = new HashMap<>();
    
    public EvaluationService() {
        initializeWeights();
    }
    
    private void initializeWeights() {
        // 前端开发岗位权重
        Map<String, Double> frontendWeights = new HashMap<>();
        frontendWeights.put("技术能力", 0.4);
        frontendWeights.put("项目经验", 0.3);
        frontendWeights.put("沟通能力", 0.2);
        frontendWeights.put("学习能力", 0.1);
        positionWeights.put("前端开发", frontendWeights);
        
        // 后端开发岗位权重
        Map<String, Double> backendWeights = new HashMap<>();
        backendWeights.put("技术能力", 0.5);
        backendWeights.put("系统设计", 0.3);
        backendWeights.put("问题解决", 0.2);
        positionWeights.put("后端开发", backendWeights);
        
        // 全栈开发岗位权重
        Map<String, Double> fullstackWeights = new HashMap<>();
        fullstackWeights.put("技术能力", 0.4);
        fullstackWeights.put("系统设计", 0.3);
        fullstackWeights.put("项目经验", 0.2);
        fullstackWeights.put("沟通能力", 0.1);
        positionWeights.put("全栈开发", fullstackWeights);
    }
    
    public EvaluationResult evaluateInterview(InterviewRequest request) {
        String position = request.getPosition();
        List<String> answers = request.getAnswers();
        
        // 计算各维度分数
        Map<String, Double> categoryScores = calculateCategoryScores(answers, position);
        
        // 计算总分
        double totalScore = calculateTotalScore(categoryScores, position);
        
        // 生成建议
        List<String> recommendations = generateRecommendations(categoryScores, position);
        
        // 生成总体反馈
        String overallFeedback = generateOverallFeedback(totalScore, categoryScores);
        
        return new EvaluationResult(totalScore, categoryScores, recommendations, overallFeedback);
    }
    
    private Map<String, Double> calculateCategoryScores(List<String> answers, String position) {
        Map<String, Double> scores = new HashMap<>();
        
        // 这里实现具体的评分逻辑
        // 可以根据答案内容、长度、关键词等进行评分
        for (int i = 0; i < answers.size(); i++) {
            String answer = answers.get(i);
            double score = evaluateAnswer(answer, i, position);
            
            // 根据问题类型分配分数到不同维度
            if (i < 3) {
                scores.put("技术能力", scores.getOrDefault("技术能力", 0.0) + score);
            } else if (i < 5) {
                scores.put("项目经验", scores.getOrDefault("项目经验", 0.0) + score);
            } else if (i < 7) {
                scores.put("沟通能力", scores.getOrDefault("沟通能力", 0.0) + score);
            } else {
                scores.put("学习能力", scores.getOrDefault("学习能力", 0.0) + score);
            }
        }
        
        // 标准化分数到0-100
        for (String category : scores.keySet()) {
            scores.put(category, Math.min(100.0, scores.get(category) * 20));
        }
        
        return scores;
    }
    
    private double evaluateAnswer(String answer, int questionIndex, String position) {
        // 简单的评分逻辑，可以根据需要优化
        double score = 0.0;
        
        if (answer != null && !answer.trim().isEmpty()) {
            // 基础分数
            score += 2.0;
            
            // 根据答案长度加分
            if (answer.length() > 50) score += 1.0;
            if (answer.length() > 100) score += 1.0;
            
            // 根据关键词加分
            if (answer.toLowerCase().contains("项目") || answer.toLowerCase().contains("经验")) {
                score += 1.0;
            }
            if (answer.toLowerCase().contains("技术") || answer.toLowerCase().contains("技能")) {
                score += 1.0;
            }
            if (answer.toLowerCase().contains("学习") || answer.toLowerCase().contains("提升")) {
                score += 1.0;
            }
        }
        
        return Math.min(5.0, score);
    }
    
    private double calculateTotalScore(Map<String, Double> categoryScores, String position) {
        Map<String, Double> weights = positionWeights.getOrDefault(position, positionWeights.get("前端开发"));
        double totalScore = 0.0;
        
        for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            Double score = entry.getValue();
            Double weight = weights.getOrDefault(category, 0.25);
            totalScore += score * weight;
        }
        
        return Math.round(totalScore * 100.0) / 100.0;
    }
    
    private List<String> generateRecommendations(Map<String, Double> categoryScores, String position) {
        List<String> recommendations = new ArrayList<>();
        
        for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            Double score = entry.getValue();
            
            if (score < 60) {
                switch (category) {
                    case "技术能力":
                        recommendations.add("建议加强技术基础知识的学习，可以参加相关技术培训");
                        break;
                    case "项目经验":
                        recommendations.add("建议多参与实际项目，积累项目经验");
                        break;
                    case "沟通能力":
                        recommendations.add("建议提升沟通表达能力，可以参加演讲培训");
                        break;
                    case "学习能力":
                        recommendations.add("建议培养持续学习的习惯，关注行业动态");
                        break;
                }
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("您的表现很好，继续保持！");
        }
        
        return recommendations;
    }
    
    private String generateOverallFeedback(double totalScore, Map<String, Double> categoryScores) {
        if (totalScore >= 80) {
            return "优秀！您展现了很强的综合能力，非常适合该岗位。";
        } else if (totalScore >= 60) {
            return "良好！您具备该岗位的基本要求，建议在某些方面继续提升。";
        } else {
            return "需要提升！建议在多个方面加强学习和实践。";
        }
    }
} 