-- 创建数据库
CREATE DATABASE IF NOT EXISTS interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE interview_system;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    phone_number VARCHAR(20),
    resume_url VARCHAR(500),
    target_position VARCHAR(100),
    experience_years INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_target_position (target_position),
    INDEX idx_created_at (created_at)
);

-- 面试会话表
CREATE TABLE IF NOT EXISTS interview_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    position VARCHAR(100) NOT NULL,
    session_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'in_progress',
    total_score DOUBLE,
    evaluation_mode VARCHAR(50),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    duration_minutes INT,
    questions_count INT DEFAULT 0,
    answers_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_position (position),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_completed_at (completed_at)
);

-- 面试问题表
CREATE TABLE IF NOT EXISTS interview_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_category VARCHAR(100),
    question_type VARCHAR(50),
    question_order INT,
    answer_text TEXT,
    audio_url VARCHAR(500),
    video_url VARCHAR(500),
    answer_duration_seconds INT,
    score DOUBLE,
    feedback TEXT,
    answered_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_question_category (question_category),
    INDEX idx_question_order (question_order),
    INDEX idx_answered_at (answered_at)
);

-- 评估结果表
CREATE TABLE IF NOT EXISTS evaluation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    evaluation_type VARCHAR(50),
    total_score DOUBLE,
    overall_feedback TEXT,
    key_issues TEXT,
    improvement_suggestions TEXT,
    competency_scores TEXT,
    facial_analysis TEXT,
    body_language_analysis TEXT,
    learning_paths TEXT,
    evaluation_duration_seconds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_evaluation_type (evaluation_type),
    INDEX idx_total_score (total_score),
    INDEX idx_created_at (created_at)
);

-- 插入初始数据

