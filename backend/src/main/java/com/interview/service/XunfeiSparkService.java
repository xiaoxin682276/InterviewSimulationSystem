package com.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class XunfeiSparkService {
    
    @Value("${xunfei.appid}")
    private String appId;
    
    @Value("${xunfei.apiKey}")
    private String apiKey;
    
    @Value("${xunfei.apiSecret}")
    private String apiSecret;
    
    @Value("${xunfei.sparkUrl}")
    private String sparkUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public XunfeiSparkService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 调用讯飞星火大模型进行多模态分析
     */
    public String analyzeWithSpark(String prompt, Map<String, Object> multimodalData) {
        try {
            // 构建请求数据
            Map<String, Object> requestData = buildSparkRequest(prompt, multimodalData);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + generateToken());
            
            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sparkUrl, entity, String.class);
            
            // 解析响应
            return parseSparkResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("讯飞星火API调用失败: " + e.getMessage());
            return "分析过程中出现错误，请重试";
        }
    }
    
    /**
     * 构建星火API请求
     */
    private Map<String, Object> buildSparkRequest(String prompt, Map<String, Object> multimodalData) {
        Map<String, Object> request = new HashMap<>();
        
        // 构建消息
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", buildContent(prompt, multimodalData));
        
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(message);
        
        request.put("header", buildHeader());
        request.put("parameter", buildParameter());
        request.put("payload", buildPayload(messages));
        
        return request;
    }
    
    /**
     * 构建请求头
     */
    private Map<String, Object> buildHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", appId);
        return header;
    }
    
    /**
     * 构建参数
     */
    private Map<String, Object> buildParameter() {
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> chat = new HashMap<>();
        chat.put("domain", "general");
        chat.put("temperature", 0.5);
        chat.put("max_tokens", 4096);
        parameter.put("chat", chat);
        return parameter;
    }
    
    /**
     * 构建载荷
     */
    private Map<String, Object> buildPayload(List<Map<String, Object>> messages) {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("text", messages);
        payload.put("message", message);
        return payload;
    }
    
    /**
     * 构建内容（支持多模态）
     */
    private List<Map<String, Object>> buildContent(String prompt, Map<String, Object> multimodalData) {
        List<Map<String, Object>> content = new ArrayList<>();
        
        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        content.add(textContent);
        
        // 添加多模态数据
        if (multimodalData != null) {
            // 如果有音频数据
            if (multimodalData.containsKey("audio")) {
                Map<String, Object> audioContent = new HashMap<>();
                audioContent.put("type", "audio");
                audioContent.put("audio", multimodalData.get("audio"));
                content.add(audioContent);
            }
            
            // 如果有视频数据
            if (multimodalData.containsKey("video")) {
                Map<String, Object> videoContent = new HashMap<>();
                videoContent.put("type", "video");
                videoContent.put("video", multimodalData.get("video"));
                content.add(videoContent);
            }
        }
        
        return content;
    }
    
    /**
     * 生成认证token
     */
    private String generateToken() {
        // 这里应该实现讯飞的token生成逻辑
        // 简化实现，实际应该按照讯飞文档生成
        return apiKey + ":" + apiSecret;
    }
    
    /**
     * 解析星火API响应
     */
    private String parseSparkResponse(String responseBody) {
        try {
            // 解析JSON响应
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            
            // 提取文本内容
            Map<String, Object> payload = (Map<String, Object>) response.get("payload");
            Map<String, Object> choices = (Map<String, Object>) payload.get("choices");
            Map<String, Object> text = (Map<String, Object>) choices.get("text");
            
            return (String) text.get("content");
            
        } catch (Exception e) {
            System.err.println("解析星火响应失败: " + e.getMessage());
            return "响应解析失败";
        }
    }
    
    /**
     * 面试能力评估分析
     */
    public Map<String, Object> evaluateInterviewCapabilities(String position, 
                                                           List<String> answers, 
                                                           List<String> questions,
                                                           Map<String, Object> multimodalData) {
        
        String prompt = buildEvaluationPrompt(position, answers, questions);
        String analysis = analyzeWithSpark(prompt, multimodalData);
        
        // 解析分析结果
        return parseEvaluationResult(analysis);
    }
    
    /**
     * 构建评估提示词
     */
    private String buildEvaluationPrompt(String position, List<String> answers, List<String> questions) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请对以下面试回答进行全面的能力评估分析。\n\n");
        prompt.append("岗位：").append(position).append("\n\n");
        
        prompt.append("面试问答：\n");
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            prompt.append("问题").append(i + 1).append("：").append(questions.get(i)).append("\n");
            prompt.append("回答").append(i + 1).append("：").append(answers.get(i)).append("\n\n");
        }
        
        prompt.append("请从以下6个维度进行评估：\n");
        prompt.append("1. 专业知识水平\n");
        prompt.append("2. 技能匹配度\n");
        prompt.append("3. 语言表达能力\n");
        prompt.append("4. 逻辑思维能力\n");
        prompt.append("5. 创新能力\n");
        prompt.append("6. 应变抗压能力\n\n");
        
        prompt.append("请提供：\n");
        prompt.append("- 每个维度的评分（0-100分）\n");
        prompt.append("- 关键问题定位\n");
        prompt.append("- 具体改进建议\n");
        prompt.append("- 总体评价\n");
        
        return prompt.toString();
    }
    
    /**
     * 解析评估结果
     */
    private Map<String, Object> parseEvaluationResult(String analysis) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里应该实现更复杂的解析逻辑
            // 简化实现，实际应该解析AI返回的结构化数据
            
            // 模拟解析结果
            Map<String, Double> competencies = new HashMap<>();
            competencies.put("专业知识水平", 75.0);
            competencies.put("技能匹配度", 80.0);
            competencies.put("语言表达能力", 70.0);
            competencies.put("逻辑思维能力", 85.0);
            competencies.put("创新能力", 65.0);
            competencies.put("应变抗压能力", 75.0);
            
            List<String> keyIssues = Arrays.asList(
                "回答缺乏STAR结构",
                "眼神交流不足",
                "技术深度有待提升"
            );
            
            List<String> suggestions = Arrays.asList(
                "建议使用STAR方法组织回答",
                "加强面试时的眼神交流训练",
                "深入学习相关技术栈"
            );
            
            result.put("competencies", competencies);
            result.put("keyIssues", keyIssues);
            result.put("suggestions", suggestions);
            result.put("overallFeedback", "整体表现良好，在逻辑思维方面表现突出，建议在表达结构和非语言交流方面加强训练。");
            
        } catch (Exception e) {
            System.err.println("解析评估结果失败: " + e.getMessage());
        }
        
        return result;
    }
} 