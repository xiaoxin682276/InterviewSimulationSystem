# 智能面试模拟系统 - 前端

这是智能面试模拟系统的前端部分，基于React开发，提供直观的用户界面来帮助求职者进行面试模拟。

## 功能特性

- 🎯 **岗位选择**: 支持前端开发、后端开发、全栈开发等岗位
- 📝 **题目展示**: 根据岗位动态加载相关面试题目
- 🎤 **答案记录**: 支持文字输入和语音录制两种方式
- 📊 **能力评估**: 智能分析答案并给出评分
- 📈 **雷达图**: 可视化展示各维度能力评分
- 💡 **改进建议**: 提供个性化的学习和提升建议

## 技术栈

- **React 18**: 前端框架
- **Ant Design**: UI组件库
- **Recharts**: 图表库（雷达图）
- **Axios**: HTTP客户端
- **React Scripts**: 开发和构建工具

## 项目结构

```
frontend/
├── public/
│   └── index.html              # HTML主页面
├── src/
│   ├── components/
│   │   ├── PositionSelector.jsx    # 岗位选择组件
│   │   ├── QuestionPanel.jsx       # 题目展示组件
│   │   ├── AnswerRecorder.jsx      # 答案记录组件
│   │   ├── RadarChart.jsx          # 雷达图组件
│   │   └── FeedbackPanel.jsx       # 反馈面板组件
│   ├── App.jsx                     # 主应用组件
│   ├── api.js                      # API接口封装
│   ├── index.js                    # 应用入口
│   └── index.css                   # 全局样式
├── package.json                    # 项目配置
└── README.md                       # 项目文档
```

## 快速开始

### 环境要求

- Node.js >= 14.0.0
- npm >= 6.0.0

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm start
```

应用将在 [http://localhost:3000](http://localhost:3000) 启动。

### 构建生产版本

```bash
npm run build
```

## 使用说明

1. **选择岗位**: 在首页选择您要面试的岗位类型
2. **回答问题**: 根据题目提供文字或语音答案
3. **查看评估**: 系统会智能分析您的答案并给出评分
4. **能力分析**: 通过雷达图查看各维度的详细评分
5. **改进建议**: 根据评估结果获得个性化的学习建议

## API接口

前端通过以下接口与后端通信：

- `GET /api/interview/positions` - 获取可用岗位列表
- `GET /api/interview/questions` - 根据岗位获取面试题目
- `POST /api/interview/evaluate` - 提交面试评估

## 开发说明

### 组件说明

- **PositionSelector**: 岗位选择器，支持下拉选择
- **QuestionPanel**: 题目展示面板，包含进度指示器
- **AnswerRecorder**: 答案记录器，支持文字和语音输入
- **RadarChart**: 能力雷达图，可视化展示评分
- **FeedbackPanel**: 反馈面板，展示评估结果和建议

### 状态管理

使用React Hooks进行状态管理：
- `useState`: 管理组件内部状态
- `useEffect`: 处理副作用和API调用

### 样式设计

- 使用Ant Design组件库确保UI一致性
- 自定义CSS类提供额外样式
- 响应式设计适配不同屏幕尺寸

## 部署说明

### 开发环境

1. 确保后端服务运行在 `http://localhost:8080`
2. 启动前端开发服务器：`npm start`
3. 访问 `http://localhost:3000`

### 生产环境

1. 构建项目：`npm run build`
2. 将 `build` 目录部署到Web服务器
3. 配置反向代理将API请求转发到后端

## 故障排除

### 常见问题

1. **API连接失败**: 检查后端服务是否正常运行
2. **录音功能不可用**: 确保浏览器支持MediaRecorder API
3. **图表不显示**: 检查Recharts依赖是否正确安装

### 调试技巧

- 使用浏览器开发者工具查看网络请求
- 检查控制台错误信息
- 验证API接口返回的数据格式

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License 