package com.interview.repository;

import com.interview.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {
    
    /**
     * 根据岗位查找活跃的分类
     */
    @Query("SELECT qc FROM QuestionCategory qc WHERE qc.position = :position AND qc.isActive = true ORDER BY qc.sortOrder")
    List<QuestionCategory> findByPositionAndActive(@Param("position") String position);
    
    /**
     * 查找所有活跃的分类
     */
    @Query("SELECT qc FROM QuestionCategory qc WHERE qc.isActive = true ORDER BY qc.position, qc.sortOrder")
    List<QuestionCategory> findAllActive();
    
    /**
     * 根据岗位和分类名称查找
     */
    QuestionCategory findByPositionAndNameAndIsActiveTrue(String position, String name);
    
    /**
     * 检查指定岗位是否存在分类
     */
    boolean existsByPositionAndIsActiveTrue(String position);
} 