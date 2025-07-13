-- 智能面试模拟系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS interview_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE interview_system;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS evaluation_results;
DROP TABLE IF EXISTS interview_questions;
DROP TABLE IF EXISTS interview_sessions;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    target_position VARCHAR(100),
    experience_years INT,
    resume_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建面试会话表
CREATE TABLE interview_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    position VARCHAR(100) NOT NULL,
    session_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    total_score DECIMAL(5,2),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建面试问题表
CREATE TABLE interview_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    question_order INT NOT NULL,
    answer_text TEXT,
    answer_audio_url VARCHAR(255),
    answer_video_url VARCHAR(255),
    score DECIMAL(5,2),
    feedback TEXT,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id) ON DELETE CASCADE
);

-- 创建评估结果表
CREATE TABLE evaluation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    overall_score DECIMAL(5,2) NOT NULL,
    category_scores JSON,
    core_competencies JSON,
    key_issues JSON,
    suggestions JSON,
    learning_paths JSON,
    evaluation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id) ON DELETE CASCADE
);

-- 插入测试用户数据（密码都是 'password' 的BCrypt加密形式）
INSERT INTO users (username, password, email, full_name, is_active) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@example.com', '系统管理员', TRUE),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'test@example.com', '测试用户', TRUE),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'zhangsan@example.com', '张三', TRUE),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'lisi@example.com', '李四', TRUE),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'wangwu@example.com', '王五', TRUE);

-- 显示创建的表
SHOW TABLES;

-- 显示用户数据
SELECT id, username, email, full_name, is_active, created_at FROM users;

-- 显示表结构
DESCRIBE users;
DESCRIBE interview_sessions;
DESCRIBE interview_questions;
DESCRIBE evaluation_results;

-- 完成提示
SELECT '数据库初始化完成！' AS message;
SELECT '测试账号已创建，密码都是: password' AS info; 