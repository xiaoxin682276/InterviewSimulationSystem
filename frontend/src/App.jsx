import React, { useState, useEffect } from 'react';
import { Layout, Card, Typography, Button, Steps, message, Tabs, Dropdown, Menu, Avatar, Spin, List, Divider, Space, Tag } from 'antd';
import { 
  UserOutlined, 
  LogoutOutlined, 
  FileTextOutlined, 
  BarChartOutlined, 
  TrophyOutlined,
  VideoCameraOutlined
} from '@ant-design/icons';
import 'antd/dist/reset.css';

// 导入组件
import Login from './components/Login';
import PositionSelector from './components/PositionSelector';
import QuestionPanel from './components/QuestionPanel';
import AnswerRecorder from './components/AnswerRecorder';
import VideoRecorder from './components/VideoRecorder';
import RadarChart from './components/RadarChart';
import FeedbackPanel from './components/FeedbackPanel';
import EnhancedFeedbackPanel from './components/EnhancedFeedbackPanel';
import { interviewAPI } from './api';

const { Header, Content, Footer } = Layout;
const { Title } = Typography;

const App = () => {
  const [currentStep, setCurrentStep] = useState(0);
  const [selectedPosition, setSelectedPosition] = useState('');
  const [questions, setQuestions] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [videos, setVideos] = useState({});
  const [evaluationResult, setEvaluationResult] = useState(null);
  const [enhancedEvaluationResult, setEnhancedEvaluationResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [evaluationMode, setEvaluationMode] = useState('basic'); // 'basic' or 'enhanced'
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentApiSource, setCurrentApiSource] = useState('默认题库');

  // 检查用户登录状态
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      const userData = JSON.parse(savedUser);
      setUser(userData);
      setIsLoggedIn(true);
    }
  }, []);

  const steps = [
    {
      title: '选择岗位',
      icon: <UserOutlined />,
      description: '选择面试岗位'
    },
    {
      title: '回答问题',
      icon: <FileTextOutlined />,
      description: '完成面试题目'
    },
    {
      title: '查看结果',
      icon: <BarChartOutlined />,
      description: '查看评估结果'
    },
    {
      title: '能力分析',
      icon: <TrophyOutlined />,
      description: '详细能力分析'
    }
  ];

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    setIsLoggedIn(true);
    message.success(`欢迎回来，${userData.fullName || userData.username}！`);
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
    setIsLoggedIn(false);
    resetInterview();
    message.success('已退出登录');
  };

  const handlePositionSelect = (position) => {
    setSelectedPosition(position);
    setCurrentStep(1);
  };

  const handleQuestionsLoaded = (loadedQuestions) => {
    setQuestions(loadedQuestions);
  };

  const handleAnswerSubmit = (questionId, answerData) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: answerData
    }));
  };

  const handleVideoSubmit = (questionId, videoData) => {
    setVideos(prev => ({
      ...prev,
      [questionId]: videoData
    }));
  };

  const handleNextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  const handlePrevQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const handleQuestionSelect = (index) => {
    setCurrentQuestionIndex(index);
  };

  // 新增：判断当前题目已选的回答方式
  const getCurrentAnswerMode = (qid) => {
    if (videos[qid] && videos[qid].video) return 'video';
    if (answers[qid] && answers[qid].audio) return 'audio';
    if (answers[qid] && answers[qid].text) return 'text';
    return null;
  };

  const handleEvaluateInterview = async () => {
    if (Object.keys(answers).length < questions.length) {
      message.warning('请完成所有题目的回答后再进行评估');
      return;
    }

    setLoading(true);
    try {
      // 多模态评估，每题只提交一种方式，优先级：视频>音频>文本
      const multimodalData = {
        position: selectedPosition,
        textData: [],
        audioData: [],
        videoData: []
      };
      questions.forEach(q => {
        const qid = q.id;
        if (videos[qid] && videos[qid].video) {
          multimodalData.videoData.push({
            questionId: qid,
            videoUrl: videos[qid].video
          });
        } else if (answers[qid] && answers[qid].audio) {
          multimodalData.audioData.push({
            questionId: qid,
            audioUrl: answers[qid].audio
          });
        } else if (answers[qid] && answers[qid].text) {
          multimodalData.textData.push({
            questionId: qid,
            question: q.question,
            answer: answers[qid].text,
            resume: ''
          });
        }
      });

      // 检查是否有视频文件用于增强分析
      const hasVideoFile = Object.values(videos).some(video => video && video.file);
      if (hasVideoFile && evaluationMode === 'enhanced') {
        const videoFile = Object.values(videos).find(video => video && video.file)?.file;
        console.log('开始增强多模态评测...');
        const result = await interviewAPI.evaluateInterviewEnhancedWithVideo(multimodalData, videoFile);
        console.log('API返回（增强）:', result);
        setEnhancedEvaluationResult(result);
        console.log('setEnhancedEvaluationResult后:', result);
        setCurrentStep(2);
        message.success('增强多模态评测完成！');
      } else {
        console.log('开始基础多模态评测...');
        const result = await interviewAPI.evaluateInterviewEnhanced(multimodalData);
        console.log('API返回（基础）:', result);
        setEnhancedEvaluationResult(result);
        console.log('setEnhancedEvaluationResult后:', result);
        setCurrentStep(2);
        message.success('多模态评测完成！');
      }
    } catch (error) {
      console.error('评估失败:', error);
      message.error('评估失败，请重试');
    } finally {
      setLoading(false);
      console.log('【handleEvaluateInterview finally】setLoading(false) 执行，当前loading:', loading);
    }
  };

  const resetInterview = () => {
    setCurrentStep(0);
    setSelectedPosition('');
    setQuestions([]);
    setCurrentQuestionIndex(0);
    setAnswers({});
    setVideos({});
    setEvaluationResult(null);
    setEnhancedEvaluationResult(null);
  };

  const renderStepContent = () => {
    switch (currentStep) {
      case 0:
        return <PositionSelector onPositionSelect={handlePositionSelect} />;
      
      case 1:
        return (
          <div>
            <QuestionPanel 
              position={selectedPosition} 
              onQuestionsLoaded={handleQuestionsLoaded}
            />
            {/* 多模态评估：分为“文字/语音回答”和“视频回答”两个Tab */}
            <Card className="card" style={{ marginBottom: '20px' }}>
              <Title level={4}>多模态评估</Title>
              <div style={{color:'#faad14',marginBottom:8,fontWeight:500}}>
                每道题只能提交一种方式，优先级：<b>视频 &gt; 音频 &gt; 文本</b>。如已提交视频，音频和文本按钮将不可用。
              </div>
              <Tabs defaultActiveKey="text_audio" size="large">
                <Tabs.TabPane tab="文字/语音回答" key="text_audio">
                  <AnswerRecorder 
                    currentQuestion={questions[currentQuestionIndex]} 
                    onAnswerSubmit={handleAnswerSubmit}
                    answers={answers}
                    mode="text" // 允许AnswerRecorder内部切换文字/语音
                    totalQuestions={questions.length}
                    currentQuestionIndex={currentQuestionIndex}
                    onNextQuestion={handleNextQuestion}
                    onPrevQuestion={handlePrevQuestion}
                    onQuestionSelect={handleQuestionSelect}
                    answerMode={getCurrentAnswerMode(questions[currentQuestionIndex]?.id)}
                  />
                </Tabs.TabPane>
                <Tabs.TabPane tab="视频回答" key="video">
                  <VideoRecorder 
                    currentQuestion={questions[currentQuestionIndex]} 
                    onVideoSubmit={handleVideoSubmit}
                    videos={videos}
                    answerMode={getCurrentAnswerMode(questions[currentQuestionIndex]?.id)}
                  />
                </Tabs.TabPane>
              </Tabs>
            </Card>
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <Button 
                type="primary" 
                size="large"
                onClick={handleEvaluateInterview}
                loading={loading}
                disabled={Object.keys(answers).length < questions.length}
              >
                完成多模态评估
              </Button>
            </div>
          </div>
        );
      
      case 2:
        console.log('【case 2】currentStep:', currentStep);
        console.log('【case 2】loading:', loading);
        console.log('【case 2】enhancedEvaluationResult:', enhancedEvaluationResult);
        console.log('【case 2】evaluationMode:', evaluationMode);
        return (
          <div>
            {loading || !enhancedEvaluationResult ? (
              <div style={{textAlign:'center',padding:'80px 0'}}>
                <Spin size="large" tip="评估中，请稍候..." style={{fontSize:20}} />
              </div>
            ) : (
              <>
                {console.log('【case 2】准备渲染 EnhancedFeedbackPanel，数据：', enhancedEvaluationResult)}
                <EnhancedFeedbackPanel evaluationResult={enhancedEvaluationResult} hideSummaryTab={true} />
                {console.log('【case 2】EnhancedFeedbackPanel 渲染完毕')}
                <div style={{ textAlign: 'center', marginTop: '20px' }}>
                  <Button 
                    type="primary" 
                    onClick={() => setCurrentStep(3)}
                    style={{ marginRight: '10px' }}
                  >
                    查看详细分析
                  </Button>
                  <Button onClick={resetInterview}>
                    重新开始
                  </Button>
                </div>
              </>
            )}
          </div>
        );
      
      case 3:
        // 课程名与国内可访问学习资源的映射
        const learningResourceMap = {
          '面试表达技巧训练': 'https://www.bilibili.com/video/BV1wF411w7oA',
          '前端开发技能提升课程': 'https://www.bilibili.com/video/BV1oz421q7BB',
          '后端开发技能提升课程': 'https://www.bilibili.com/video/BV1m84y1w7Tb',
          '全栈开发技能提升课程': 'https://www.bilibili.com/video/BV14z4y1N7pg',
          // 可继续扩展其它课程
        };
        // 对learningPaths做前端兜底补全
        const patchedLearningPaths = (enhancedEvaluationResult?.learningPaths || []).map(path => {
          // 判断后端返回的链接是否为无效example.com等
          const isInvalidUrl = path.resourceUrl && /example\.com|test\.com|localhost|127\.0\.0\.1/.test(path.resourceUrl);
          return {
            ...path,
            resourceUrl: (!path.resourceUrl || isInvalidUrl) ? (learningResourceMap[path.title] || '') : path.resourceUrl
          };
        });
        return (
          <div>
            <Card className="card">
              <Title level={3}>个性化改进建议</Title>
              <List
                dataSource={enhancedEvaluationResult?.improvementSuggestions || []}
                renderItem={(item, index) => (
                  <List.Item style={{ padding: '12px 0' }}>
                    <div style={{ display: 'flex', alignItems: 'flex-start' }}>
                      <div style={{
                        backgroundColor: '#52c41a',
                        color: 'white',
                        borderRadius: '50%',
                        width: '24px',
                        height: '24px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '12px',
                        marginRight: '12px',
                        flexShrink: 0
                      }}>
                        {index + 1}
                      </div>
                      <Typography.Paragraph style={{ margin: 0, fontSize: '14px', lineHeight: '1.6' }}>
                        {item}
                      </Typography.Paragraph>
                    </div>
                  </List.Item>
                )}
              />
              <Divider />
              <Title level={3}>个性化学习路径</Title>
              {patchedLearningPaths.length > 0 ? (
                <List
                  dataSource={patchedLearningPaths}
                  renderItem={(path, index) => (
                    <List.Item style={{ padding: '16px 0' }}>
                      <div style={{
                        padding: '16px',
                        border: '1px solid #e8e8e8',
                        borderRadius: '8px',
                        width: '100%'
                      }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                          <div>
                            <Title level={5} style={{ margin: '0 0 4px 0' }}>
                              {path.title}
                            </Title>
                            <Tag color="blue" style={{ marginBottom: '8px' }}>
                              {path.category}
                            </Tag>
                          </div>
                          <div style={{ textAlign: 'right' }}>
                            <Tag color="orange">优先级: {path.priority}</Tag>
                            <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                              预计时长: {path.estimatedTime}小时
                            </div>
                          </div>
                        </div>
                        <Typography.Paragraph style={{ margin: '8px 0', fontSize: '14px' }}>
                          {path.description}
                        </Typography.Paragraph>
                        <Space>
                          <Button
                            type="primary"
                            size="small"
                            icon={<VideoCameraOutlined />}
                            onClick={() => path.resourceUrl && window.open(path.resourceUrl, '_blank')}
                            disabled={!path.resourceUrl}
                          >
                            开始学习
                          </Button>
                          <Button size="small">
                            添加到收藏
                          </Button>
                        </Space>
                        {!path.resourceUrl && (
                          <div style={{ color: '#ff4d4f', marginTop: 8, fontSize: 12 }}>
                            暂无可用学习链接
                          </div>
                        )}
                      </div>
                    </List.Item>
                  )}
                />
              ) : (
                <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                  <VideoCameraOutlined style={{ fontSize: '48px', marginBottom: '16px' }} />
                  <p>暂无推荐的学习路径</p>
                </div>
              )}
            </Card>
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <Button onClick={resetInterview}>
                重新开始
              </Button>
            </div>
          </div>
        );
      
      default:
        return null;
    }
  };

  // 如果未登录，显示登录页面
  if (!isLoggedIn) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  // 用户菜单
  const userMenu = (
    <Menu>
      <Menu.Item key="profile" icon={<UserOutlined />}>
        个人信息
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={handleLogout}>
        退出登录
      </Menu.Item>
    </Menu>
  );

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        background: '#fff', 
        padding: '0 20px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        position: 'sticky',
        top: 0,
        zIndex: 1000
      }}>
        <div style={{ 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'space-between',
          height: '100%',
          maxWidth: '1200px',
          margin: '0 auto'
        }}>
          <Title level={3} style={{ margin: 0, color: '#1890ff' }}>
            智能面试模拟系统
          </Title>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{ color: '#666' }}>
              欢迎，{user?.fullName || user?.username}
            </span>
            <Dropdown overlay={userMenu} trigger={['click']}>
              <Avatar 
                style={{ backgroundColor: '#1890ff', cursor: 'pointer' }}
                icon={<UserOutlined />}
              />
            </Dropdown>
          </div>
        </div>
      </Header>

      <Content style={{ padding: '20px', background: '#f5f5f5' }}>
        <div className="container">
          {/* 步骤指示器 */}
          <div style={{ marginBottom: '30px' }}>
            <Steps 
              current={currentStep} 
              items={steps}
              style={{ background: '#fff', padding: '20px', borderRadius: '8px' }}
            />
          </div>

          {/* 主要内容 */}
          {renderStepContent()}
        </div>
      </Content>

      <Footer style={{ 
        textAlign: 'center', 
        background: '#fff',
        borderTop: '1px solid #e8e8e8'
      }}>
        智能面试模拟系统 ©2024 帮助求职者提升面试技能
      </Footer>
    </Layout>
  );
};

export default App; 