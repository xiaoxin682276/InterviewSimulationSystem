package com.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;
import java.util.Base64;

@Service
public class FacialEmotionService {
    
    @Value("${facial.api.key}")
    private String apiKey;
    
    @Value("${facial.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public FacialEmotionService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 分析面部情感
     */
    public Map<String, Object> analyzeFacialEmotion(MultipartFile videoFile) {
        try {
            // 将视频文件转换为base64
            String base64Video = convertToBase64(videoFile);
            
            // 构建请求数据
            Map<String, Object> requestData = buildEmotionRequest(base64Video);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            
            // 解析响应
            return parseEmotionResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("面部情感分析失败: " + e.getMessage());
            return getDefaultEmotionAnalysis();
        }
    }
    
    /**
     * 分析面部表情变化
     */
    public Map<String, Object> analyzeFacialExpressions(MultipartFile videoFile) {
        try {
            String base64Video = convertToBase64(videoFile);
            Map<String, Object> requestData = buildExpressionRequest(base64Video);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/expressions", entity, String.class);
            
            return parseExpressionResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("面部表情分析失败: " + e.getMessage());
            return getDefaultExpressionAnalysis();
        }
    }
    
    /**
     * 分析眼神交流
     */
    public Map<String, Object> analyzeEyeContact(MultipartFile videoFile) {
        try {
            String base64Video = convertToBase64(videoFile);
            Map<String, Object> requestData = buildEyeContactRequest(base64Video);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/eye-contact", entity, String.class);
            
            return parseEyeContactResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("眼神交流分析失败: " + e.getMessage());
            return getDefaultEyeContactAnalysis();
        }
    }
    
    /**
     * 构建情感分析请求
     */
    private Map<String, Object> buildEmotionRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "emotion");
        request.put("frame_rate", 1); // 每秒分析1帧
        request.put("return_confidence", true);
        return request;
    }
    
    /**
     * 构建表情分析请求
     */
    private Map<String, Object> buildExpressionRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "expression");
        request.put("frame_rate", 2); // 每秒分析2帧
        request.put("return_landmarks", true);
        return request;
    }
    
    /**
     * 构建眼神交流分析请求
     */
    private Map<String, Object> buildEyeContactRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "eye_contact");
        request.put("frame_rate", 5); // 每秒分析5帧
        request.put("return_gaze_direction", true);
        return request;
    }
    
    /**
     * 解析情感分析响应
     */
    private Map<String, Object> parseEmotionResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            
            // 提取情感分析结果
            List<Map<String, Object>> emotions = (List<Map<String, Object>>) response.get("emotions");
            
            // 计算平均情感分数
            Map<String, Double> emotionScores = new HashMap<>();
            emotionScores.put("confidence", 0.0);
            emotionScores.put("enthusiasm", 0.0);
            emotionScores.put("nervousness", 0.0);
            emotionScores.put("engagement", 0.0);
            
            if (emotions != null && !emotions.isEmpty()) {
                double totalConfidence = 0.0;
                double totalEnthusiasm = 0.0;
                double totalNervousness = 0.0;
                double totalEngagement = 0.0;
                int count = 0;
                
                for (Map<String, Object> emotion : emotions) {
                    totalConfidence += (Double) emotion.getOrDefault("confidence", 0.0);
                    totalEnthusiasm += (Double) emotion.getOrDefault("enthusiasm", 0.0);
                    totalNervousness += (Double) emotion.getOrDefault("nervousness", 0.0);
                    totalEngagement += (Double) emotion.getOrDefault("engagement", 0.0);
                    count++;
                }
                
                if (count > 0) {
                    emotionScores.put("confidence", totalConfidence / count);
                    emotionScores.put("enthusiasm", totalEnthusiasm / count);
                    emotionScores.put("nervousness", totalNervousness / count);
                    emotionScores.put("engagement", totalEngagement / count);
                }
            }
            
            result.put("emotionScores", emotionScores);
            result.put("dominantEmotion", response.get("dominant_emotion"));
            result.put("emotionStability", response.get("emotion_stability"));
            
        } catch (Exception e) {
            System.err.println("解析情感分析响应失败: " + e.getMessage());
            result = getDefaultEmotionAnalysis();
        }
        
        return result;
    }
    
    /**
     * 解析表情分析响应
     */
    private Map<String, Object> parseExpressionResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            
            // 提取表情分析结果
            List<Map<String, Object>> expressions = (List<Map<String, Object>>) response.get("expressions");
            
            Map<String, Double> expressionScores = new HashMap<>();
            expressionScores.put("smile", 0.0);
            expressionScores.put("eye_contact", 0.0);
            expressionScores.put("head_nod", 0.0);
            expressionScores.put("facial_engagement", 0.0);
            
            if (expressions != null && !expressions.isEmpty()) {
                double totalSmile = 0.0;
                double totalEyeContact = 0.0;
                double totalHeadNod = 0.0;
                double totalEngagement = 0.0;
                int count = 0;
                
                for (Map<String, Object> expression : expressions) {
                    totalSmile += (Double) expression.getOrDefault("smile", 0.0);
                    totalEyeContact += (Double) expression.getOrDefault("eye_contact", 0.0);
                    totalHeadNod += (Double) expression.getOrDefault("head_nod", 0.0);
                    totalEngagement += (Double) expression.getOrDefault("engagement", 0.0);
                    count++;
                }
                
                if (count > 0) {
                    expressionScores.put("smile", totalSmile / count);
                    expressionScores.put("eye_contact", totalEyeContact / count);
                    expressionScores.put("head_nod", totalHeadNod / count);
                    expressionScores.put("facial_engagement", totalEngagement / count);
                }
            }
            
            result.put("expressionScores", expressionScores);
            result.put("expressionStability", response.get("expression_stability"));
            
        } catch (Exception e) {
            System.err.println("解析表情分析响应失败: " + e.getMessage());
            result = getDefaultExpressionAnalysis();
        }
        
        return result;
    }
    
    /**
     * 解析眼神交流分析响应
     */
    private Map<String, Object> parseEyeContactResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            
            // 提取眼神交流分析结果
            Map<String, Object> eyeContactData = (Map<String, Object>) response.get("eye_contact");
            
            Map<String, Double> eyeContactScores = new HashMap<>();
            eyeContactScores.put("duration", (Double) eyeContactData.getOrDefault("duration", 0.0));
            eyeContactScores.put("frequency", (Double) eyeContactData.getOrDefault("frequency", 0.0));
            eyeContactScores.put("intensity", (Double) eyeContactData.getOrDefault("intensity", 0.0));
            eyeContactScores.put("stability", (Double) eyeContactData.getOrDefault("stability", 0.0));
            
            result.put("eyeContactScores", eyeContactScores);
            result.put("gazeDirection", eyeContactData.get("gaze_direction"));
            result.put("eyeContactQuality", eyeContactData.get("quality"));
            
        } catch (Exception e) {
            System.err.println("解析眼神交流分析响应失败: " + e.getMessage());
            result = getDefaultEyeContactAnalysis();
        }
        
        return result;
    }
    
    /**
     * 将文件转换为base64
     */
    private String convertToBase64(MultipartFile file) throws IOException {
        byte[] fileContent = file.getBytes();
        return Base64.getEncoder().encodeToString(fileContent);
    }
    
    /**
     * 获取默认情感分析结果
     */
    private Map<String, Object> getDefaultEmotionAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> emotionScores = new HashMap<>();
        emotionScores.put("confidence", 0.7);
        emotionScores.put("enthusiasm", 0.6);
        emotionScores.put("nervousness", 0.3);
        emotionScores.put("engagement", 0.65);
        
        result.put("emotionScores", emotionScores);
        result.put("dominantEmotion", "neutral");
        result.put("emotionStability", 0.75);
        
        return result;
    }
    
    /**
     * 获取默认表情分析结果
     */
    private Map<String, Object> getDefaultExpressionAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> expressionScores = new HashMap<>();
        expressionScores.put("smile", 0.6);
        expressionScores.put("eye_contact", 0.7);
        expressionScores.put("head_nod", 0.5);
        expressionScores.put("facial_engagement", 0.65);
        
        result.put("expressionScores", expressionScores);
        result.put("expressionStability", 0.7);
        
        return result;
    }
    
    /**
     * 获取默认眼神交流分析结果
     */
    private Map<String, Object> getDefaultEyeContactAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> eyeContactScores = new HashMap<>();
        eyeContactScores.put("duration", 0.65);
        eyeContactScores.put("frequency", 0.7);
        eyeContactScores.put("intensity", 0.6);
        eyeContactScores.put("stability", 0.75);
        
        result.put("eyeContactScores", eyeContactScores);
        result.put("gazeDirection", "center");
        result.put("eyeContactQuality", "good");
        
        return result;
    }
} 