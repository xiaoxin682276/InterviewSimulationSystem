import React, { useState, useEffect } from 'react';
import { Card, Typography, Steps, Tag } from 'antd';
import { interviewAPI } from '../api';

const { Title, Paragraph } = Typography;

const QuestionPanel = ({ position, onQuestionsLoaded }) => {
  const [questions, setQuestions] = useState({});
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const [allQuestions, setAllQuestions] = useState([]);

  useEffect(() => {
    if (position) {
      loadQuestions();
    }
  }, [position]);

  const loadQuestions = async () => {
    setLoading(true);
    try {
      const data = await interviewAPI.getQuestions(position);
      setQuestions(data);
      
      // 将问题转换为扁平数组
      const flatQuestions = [];
      Object.keys(data).forEach(category => {
        data[category].forEach((question, index) => {
          flatQuestions.push({
            id: `${category}-${index}`,
            category,
            question,
            step: flatQuestions.length
          });
        });
      });
      
      setAllQuestions(flatQuestions);
      onQuestionsLoaded(flatQuestions);
    } catch (error) {
      console.error('加载面试题目失败:', error);
      // 使用默认题目
      const defaultQuestions = getDefaultQuestions(position);
      setQuestions(defaultQuestions);
      const flatQuestions = [];
      Object.keys(defaultQuestions).forEach(category => {
        defaultQuestions[category].forEach((question, index) => {
          flatQuestions.push({
            id: `${category}-${index}`,
            category,
            question,
            step: flatQuestions.length
          });
        });
      });
      setAllQuestions(flatQuestions);
      onQuestionsLoaded(flatQuestions);
    } finally {
      setLoading(false);
    }
  };

  const getDefaultQuestions = (pos) => {
    const defaultQuestions = {
      '前端开发': {
        '技术能力': [
          '请介绍一下你对HTML、CSS、JavaScript的理解',
          '你熟悉哪些前端框架？请谈谈你的使用经验',
          '如何优化前端性能？'
        ],
        '项目经验': [
          '请描述一个你参与的前端项目',
          '在项目中遇到过哪些技术难点？如何解决的？'
        ],
        '沟通能力': [
          '如何与后端开发人员协作？',
          '如何向非技术人员解释技术问题？'
        ],
        '学习能力': [
          '你如何保持技术更新？',
          '最近学习了什么新技术？'
        ]
      },
      '后端开发': {
        '技术能力': [
          '请介绍一下你对Java的理解',
          '你熟悉哪些数据库？请谈谈你的使用经验',
          '如何设计一个高并发的系统？'
        ],
        '系统设计': [
          '请描述一个你参与的后端项目',
          '如何设计微服务架构？'
        ],
        '问题解决': [
          '如何排查生产环境的问题？',
          '如何保证系统的安全性？'
        ]
      },
      '全栈开发': {
        '技术能力': [
          '请介绍一下你的技术栈',
          '前后端分离的优势是什么？',
          '如何保证前后端接口的一致性？'
        ],
        '系统设计': [
          '请描述一个你参与的全栈项目',
          '如何设计一个完整的系统架构？'
        ],
        '项目经验': [
          '在项目中遇到过哪些挑战？如何解决的？',
          '如何管理项目的技术债务？'
        ],
        '沟通能力': [
          '如何协调前后端开发进度？',
          '如何与产品经理沟通需求？'
        ]
      }
    };
    
    return defaultQuestions[pos] || defaultQuestions['前端开发'];
  };

  const getCurrentQuestion = () => {
    return allQuestions[currentStep] || null;
  };

  const nextQuestion = () => {
    if (currentStep < allQuestions.length - 1) {
      setCurrentStep(currentStep + 1);
    }
  };

  const prevQuestion = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  };

  if (loading) {
    return (
      <Card className="card">
        <Title level={3}>加载面试题目中...</Title>
        <p>正在为您准备面试题目，请稍候...</p>
      </Card>
    );
  }

  if (!position || allQuestions.length === 0) {
    return null;
  }

  const currentQuestion = getCurrentQuestion();

  return (
    <Card className="card">
      <Title level={3}>面试题目</Title>
      
      {/* 进度指示器 */}
      <Steps
        current={currentStep}
        style={{ marginBottom: 24 }}
        items={allQuestions.map((q, index) => ({
          title: `题目 ${index + 1}`,
          description: q.category
        }))}
      />
      
      {/* 当前题目 */}
      {currentQuestion && (
        <div>
          <div style={{ marginBottom: 16 }}>
            <Tag color="blue" style={{ marginBottom: 8 }}>
              {currentQuestion.category}
            </Tag>
            <Title level={4} style={{ marginTop: 8 }}>
              题目 {currentStep + 1} / {allQuestions.length}
            </Title>
          </div>
          
          <Paragraph style={{ 
            fontSize: '16px', 
            lineHeight: '1.6',
            backgroundColor: '#fafafa',
            padding: '16px',
            borderRadius: '4px',
            border: '1px solid #e8e8e8'
          }}>
            {currentQuestion.question}
          </Paragraph>
          
          {/* 导航按钮 */}
          <div style={{ marginTop: 24, display: 'flex', justifyContent: 'space-between' }}>
            <button 
              className="btn" 
              onClick={prevQuestion}
              disabled={currentStep === 0}
              style={{ backgroundColor: currentStep === 0 ? '#d9d9d9' : '#1890ff' }}
            >
              上一题
            </button>
            
            <span style={{ color: '#666' }}>
              {currentStep + 1} / {allQuestions.length}
            </span>
            
            <button 
              className="btn" 
              onClick={nextQuestion}
              disabled={currentStep === allQuestions.length - 1}
              style={{ backgroundColor: currentStep === allQuestions.length - 1 ? '#d9d9d9' : '#1890ff' }}
            >
              下一题
            </button>
          </div>
        </div>
      )}
    </Card>
  );
};

export default QuestionPanel; 