-- 插入测试用户
INSERT INTO users (username, email, password, full_name, target_position, experience_years) VALUES
('testuser1', 'test1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张三', '前端开发', 2),
('testuser2', 'test2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李四', '后端开发', 3),
('testuser3', 'test3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '王五', '全栈开发', 4);

-- 插入测试面试会话
INSERT INTO interview_sessions (user_id, position, session_type, status, total_score, evaluation_mode, completed_at, duration_minutes, questions_count, answers_count) VALUES
(1, '前端开发', 'basic', 'completed', 85.5, 'basic', DATE_SUB(NOW(), INTERVAL 1 DAY), 15, 5, 5),
(1, '前端开发', 'enhanced', 'completed', 88.0, 'enhanced', DATE_SUB(NOW(), INTERVAL 2 DAY), 20, 5, 5),
(2, '后端开发', 'basic', 'completed', 82.0, 'basic', DATE_SUB(NOW(), INTERVAL 3 DAY), 18, 5, 5),
(2, '后端开发', 'multimodal', 'completed', 90.5, 'enhanced', DATE_SUB(NOW(), INTERVAL 4 DAY), 25, 5, 5),
(3, '全栈开发', 'basic', 'completed', 87.0, 'basic', DATE_SUB(NOW(), INTERVAL 5 DAY), 22, 5, 5);

-- 插入测试面试问题
INSERT INTO interview_questions (session_id, question_text, question_category, question_type, question_order, answer_text, score) VALUES
(1, '请介绍一下你对HTML、CSS、JavaScript的理解', '技术能力', 'text', 1, 'HTML是超文本标记语言，用于构建网页结构；CSS用于样式设计；JavaScript是脚本语言，用于交互功能。', 85.0),
(1, '你熟悉哪些前端框架？请谈谈你的使用经验', '技术能力', 'text', 2, '我熟悉Vue.js和React，在项目中主要使用Vue.js进行开发。', 88.0),
(1, '如何优化前端性能？', '技术能力', 'text', 3, '可以通过代码分割、懒加载、图片压缩等方式优化性能。', 82.0),
(1, '请描述一个你参与的前端项目', '项目经验', 'text', 4, '我参与了一个电商网站的前端开发，负责商品展示和购物车功能。', 86.0),
(1, '在项目中遇到过哪些技术难点？如何解决的？', '项目经验', 'text', 5, '遇到过跨域问题，通过配置代理服务器解决。', 84.0);

-- 插入测试评估结果
INSERT INTO evaluation_results (session_id, evaluation_type, total_score, overall_feedback, key_issues, improvement_suggestions, competency_scores) VALUES
(1, 'basic', 85.5, '整体表现良好，技术基础扎实，建议在项目经验方面加强。', '["项目经验描述不够详细", "技术深度有待提升"]', '["建议多参与实际项目", "深入学习前端技术栈"]', '{"专业知识水平": 85.0, "技能匹配度": 88.0, "语言表达能力": 82.0, "逻辑思维能力": 86.0, "创新能力": 80.0, "应变抗压能力": 84.0}'),
(2, 'enhanced', 88.0, '表现优秀，各项能力均衡发展，建议继续保持。', '["可以进一步提升创新思维"]', '["建议学习新技术", "参与开源项目"]', '{"专业知识水平": 88.0, "技能匹配度": 90.0, "语言表达能力": 85.0, "逻辑思维能力": 89.0, "创新能力": 85.0, "应变抗压能力": 87.0}'),
(3, 'basic', 82.0, '基础扎实，具备后端开发的基本要求。', '["系统设计经验不足", "性能优化知识欠缺"]', '["建议学习系统设计", "深入了解性能优化"]', '{"专业知识水平": 82.0, "技能匹配度": 85.0, "语言表达能力": 80.0, "逻辑思维能力": 84.0, "创新能力": 78.0, "应变抗压能力": 82.0}'),
(4, 'multimodal', 90.5, '表现卓越，多模态评估显示综合素质优秀。', '["可以进一步提升非语言表达能力"]', '["建议参加演讲培训", "提升肢体语言技巧"]', '{"专业知识水平": 90.0, "技能匹配度": 92.0, "语言表达能力": 88.0, "逻辑思维能力": 91.0, "创新能力": 89.0, "应变抗压能力": 90.0, "自信度": 88.0, "肢体开放度": 85.0}'),
(5, 'basic', 87.0, '全栈能力突出，技术视野广阔。', '["可以加强前后端协调经验"]', '["建议参与全栈项目", "提升架构设计能力"]', '{"专业知识水平": 87.0, "技能匹配度": 89.0, "语言表达能力": 85.0, "逻辑思维能力": 88.0, "创新能力": 86.0, "应变抗压能力": 87.0}');

-- 创建视图：用户面试统计
CREATE VIEW user_interview_stats AS
SELECT 
    u.id as user_id,
    u.username,
    u.full_name,
    u.target_position,
    COUNT(s.id) as total_sessions,
    COUNT(CASE WHEN s.status = 'completed' THEN 1 END) as completed_sessions,
    AVG(CASE WHEN s.status = 'completed' THEN s.total_score END) as average_score,
    MAX(CASE WHEN s.status = 'completed' THEN s.total_score END) as best_score,
    MIN(s.created_at) as first_session,
    MAX(s.created_at) as last_session
FROM users u
LEFT JOIN interview_sessions s ON u.id = s.user_id
GROUP BY u.id, u.username, u.full_name, u.target_position;

-- 创建视图：岗位统计
CREATE VIEW position_stats AS
SELECT 
    position,
    COUNT(*) as total_sessions,
    COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed_sessions,
    AVG(CASE WHEN status = 'completed' THEN total_score END) as average_score,
    MAX(CASE WHEN status = 'completed' THEN total_score END) as highest_score,
    MIN(CASE WHEN status = 'completed' THEN total_score END) as lowest_score
FROM interview_sessions
GROUP BY position;

-- 创建视图：评估类型统计
CREATE VIEW evaluation_type_stats AS
SELECT 
    evaluation_type,
    COUNT(*) as total_evaluations,
    AVG(total_score) as average_score,
    MAX(total_score) as highest_score,
    MIN(total_score) as lowest_score
FROM evaluation_results
GROUP BY evaluation_type; 