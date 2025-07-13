package com.interview.service;

import com.interview.entity.User;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.interview.config.PasswordConfig.SimplePasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SimplePasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     */
    public User registerUser(String username, String password, String email, String fullName) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User(username, email, passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录
     */
    public Optional<User> loginUser(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 更新最后登录时间
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * 用户认证（用于AuthController）
     */
    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 更新最后登录时间
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
                return user;
            }
        }
        
        return null;
    }

    /**
     * 根据用户名查找用户（用于AuthController）
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 根据ID查找用户
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 根据用户名查找用户
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据邮箱查找用户
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 更新用户信息
     */
    public User updateUser(Long userId, String fullName, String phoneNumber, String targetPosition, Integer experienceYears) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setTargetPosition(targetPosition);
            user.setExperienceYears(experienceYears);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("用户不存在");
    }
    
    /**
     * 更新用户简历
     */
    public User updateUserResume(Long userId, String resumeUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setResumeUrl(resumeUrl);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("用户不存在");
    }
    
    /**
     * 修改密码
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 停用用户
     */
    public void deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    /**
     * 激活用户
     */
    public void activateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    /**
     * 根据目标岗位查找用户
     */
    public List<User> findUsersByTargetPosition(String targetPosition) {
        return userRepository.findByTargetPosition(targetPosition);
    }
    
    /**
     * 根据工作经验查找用户
     */
    public List<User> findUsersByExperienceYears(Integer experienceYears) {
        return userRepository.findByExperienceYears(experienceYears);
    }
    
    /**
     * 查找活跃用户
     */
    public List<User> findActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    /**
     * 根据用户名模糊查询
     */
    public List<User> findUsersByUsernameContaining(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }
    
    /**
     * 根据邮箱模糊查询
     */
    public List<User> findUsersByEmailContaining(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email);
    }
    
    /**
     * 根据目标岗位模糊查询
     */
    public List<User> findUsersByTargetPositionContaining(String targetPosition) {
        return userRepository.findByTargetPositionContainingIgnoreCase(targetPosition);
    }
    
    /**
     * 查找最近注册的用户
     */
    public List<User> findRecentUsers() {
        return userRepository.findRecentUsers();
    }
    
    /**
     * 查找最近登录的用户
     */
    public List<User> findRecentlyLoggedInUsers() {
        return userRepository.findRecentlyLoggedInUsers();
    }
    
    /**
     * 统计用户总数
     */
    public long countAllUsers() {
        return userRepository.countAllUsers();
    }
    
    /**
     * 统计活跃用户数
     */
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    /**
     * 根据岗位统计用户数
     */
    public long countUsersByPosition(String position) {
        return userRepository.countUsersByPosition(position);
    }
    
    /**
     * 验证用户凭据
     */
    public boolean validateUserCredentials(String usernameOrEmail, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword()) && user.getIsActive();
        }
        return false;
    }
    
    /**
     * 检查用户名是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * 检查邮箱是否可用
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
} 