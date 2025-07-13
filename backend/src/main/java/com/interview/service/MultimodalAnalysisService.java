package com.interview.service;

import com.interview.model.MultiModalData;
import com.interview.model.EnhancedEvaluationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MultimodalAnalysisService {
    
    @Autowired
    private XunfeiSparkService sparkService;
    
    @Autowired
    private FacialEmotionService facialEmotionService;
    
    @Autowired
    private BodyLanguageService bodyLanguageService;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(6);
    
    /**
     * 执行多模态分析
     */
    public EnhancedEvaluationResult analyzeMultimodalData(MultiModalData multimodalData) {
        try {
            // 并行处理不同类型的分析
            CompletableFuture<Map<String, Double>> textAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeTextData(multimodalData.getTextData()), executorService);
            
            CompletableFuture<Map<String, Double>> audioAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeAudioData(multimodalData.getAudioData()), executorService);
            
            CompletableFuture<Map<String, Double>> videoAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeVideoData(multimodalData.getVideoData()), executorService);
            
            // 等待所有分析完成
            CompletableFuture.allOf(textAnalysis, audioAnalysis, videoAnalysis).join();
            
            // 整合分析结果
            Map<String, Double> textResults = textAnalysis.get();
            Map<String, Double> audioResults = audioAnalysis.get();
            Map<String, Double> videoResults = videoAnalysis.get();
            
            // 综合评估
            return generateComprehensiveEvaluation(multimodalData.getPosition(), 
                                                textResults, audioResults, videoResults);
            
        } catch (Exception e) {
            System.err.println("多模态分析失败: " + e.getMessage());
            return generateDefaultEvaluation();
        }
    }
    
    /**
     * 执行增强的多模态分析（包含面部情感和肢体语言）
     */
    public EnhancedEvaluationResult analyzeEnhancedMultimodalData(MultiModalData multimodalData, 
                                                                MultipartFile videoFile) {
        try {
            // 并行处理所有类型的分析
            CompletableFuture<Map<String, Double>> textAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeTextData(multimodalData.getTextData()), executorService);
            
            CompletableFuture<Map<String, Double>> audioAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeAudioData(multimodalData.getAudioData()), executorService);
            
            CompletableFuture<Map<String, Double>> videoAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeVideoData(multimodalData.getVideoData()), executorService);
            
            CompletableFuture<Map<String, Object>> facialAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeFacialData(videoFile), executorService);
            
            CompletableFuture<Map<String, Object>> bodyLanguageAnalysis = 
                CompletableFuture.supplyAsync(() -> analyzeBodyLanguageData(videoFile), executorService);
            
            // 等待所有分析完成
            CompletableFuture.allOf(textAnalysis, audioAnalysis, videoAnalysis, 
                                  facialAnalysis, bodyLanguageAnalysis).join();
            
            // 整合分析结果
            Map<String, Double> textResults = textAnalysis.get();
            Map<String, Double> audioResults = audioAnalysis.get();
            Map<String, Double> videoResults = videoAnalysis.get();
            Map<String, Object> facialResults = facialAnalysis.get();
            Map<String, Object> bodyLanguageResults = bodyLanguageAnalysis.get();
            
            // 综合评估
            return generateEnhancedComprehensiveEvaluation(multimodalData.getPosition(), 
                                                        textResults, audioResults, videoResults,
                                                        facialResults, bodyLanguageResults);
            
        } catch (Exception e) {
            System.err.println("增强多模态分析失败: " + e.getMessage());
            return generateDefaultEvaluation();
        }
    }
    
    /**
     * 分析文本数据
     */
    private Map<String, Double> analyzeTextData(List<MultiModalData.TextData> textDataList) {
        Map<String, Double> analysis = new HashMap<>();
        
        if (textDataList == null || textDataList.isEmpty()) {
            return getDefaultTextAnalysis();
        }
        
        // 分析专业知识水平
        double professionalKnowledge = analyzeProfessionalKnowledge(textDataList);
        analysis.put(EnhancedEvaluationResult.PROFESSIONAL_KNOWLEDGE, professionalKnowledge);
        
        // 分析技能匹配度
        double skillMatch = analyzeSkillMatch(textDataList);
        analysis.put(EnhancedEvaluationResult.SKILL_MATCH, skillMatch);
        
        // 分析逻辑思维能力
        double logicalThinking = analyzeLogicalThinking(textDataList);
        analysis.put(EnhancedEvaluationResult.LOGICAL_THINKING, logicalThinking);
        
        // 分析创新能力
        double innovationCapability = analyzeInnovationCapability(textDataList);
        analysis.put(EnhancedEvaluationResult.INNOVATION_CAPABILITY, innovationCapability);
        
        return analysis;
    }
    
    /**
     * 分析音频数据
     */
    private Map<String, Double> analyzeAudioData(List<MultiModalData.AudioData> audioDataList) {
        Map<String, Double> analysis = new HashMap<>();
        
        if (audioDataList == null || audioDataList.isEmpty()) {
            return getDefaultAudioAnalysis();
        }
        
        // 分析语言表达能力
        double languageExpression = analyzeLanguageExpression(audioDataList);
        analysis.put(EnhancedEvaluationResult.LANGUAGE_EXPRESSION, languageExpression);
        
        // 分析应变抗压能力
        double stressResistance = analyzeStressResistance(audioDataList);
        analysis.put(EnhancedEvaluationResult.STRESS_RESISTANCE, stressResistance);
        
        return analysis;
    }
    
    /**
     * 分析视频数据
     */
    private Map<String, Double> analyzeVideoData(List<MultiModalData.VideoData> videoDataList) {
        Map<String, Double> analysis = new HashMap<>();
        
        if (videoDataList == null || videoDataList.isEmpty()) {
            return getDefaultVideoAnalysis();
        }
        
        // 分析非语言表达能力
        double nonverbalExpression = analyzeNonverbalExpression(videoDataList);
        analysis.put("非语言表达能力", nonverbalExpression);
        
        // 分析自信度
        double confidence = analyzeConfidence(videoDataList);
        analysis.put("自信度", confidence);
        
        return analysis;
    }
    
    /**
     * 生成综合评估结果
     */
    private EnhancedEvaluationResult generateComprehensiveEvaluation(String position,
                                                                  Map<String, Double> textResults,
                                                                  Map<String, Double> audioResults,
                                                                  Map<String, Double> videoResults) {
        
        // 整合所有能力指标
        Map<String, Double> coreCompetencies = new HashMap<>();
        coreCompetencies.putAll(textResults);
        coreCompetencies.putAll(audioResults);
        
        // 计算总分
        double totalScore = calculateTotalScore(coreCompetencies);
        
        // 生成关键问题
        List<String> keyIssues = identifyKeyIssues(coreCompetencies, videoResults);
        
        // 生成改进建议
        List<String> improvementSuggestions = generateImprovementSuggestions(coreCompetencies, keyIssues);
        
        // 生成总体反馈
        String overallFeedback = generateOverallFeedback(totalScore, coreCompetencies);
        
        // 生成学习路径
        List<EnhancedEvaluationResult.LearningPath> learningPaths = generateLearningPaths(position, coreCompetencies);
        
        // 构建多模态洞察
        Map<String, Object> multimodalInsights = buildMultimodalInsights(textResults, audioResults, videoResults);
        
        return new EnhancedEvaluationResult(totalScore, coreCompetencies, 
                                         new HashMap<>(), keyIssues, 
                                         improvementSuggestions, overallFeedback);
    }
    
    // 具体的分析方法实现
    private double analyzeProfessionalKnowledge(List<MultiModalData.TextData> textDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.TextData data : textDataList) {
            String answer = data.getAnswer();
            if (answer != null && !answer.trim().isEmpty()) {
                // 基于关键词和技术术语的评分
                score += evaluateTechnicalDepth(answer);
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count * 20) : 70.0;
    }
    
    private double analyzeSkillMatch(List<MultiModalData.TextData> textDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.TextData data : textDataList) {
            String answer = data.getAnswer();
            if (answer != null && !answer.trim().isEmpty()) {
                // 基于技能匹配度的评分
                score += evaluateSkillRelevance(answer);
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count * 20) : 75.0;
    }
    
    private double analyzeLogicalThinking(List<MultiModalData.TextData> textDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.TextData data : textDataList) {
            String answer = data.getAnswer();
            if (answer != null && !answer.trim().isEmpty()) {
                // 基于逻辑结构的评分
                score += evaluateLogicalStructure(answer);
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count * 20) : 80.0;
    }
    
    private double analyzeInnovationCapability(List<MultiModalData.TextData> textDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.TextData data : textDataList) {
            String answer = data.getAnswer();
            if (answer != null && !answer.trim().isEmpty()) {
                // 基于创新思维的评分
                score += evaluateInnovationThinking(answer);
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count * 20) : 65.0;
    }
    
    private double analyzeLanguageExpression(List<MultiModalData.AudioData> audioDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.AudioData data : audioDataList) {
            if (data.getEmotionAnalysis() != null) {
                // 基于情感分析的评分
                score += evaluateEmotionalExpression(data.getEmotionAnalysis());
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count) : 70.0;
    }
    
    private double analyzeStressResistance(List<MultiModalData.AudioData> audioDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.AudioData data : audioDataList) {
            if (data.getLanguageLogic() != null) {
                // 基于语言逻辑的评分
                score += evaluateStressResponse(data.getLanguageLogic());
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count) : 75.0;
    }
    
    private double analyzeNonverbalExpression(List<MultiModalData.VideoData> videoDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.VideoData data : videoDataList) {
            if (data.getBodyLanguage() != null) {
                // 基于肢体语言的评分
                score += evaluateBodyLanguage(data.getBodyLanguage());
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count) : 70.0;
    }
    
    private double analyzeConfidence(List<MultiModalData.VideoData> videoDataList) {
        double score = 0.0;
        int count = 0;
        
        for (MultiModalData.VideoData data : videoDataList) {
            if (data.getEyeContact() != null) {
                // 基于眼神交流的评分
                score += evaluateEyeContact(data.getEyeContact());
                count++;
            }
        }
        
        return count > 0 ? Math.min(100.0, score / count) : 75.0;
    }
    
    // 辅助评估方法
    private double evaluateTechnicalDepth(String answer) {
        double score = 0.0;
        String lowerAnswer = answer.toLowerCase();
        
        // 技术术语加分
        if (lowerAnswer.contains("架构") || lowerAnswer.contains("设计模式")) score += 2.0;
        if (lowerAnswer.contains("算法") || lowerAnswer.contains("数据结构")) score += 2.0;
        if (lowerAnswer.contains("框架") || lowerAnswer.contains("库")) score += 1.5;
        if (lowerAnswer.contains("性能") || lowerAnswer.contains("优化")) score += 1.5;
        
        // 项目经验加分
        if (lowerAnswer.contains("项目") || lowerAnswer.contains("经验")) score += 1.0;
        if (lowerAnswer.contains("解决") || lowerAnswer.contains("问题")) score += 1.0;
        
        return Math.min(5.0, score);
    }
    
    private double evaluateSkillRelevance(String answer) {
        double score = 0.0;
        String lowerAnswer = answer.toLowerCase();
        
        // 技能匹配度评分
        if (lowerAnswer.contains("相关") || lowerAnswer.contains("匹配")) score += 2.0;
        if (lowerAnswer.contains("技能") || lowerAnswer.contains("能力")) score += 1.5;
        if (lowerAnswer.contains("学习") || lowerAnswer.contains("提升")) score += 1.0;
        
        return Math.min(5.0, score);
    }
    
    private double evaluateLogicalStructure(String answer) {
        double score = 0.0;
        String lowerAnswer = answer.toLowerCase();
        
        // 逻辑结构评分
        if (lowerAnswer.contains("首先") || lowerAnswer.contains("然后")) score += 2.0;
        if (lowerAnswer.contains("因为") || lowerAnswer.contains("所以")) score += 1.5;
        if (lowerAnswer.contains("步骤") || lowerAnswer.contains("流程")) score += 1.5;
        if (lowerAnswer.contains("分析") || lowerAnswer.contains("思考")) score += 1.0;
        
        return Math.min(5.0, score);
    }
    
    private double evaluateInnovationThinking(String answer) {
        double score = 0.0;
        String lowerAnswer = answer.toLowerCase();
        
        // 创新思维评分
        if (lowerAnswer.contains("创新") || lowerAnswer.contains("改进")) score += 2.0;
        if (lowerAnswer.contains("新方法") || lowerAnswer.contains("新思路")) score += 2.0;
        if (lowerAnswer.contains("优化") || lowerAnswer.contains("提升")) score += 1.5;
        
        return Math.min(5.0, score);
    }
    
    private double evaluateEmotionalExpression(Map<String, Double> emotionAnalysis) {
        // 基于情感分析结果评分
        double confidence = emotionAnalysis.getOrDefault("confidence", 0.0);
        double enthusiasm = emotionAnalysis.getOrDefault("enthusiasm", 0.0);
        return (confidence + enthusiasm) * 50; // 转换为0-100分
    }
    
    private double evaluateStressResponse(Map<String, Double> languageLogic) {
        // 基于语言逻辑分析评分
        double fluency = languageLogic.getOrDefault("fluency", 0.0);
        double coherence = languageLogic.getOrDefault("coherence", 0.0);
        return (fluency + coherence) * 50;
    }
    
    private double evaluateBodyLanguage(Map<String, Double> bodyLanguage) {
        // 基于肢体语言分析评分
        double posture = bodyLanguage.getOrDefault("posture", 0.0);
        double gesture = bodyLanguage.getOrDefault("gesture", 0.0);
        return (posture + gesture) * 50;
    }
    
    private double evaluateEyeContact(Map<String, Double> eyeContact) {
        // 基于眼神交流分析评分
        double duration = eyeContact.getOrDefault("duration", 0.0);
        double frequency = eyeContact.getOrDefault("frequency", 0.0);
        return (duration + frequency) * 50;
    }
    
    /**
     * 分析面部数据
     */
    private Map<String, Object> analyzeFacialData(MultipartFile videoFile) {
        try {
            Map<String, Object> facialResults = new HashMap<>();
            
            // 分析面部情感
            Map<String, Object> emotionAnalysis = facialEmotionService.analyzeFacialEmotion(videoFile);
            facialResults.put("emotion", emotionAnalysis);
            
            // 分析面部表情
            Map<String, Object> expressionAnalysis = facialEmotionService.analyzeFacialExpressions(videoFile);
            facialResults.put("expression", expressionAnalysis);
            
            // 分析眼神交流
            Map<String, Object> eyeContactAnalysis = facialEmotionService.analyzeEyeContact(videoFile);
            facialResults.put("eyeContact", eyeContactAnalysis);
            
            return facialResults;
            
        } catch (Exception e) {
            System.err.println("面部数据分析失败: " + e.getMessage());
            return getDefaultFacialAnalysis();
        }
    }
    
    /**
     * 分析肢体语言数据
     */
    private Map<String, Object> analyzeBodyLanguageData(MultipartFile videoFile) {
        try {
            Map<String, Object> bodyLanguageResults = new HashMap<>();
            
            // 分析肢体语言
            Map<String, Object> bodyLanguageAnalysis = bodyLanguageService.analyzeBodyLanguage(videoFile);
            bodyLanguageResults.put("bodyLanguage", bodyLanguageAnalysis);
            
            // 分析姿态
            Map<String, Object> postureAnalysis = bodyLanguageService.analyzePosture(videoFile);
            bodyLanguageResults.put("posture", postureAnalysis);
            
            // 分析手势
            Map<String, Object> gestureAnalysis = bodyLanguageService.analyzeGestures(videoFile);
            bodyLanguageResults.put("gesture", gestureAnalysis);
            
            // 分析身体动作
            Map<String, Object> movementAnalysis = bodyLanguageService.analyzeBodyMovements(videoFile);
            bodyLanguageResults.put("movement", movementAnalysis);
            
            return bodyLanguageResults;
            
        } catch (Exception e) {
            System.err.println("肢体语言数据分析失败: " + e.getMessage());
            return getDefaultBodyLanguageAnalysis();
        }
    }
    
    // 其他辅助方法
    private Map<String, Double> getDefaultTextAnalysis() {
        Map<String, Double> analysis = new HashMap<>();
        analysis.put(EnhancedEvaluationResult.PROFESSIONAL_KNOWLEDGE, 70.0);
        analysis.put(EnhancedEvaluationResult.SKILL_MATCH, 75.0);
        analysis.put(EnhancedEvaluationResult.LOGICAL_THINKING, 80.0);
        analysis.put(EnhancedEvaluationResult.INNOVATION_CAPABILITY, 65.0);
        return analysis;
    }
    
    private Map<String, Double> getDefaultAudioAnalysis() {
        Map<String, Double> analysis = new HashMap<>();
        analysis.put(EnhancedEvaluationResult.LANGUAGE_EXPRESSION, 70.0);
        analysis.put(EnhancedEvaluationResult.STRESS_RESISTANCE, 75.0);
        return analysis;
    }
    
    private Map<String, Double> getDefaultVideoAnalysis() {
        Map<String, Double> analysis = new HashMap<>();
        analysis.put("非语言表达能力", 70.0);
        analysis.put("自信度", 75.0);
        return analysis;
    }
    
    private double calculateTotalScore(Map<String, Double> competencies) {
        return competencies.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(70.0);
    }
    
    private List<String> identifyKeyIssues(Map<String, Double> competencies, Map<String, Double> videoResults) {
        List<String> issues = new ArrayList<>();
        
        if (competencies.get(EnhancedEvaluationResult.LANGUAGE_EXPRESSION) < 70) {
            issues.add("语言表达不够清晰流畅");
        }
        if (competencies.get(EnhancedEvaluationResult.LOGICAL_THINKING) < 75) {
            issues.add("回答缺乏逻辑结构");
        }
        if (videoResults != null && videoResults.get("自信度") < 70) {
            issues.add("面试时缺乏自信表现");
        }
        
        return issues;
    }
    
    private List<String> generateImprovementSuggestions(Map<String, Double> competencies, List<String> keyIssues) {
        List<String> suggestions = new ArrayList<>();
        
        for (String issue : keyIssues) {
            if (issue.contains("语言表达")) {
                suggestions.add("建议参加演讲培训，提升表达能力");
            }
            if (issue.contains("逻辑结构")) {
                suggestions.add("建议使用STAR方法组织回答");
            }
            if (issue.contains("自信")) {
                suggestions.add("建议进行模拟面试训练，增强自信");
            }
        }
        
        return suggestions;
    }
    
    private String generateOverallFeedback(double totalScore, Map<String, Double> competencies) {
        if (totalScore >= 80) {
            return "整体表现优秀，各项能力均衡发展，建议继续保持并进一步提升专业技能。";
        } else if (totalScore >= 70) {
            return "整体表现良好，具备该岗位的基本要求，建议在薄弱环节加强训练。";
        } else {
            return "需要系统性地提升相关技能，建议制定详细的学习计划。";
        }
    }
    
    private List<EnhancedEvaluationResult.LearningPath> generateLearningPaths(String position, Map<String, Double> competencies) {
        List<EnhancedEvaluationResult.LearningPath> paths = new ArrayList<>();
        
        // 根据岗位和能力评分生成个性化学习路径
        if (competencies.get(EnhancedEvaluationResult.LANGUAGE_EXPRESSION) < 75) {
            paths.add(new EnhancedEvaluationResult.LearningPath(
                "表达能力", "面试表达技巧训练", "提升面试时的语言表达能力",
                "https://example.com/expression-course", "course", 1, 8.0
            ));
        }
        
        if (competencies.get(EnhancedEvaluationResult.PROFESSIONAL_KNOWLEDGE) < 75) {
            paths.add(new EnhancedEvaluationResult.LearningPath(
                "专业技能", position + "技能提升课程", "深入学习相关技术栈",
                "https://example.com/skill-course", "course", 1, 20.0
            ));
        }
        
        return paths;
    }
    
    private Map<String, Object> buildMultimodalInsights(Map<String, Double> textResults, 
                                                       Map<String, Double> audioResults, 
                                                       Map<String, Double> videoResults) {
        Map<String, Object> insights = new HashMap<>();
        insights.put("textAnalysis", textResults);
        insights.put("audioAnalysis", audioResults);
        insights.put("videoAnalysis", videoResults);
        return insights;
    }
    
    private EnhancedEvaluationResult generateDefaultEvaluation() {
        Map<String, Double> defaultCompetencies = new HashMap<>();
        defaultCompetencies.put(EnhancedEvaluationResult.PROFESSIONAL_KNOWLEDGE, 70.0);
        defaultCompetencies.put(EnhancedEvaluationResult.SKILL_MATCH, 75.0);
        defaultCompetencies.put(EnhancedEvaluationResult.LANGUAGE_EXPRESSION, 70.0);
        defaultCompetencies.put(EnhancedEvaluationResult.LOGICAL_THINKING, 80.0);
        defaultCompetencies.put(EnhancedEvaluationResult.INNOVATION_CAPABILITY, 65.0);
        defaultCompetencies.put(EnhancedEvaluationResult.STRESS_RESISTANCE, 75.0);
        
        return new EnhancedEvaluationResult(72.5, defaultCompetencies, 
                                         new HashMap<>(), 
                                         Arrays.asList("需要更多数据进行分析"), 
                                         Arrays.asList("建议提供更完整的面试数据"), 
                                         "数据不足，无法进行完整分析");
    }
    
    /**
     * 生成增强的综合评估结果
     */
    private EnhancedEvaluationResult generateEnhancedComprehensiveEvaluation(String position,
                                                                          Map<String, Double> textResults,
                                                                          Map<String, Double> audioResults,
                                                                          Map<String, Double> videoResults,
                                                                          Map<String, Object> facialResults,
                                                                          Map<String, Object> bodyLanguageResults) {
        
        // 整合所有能力指标
        Map<String, Double> coreCompetencies = new HashMap<>();
        coreCompetencies.putAll(textResults);
        coreCompetencies.putAll(audioResults);
        
        // 添加面部情感分析结果
        if (facialResults != null) {
            Map<String, Object> emotionData = (Map<String, Object>) facialResults.get("emotion");
            if (emotionData != null) {
                Map<String, Double> emotionScores = (Map<String, Double>) emotionData.get("emotionScores");
                if (emotionScores != null) {
                    coreCompetencies.put("自信度", emotionScores.get("confidence") * 100);
                    coreCompetencies.put("热情度", emotionScores.get("enthusiasm") * 100);
                    coreCompetencies.put("参与度", emotionScores.get("engagement") * 100);
                }
            }
            
            Map<String, Object> eyeContactData = (Map<String, Object>) facialResults.get("eyeContact");
            if (eyeContactData != null) {
                Map<String, Double> eyeContactScores = (Map<String, Double>) eyeContactData.get("eyeContactScores");
                if (eyeContactScores != null) {
                    coreCompetencies.put("眼神交流", (eyeContactScores.get("duration") + eyeContactScores.get("frequency")) * 50);
                }
            }
        }
        
        // 添加肢体语言分析结果
        if (bodyLanguageResults != null) {
            Map<String, Object> bodyLanguageData = (Map<String, Object>) bodyLanguageResults.get("bodyLanguage");
            if (bodyLanguageData != null) {
                Map<String, Double> bodyLanguageScores = (Map<String, Double>) bodyLanguageData.get("bodyLanguageScores");
                if (bodyLanguageScores != null) {
                    coreCompetencies.put("肢体开放度", bodyLanguageScores.get("openness") * 100);
                    coreCompetencies.put("肢体自信度", bodyLanguageScores.get("confidence") * 100);
                    coreCompetencies.put("肢体参与度", bodyLanguageScores.get("engagement") * 100);
                    coreCompetencies.put("专业度", bodyLanguageScores.get("professionalism") * 100);
                }
            }
        }
        
        // 计算总分
        double totalScore = calculateTotalScore(coreCompetencies);
        
        // 生成关键问题
        List<String> keyIssues = identifyEnhancedKeyIssues(coreCompetencies, facialResults, bodyLanguageResults);
        
        // 生成改进建议
        List<String> improvementSuggestions = generateEnhancedImprovementSuggestions(coreCompetencies, keyIssues);
        
        // 生成总体反馈
        String overallFeedback = generateEnhancedOverallFeedback(totalScore, coreCompetencies, facialResults, bodyLanguageResults);
        
        // 生成学习路径
        List<EnhancedEvaluationResult.LearningPath> learningPaths = generateEnhancedLearningPaths(position, coreCompetencies);
        
        return new EnhancedEvaluationResult(totalScore, coreCompetencies, 
                                         new HashMap<>(), keyIssues, 
                                         improvementSuggestions, overallFeedback);
    }
    
    /**
     * 识别增强的关键问题
     */
    private List<String> identifyEnhancedKeyIssues(Map<String, Double> competencies, 
                                                 Map<String, Object> facialResults, 
                                                 Map<String, Object> bodyLanguageResults) {
        List<String> issues = new ArrayList<>();
        
        // 基于核心能力的问题
        if (competencies.get(EnhancedEvaluationResult.LANGUAGE_EXPRESSION) < 70) {
            issues.add("语言表达不够清晰流畅");
        }
        if (competencies.get(EnhancedEvaluationResult.LOGICAL_THINKING) < 75) {
            issues.add("回答缺乏逻辑结构");
        }
        
        // 基于面部情感的问题
        if (facialResults != null) {
            Map<String, Object> emotionData = (Map<String, Object>) facialResults.get("emotion");
            if (emotionData != null) {
                Map<String, Double> emotionScores = (Map<String, Double>) emotionData.get("emotionScores");
                if (emotionScores != null && emotionScores.get("confidence") < 0.6) {
                    issues.add("面试时缺乏自信表现");
                }
                if (emotionScores.get("engagement") < 0.6) {
                    issues.add("面试参与度不够高");
                }
            }
        }
        
        // 基于肢体语言的问题
        if (bodyLanguageResults != null) {
            Map<String, Object> bodyLanguageData = (Map<String, Object>) bodyLanguageResults.get("bodyLanguage");
            if (bodyLanguageData != null) {
                Map<String, Double> bodyLanguageScores = (Map<String, Double>) bodyLanguageData.get("bodyLanguageScores");
                if (bodyLanguageScores != null && bodyLanguageScores.get("openness") < 0.6) {
                    issues.add("肢体语言过于封闭");
                }
                if (bodyLanguageScores.get("professionalism") < 0.7) {
                    issues.add("肢体表现不够专业");
                }
            }
        }
        
        return issues;
    }
    
    /**
     * 生成增强的改进建议
     */
    private List<String> generateEnhancedImprovementSuggestions(Map<String, Double> competencies, List<String> keyIssues) {
        List<String> suggestions = new ArrayList<>();
        
        for (String issue : keyIssues) {
            if (issue.contains("语言表达")) {
                suggestions.add("建议参加演讲培训，提升表达能力");
            }
            if (issue.contains("逻辑结构")) {
                suggestions.add("建议使用STAR方法组织回答");
            }
            if (issue.contains("自信")) {
                suggestions.add("建议进行模拟面试训练，增强自信");
            }
            if (issue.contains("参与度")) {
                suggestions.add("建议提高面试时的积极性和参与度");
            }
            if (issue.contains("肢体语言")) {
                suggestions.add("建议学习专业的肢体语言技巧");
            }
            if (issue.contains("专业")) {
                suggestions.add("建议提升面试时的专业表现");
            }
        }
        
        return suggestions;
    }
    
    /**
     * 生成增强的总体反馈
     */
    private String generateEnhancedOverallFeedback(double totalScore, Map<String, Double> competencies,
                                                 Map<String, Object> facialResults, Map<String, Object> bodyLanguageResults) {
        StringBuilder feedback = new StringBuilder();
        
        if (totalScore >= 80) {
            feedback.append("整体表现优秀，各项能力均衡发展。");
        } else if (totalScore >= 70) {
            feedback.append("整体表现良好，具备该岗位的基本要求。");
        } else {
            feedback.append("需要系统性地提升相关技能。");
        }
        
        // 添加面部情感反馈
        if (facialResults != null) {
            Map<String, Object> emotionData = (Map<String, Object>) facialResults.get("emotion");
            if (emotionData != null) {
                Map<String, Double> emotionScores = (Map<String, Double>) emotionData.get("emotionScores");
                if (emotionScores != null) {
                    if (emotionScores.get("confidence") >= 0.7) {
                        feedback.append(" 面部表情自信自然。");
                    } else {
                        feedback.append(" 建议提升面试时的自信表现。");
                    }
                }
            }
        }
        
        // 添加肢体语言反馈
        if (bodyLanguageResults != null) {
            Map<String, Object> bodyLanguageData = (Map<String, Object>) bodyLanguageResults.get("bodyLanguage");
            if (bodyLanguageData != null) {
                Map<String, Double> bodyLanguageScores = (Map<String, Double>) bodyLanguageData.get("bodyLanguageScores");
                if (bodyLanguageScores != null && bodyLanguageScores.get("professionalism") >= 0.8) {
                    feedback.append(" 肢体语言专业得体。");
                } else {
                    feedback.append(" 建议改善肢体语言的专业性。");
                }
            }
        }
        
        return feedback.toString();
    }
    
    /**
     * 生成增强的学习路径
     */
    private List<EnhancedEvaluationResult.LearningPath> generateEnhancedLearningPaths(String position, Map<String, Double> competencies) {
        List<EnhancedEvaluationResult.LearningPath> paths = new ArrayList<>();
        
        // 根据岗位和能力评分生成个性化学习路径
        if (competencies.get(EnhancedEvaluationResult.LANGUAGE_EXPRESSION) < 75) {
            paths.add(new EnhancedEvaluationResult.LearningPath(
                "表达能力", "面试表达技巧训练", "提升面试时的语言表达能力",
                "https://example.com/expression-course", "course", 1, 8.0
            ));
        }
        
        if (competencies.get(EnhancedEvaluationResult.PROFESSIONAL_KNOWLEDGE) < 75) {
            paths.add(new EnhancedEvaluationResult.LearningPath(
                "专业技能", position + "技能提升课程", "深入学习相关技术栈",
                "https://example.com/skill-course", "course", 1, 20.0
            ));
        }
        
        if (competencies.get("自信度") < 70) {
            paths.add(new EnhancedEvaluationResult.LearningPath(
                "自信训练", "面试自信提升课程", "提升面试时的自信表现",
                "https://example.com/confidence-course", "course", 1, 6.0
            ));
        }
        
        if (competencies.get("肢体开放度") < 70) {
            paths.add(new EnhancedEvaluationResult.LearningPath(
                "肢体语言", "专业肢体语言训练", "学习专业的肢体语言技巧",
                "https://example.com/body-language-course", "course", 1, 4.0
            ));
        }
        
        return paths;
    }
    
    /**
     * 获取默认面部分析结果
     */
    private Map<String, Object> getDefaultFacialAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> emotionData = new HashMap<>();
        Map<String, Double> emotionScores = new HashMap<>();
        emotionScores.put("confidence", 0.7);
        emotionScores.put("enthusiasm", 0.6);
        emotionScores.put("nervousness", 0.3);
        emotionScores.put("engagement", 0.65);
        emotionData.put("emotionScores", emotionScores);
        result.put("emotion", emotionData);
        
        Map<String, Object> eyeContactData = new HashMap<>();
        Map<String, Double> eyeContactScores = new HashMap<>();
        eyeContactScores.put("duration", 0.65);
        eyeContactScores.put("frequency", 0.7);
        eyeContactScores.put("intensity", 0.6);
        eyeContactScores.put("stability", 0.75);
        eyeContactData.put("eyeContactScores", eyeContactScores);
        result.put("eyeContact", eyeContactData);
        
        return result;
    }
    
    /**
     * 获取默认肢体语言分析结果
     */
    private Map<String, Object> getDefaultBodyLanguageAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> bodyLanguageData = new HashMap<>();
        Map<String, Double> bodyLanguageScores = new HashMap<>();
        bodyLanguageScores.put("openness", 0.7);
        bodyLanguageScores.put("confidence", 0.65);
        bodyLanguageScores.put("engagement", 0.75);
        bodyLanguageScores.put("professionalism", 0.8);
        bodyLanguageData.put("bodyLanguageScores", bodyLanguageScores);
        result.put("bodyLanguage", bodyLanguageData);
        
        return result;
    }
} 