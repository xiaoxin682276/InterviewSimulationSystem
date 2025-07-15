import React, { useState, useEffect } from 'react';
import { Card, Typography, Steps, Tag, Button, Spin } from 'antd';
import { interviewAPI } from '../api';
import { generateQuestionDirect } from '../api';
import { generateQuestionsBatch } from '../api';
import { startGenerateQuestionsAsync, getGenerateQuestionsResult } from '../api';

const { Title, Paragraph } = Typography;

const QuestionPanel = ({ position, onQuestionsLoaded }) => {
  const [questions, setQuestions] = useState({});
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const [allQuestions, setAllQuestions] = useState([]);
  const [apiSource, setApiSource] = useState('');
  const [progressText, setProgressText] = useState('正在生成题目...');

  useEffect(() => {
    if (position) {
      autoGenerateBatchQuestionsAsync();
    }
  }, [position]);

  // 异步批量生成题目（带进度条）
  const autoGenerateBatchQuestionsAsync = async () => {
    setLoading(true);
    setProgressText('正在为您生成面试题目，请耐心等待...');
    try {
      const { taskId } = await startGenerateQuestionsAsync(position, 1, 5); // 只生成5道题
      let pollCount = 0;
      let maxPoll = 150; // 最多轮询5分钟
      let done = false;
      while (!done && pollCount < maxPoll) {
        await new Promise(r => setTimeout(r, 2000));
        pollCount++;
        const { status, questions } = await getGenerateQuestionsResult(taskId);
        if (status === 'done') {
          let questionsArr = questions || [];
          const flatQuestions = (questionsArr || []).map((q, idx) => ({
            id: q.id || idx,
            category: q.category_id || '未分组',
            question: q.question_text || '',
            step: idx
          }));
          setAllQuestions(flatQuestions);
          onQuestionsLoaded(flatQuestions);
          setApiSource('大模型异步批量生成');
          setProgressText('题目生成完成！');
          done = true;
        } else if (status === 'error') {
          setProgressText('生成失败，请重试');
          setLoading(false);
          done = true;
        }
      }
      if (!done) {
        setProgressText('生成超时，请重试');
      }
    } catch (e) {
      setProgressText('生成失败，请重试');
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  const loadQuestions = async () => {
    setLoading(true);
    try {
      console.log('从数据库获取题目...');
      const data = await interviewAPI.getQuestions(position, { count: 5 }); // 只获取5道题
      console.log('题目数据:', data);
      let flatQuestions = [];
      // 适配对象数组
      if (Array.isArray(data)) {
        flatQuestions = data.map((q, idx) => ({
          id: q.id || idx,
          category: q.category?.name || q.category || '未分组',
          question: q.question_text || q.question || '',
          step: idx
        }));
      } else {
        // 兼容原有分组结构
        Object.keys(data).forEach(category => {
          data[category].forEach((question, index) => {
            flatQuestions.push({
              id: `${category}-${index}`,
              category,
              question: typeof question === 'string' ? question : (question.question_text || question.question || ''),
              step: flatQuestions.length
            });
          });
        });
      }
      setAllQuestions(flatQuestions);
      onQuestionsLoaded(flatQuestions);
      setApiSource('数据库题库');
      console.log('成功从数据库获取题目');
    } catch (error) {
      console.error('数据库获取失败，使用默认题目:', error);
      
      // 如果数据库获取失败，使用默认题目
      const defaultQuestions = getDefaultQuestions(position);
      setQuestions(defaultQuestions);
      setApiSource('默认题库');
      
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
      console.log('使用默认题库');
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

  // 新增：直接生成题目（不入库）
  const handleGenerateDirect = async () => {
    const prompt = `请生成一道${position || '前端开发'}相关的中等难度技术面试题`;
    const categoryId = 1;
    try {
      const data = await generateQuestionDirect(prompt, categoryId);
      // data为JSON字符串，需解析
      const questionObj = typeof data === 'string' ? JSON.parse(data) : data;
      const generatedQuestions = [{
        id: questionObj.id || 0,
        category: questionObj.category_id || '未分组',
        question: questionObj.question_text || '',
        step: 0
      }];
      setAllQuestions(generatedQuestions);
      onQuestionsLoaded(generatedQuestions);
    } catch (e) {
      console.error('直接生成题目失败', e);
    }
  };

  if (loading) {
    return (
      <div style={{ margin: 48, textAlign: 'center' }}>
        <Spin size="large" tip={progressText} style={{ fontSize: 20 }} />
        <div style={{ marginTop: 16, color: '#888', fontSize: 18 }}>
          <span role="img" aria-label="loading">⏳</span> 正在生成面试题目，请稍候...
        </div>
      </div>
    );
  }

  if (!position) {
    return null;
  }

  const currentQuestion = getCurrentQuestion();

  return (
    <Card className="card">
      <Title level={3}>面试题目</Title>
      {/* 按钮始终显示 */}
      {/* 注释掉按钮相关代码（如需保留可隐藏） */}
      {/* <Button 
        type="primary" 
        onClick={loadQuestions}
        style={{ marginBottom: 16, marginRight: 8 }}
      >
        换一批题目
      </Button> */}
      {/* <Button
        type="dashed"
        onClick={handleGenerateDirect}
        style={{ marginBottom: 16 }}
      >
        直接生成题目（大模型）
      </Button> */}
      {/* 只有有题目时才渲染题目内容 */}
      {allQuestions.length > 0 && (
        <>
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
              {/* 导航按钮（题目正下方） */}
              <div style={{ display: 'flex', justifyContent: 'center', margin: '24px 0' }}>
                <Button
                  onClick={prevQuestion}
                  disabled={currentStep === 0}
                  style={{ marginRight: 16 }}
                >
                  上一题
                </Button>
                <Button
                  type="primary"
                  onClick={nextQuestion}
                  disabled={currentStep === allQuestions.length - 1}
                >
                  下一题
                </Button>
              </div>
            </div>
          )}
        </>
      )}
    </Card>
  );
};

export default QuestionPanel; 