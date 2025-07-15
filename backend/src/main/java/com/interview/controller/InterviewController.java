package com.interview.controller;

import com.interview.model.EvaluationResult;
import com.interview.model.InterviewRequest;
import com.interview.model.MultiModalData;
import com.interview.model.EnhancedEvaluationResult;
import com.interview.service.EvaluationService;
import com.interview.service.MultimodalAnalysisService;
import com.interview.service.UserService;
import com.interview.service.InterviewSessionService;
import com.interview.service.QuestionBankService;
import com.interview.service.XunfeiSparkService;
import com.interview.service.QuestionAsyncTaskService;
import com.interview.service.XunfeiIatService;
import com.interview.entity.User;
import com.interview.entity.InterviewSession;
import com.interview.entity.Question;
import com.interview.entity.QuestionCategory;
import com.interview.repository.EvaluationResultRepository;
import com.interview.repository.QuestionCategoryRepository;
import com.interview.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class InterviewController {
    
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private MultimodalAnalysisService multimodalAnalysisService;
    
    @Autowired
    private QuestionBankService questionBankService;
    
    @Autowired
    private XunfeiSparkService xunfeiSparkService;
    
    @Autowired
    private XunfeiIatService xunfeiIatService;
    
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionCategoryRepository categoryRepository;
    
    @Autowired
    private QuestionAsyncTaskService questionAsyncTaskService;
    
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
    
    @PostMapping("/parse-answer")
    public ResponseEntity<?> parseAnswer(
        @RequestParam String question, // 新增题目参数
        @RequestParam(required = false) String text,
        @RequestParam(required = false) MultipartFile audio,
        @RequestParam(required = false) MultipartFile video
    ) {
        try {
            String userAnswer = text;
            if (userAnswer == null && audio != null && !audio.isEmpty()) {
                userAnswer = xunfeiIatService.recognize(audio);
            }
            if (userAnswer == null || userAnswer.isBlank()) {
                return ResponseEntity.badRequest().body("未检测到有效回答内容");
            }
            // 拼接大模型prompt
            String prompt = "题目：" + question + "\n用户回答：" + userAnswer;
            String analysis = xunfeiSparkService.analyzeWithSpark(prompt, null);
            // 可存库：question, userAnswer, analysis
            return ResponseEntity.ok(Map.of("question", question, "text", userAnswer, "analysis", analysis));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("解析失败: " + e.getMessage());
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
    
    /**
     * 获取随机题目
     */
    @GetMapping("/questions")
    public ResponseEntity<Map<String, List<String>>> getQuestions(
            @RequestParam String position,
            @RequestParam(defaultValue = "7") int count,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> tags) {
        try {
            Map<String, List<String>> questions;
            
            if (difficulty != null) {
                // 根据难度级别获取题目
                Question.DifficultyLevel difficultyLevel = Question.DifficultyLevel.valueOf(difficulty.toUpperCase());
                questions = questionBankService.getRandomQuestionsByPositionAndDifficulty(position, difficultyLevel, count);
            } else if (type != null) {
                // 根据题目类型获取题目
                Question.QuestionType questionType = Question.QuestionType.valueOf(type.toUpperCase());
                questions = questionBankService.getRandomQuestionsByPositionAndType(position, questionType, count);
            } else if (tags != null && !tags.isEmpty()) {
                // 根据标签获取题目
                questions = questionBankService.getRandomQuestionsByPositionAndTags(position, tags, count);
            } else {
                // 随机获取题目
                questions = questionBankService.getRandomQuestionsByPosition(position, count);
            }
            
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            System.err.println("获取题目失败: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取可用岗位列表
     */
    @GetMapping("/positions")
    public ResponseEntity<List<String>> getAvailablePositions() {
        try {
            List<String> positions = questionBankService.getAvailablePositions();
            return ResponseEntity.ok(positions);
        } catch (Exception e) {
            System.err.println("获取岗位列表失败: " + e.getMessage());
            // 返回默认岗位列表
            List<String> defaultPositions = List.of("前端开发", "后端开发", "全栈开发");
            return ResponseEntity.ok(defaultPositions);
        }
    }
    
    /**
     * 获取题目统计信息
     */
    @GetMapping("/questions/stats")
    public ResponseEntity<Map<String, Object>> getQuestionStats(@RequestParam String position) {
        try {
            long totalQuestions = questionBankService.countQuestionsByPosition(position);
            List<String> availablePositions = questionBankService.getAvailablePositions();
            
            Map<String, Object> stats = Map.of(
                "position", position,
                "totalQuestions", totalQuestions,
                "availablePositions", availablePositions
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("获取题目统计失败: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 自动生成面试题并插入数据库
     */
    @PostMapping("/generate-question")
    public ResponseEntity<Question> generateQuestion(@RequestParam String prompt, @RequestParam int categoryId) {
        try {
            Question q = xunfeiSparkService.generateAndSaveQuestion(prompt, categoryId);
            return ResponseEntity.ok(q);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 批量导入题目接口
     */
    @PostMapping("/import-questions")
    public ResponseEntity<?> importQuestions(@RequestBody List<Map<String, Object>> questions) {
        int success = 0, fail = 0;
        for (Map<String, Object> q : questions) {
            try {
                Question question = new Question();
                question.setQuestionText((String) q.get("question_text"));
                question.setPosition((String) q.get("position"));
                question.setDifficultyLevel(Question.DifficultyLevel.valueOf((String) q.get("difficulty_level")));
                question.setQuestionType(Question.QuestionType.valueOf((String) q.get("question_type")));
                question.setTags((String) q.get("tags"));
                question.setIsActive((Boolean) q.get("is_active"));
                Long categoryId = Long.valueOf(q.get("category_id").toString());
                QuestionCategory category = categoryRepository.findById(categoryId).orElse(null);
                question.setCategory(category);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                question.setCreatedAt(LocalDateTime.parse((String) q.get("created_at"), formatter));
                question.setUpdatedAt(LocalDateTime.parse((String) q.get("updated_at"), formatter));
                questionRepository.save(question);
                success++;
            } catch (Exception e) {
                fail++;
            }
        }
        return ResponseEntity.ok(Map.of("success", success, "fail", fail));
    }
    
    /**
     * 直接生成题目JSON返回前端（不入库）
     */
    @PostMapping("/generate-question-direct")
    public ResponseEntity<?> generateQuestionDirect(@RequestParam String prompt, @RequestParam int categoryId) {
        try {
            String questionJson = xunfeiSparkService.generateQuestionJson(prompt, categoryId);
            return ResponseEntity.ok(questionJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("生成题目失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量生成题目并返回JSON数组，自动加载用
     */
    @RequestMapping(value = "/generate-questions-batch", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> generateQuestionsBatch(@RequestParam String position, @RequestParam int categoryId, @RequestParam(defaultValue = "10") int count) {
        try {
            String questionsJson = xunfeiSparkService.generateQuestionsJsonBatch(position, categoryId, count);
            return ResponseEntity.ok(questionsJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("批量生成题目失败: " + e.getMessage());
        }
    }
    
    // 异步批量生成题目任务提交接口
    @PostMapping("/generate-questions-async")
    public Map<String, String> generateQuestionsAsync(@RequestParam String position,
                                                  @RequestParam int categoryId,
                                                  @RequestParam(defaultValue = "10") int count) {
        String taskId = questionAsyncTaskService.submitTask();
        questionAsyncTaskService.runTask(taskId, () -> {
            String json = xunfeiSparkService.generateQuestionsJsonBatch(position, categoryId, count);
            return new ObjectMapper().readValue(json, List.class);
        });
        return Collections.singletonMap("taskId", taskId);
    }

    // 查询异步批量生成题目任务进度/结果
    @GetMapping("/generate-questions-result")
    public Map<String, Object> getGenerateQuestionsResult(@RequestParam String taskId) {
        QuestionAsyncTaskService.TaskStatus status = questionAsyncTaskService.getTaskStatus(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("status", status.status);
        result.put("questions", status.questions);
        return result;
    }
    
    // 私有辅助方法
    private String processAudioUpload(MultipartFile file, String questionId) {
        // 实现音频文件处理逻辑
        return "audio_url_" + questionId;
    }
    
    private String processVideoUpload(MultipartFile file, String questionId) {
        // 实现视频文件处理逻辑
        return "video_url_" + questionId;
    }
    
    private List<EnhancedEvaluationResult.LearningPath> generateLearningPaths(String position, Map<String, Double> competencies) {
        // 实现学习路径生成逻辑
        return new ArrayList<>();
    }
} 