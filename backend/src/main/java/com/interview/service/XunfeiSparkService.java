package com.interview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.interview.entity.Question;
import com.interview.entity.QuestionCategory;
import com.interview.repository.QuestionRepository;
import com.interview.repository.QuestionCategoryRepository;
import com.interview.config.XunfeiSparkConfig;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class XunfeiSparkService {
    
    @Autowired
    private XunfeiSparkConfig sparkConfig;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionCategoryRepository categoryRepository;
    
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
            ResponseEntity<String> response = restTemplate.postForEntity(sparkConfig.getUrl(), entity, String.class);
            
            // 解析响应
            return parseSparkResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("讯飞星火API调用失败: " + e.getMessage());
            return "分析过程中出现错误，请重试";
        }
    }
    
    /**
     * 调用星火API生成结构化面试题并保存到数据库
     */
    public Question generateAndSaveQuestion(String prompt, int categoryId) throws Exception {
        // 1. 构建API请求体
        String apiUrl = "https://spark-api-open.xf-yun.com/v1/chat/completions";
        String token = "Bearer hHRMAsRCbiCFMyuKCfEG:cckCnWqhWHpIzaGjhQba";
        String model = "generalv3.5";
        String systemPrompt = "你是一个面试题生成专家。请根据以下要求生成一道结构化面试题，并以 JSON 格式返回，字段必须包含数据库中的全部属性，字段顺序、名称、格式必须严格匹配：id：请设为 null，数据库会自动分配主键 category_id：请设为某个固定数值，如 1，由外部控制（后续批量赋值） question_text：字符串，题目的完整描述内容，不能只是一句话题干，至少一句完整问题 position：字符串，岗位方向，限定为：前端开发、后端开发、全栈开发 difficulty_level：枚举，值为：EASY、MEDIUM、HARD question_type：枚举，值为：TECHNICAL、BEHAVIORAL、PROBLEM_SOLVING、SYSTEM_DESIGN tags：字符串格式的 JSON 数组，注意是字符串 is_active：布尔值，统一设为 true created_at 与 updated_at：统一设为当前时间，如 \"2025-07-14 20:00:00\"";
        String userPrompt = prompt;
        String requestBody = "{" +
                "\"max_tokens\":4096," +
                "\"top_k\":4," +
                "\"temperature\":0.5," +
                "\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"" + systemPrompt + "\"}," +
                "{\"role\":\"user\",\"content\":\"" + userPrompt + "\"}" +
                "]," +
                "\"model\":\"" + model + "\"" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        // 2. 解析返回的JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        String questionJson = root.path("choices").get(0).path("message").path("content").asText();
        JsonNode qNode = mapper.readTree(questionJson);
        Question question = new Question();
        QuestionCategory category = categoryRepository.findById((long)categoryId).orElse(null);
        question.setCategory(category);
        question.setQuestionText(qNode.get("question_text").asText());
        question.setPosition(qNode.get("position").asText());
        question.setDifficultyLevel(Question.DifficultyLevel.valueOf(qNode.get("difficulty_level").asText()));
        question.setQuestionType(Question.QuestionType.valueOf(qNode.get("question_type").asText()));
        question.setTags(qNode.get("tags").asText());
        question.setIsActive(qNode.get("is_active").asBoolean());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        question.setCreatedAt(LocalDateTime.parse(qNode.get("created_at").asText(), formatter));
        question.setUpdatedAt(LocalDateTime.parse(qNode.get("updated_at").asText(), formatter));
        return questionRepository.save(question);
    }
    
    /**
     * 调用星火API生成结构化面试题并返回JSON字符串（不入库）
     */
    public String generateQuestionJson(String prompt, int categoryId) throws Exception {
        String apiUrl = "https://spark-api-open.xf-yun.com/v1/chat/completions";
        String token = "Bearer hHRMAsRCbiCFMyuKCfEG:cckCnWqhWHpIzaGjhQba";
        String model = "generalv3.5";
        String systemPrompt = "你是一个面试题生成专家。请根据以下要求生成一道结构化面试题，并以 JSON 格式返回，字段必须包含数据库中的全部属性，字段顺序、名称、格式必须严格匹配：id：请设为 null，数据库会自动分配主键 category_id：请设为某个固定数值，如 1，由外部控制（后续批量赋值） question_text：字符串，题目的完整描述内容，不能只是一句话题干，至少一句完整问题 position：字符串，岗位方向，限定为：前端开发、后端开发、全栈开发 difficulty_level：枚举，值为：EASY、MEDIUM、HARD question_type：枚举，值为：TECHNICAL、BEHAVIORAL、PROBLEM_SOLVING、SYSTEM_DESIGN tags：字符串格式的 JSON 数组，注意是字符串 is_active：布尔值，统一设为 true created_at 与 updated_at：统一设为当前时间，如 \"2025-07-14 20:00:00\"";
        String userPrompt = prompt;

        // 构造请求体
        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("max_tokens", 4096);
        requestBody.put("top_k", 4);
        requestBody.put("temperature", 0.5);
        requestBody.put("model", model);
        java.util.List<java.util.Map<String, String>> messages = new java.util.ArrayList<>();
        java.util.Map<String, String> sysMsg = new java.util.HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);
        java.util.Map<String, String> userMsg = new java.util.HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 打印请求体
        ObjectMapper debugMapper = new ObjectMapper();
        System.out.println("[讯飞API调试] 请求体: " + debugMapper.writeValueAsString(requestBody));
        System.out.println("[讯飞API调试] 请求头: " + headers.toString());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            System.out.println("[讯飞API调试] 响应状态: " + response.getStatusCode());
            System.out.println("[讯飞API调试] 响应体: " + response.getBody());
            JsonNode root = debugMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            // 去除markdown代码块
            if (content.startsWith("```json")) {
                content = content.replaceFirst("```json", "").trim();
            }
            if (content.startsWith("```")) {
                content = content.replaceFirst("```", "").trim();
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.lastIndexOf("```"));
                content = content.trim();
            }
            String questionJson = content;
            return questionJson;
        } catch (Exception e) {
            System.err.println("[讯飞API调试] 调用异常: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 批量生成结构化面试题，返回JSON数组字符串
     */
    public String generateQuestionsJsonBatch(String position, int categoryId, int count) throws Exception {
        String apiUrl = "https://spark-api-open.xf-yun.com/v1/chat/completions";
        String token = "Bearer hHRMAsRCbiCFMyuKCfEG:cckCnWqhWHpIzaGjhQba";
        String model = "generalv3.5";
        String systemPrompt = "你是一个面试题生成专家。请根据以下要求生成" + count + "道结构化面试题，并以 JSON 数组格式返回，每个元素为一个题目对象，字段必须包含数据库中的全部属性，字段顺序、名称、格式必须严格匹配：id：请设为 null，数据库会自动分配主键 category_id：请设为" + categoryId + " question_text：字符串，题目的完整描述内容，不能只是一句话题干，至少一句完整问题 position：字符串，岗位方向，限定为：" + position + " difficulty_level：枚举，值为：EASY、MEDIUM、HARD question_type：枚举，值为：TECHNICAL、BEHAVIORAL、PROBLEM_SOLVING、SYSTEM_DESIGN tags：字符串格式的 JSON 数组，注意是字符串 is_active：布尔值，统一设为 true created_at 与 updated_at：统一设为当前时间，如 \"2025-07-14 20:00:00\"";
        String userPrompt = "请生成" + count + "道" + position + "相关的中等难度技术面试题";

        // 构造请求体
        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("max_tokens", 4096);
        requestBody.put("top_k", 4);
        requestBody.put("temperature", 0.5);
        requestBody.put("model", model);
        java.util.List<java.util.Map<String, String>> messages = new java.util.ArrayList<>();
        java.util.Map<String, String> sysMsg = new java.util.HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        messages.add(sysMsg);
        java.util.Map<String, String> userMsg = new java.util.HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 打印请求体
        ObjectMapper debugMapper = new ObjectMapper();
        System.out.println("[讯飞API调试] 批量请求体: " + debugMapper.writeValueAsString(requestBody));
        System.out.println("[讯飞API调试] 批量请求头: " + headers.toString());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            System.out.println("[讯飞API调试] 批量响应状态: " + response.getStatusCode());
            System.out.println("[讯飞API调试] 批量响应体: " + response.getBody());
            JsonNode root = debugMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            // 去除markdown代码块
            if (content.startsWith("```json")) {
                content = content.replaceFirst("```json", "").trim();
            }
            if (content.startsWith("```")) {
                content = content.replaceFirst("```", "").trim();
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.lastIndexOf("```"));
                content = content.trim();
            }
            return content;
        } catch (Exception e) {
            System.err.println("[讯飞API调试] 批量调用异常: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 构建星火API请求
     */
    private Map<String, Object> buildSparkRequest(String prompt, Map<String, Object> multimodalData) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "generalv3.5");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt); // 只用prompt，不嵌套其它结构
        messages.add(userMsg);
        request.put("messages", messages);
        return request;
    }
    
    /**
     * 构建请求头
     */
    private Map<String, Object> buildHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", sparkConfig.getAppid());
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
        return sparkConfig.getApiKey() + ":" + sparkConfig.getApiSecret();
    }
    
    /**
     * 解析星火API响应
     */
    private String parseSparkResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            // 直接取choices[0].message.content
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                String content = message.path("content").asText();
                return content;
            }
            return "未获取到大模型返回内容";
        } catch (Exception e) {
            e.printStackTrace();
            return "解析星火响应失败: " + e.getMessage();
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