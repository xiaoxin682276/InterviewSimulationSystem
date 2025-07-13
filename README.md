# 智能面试模拟系统 (SmartInterviewProject)

一个基于Spring Boot + React的智能面试模拟系统，帮助求职者提升面试技能。

## 项目概述

本系统提供智能化的面试模拟体验，包括岗位选择、题目展示、答案记录、能力评估和个性化建议等功能。通过AI驱动的评分算法，为用户提供客观的能力评估和改进建议。

## 技术架构

### 后端技术栈
- **Spring Boot 2.7+**: 主框架
- **Java 11+**: 编程语言
- **Maven**: 依赖管理
- **Spring Web**: RESTful API
- **Spring Boot DevTools**: 开发工具

### 前端技术栈
- **React 18**: 前端框架
- **Ant Design**: UI组件库
- **Recharts**: 图表库
- **Axios**: HTTP客户端
- **React Scripts**: 开发和构建工具

## 项目结构

```
SmartInterviewProject/
├── backend/                             # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/interview/
│   │   │   │   ├── InterviewApplication.java        # 启动程序
│   │   │   │   ├── controller/
│   │   │   │   │   └── InterviewController.java      # 控制器
│   │   │   │   ├── service/
│   │   │   │   │   └── EvaluationService.java        # 打分逻辑
│   │   │   │   ├── model/
│   │   │   │   │   ├── InterviewRequest.java         # 接收前端数据
│   │   │   │   │   └── EvaluationResult.java         # 返回数据结构
│   │   │   ├── resources/
│   │   │   │   ├── application.yml                   # 配置文件
│   │   │   │   ├── static/questions.json             # 面试题库
│   │   │   │   └── static/recommendations.json       # 推荐资源
│   ├── pom.xml                                        # Maven配置
│
├── frontend/                            # React 前端项目
│   ├── public/
│   │   └── index.html                   # HTML 主页面
│   ├── src/
│   │   ├── components/
│   │   │   ├── PositionSelector.jsx     # 岗位选择
│   │   │   ├── QuestionPanel.jsx       # 题目展示
│   │   │   ├── AnswerRecorder.jsx      # 记录/输入回答
│   │   │   ├── RadarChart.jsx          # 能力雷达图
│   │   │   └── FeedbackPanel.jsx       # 分数和建议展示
│   │   ├── App.jsx
│   │   ├── api.js                      # 调用 Spring Boot 接口
│   │   └── index.js
│   ├── package.json
│   └── README.md

└── README.md                           # 项目总述 + 部署说明
```

## 核心功能

### 🎯 岗位选择
- 支持前端开发、后端开发、全栈开发等岗位
- 根据岗位动态加载相关面试题目

### 📝 智能问答
- 文字输入和语音录制两种答题方式
- 实时保存答案，支持断点续答

### 📊 能力评估
- AI驱动的智能评分算法
- 多维度能力分析（技术能力、项目经验、沟通能力等）
- 个性化权重配置

### 📈 可视化分析
- 雷达图展示各维度能力评分
- 详细的能力分析和改进建议
- 综合评分和等级评定

### 💡 个性化建议
- 基于评估结果的个性化学习建议
- 针对性的技能提升指导
- 学习资源推荐

## 快速开始

### 环境要求

- **Java**: 11+
- **Node.js**: 14+
- **Maven**: 3.6+
- **npm**: 6+

### 后端启动

```bash
# 进入后端目录
cd backend

# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm start
```

前端应用将在 `http://localhost:3000` 启动。

## API接口文档

### 获取岗位列表
```
GET /api/interview/positions
```

### 获取面试题目
```
GET /api/interview/questions?position={position}
```

### 提交面试评估
```
POST /api/interview/evaluate
Content-Type: application/json

{
  "position": "前端开发",
  "answers": ["答案1", "答案2", ...],
  "questions": ["问题1", "问题2", ...]
}
```

## 部署说明

### 开发环境

1. 启动后端服务
2. 启动前端开发服务器
3. 访问 `http://localhost:3000`

### 生产环境

#### 后端部署
```bash
# 打包
mvn clean package

# 运行JAR文件
java -jar target/interview-simulation-system-0.1.0.jar
```

#### 前端部署
```bash
# 构建生产版本
npm run build

# 将build目录部署到Web服务器
```

### Docker部署

```bash
# 构建后端镜像
docker build -t interview-backend ./backend

# 构建前端镜像
docker build -t interview-frontend ./frontend

# 运行容器
docker run -p 8080:8080 interview-backend
docker run -p 80:80 interview-frontend
```

## 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: interview-simulation-system

logging:
  level:
    com.interview: DEBUG
```

### 前端配置 (package.json)

```json
{
  "proxy": "http://localhost:8080"
}
```

## 开发指南

### 添加新的岗位类型

1. 在 `backend/src/main/java/com/interview/controller/InterviewController.java` 中添加岗位
2. 在 `backend/src/main/resources/static/questions.json` 中添加题目
3. 在 `backend/src/main/resources/static/recommendations.json` 中添加建议

### 自定义评分算法

修改 `backend/src/main/java/com/interview/service/EvaluationService.java` 中的评分逻辑。

### 扩展前端组件

在 `frontend/src/components/` 目录下添加新的React组件。

## 测试

### 后端测试
```bash
cd backend
mvn test
```

### 前端测试
```bash
cd frontend
npm test
```

## 故障排除

### 常见问题

1. **端口冲突**: 修改 `application.yml` 中的端口配置
2. **跨域问题**: 检查CORS配置
3. **API连接失败**: 确认后端服务正常运行
4. **录音功能不可用**: 检查浏览器权限设置

### 日志查看

```bash
# 后端日志
tail -f backend/logs/application.log

# 前端控制台
# 打开浏览器开发者工具查看
```

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 版本历史

- **v1.0.0** - 初始版本，基础面试模拟功能
- **v1.1.0** - 添加语音录制功能
- **v1.2.0** - 优化评分算法，增加更多岗位支持

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [GitHub仓库地址]

## 致谢

感谢所有为这个项目做出贡献的开发者和用户。 