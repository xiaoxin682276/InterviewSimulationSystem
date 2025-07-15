-- 智能面试模拟系统数据库初始化脚本（修复版）

-- 创建数据库
CREATE DATABASE IF NOT EXISTS interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE interview_system;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS interview_answers;
DROP TABLE IF EXISTS evaluation_results;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS question_categories;
DROP TABLE IF EXISTS interview_sessions;
DROP TABLE IF EXISTS users;

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 面试会话表
CREATE TABLE interview_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    position VARCHAR(100) NOT NULL,
    session_type VARCHAR(50) DEFAULT 'STANDARD',
    evaluation_mode VARCHAR(50) DEFAULT 'BASIC',
    questions_count INT DEFAULT 0,
    answers_count INT DEFAULT 0,
    overall_score DECIMAL(5,2),
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 评估结果表
CREATE TABLE evaluation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    category_scores TEXT,
    overall_score DECIMAL(5,2),
    feedback TEXT,
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id)
);

-- 题库分类表
CREATE TABLE question_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    position VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 题目表
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    position VARCHAR(100) NOT NULL,
    difficulty_level ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    question_type ENUM('TECHNICAL', 'BEHAVIORAL', 'PROBLEM_SOLVING', 'SYSTEM_DESIGN') DEFAULT 'TECHNICAL',
    tags TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES question_categories(id)
);

