package com.interview.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@Service
public class FacialEmotionService {
    private String apiKey = "dummy";
    private String apiUrl = "http://dummy-url";

    public FacialEmotionService() {}

    public Map<String, Object> analyzeFacialEmotion(MultipartFile videoFile) {
        return new HashMap<>();
    }
    public Map<String, Object> analyzeFacialExpressions(MultipartFile videoFile) {
        return new HashMap<>();
    }
    public Map<String, Object> analyzeEyeContact(MultipartFile videoFile) {
        return new HashMap<>();
    }
}