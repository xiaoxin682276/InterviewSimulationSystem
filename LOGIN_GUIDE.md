# 登录功能使用指南

## 功能概述

智能面试模拟系统现已集成完整的用户认证系统，包括：

- 🔐 用户登录/注册
- 👤 用户信息管理
- 💾 数据持久化存储
- 🔒 密码加密保护

## 系统架构

### 后端组件

1. **AuthController** (`backend/src/main/java/com/interview/controller/AuthController.java`)
   - 处理登录请求 (`POST /api/auth/login`)
   - 处理注册请求 (`POST /api/auth/register`)
   - 检查认证状态 (`GET /api/auth/check-auth`)

2. **UserService** (`backend/src/main/java/com/interview/service/UserService.java`)
   - 用户认证逻辑
   - 密码加密验证
   - 用户信息管理

3. **数据库模型**
   - `users` 表：存储用户信息
   - `interview_sessions` 表：面试会话记录
   - `interview_questions` 表：面试问题记录
   - `evaluation_results` 表：评估结果记录

### 前端组件

1. **Login组件** (`frontend/src/components/Login.jsx`)
   - 美观的登录/注册界面
   - 表单验证
   - 错误处理

2. **App组件更新** (`frontend/src/App.jsx`)
   - 登录状态管理
   - 用户菜单
   - 路由保护

## 使用方法

### 1. 启动系统

```bash
# 启动后端服务
cd backend
mvn spring-boot:run

# 启动前端服务
cd frontend
npm start
```

### 2. 访问系统

打开浏览器访问 `http://localhost:3000`，系统会自动跳转到登录页面。

### 3. 测试账号

系统预置了以下测试账号：

| 用户名 | 密码 | 姓名 |
|--------|------|------|
| admin | password | 系统管理员 |
| testuser | password | 测试用户 |
| zhangsan | password | 张三 |
| lisi | password | 李四 |
| wangwu | password | 王五 |

### 4. 注册新用户

1. 在登录页面点击"注册"标签
2. 填写用户信息：
   - 用户名（至少3个字符）
   - 姓名
   - 邮箱（有效格式）
   - 密码（至少6个字符）
   - 确认密码
3. 点击"注册"按钮

### 5. 登录系统

1. 输入用户名和密码
2. 点击"登录"按钮
3. 登录成功后自动进入面试系统

## 功能特性

### 🔐 安全特性

- **密码加密**：使用BCrypt算法加密存储
- **表单验证**：前端和后端双重验证
- **错误处理**：友好的错误提示信息

### 🎨 界面特性

- **响应式设计**：适配不同屏幕尺寸
- **现代化UI**：渐变背景、毛玻璃效果
- **动画效果**：平滑的过渡动画
- **用户友好**：直观的操作流程

### 📊 数据管理

- **用户信息**：完整的用户档案管理
- **面试记录**：自动保存面试会话
- **评估历史**：查看历史评估结果
- **学习路径**：个性化学习建议

## 技术栈

### 后端
- **Spring Boot**：主框架
- **Spring Data JPA**：数据访问
- **MySQL**：数据库
- **BCrypt**：密码加密

### 前端
- **React**：UI框架
- **Ant Design**：组件库
- **CSS3**：样式设计
- **LocalStorage**：本地存储

## 数据库配置

确保MySQL服务已启动，并创建数据库：

```sql
CREATE DATABASE interview_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

更新 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/interview_system?useSSL=false&serverTimezone=UTC
    username: your_username
    password: your_password
```

## 故障排除

### 常见问题

1. **登录失败**
   - 检查用户名和密码是否正确
   - 确认数据库连接正常
   - 查看后端日志错误信息

2. **注册失败**
   - 检查用户名是否已存在
   - 确认邮箱格式正确
   - 验证密码长度是否符合要求

3. **数据库连接错误**
   - 确认MySQL服务正在运行
   - 检查数据库连接配置
   - 验证数据库用户权限

### 日志查看

```bash
# 查看后端日志
tail -f backend/logs/application.log

# 查看前端控制台
# 在浏览器中按F12打开开发者工具
```

## 扩展功能

### 计划中的功能

- [ ] 邮箱验证
- [ ] 密码重置
- [ ] 第三方登录（微信、QQ等）
- [ ] 用户头像上传
- [ ] 面试历史统计
- [ ] 学习进度跟踪

### 自定义开发

如需添加新功能，请参考现有代码结构：

1. 在 `backend/src/main/java/com/interview/controller/` 添加新的控制器
2. 在 `backend/src/main/java/com/interview/service/` 添加业务逻辑
3. 在 `backend/src/main/java/com/interview/entity/` 添加数据模型
4. 在 `frontend/src/components/` 添加前端组件

## 联系支持

如有问题或建议，请联系开发团队或提交Issue。 