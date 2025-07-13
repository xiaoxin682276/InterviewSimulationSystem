-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建面试会话表
CREATE TABLE IF NOT EXISTS interview_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    position VARCHAR(100) NOT NULL,
    session_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    total_score DECIMAL(5,2),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 创建面试问题表
CREATE TABLE IF NOT EXISTS interview_questions (
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
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id)
);

-- 创建评估结果表
CREATE TABLE IF NOT EXISTS evaluation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    overall_score DECIMAL(5,2) NOT NULL,
    category_scores JSON,
    core_competencies JSON,
    key_issues JSON,
    suggestions JSON,
    learning_paths JSON,
    evaluation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id)
);

-- 插入测试用户数据
INSERT INTO users (username, password, email, full_name) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@example.com', '系统管理员'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'test@example.com', '测试用户'),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'zhangsan@example.com', '张三'),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'lisi@example.com', '李四'),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'wangwu@example.com', '王五');

-- 注意：密码都是 'password' 的BCrypt加密形式
-- 用户可以使用以下账号登录：
-- 用户名: admin, testuser, zhangsan, lisi, wangwu
-- 密码: password 