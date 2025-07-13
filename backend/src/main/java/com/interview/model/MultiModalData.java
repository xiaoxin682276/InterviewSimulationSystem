package com.interview.model;

import java.util.List;
import java.util.Map;

public class MultiModalData {
    private String position;
    private List<TextData> textData;      // 文本数据（答案内容、简历）
    private List<AudioData> audioData;    // 语音数据
    private List<VideoData> videoData;    // 视频数据
    private Map<String, Object> metadata; // 元数据

    // 构造函数
    public MultiModalData() {}

    public MultiModalData(String position, List<TextData> textData, 
                         List<AudioData> audioData, List<VideoData> videoData) {
        this.position = position;
        this.textData = textData;
        this.audioData = audioData;
        this.videoData = videoData;
    }

    // Getter和Setter方法
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<TextData> getTextData() {
        return textData;
    }

    public void setTextData(List<TextData> textData) {
        this.textData = textData;
    }

    public List<AudioData> getAudioData() {
        return audioData;
    }

    public void setAudioData(List<AudioData> audioData) {
        this.audioData = audioData;
    }

    public List<VideoData> getVideoData() {
        return videoData;
    }

    public void setVideoData(List<VideoData> videoData) {
        this.videoData = videoData;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // 内部数据类
    public static class TextData {
        private String questionId;
        private String question;
        private String answer;
        private String resume; // 简历内容
        private long timestamp;

        public TextData() {}

        public TextData(String questionId, String question, String answer, String resume) {
            this.questionId = questionId;
            this.question = question;
            this.answer = answer;
            this.resume = resume;
            this.timestamp = System.currentTimeMillis();
        }

        // Getter和Setter
        public String getQuestionId() { return questionId; }
        public void setQuestionId(String questionId) { this.questionId = questionId; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        
        public String getResume() { return resume; }
        public void setResume(String resume) { this.resume = resume; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class AudioData {
        private String questionId;
        private String audioUrl;
        private String audioText; // 语音转文字结果
        private Map<String, Double> emotionAnalysis; // 情感分析结果
        private Map<String, Double> languageLogic; // 语言逻辑分析
        private long timestamp;

        public AudioData() {}

        public AudioData(String questionId, String audioUrl) {
            this.questionId = questionId;
            this.audioUrl = audioUrl;
            this.timestamp = System.currentTimeMillis();
        }

        // Getter和Setter
        public String getQuestionId() { return questionId; }
        public void setQuestionId(String questionId) { this.questionId = questionId; }
        
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
        
        public String getAudioText() { return audioText; }
        public void setAudioText(String audioText) { this.audioText = audioText; }
        
        public Map<String, Double> getEmotionAnalysis() { return emotionAnalysis; }
        public void setEmotionAnalysis(Map<String, Double> emotionAnalysis) { this.emotionAnalysis = emotionAnalysis; }
        
        public Map<String, Double> getLanguageLogic() { return languageLogic; }
        public void setLanguageLogic(Map<String, Double> languageLogic) { this.languageLogic = languageLogic; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class VideoData {
        private String questionId;
        private String videoUrl;
        private Map<String, Double> microExpressions; // 微表情分析
        private Map<String, Double> bodyLanguage; // 肢体语言分析
        private Map<String, Double> eyeContact; // 眼神交流分析
        private long timestamp;

        public VideoData() {}

        public VideoData(String questionId, String videoUrl) {
            this.questionId = questionId;
            this.videoUrl = videoUrl;
            this.timestamp = System.currentTimeMillis();
        }

        // Getter和Setter
        public String getQuestionId() { return questionId; }
        public void setQuestionId(String questionId) { this.questionId = questionId; }
        
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        
        public Map<String, Double> getMicroExpressions() { return microExpressions; }
        public void setMicroExpressions(Map<String, Double> microExpressions) { this.microExpressions = microExpressions; }
        
        public Map<String, Double> getBodyLanguage() { return bodyLanguage; }
        public void setBodyLanguage(Map<String, Double> bodyLanguage) { this.bodyLanguage = bodyLanguage; }
        
        public Map<String, Double> getEyeContact() { return eyeContact; }
        public void setEyeContact(Map<String, Double> eyeContact) { this.eyeContact = eyeContact; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
} 