import React, { useState, useEffect } from 'react';
import { Layout, Typography, Button, message, Steps, Tabs, Card, Avatar, Dropdown, Menu } from 'antd';
import { UserOutlined, FileTextOutlined, BarChartOutlined, TrophyOutlined, AudioOutlined, VideoCameraOutlined, LogoutOutlined } from '@ant-design/icons';
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
  const [answers, setAnswers] = useState({});
  const [videos, setVideos] = useState({});
  const [evaluationResult, setEvaluationResult] = useState(null);
  const [enhancedEvaluationResult, setEnhancedEvaluationResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [evaluationMode, setEvaluationMode] = useState('basic'); // 'basic' or 'enhanced'
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

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

  const handleEvaluateInterview = async () => {
    if (Object.keys(answers).length < questions.length) {
      message.warning('请完成所有题目的回答后再进行评估');
      return;
    }

    setLoading(true);
    try {
      if (evaluationMode === 'basic') {
        const answerTexts = questions.map(q => answers[q.id]?.text || '');
        const questionTexts = questions.map(q => q.question);

        const evaluationData = {
          position: selectedPosition,
          answers: answerTexts,
          questions: questionTexts
        };

        const result = await interviewAPI.evaluateInterview(evaluationData);
        setEvaluationResult(result);
        setCurrentStep(2);
        message.success('基础评估完成！');
      } else {
        // 多模态评估
        const multimodalData = {
          position: selectedPosition,
          textData: questions.map(q => ({
            questionId: q.id,
            question: q.question,
            answer: answers[q.id]?.text || '',
            resume: '' // 可以从用户输入获取
          })),
          audioData: questions.map(q => ({
            questionId: q.id,
            audioUrl: answers[q.id]?.audio || ''
          })),
          videoData: questions.map(q => ({
            questionId: q.id,
            videoUrl: videos[q.id]?.video || ''
          }))
        };

        // 检查是否有视频文件用于增强分析
        const hasVideoFile = Object.values(videos).some(video => video && video.file);
        
        if (hasVideoFile && evaluationMode === 'enhanced') {
          // 使用增强的多模态评估（包含面部情感和肢体语言分析）
          const videoFile = Object.values(videos).find(video => video && video.file)?.file;
          const result = await interviewAPI.evaluateInterviewEnhancedWithVideo(multimodalData, videoFile);
          setEnhancedEvaluationResult(result);
          setCurrentStep(2);
          message.success('增强多模态评估完成！包含面部情感和肢体语言分析');
        } else {
          // 使用基础多模态评估
          const result = await interviewAPI.evaluateInterviewEnhanced(multimodalData);
          setEnhancedEvaluationResult(result);
          setCurrentStep(2);
          message.success('多模态评估完成！');
        }
      }
    } catch (error) {
      console.error('评估失败:', error);
      message.error('评估失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const resetInterview = () => {
    setCurrentStep(0);
    setSelectedPosition('');
    setQuestions([]);
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
            
            {/* 评估模式选择 */}
            <Card className="card" style={{ marginBottom: '20px' }}>
              <Title level={4}>选择评估模式</Title>
              <Tabs 
                activeKey={evaluationMode} 
                onChange={setEvaluationMode}
                items={[
                  {
                    key: 'basic',
                    label: (
                      <span>
                        <FileTextOutlined /> 基础评估
                      </span>
                    ),
                    children: (
                      <div>
                        <AnswerRecorder 
                          currentQuestion={questions[0]} 
                          onAnswerSubmit={handleAnswerSubmit}
                          answers={answers}
                        />
                      </div>
                    )
                  },
                  {
                    key: 'enhanced',
                    label: (
                      <span>
                        <VideoCameraOutlined /> 多模态评估
                      </span>
                    ),
                    children: (
                      <div>
                        <Tabs defaultActiveKey="text" size="small">
                          <Tabs.TabPane tab="文字回答" key="text">
                            <AnswerRecorder 
                              currentQuestion={questions[0]} 
                              onAnswerSubmit={handleAnswerSubmit}
                              answers={answers}
                            />
                          </Tabs.TabPane>
                          <Tabs.TabPane tab="语音录制" key="audio">
                            <AnswerRecorder 
                              currentQuestion={questions[0]} 
                              onAnswerSubmit={handleAnswerSubmit}
                              answers={answers}
                            />
                          </Tabs.TabPane>
                          <Tabs.TabPane tab="视频录制" key="video">
                            <VideoRecorder 
                              currentQuestion={questions[0]} 
                              onVideoSubmit={handleVideoSubmit}
                              videos={videos}
                            />
                          </Tabs.TabPane>
                        </Tabs>
                      </div>
                    )
                  }
                ]}
              />
            </Card>
            
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <Button 
                type="primary" 
                size="large"
                onClick={handleEvaluateInterview}
                loading={loading}
                disabled={Object.keys(answers).length < questions.length}
              >
                完成{evaluationMode === 'enhanced' ? '多模态' : ''}评估
              </Button>
            </div>
          </div>
        );
      
      case 2:
        return (
          <div>
            {evaluationMode === 'enhanced' ? (
              <EnhancedFeedbackPanel evaluationResult={enhancedEvaluationResult} />
            ) : (
              <FeedbackPanel evaluationResult={evaluationResult} />
            )}
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
          </div>
        );
      
      case 3:
        return (
          <div>
            <RadarChart categoryScores={
              evaluationMode === 'enhanced' 
                ? enhancedEvaluationResult?.coreCompetencies 
                : evaluationResult?.categoryScores
            } />
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