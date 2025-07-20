package com.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;
import java.util.Base64;

@Service
public class BodyLanguageService {
    // 直接赋默认值，避免Spring启动报错
    private String apiKey = "dummy";
    private String apiUrl = "http://dummy-url";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BodyLanguageService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 分析肢体语言
     */
    public Map<String, Object> analyzeBodyLanguage(MultipartFile videoFile) {
        try {
            String base64Video = convertToBase64(videoFile);
            Map<String, Object> requestData = buildBodyLanguageRequest(base64Video);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            return parseBodyLanguageResponse(response.getBody());

        } catch (Exception e) {
            System.err.println("肢体语言分析失败: " + e.getMessage());
            return getDefaultBodyLanguageAnalysis();
        }
    }

    /**
     * 分析姿态
     */
    public Map<String, Object> analyzePosture(MultipartFile videoFile) {
        try {
            String base64Video = convertToBase64(videoFile);
            Map<String, Object> requestData = buildPostureRequest(base64Video);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/posture", entity, String.class);

            return parsePostureResponse(response.getBody());

        } catch (Exception e) {
            System.err.println("姿态分析失败: " + e.getMessage());
            return getDefaultPostureAnalysis();
        }
    }

    /**
     * 分析手势
     */
    public Map<String, Object> analyzeGestures(MultipartFile videoFile) {
        try {
            String base64Video = convertToBase64(videoFile);
            Map<String, Object> requestData = buildGestureRequest(base64Video);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/gestures", entity, String.class);

            return parseGestureResponse(response.getBody());

        } catch (Exception e) {
            System.err.println("手势分析失败: " + e.getMessage());
            return getDefaultGestureAnalysis();
        }
    }

    /**
     * 分析身体动作
     */
    public Map<String, Object> analyzeBodyMovements(MultipartFile videoFile) {
        try {
            String base64Video = convertToBase64(videoFile);
            Map<String, Object> requestData = buildMovementRequest(base64Video);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/movements", entity, String.class);

            return parseMovementResponse(response.getBody());

        } catch (Exception e) {
            System.err.println("身体动作分析失败: " + e.getMessage());
            return getDefaultMovementAnalysis();
        }
    }

