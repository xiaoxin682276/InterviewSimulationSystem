package com.interview.repository;

import com.interview.entity.Question;
import com.interview.entity.Question.DifficultyLevel;
import com.interview.entity.Question.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 根据岗位查找活跃的题目
     */
    @Query("SELECT q FROM Question q WHERE q.position = :position AND q.isActive = true ORDER BY q.category.sortOrder, q.id")
    List<Question> findByPositionAndActive(@Param("position") String position);
    
    /**
     * 根据岗位和分类查找题目
     */
    @Query("SELECT q FROM Question q WHERE q.position = :position AND q.category.id = :categoryId AND q.isActive = true")
    List<Question> findByPositionAndCategoryAndActive(@Param("position") String position, @Param("categoryId") Long categoryId);
    
    /**
     * 根据岗位和难度级别查找题目
     */
    @Query("SELECT q FROM Question q WHERE q.position = :position AND q.difficultyLevel = :difficultyLevel AND q.isActive = true")
    List<Question> findByPositionAndDifficultyAndActive(@Param("position") String position, @Param("difficultyLevel") DifficultyLevel difficultyLevel);
    
    /**
     * 根据岗位和题目类型查找题目
     */
    @Query("SELECT q FROM Question q WHERE q.position = :position AND q.questionType = :questionType AND q.isActive = true")
    List<Question> findByPositionAndTypeAndActive(@Param("position") String position, @Param("questionType") QuestionType questionType);
    
    /**
     * 统计指定岗位的题目数量
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.position = :position AND q.isActive = true")
    long countByPositionAndActive(@Param("position") String position);
    
    /**
     * 统计指定岗位和分类的题目数量
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.position = :position AND q.category.id = :categoryId AND q.isActive = true")
    long countByPositionAndCategoryAndActive(@Param("position") String position, @Param("categoryId") Long categoryId);
    
    /**
     * 根据标签查找题目
     */
    @Query("SELECT q FROM Question q WHERE q.position = :position AND q.isActive = true AND q.tags LIKE %:tag%")
    List<Question> findByPositionAndTagAndActive(@Param("position") String position, @Param("tag") String tag);
    
    /**
     * 查找所有活跃的题目
     */
    @Query("SELECT q FROM Question q WHERE q.isActive = true ORDER BY q.position, q.category.sortOrder, q.id")
    List<Question> findAllActive();
} 