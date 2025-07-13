package com.interview.controller;

import com.interview.model.EvaluationResult;
import com.interview.model.InterviewRequest;
import com.interview.model.MultiModalData;
import com.interview.model.EnhancedEvaluationResult;
import com.interview.service.EvaluationService;
import com.interview.service.MultimodalAnalysisService;
import com.interview.service.UserService;
import com.interview.service.InterviewSessionService;
import com.interview.entity.User;
import com.interview.entity.InterviewSession;
import com.interview.repository.EvaluationResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class InterviewController {
    
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private MultimodalAnalysisService multimodalAnalysisService;
    
    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResult> evaluateInterview(@RequestBody InterviewRequest request) {
        try {
            EvaluationResult result = evaluationService.evaluateInterview(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/evaluate-enhanced")
    public ResponseEntity<EnhancedEvaluationResult> evaluateInterviewEnhanced(@RequestBody MultiModalData multimodalData) {
        try {
            EnhancedEvaluationResult result = multimodalAnalysisService.analyzeMultimodalData(multimodalData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 增强多模态评估接口（包含面部情感和肢体语言分析）
     */
    @PostMapping("/evaluate-enhanced-with-video")
    public ResponseEntity<EnhancedEvaluationResult> evaluateInterviewEnhancedWithVideo(
            @RequestParam("multimodalData") String multimodalDataJson,
            @RequestParam("videoFile") MultipartFile videoFile) {
        try {
            // 解析多模态数据
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            MultiModalData multimodalData = objectMapper.readValue(multimodalDataJson, MultiModalData.class);
            
            // 执行增强的多模态分析
            EnhancedEvaluationResult result = multimodalAnalysisService.analyzeEnhancedMultimodalData(multimodalData, videoFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("增强多模态评估失败: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/upload-audio")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file, 
                                           @RequestParam("questionId") String questionId) {
        try {
            // 处理音频文件上传
            String audioUrl = processAudioUpload(file, questionId);
            return ResponseEntity.ok(audioUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("音频上传失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/upload-video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file, 
                                           @RequestParam("questionId") String questionId) {
        try {
            // 处理视频文件上传
            String videoUrl = processVideoUpload(file, questionId);
            return ResponseEntity.ok(videoUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("视频上传失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/learning-paths")
    public ResponseEntity<List<EnhancedEvaluationResult.LearningPath>> getLearningPaths(
            @RequestParam String position, 
            @RequestParam Map<String, Double> competencies) {
        try {
            List<EnhancedEvaluationResult.LearningPath> paths = generateLearningPaths(position, competencies);
            return ResponseEntity.ok(paths);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/questions")
    public ResponseEntity<Map<String, List<String>>> getQuestions(@RequestParam String position) {
        // 根据岗位返回相应的面试题目
        Map<String, List<String>> questions = getQuestionsByPosition(position);
        return ResponseEntity.ok(questions);
    }
    
    @GetMapping("/positions")
    public ResponseEntity<List<String>> getAvailablePositions() {
        List<String> positions = List.of("前端开发", "后端开发", "全栈开发");
        return ResponseEntity.ok(positions);
    }
    
    private Map<String, List<String>> getQuestionsByPosition(String position) {
        Map<String, List<String>> questions = new java.util.HashMap<>();
        
        switch (position) {
            case "前端开发":
                questions.put("技术能力", List.of(
                    "请介绍一下你对HTML、CSS、JavaScript的理解",
                    "你熟悉哪些前端框架？请谈谈你的使用经验",
                    "如何优化前端性能？"
                ));
                questions.put("项目经验", List.of(
                    "请描述一个你参与的前端项目",
                    "在项目中遇到过哪些技术难点？如何解决的？"
                ));
                questions.put("沟通能力", List.of(
                    "如何与后端开发人员协作？",
                    "如何向非技术人员解释技术问题？"
                ));
                questions.put("学习能力", List.of(
                    "你如何保持技术更新？",
                    "最近学习了什么新技术？"
                ));
                break;
                
            case "后端开发":
                questions.put("技术能力", List.of(
                    "请介绍一下你对Java的理解",
                    "你熟悉哪些数据库？请谈谈你的使用经验",
                    "如何设计一个高并发的系统？"
                ));
                questions.put("系统设计", List.of(
                    "请描述一个你参与的后端项目",
                    "如何设计微服务架构？"
                ));
                questions.put("问题解决", List.of(
                    "如何排查生产环境的问题？",
                    "如何保证系统的安全性？"
                ));
                break;
                
            case "全栈开发":
                questions.put("技术能力", List.of(
                    "请介绍一下你的技术栈",
                    "前后端分离的优势是什么？",
                    "如何保证前后端接口的一致性？"
                ));
                questions.put("系统设计", List.of(
                    "请描述一个你参与的全栈项目",
                    "如何设计一个完整的系统架构？"
                ));
                questions.put("项目经验", List.of(
                    "在项目中遇到过哪些挑战？如何解决的？",
                    "如何管理项目的技术债务？"
                ));
                questions.put("沟通能力", List.of(
                    "如何协调前后端开发进度？",
                    "如何与产品经理沟通需求？"
                ));
                break;
                
            default:
                // 默认返回前端开发的问题
                questions.put("技术能力", List.of(
                    "请介绍一下你的技术背景",
                    "你熟悉哪些编程语言？",
                    "如何解决技术问题？"
                ));
                questions.put("项目经验", List.of(
                    "请描述一个你参与的项目",
                    "在项目中遇到过哪些问题？如何解决的？"
                ));
                questions.put("沟通能力", List.of(
                    "如何与团队成员协作？",
                    "如何向他人解释技术问题？"
                ));
                questions.put("学习能力", List.of(
                    "你如何学习新技术？",
                    "最近学习了什么新技能？"
                ));
                break;
        }
        
        return questions;
    }
    
    // 处理音频文件上传
    private String processAudioUpload(MultipartFile file, String questionId) {
        // 这里应该实现音频文件处理逻辑
        // 包括文件保存、格式转换、语音识别等
        return "/uploads/audio/" + questionId + "_" + System.currentTimeMillis() + ".wav";
    }
    
    // 处理视频文件上传
    private String processVideoUpload(MultipartFile file, String questionId) {
        // 这里应该实现视频文件处理逻辑
        // 包括文件保存、格式转换、视频分析等
        return "/uploads/video/" + questionId + "_" + System.currentTimeMillis() + ".mp4";
    }
    
    // 生成学习路径
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
} 