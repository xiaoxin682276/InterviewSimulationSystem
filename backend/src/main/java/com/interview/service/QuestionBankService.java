package com.interview.service;

import com.interview.entity.Question;
import com.interview.entity.QuestionCategory;
import com.interview.repository.QuestionCategoryRepository;
import com.interview.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionBankService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionCategoryRepository categoryRepository;
    
    /**
     * 根据岗位随机抽取题目
     */
    public Map<String, List<String>> getRandomQuestionsByPosition(String position, int totalQuestions) {
        // 获取该岗位的所有分类
        List<QuestionCategory> categories = categoryRepository.findByPositionAndActive(position);
        
        if (categories.isEmpty()) {
            return getDefaultQuestions(position);
        }
        
        Map<String, List<String>> result = new LinkedHashMap<>();
        
        // 为每个分类分配题目数量
        Map<String, Integer> categoryQuestionCounts = distributeQuestionsByCategory(categories, totalQuestions);
        
        for (QuestionCategory category : categories) {
            String categoryName = category.getName();
            int questionCount = categoryQuestionCounts.getOrDefault(categoryName, 2);
            
            // 获取该分类的所有题目
            List<Question> allQuestions = questionRepository.findByPositionAndCategoryAndActive(position, category.getId());
            
            // 在Java代码中进行随机选择
            List<Question> selectedQuestions = getRandomQuestions(allQuestions, questionCount);
            
            // 转换为题目文本列表
            List<String> questionTexts = selectedQuestions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());
            
            result.put(categoryName, questionTexts);
        }
        
        return result;
    }
    
    /**
     * 根据岗位和难度级别随机抽取题目
     */
    public Map<String, List<String>> getRandomQuestionsByPositionAndDifficulty(String position, 
                                                                             Question.DifficultyLevel difficultyLevel, 
                                                                             int totalQuestions) {
        List<QuestionCategory> categories = categoryRepository.findByPositionAndActive(position);
        
        if (categories.isEmpty()) {
            return getDefaultQuestions(position);
        }
        
        Map<String, List<String>> result = new LinkedHashMap<>();
        
        // 为每个分类分配题目数量
        Map<String, Integer> categoryQuestionCounts = distributeQuestionsByCategory(categories, totalQuestions);
        
        for (QuestionCategory category : categories) {
            String categoryName = category.getName();
            int questionCount = categoryQuestionCounts.getOrDefault(categoryName, 2);
            
            // 获取该分类下指定难度的题目
            List<Question> questions = questionRepository.findByPositionAndCategoryAndActive(position, category.getId());
            
            // 过滤指定难度的题目
            List<Question> filteredQuestions = questions.stream()
                .filter(q -> q.getDifficultyLevel() == difficultyLevel)
                .collect(Collectors.toList());
            
            // 如果指定难度的题目不够，则从所有题目中随机选择
            if (filteredQuestions.size() < questionCount) {
                filteredQuestions = questions;
            }
            
            // 随机选择指定数量的题目
            List<Question> selectedQuestions = getRandomQuestions(filteredQuestions, questionCount);
            
            // 转换为题目文本列表
            List<String> questionTexts = selectedQuestions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());
            
            result.put(categoryName, questionTexts);
        }
        
        return result;
    }
    
    /**
     * 根据岗位和题目类型随机抽取题目
     */
    public Map<String, List<String>> getRandomQuestionsByPositionAndType(String position, 
                                                                         Question.QuestionType questionType, 
                                                                         int totalQuestions) {
        List<QuestionCategory> categories = categoryRepository.findByPositionAndActive(position);
        
        if (categories.isEmpty()) {
            return getDefaultQuestions(position);
        }
        
        Map<String, List<String>> result = new LinkedHashMap<>();
        
        // 为每个分类分配题目数量
        Map<String, Integer> categoryQuestionCounts = distributeQuestionsByCategory(categories, totalQuestions);
        
        for (QuestionCategory category : categories) {
            String categoryName = category.getName();
            int questionCount = categoryQuestionCounts.getOrDefault(categoryName, 2);
            
            // 获取该分类下的题目
            List<Question> questions = questionRepository.findByPositionAndCategoryAndActive(position, category.getId());
            
            // 过滤指定类型的题目
            List<Question> filteredQuestions = questions.stream()
                .filter(q -> q.getQuestionType() == questionType)
                .collect(Collectors.toList());
            
            // 如果指定类型的题目不够，则从所有题目中随机选择
            if (filteredQuestions.size() < questionCount) {
                filteredQuestions = questions;
            }
            
            // 随机选择指定数量的题目
            List<Question> selectedQuestions = getRandomQuestions(filteredQuestions, questionCount);
            
            // 转换为题目文本列表
            List<String> questionTexts = selectedQuestions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());
            
            result.put(categoryName, questionTexts);
        }
        
        return result;
    }
    
    /**
     * 根据标签随机抽取题目
     */
    public Map<String, List<String>> getRandomQuestionsByPositionAndTags(String position, 
                                                                         List<String> tags, 
                                                                         int totalQuestions) {
        List<QuestionCategory> categories = categoryRepository.findByPositionAndActive(position);
        
        if (categories.isEmpty()) {
            return getDefaultQuestions(position);
        }
        
        Map<String, List<String>> result = new LinkedHashMap<>();
        
        // 为每个分类分配题目数量
        Map<String, Integer> categoryQuestionCounts = distributeQuestionsByCategory(categories, totalQuestions);
        
        for (QuestionCategory category : categories) {
            String categoryName = category.getName();
            int questionCount = categoryQuestionCounts.getOrDefault(categoryName, 2);
            
            List<Question> allQuestions = new ArrayList<>();
            
            // 根据标签查找题目
            for (String tag : tags) {
                List<Question> taggedQuestions = questionRepository.findByPositionAndTagAndActive(position, tag);
                allQuestions.addAll(taggedQuestions);
            }
            
            // 去重
            allQuestions = allQuestions.stream()
                .distinct()
                .collect(Collectors.toList());
            
            // 如果标签匹配的题目不够，则从该分类的所有题目中随机选择
            if (allQuestions.size() < questionCount) {
                allQuestions = questionRepository.findByPositionAndCategoryAndActive(position, category.getId());
            }
            
            // 随机选择指定数量的题目
            List<Question> selectedQuestions = getRandomQuestions(allQuestions, questionCount);
            
            // 转换为题目文本列表
            List<String> questionTexts = selectedQuestions.stream()
                .map(Question::getQuestionText)
                .collect(Collectors.toList());
            
            result.put(categoryName, questionTexts);
        }
        
        return result;
    }
    
    /**
     * 为每个分类分配题目数量
     */
    private Map<String, Integer> distributeQuestionsByCategory(List<QuestionCategory> categories, int totalQuestions) {
        Map<String, Integer> distribution = new HashMap<>();
        
        if (categories.isEmpty()) {
            return distribution;
        }
        
        int baseCount = totalQuestions / categories.size();
        int remainder = totalQuestions % categories.size();
        
        for (int i = 0; i < categories.size(); i++) {
            QuestionCategory category = categories.get(i);
            int count = baseCount + (i < remainder ? 1 : 0);
            distribution.put(category.getName(), Math.max(count, 1)); // 确保每个分类至少有一个题目
        }
        
        return distribution;
    }
    
    /**
     * 获取默认题目（当数据库中没有题目时使用）
     */
    private Map<String, List<String>> getDefaultQuestions(String position) {
        Map<String, List<String>> defaultQuestions = new LinkedHashMap<>();
        
        switch (position) {
            case "前端开发":
                defaultQuestions.put("技术能力", Arrays.asList(
                    "请详细介绍React的生命周期钩子函数，以及它们的使用场景？",
                    "什么是虚拟DOM？请解释虚拟DOM的工作原理和优势？"
                ));
                defaultQuestions.put("项目经验", Arrays.asList(
                    "请描述你参与过的最有挑战性的前端项目，以及你在其中承担的角色？",
                    "在项目中遇到过哪些性能问题？你是如何解决的？"
                ));
                break;
                
            case "后端开发":
                defaultQuestions.put("技术能力", Arrays.asList(
                    "请详细介绍Java中的多线程编程，以及线程安全的概念？",
                    "什么是Spring框架？请解释Spring的核心特性和优势？"
                ));
                defaultQuestions.put("项目经验", Arrays.asList(
                    "请描述你参与过的最复杂的后端项目，以及你在其中承担的角色？",
                    "在项目中遇到过哪些性能问题？你是如何解决的？"
                ));
                break;
                
            case "全栈开发":
                defaultQuestions.put("技术能力", Arrays.asList(
                    "请详细介绍前后端分离架构的优势和实现方式？",
                    "什么是CI/CD？请介绍持续集成和持续部署的流程？"
                ));
                defaultQuestions.put("项目经验", Arrays.asList(
                    "请描述你参与过的最有挑战性的全栈项目，以及你在其中承担的角色？",
                    "在项目中遇到过哪些架构问题？你是如何解决的？"
                ));
                break;
                
            default:
                defaultQuestions.put("通用问题", Arrays.asList(
                    "请介绍一下你的技术背景和项目经验？",
                    "你最近学习了什么新技术？"
                ));
        }
        
        return defaultQuestions;
    }
    
    /**
     * 获取所有可用的岗位
     */
    public List<String> getAvailablePositions() {
        return categoryRepository.findAllActive().stream()
            .map(QuestionCategory::getPosition)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * 统计指定岗位的题目数量
     */
    public long countQuestionsByPosition(String position) {
        return questionRepository.countByPositionAndActive(position);
    }
    
    /**
     * 获取题目的详细信息
     */
    public List<Question> getQuestionsWithDetails(String position) {
        return questionRepository.findByPositionAndActive(position);
    }

    /**
     * 从题目列表中随机选择指定数量的题目
     */
    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        if (questions.size() <= count) {
            return new ArrayList<>(questions);
        }
        
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, count);
    }
} 