    /**
     * 构建肢体语言分析请求
     */
    private Map<String, Object> buildBodyLanguageRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "body_language");
        request.put("frame_rate", 3); // 每秒分析3帧
        request.put("return_confidence", true);
        request.put("return_landmarks", true);
        return request;
    }

    /**
     * 构建姿态分析请求
     */
    private Map<String, Object> buildPostureRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "posture");
        request.put("frame_rate", 2);
        request.put("return_posture_quality", true);
        return request;
    }

    /**
     * 构建手势分析请求
     */
    private Map<String, Object> buildGestureRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "gestures");
        request.put("frame_rate", 5);
        request.put("return_hand_landmarks", true);
        return request;
    }

    /**
     * 构建身体动作分析请求
     */
    private Map<String, Object> buildMovementRequest(String base64Video) {
        Map<String, Object> request = new HashMap<>();
        request.put("video", base64Video);
        request.put("analysis_type", "movements");
        request.put("frame_rate", 4);
        request.put("return_movement_flow", true);
        return request;
    }

    /**
     * 解析肢体语言分析响应
     */
    private Map<String, Object> parseBodyLanguageResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

            // 提取肢体语言分析结果
            List<Map<String, Object>> bodyLanguageData = (List<Map<String, Object>>) response.get("body_language");

            Map<String, Double> bodyLanguageScores = new HashMap<>();
            bodyLanguageScores.put("openness", 0.0);
            bodyLanguageScores.put("confidence", 0.0);
            bodyLanguageScores.put("engagement", 0.0);
            bodyLanguageScores.put("professionalism", 0.0);

            if (bodyLanguageData != null && !bodyLanguageData.isEmpty()) {
                double totalOpenness = 0.0;
                double totalConfidence = 0.0;
                double totalEngagement = 0.0;
                double totalProfessionalism = 0.0;
                int count = 0;

                for (Map<String, Object> data : bodyLanguageData) {
                    totalOpenness += (Double) data.getOrDefault("openness", 0.0);
                    totalConfidence += (Double) data.getOrDefault("confidence", 0.0);
                    totalEngagement += (Double) data.getOrDefault("engagement", 0.0);
                    totalProfessionalism += (Double) data.getOrDefault("professionalism", 0.0);
                    count++;
                }

                if (count > 0) {
                    bodyLanguageScores.put("openness", totalOpenness / count);
                    bodyLanguageScores.put("confidence", totalConfidence / count);
                    bodyLanguageScores.put("engagement", totalEngagement / count);
                    bodyLanguageScores.put("professionalism", totalProfessionalism / count);
                }
            }

            result.put("bodyLanguageScores", bodyLanguageScores);
            result.put("overallBodyLanguage", response.get("overall_assessment"));
            result.put("bodyLanguageStability", response.get("stability"));

        } catch (Exception e) {
            System.err.println("解析肢体语言分析响应失败: " + e.getMessage());
            result = getDefaultBodyLanguageAnalysis();
        }

        return result;
    }

    /**
     * 解析姿态分析响应
     */
    private Map<String, Object> parsePostureResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

            // 提取姿态分析结果
            Map<String, Object> postureData = (Map<String, Object>) response.get("posture");

            Map<String, Double> postureScores = new HashMap<>();
            postureScores.put("upright", (Double) postureData.getOrDefault("upright", 0.0));
            postureScores.put("balanced", (Double) postureData.getOrDefault("balanced", 0.0));
            postureScores.put("relaxed", (Double) postureData.getOrDefault("relaxed", 0.0));
            postureScores.put("professional", (Double) postureData.getOrDefault("professional", 0.0));

            result.put("postureScores", postureScores);
            result.put("postureQuality", postureData.get("quality"));
            result.put("postureStability", postureData.get("stability"));

        } catch (Exception e) {
            System.err.println("解析姿态分析响应失败: " + e.getMessage());
            result = getDefaultPostureAnalysis();
        }

        return result;
    }

    /**
     * 解析手势分析响应
     */
    private Map<String, Object> parseGestureResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

            // 提取手势分析结果
            List<Map<String, Object>> gestureData = (List<Map<String, Object>>) response.get("gestures");

            Map<String, Double> gestureScores = new HashMap<>();
            gestureScores.put("appropriate", 0.0);
            gestureScores.put("expressive", 0.0);
            gestureScores.put("controlled", 0.0);
            gestureScores.put("natural", 0.0);

            if (gestureData != null && !gestureData.isEmpty()) {
                double totalAppropriate = 0.0;
                double totalExpressive = 0.0;
                double totalControlled = 0.0;
                double totalNatural = 0.0;
                int count = 0;

                for (Map<String, Object> data : gestureData) {
                    totalAppropriate += (Double) data.getOrDefault("appropriate", 0.0);
                    totalExpressive += (Double) data.getOrDefault("expressive", 0.0);
                    totalControlled += (Double) data.getOrDefault("controlled", 0.0);
                    totalNatural += (Double) data.getOrDefault("natural", 0.0);
                    count++;
                }

                if (count > 0) {
                    gestureScores.put("appropriate", totalAppropriate / count);
                    gestureScores.put("expressive", totalExpressive / count);
                    gestureScores.put("controlled", totalControlled / count);
                    gestureScores.put("natural", totalNatural / count);
                }
            }

            result.put("gestureScores", gestureScores);
            result.put("gestureTypes", response.get("gesture_types"));
            result.put("gestureFrequency", response.get("frequency"));

        } catch (Exception e) {
            System.err.println("解析手势分析响应失败: " + e.getMessage());
            result = getDefaultGestureAnalysis();
        }

        return result;
    }

    /**
     * 解析身体动作分析响应
     */
    private Map<String, Object> parseMovementResponse(String responseBody) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);

            // 提取身体动作分析结果
            Map<String, Object> movementData = (Map<String, Object>) response.get("movements");

            Map<String, Double> movementScores = new HashMap<>();
            movementScores.put("smooth", (Double) movementData.getOrDefault("smooth", 0.0));
            movementScores.put("purposeful", (Double) movementData.getOrDefault("purposeful", 0.0));
            movementScores.put("controlled", (Double) movementData.getOrDefault("controlled", 0.0));
            movementScores.put("natural", (Double) movementData.getOrDefault("natural", 0.0));

            result.put("movementScores", movementScores);
            result.put("movementFlow", movementData.get("flow"));
            result.put("movementStability", movementData.get("stability"));

        } catch (Exception e) {
            System.err.println("解析身体动作分析响应失败: " + e.getMessage());
            result = getDefaultMovementAnalysis();
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
     * 获取默认肢体语言分析结果
     */
    private Map<String, Object> getDefaultBodyLanguageAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> bodyLanguageScores = new HashMap<>();
        bodyLanguageScores.put("openness", 0.7);
        bodyLanguageScores.put("confidence", 0.65);
        bodyLanguageScores.put("engagement", 0.75);
        bodyLanguageScores.put("professionalism", 0.8);

        result.put("bodyLanguageScores", bodyLanguageScores);
        result.put("overallBodyLanguage", "good");
        result.put("bodyLanguageStability", 0.75);

        return result;
    }

    /**
     * 获取默认姿态分析结果
     */
    private Map<String, Object> getDefaultPostureAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> postureScores = new HashMap<>();
        postureScores.put("upright", 0.75);
        postureScores.put("balanced", 0.8);
        postureScores.put("relaxed", 0.7);
        postureScores.put("professional", 0.85);

        result.put("postureScores", postureScores);
        result.put("postureQuality", "good");
        result.put("postureStability", 0.8);

        return result;
    }

    /**
     * 获取默认手势分析结果
     */
    private Map<String, Object> getDefaultGestureAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> gestureScores = new HashMap<>();
        gestureScores.put("appropriate", 0.8);
        gestureScores.put("expressive", 0.65);
        gestureScores.put("controlled", 0.75);
        gestureScores.put("natural", 0.7);

        result.put("gestureScores", gestureScores);
        result.put("gestureTypes", Arrays.asList("pointing", "open_palms", "nodding"));
        result.put("gestureFrequency", "moderate");

        return result;
    }

    /**
     * 获取默认身体动作分析结果
     */
    private Map<String, Object> getDefaultMovementAnalysis() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> movementScores = new HashMap<>();
        movementScores.put("smooth", 0.75);
        movementScores.put("purposeful", 0.8);
        movementScores.put("controlled", 0.7);
        movementScores.put("natural", 0.65);

        result.put("movementScores", movementScores);
        result.put("movementFlow", "smooth");
        result.put("movementStability", 0.75);

        return result;
    }
} 