-- 面试答案表
CREATE TABLE interview_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text TEXT,
    audio_url VARCHAR(500),
    video_url VARCHAR(500),
    score DECIMAL(5,2),
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES interview_sessions(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- 插入默认管理员用户
INSERT INTO users (username, password, full_name, email, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'admin@example.com', 'ADMIN');

-- 插入题库分类数据
INSERT INTO question_categories (name, description, position, sort_order) VALUES 
('技术能力', '评估候选人的技术基础和专业知识', '前端开发', 1),
('项目经验', '了解候选人的实际项目经验和解决问题的能力', '前端开发', 2),
('学习能力', '评估候选人的学习能力和技术更新意识', '前端开发', 3),
('沟通能力', '评估候选人的沟通表达和团队协作能力', '前端开发', 4),

('技术能力', '评估候选人的技术基础和专业知识', '后端开发', 1),
('项目经验', '了解候选人的实际项目经验和解决问题的能力', '后端开发', 2),
('学习能力', '评估候选人的学习能力和技术更新意识', '后端开发', 3),
('沟通能力', '评估候选人的沟通表达和团队协作能力', '后端开发', 4),

('技术能力', '评估候选人的技术基础和专业知识', '全栈开发', 1),
('项目经验', '了解候选人的实际项目经验和解决问题的能力', '全栈开发', 2),
('学习能力', '评估候选人的学习能力和技术更新意识', '全栈开发', 3),
('沟通能力', '评估候选人的沟通表达和团队协作能力', '全栈开发', 4);

-- 插入前端开发题目
INSERT INTO questions (category_id, question_text, position, difficulty_level, question_type, tags) VALUES 
-- 技术能力题目
(1, '请详细介绍React的生命周期钩子函数，以及它们的使用场景？', '前端开发', 'MEDIUM', 'TECHNICAL', '["React", "JavaScript", "前端框架"]'),
(1, '什么是虚拟DOM？请解释虚拟DOM的工作原理和优势？', '前端开发', 'MEDIUM', 'TECHNICAL', '["React", "虚拟DOM", "性能优化"]'),
(1, '请解释JavaScript中的闭包概念，并举例说明其应用场景？', '前端开发', 'MEDIUM', 'TECHNICAL', '["JavaScript", "闭包", "作用域"]'),
(1, '什么是CSS盒模型？请解释标准盒模型和IE盒模型的区别？', '前端开发', 'EASY', 'TECHNICAL', '["CSS", "盒模型", "布局"]'),
(1, '请介绍ES6的主要特性，以及它们在前端开发中的应用？', '前端开发', 'MEDIUM', 'TECHNICAL', '["JavaScript", "ES6", "现代语法"]'),
(1, '什么是响应式设计？请介绍实现响应式设计的方法？', '前端开发', 'MEDIUM', 'TECHNICAL', '["CSS", "响应式", "移动端"]'),
(1, '请解释浏览器的事件循环机制，以及宏任务和微任务的区别？', '前端开发', 'HARD', 'TECHNICAL', '["JavaScript", "事件循环", "异步编程"]'),
(1, '什么是跨域问题？请介绍几种解决跨域的方法？', '前端开发', 'MEDIUM', 'TECHNICAL', '["网络", "跨域", "CORS"]'),

-- 项目经验题目
(2, '请描述你参与过的最有挑战性的前端项目，以及你在其中承担的角色？', '前端开发', 'MEDIUM', 'BEHAVIORAL', '["项目经验", "团队协作", "技术挑战"]'),
(2, '在项目中遇到过哪些性能问题？你是如何解决的？', '前端开发', 'MEDIUM', 'PROBLEM_SOLVING', '["性能优化", "问题解决", "调试"]'),
(2, '请分享一个你优化前端性能的案例，具体采取了哪些措施？', '前端开发', 'MEDIUM', 'PROBLEM_SOLVING', '["性能优化", "用户体验", "技术方案"]'),
(2, '在团队开发中，你是如何保证代码质量的？', '前端开发', 'MEDIUM', 'BEHAVIORAL', '["代码质量", "团队协作", "最佳实践"]'),
(2, '请描述一个你解决过的复杂前端bug，以及解决过程？', '前端开发', 'MEDIUM', 'PROBLEM_SOLVING', '["调试", "问题解决", "技术能力"]'),

-- 学习能力题目
(3, '最近学习了什么新技术？你是如何快速掌握新技术的？', '前端开发', 'EASY', 'BEHAVIORAL', '["学习能力", "技术更新", "自我提升"]'),
(3, '如何保持技术更新？你通常通过哪些渠道学习新技术？', '前端开发', 'EASY', 'BEHAVIORAL', '["学习能力", "技术更新", "持续学习"]'),
(3, '请分享一个你学习新技术的经历，从入门到应用的整个过程？', '前端开发', 'MEDIUM', 'BEHAVIORAL', '["学习能力", "技术应用", "实践经验"]'),

-- 沟通能力题目
(4, '在与产品经理或设计师沟通时，如果对需求有疑问，你会如何处理？', '前端开发', 'EASY', 'BEHAVIORAL', '["沟通能力", "团队协作", "需求理解"]'),
(4, '请描述一个你与后端开发人员协作的经历？', '前端开发', 'MEDIUM', 'BEHAVIORAL', '["团队协作", "前后端协作", "沟通能力"]'),
(4, '在技术分享或培训中，你是如何确保听众能够理解你的讲解？', '前端开发', 'MEDIUM', 'BEHAVIORAL', '["沟通能力", "技术分享", "表达能力"]');

-- 插入后端开发题目
INSERT INTO questions (category_id, question_text, position, difficulty_level, question_type, tags) VALUES 
-- 技术能力题目
(5, '请详细介绍Java中的多线程编程，以及线程安全的概念？', '后端开发', 'MEDIUM', 'TECHNICAL', '["Java", "多线程", "并发编程"]'),
(5, '什么是Spring框架？请解释Spring的核心特性和优势？', '后端开发', 'MEDIUM', 'TECHNICAL', '["Spring", "Java", "框架"]'),
(5, '请解释数据库事务的ACID特性，以及事务隔离级别？', '后端开发', 'MEDIUM', 'TECHNICAL', '["数据库", "事务", "ACID"]'),
(5, '什么是RESTful API？请介绍RESTful API的设计原则？', '后端开发', 'MEDIUM', 'TECHNICAL', '["API设计", "REST", "Web服务"]'),
(5, '请介绍微服务架构的概念，以及其优缺点？', '后端开发', 'HARD', 'TECHNICAL', '["微服务", "架构设计", "分布式"]'),
(5, '什么是缓存？请介绍几种常用的缓存策略？', '后端开发', 'MEDIUM', 'TECHNICAL', '["缓存", "性能优化", "系统设计"]'),
(5, '请解释JVM的内存模型，以及垃圾回收机制？', '后端开发', 'HARD', 'TECHNICAL', '["JVM", "内存管理", "垃圾回收"]'),
(5, '什么是设计模式？请举例说明几种常用的设计模式？', '后端开发', 'MEDIUM', 'TECHNICAL', '["设计模式", "面向对象", "代码设计"]'),

-- 项目经验题目
(6, '请描述你参与过的最复杂的后端项目，以及你在其中承担的角色？', '后端开发', 'MEDIUM', 'BEHAVIORAL', '["项目经验", "技术挑战", "系统设计"]'),
(6, '在项目中遇到过哪些性能问题？你是如何解决的？', '后端开发', 'MEDIUM', 'PROBLEM_SOLVING', '["性能优化", "问题解决", "系统调优"]'),
(6, '请分享一个你设计数据库架构的案例，考虑了哪些因素？', '后端开发', 'MEDIUM', 'SYSTEM_DESIGN', '["数据库设计", "架构设计", "系统设计"]'),
(6, '在团队开发中，你是如何保证代码质量和系统稳定性？', '后端开发', 'MEDIUM', 'BEHAVIORAL', '["代码质量", "系统稳定性", "最佳实践"]'),
(6, '请描述一个你解决过的系统故障，以及处理过程？', '后端开发', 'MEDIUM', 'PROBLEM_SOLVING', '["故障处理", "问题解决", "系统运维"]'),

-- 学习能力题目
(7, '最近学习了什么新技术？你是如何快速掌握新技术的？', '后端开发', 'EASY', 'BEHAVIORAL', '["学习能力", "技术更新", "自我提升"]'),
(7, '如何保持技术更新？你通常通过哪些渠道学习新技术？', '后端开发', 'EASY', 'BEHAVIORAL', '["学习能力", "技术更新", "持续学习"]'),
(7, '请分享一个你学习新技术的经历，从入门到应用的整个过程？', '后端开发', 'MEDIUM', 'BEHAVIORAL', '["学习能力", "技术应用", "实践经验"]'),

-- 沟通能力题目
(8, '在与产品经理沟通需求时，如果技术实现有困难，你会如何处理？', '后端开发', 'EASY', 'BEHAVIORAL', '["沟通能力", "需求理解", "技术沟通"]'),
(8, '请描述一个你与前端开发人员协作的经历？', '后端开发', 'MEDIUM', 'BEHAVIORAL', '["团队协作", "前后端协作", "沟通能力"]'),
(8, '在技术分享或培训中，你是如何确保听众能够理解你的讲解？', '后端开发', 'MEDIUM', 'BEHAVIORAL', '["沟通能力", "技术分享", "表达能力"]');

-- 插入全栈开发题目
INSERT INTO questions (category_id, question_text, position, difficulty_level, question_type, tags) VALUES 
-- 技术能力题目
(9, '请详细介绍前后端分离架构的优势和实现方式？', '全栈开发', 'MEDIUM', 'TECHNICAL', '["架构设计", "前后端分离", "系统架构"]'),
(9, '什么是CI/CD？请介绍持续集成和持续部署的流程？', '全栈开发', 'MEDIUM', 'TECHNICAL', '["CI/CD", "DevOps", "自动化部署"]'),
(9, '请解释Docker容器化技术，以及其在全栈开发中的应用？', '全栈开发', 'MEDIUM', 'TECHNICAL', '["Docker", "容器化", "部署"]'),
(9, '什么是负载均衡？请介绍几种常用的负载均衡策略？', '全栈开发', 'HARD', 'TECHNICAL', '["负载均衡", "高可用", "系统架构"]'),
(9, '请介绍分布式系统的概念，以及其面临的挑战？', '全栈开发', 'HARD', 'TECHNICAL', '["分布式系统", "系统架构", "高可用"]'),
(9, '什么是消息队列？请介绍消息队列在系统中的应用场景？', '全栈开发', 'MEDIUM', 'TECHNICAL', '["消息队列", "异步处理", "系统设计"]'),
(9, '请解释OAuth2.0认证流程，以及其安全性考虑？', '全栈开发', 'MEDIUM', 'TECHNICAL', '["认证授权", "OAuth", "安全"]'),
(9, '什么是GraphQL？请比较GraphQL和REST API的优缺点？', '全栈开发', 'MEDIUM', 'TECHNICAL', '["GraphQL", "API设计", "数据查询"]'),

-- 项目经验题目
(10, '请描述你参与过的最有挑战性的全栈项目，以及你在其中承担的角色？', '全栈开发', 'MEDIUM', 'BEHAVIORAL', '["项目经验", "全栈开发", "技术挑战"]'),
(10, '在项目中遇到过哪些架构问题？你是如何解决的？', '全栈开发', 'MEDIUM', 'PROBLEM_SOLVING', '["架构设计", "问题解决", "系统优化"]'),
(10, '请分享一个你设计系统架构的案例，考虑了哪些因素？', '全栈开发', 'MEDIUM', 'SYSTEM_DESIGN', '["系统架构", "架构设计", "技术选型"]'),
(10, '在团队开发中，你是如何协调前后端开发工作的？', '全栈开发', 'MEDIUM', 'BEHAVIORAL', '["团队协作", "项目管理", "技术协调"]'),
(10, '请描述一个你解决过的跨技术栈问题，以及处理过程？', '全栈开发', 'MEDIUM', 'PROBLEM_SOLVING', '["问题解决", "技术整合", "跨栈开发"]'),

-- 学习能力题目
(11, '最近学习了什么新技术？你是如何快速掌握新技术的？', '全栈开发', 'EASY', 'BEHAVIORAL', '["学习能力", "技术更新", "自我提升"]'),
(11, '如何保持技术更新？你通常通过哪些渠道学习新技术？', '全栈开发', 'EASY', 'BEHAVIORAL', '["学习能力", "技术更新", "持续学习"]'),
(11, '请分享一个你学习新技术的经历，从入门到应用的整个过程？', '全栈开发', 'MEDIUM', 'BEHAVIORAL', '["学习能力", "技术应用", "实践经验"]'),

-- 沟通能力题目
(12, '在与产品经理沟通需求时，如果技术实现有困难，你会如何处理？', '全栈开发', 'EASY', 'BEHAVIORAL', '["沟通能力", "需求理解", "技术沟通"]'),
(12, '请描述一个你与团队成员协作的经历？', '全栈开发', 'MEDIUM', 'BEHAVIORAL', '["团队协作", "沟通能力", "项目管理"]'),
(12, '在技术分享或培训中，你是如何确保听众能够理解你的讲解？', '全栈开发', 'MEDIUM', 'BEHAVIORAL', '["沟通能力", "技术分享", "表达能力"]');

-- 创建索引以提高查询性能
CREATE INDEX idx_questions_position ON questions(position);
CREATE INDEX idx_questions_category_id ON questions(category_id);
CREATE INDEX idx_questions_difficulty ON questions(difficulty_level);
CREATE INDEX idx_questions_type ON questions(question_type);
CREATE INDEX idx_questions_active ON questions(is_active);

CREATE INDEX idx_categories_position ON question_categories(position);
CREATE INDEX idx_categories_active ON question_categories(is_active);

-- 显示创建结果
SELECT 'Database and tables created successfully!' as result; 