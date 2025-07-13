package com.interview.repository;

import com.interview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名和邮箱查找用户
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据目标岗位查找用户
     */
    List<User> findByTargetPosition(String targetPosition);
    
    /**
     * 根据工作经验年限查找用户
     */
    List<User> findByExperienceYears(Integer experienceYears);
    
    /**
     * 查找活跃用户
     */
    List<User> findByIsActiveTrue();
    
    /**
     * 根据用户名模糊查询
     */
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * 根据邮箱模糊查询
     */
    List<User> findByEmailContainingIgnoreCase(String email);
    
    /**
     * 根据目标岗位模糊查询
     */
    List<User> findByTargetPositionContainingIgnoreCase(String targetPosition);
    
    /**
     * 统计用户总数
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
    
    /**
     * 统计活跃用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    /**
     * 根据岗位统计用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.targetPosition = :position")
    long countUsersByPosition(@Param("position") String position);
    
    /**
     * 查找最近注册的用户
     */
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers();
    
    /**
     * 查找最近登录的用户
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    List<User> findRecentlyLoggedInUsers();
} 