import React from 'react';
import { Card, Typography, List, Tag, Progress, Divider, Tabs, Button, Space } from 'antd';
import { TrophyOutlined, BulbOutlined, CheckCircleOutlined, BookOutlined, PlayCircleOutlined } from '@ant-design/icons';
import { Radar, RadarChart as RechartsRadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer } from 'recharts';

const { Title, Paragraph } = Typography;
const { TabPane } = Tabs;

const EnhancedFeedbackPanel = ({ evaluationResult, defaultActiveKey }) => {
  if (!evaluationResult || !evaluationResult.totalScore || isNaN(evaluationResult.totalScore)) {
    return (
      <Card className="card">
        <Title level={3}>多模态评估结果</Title>
        <p>暂无评估数据，请先完成面试评估。</p>
      </Card>
    );
  }

  const {
    totalScore,
    coreCompetencies = {},
    keyIssues = [],
    improvementSuggestions = [],
    overallFeedback = '',
    learningPaths = [],
    questionAnalyses = [],
    multimodalInsights = {},
  } = evaluationResult;

  // log输出每题分析内容
  console.log('EnhancedFeedbackPanel questionAnalyses:', questionAnalyses);

  // 根据总分确定等级
  const getScoreLevel = (score) => {
    if (score >= 80) return { level: '优秀', color: '#52c41a', icon: <TrophyOutlined /> };
    if (score >= 60) return { level: '良好', color: '#faad14', icon: <CheckCircleOutlined /> };
    return { level: '需要提升', color: '#ff4d4f', icon: <BulbOutlined /> };
  };

  const scoreLevel = getScoreLevel(totalScore);

  // 准备雷达图数据
  const radarData = Object.keys(coreCompetencies).map(competency => ({
    subject: competency,
    A: coreCompetencies[competency],
    fullMark: 100,
  }));

  return (
    <Card className="card">
      <Title level={3}>多模态智能评估结果</Title>
      
      {/* 总分展示 */}
      <div style={{ 
        textAlign: 'center', 
        padding: '24px', 
        backgroundColor: '#f6ffed', 
        borderRadius: '8px',
        marginBottom: '24px'
      }}>
        <div style={{ fontSize: '48px', color: scoreLevel.color, marginBottom: '8px' }}>
          {scoreLevel.icon}
        </div>
        <Title level={2} style={{ margin: '0 0 8px 0', color: scoreLevel.color }}>
          {totalScore.toFixed(1)} 分
        </Title>
        <Tag color={scoreLevel.color} style={{ fontSize: '16px', padding: '4px 12px' }}>
          {scoreLevel.level}
        </Tag>
        <Paragraph style={{ margin: '16px 0 0 0', fontSize: '16px' }}>
          {overallFeedback}
        </Paragraph>
      </div>

      {/* 标签页内容 */}
      <Tabs defaultActiveKey={defaultActiveKey || "1"} size="large">
        <TabPane tab="能力雷达图" key="1">
          <div style={{ height: '400px', marginTop: '20px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <RechartsRadarChart cx="50%" cy="50%" outerRadius="80%" data={Object.keys(coreCompetencies).map(competency => ({
                subject: competency,
                A: coreCompetencies[competency],
                fullMark: 100,
              }))}>
                <PolarGrid />
                <PolarAngleAxis dataKey="subject" />
                <PolarRadiusAxis angle={30} domain={[0, 100]} />
                <Radar
                  name="能力评分"
                  dataKey="A"
                  stroke={scoreLevel.color}
                  fill={scoreLevel.color}
                  fillOpacity={0.3}
                />
              </RechartsRadarChart>
            </ResponsiveContainer>
          </div>
        </TabPane>
        {/* 移除每题分析Tab */}
        <TabPane tab="详细评分" key="2">
          <div style={{ marginTop: '20px' }}>
            <Title level={4}>六项核心能力评估</Title>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '16px' }}>
              {Object.entries(coreCompetencies || {}).map(([competency, score]) => {
                const level = getScoreLevel(score);
                return (
                  <div
                    key={competency}
                    style={{
                      padding: '16px',
                      border: '1px solid #e8e8e8',
                      borderRadius: '8px',
                      backgroundColor: '#fafafa'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <span style={{ fontWeight: 'bold' }}>{competency}</span>
                      <Tag color={level.color}>{level.level}</Tag>
                    </div>
                    <Progress 
                      percent={score} 
                      strokeColor={level.color}
                      format={(percent) => `${percent.toFixed(1)}分`}
                      style={{ marginBottom: '8px' }}
                    />
                    <div style={{ fontSize: '12px', color: '#666' }}>
                      {score >= 80 ? '表现优秀，继续保持！' : 
                       score >= 60 ? '表现良好，有提升空间。' : 
                       '需要加强学习和实践。'}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </TabPane>

        <TabPane tab="关键问题" key="3">
          <div style={{ marginTop: '20px' }}>
            <Title level={4}>
              <BulbOutlined style={{ marginRight: '8px' }} />
              关键问题定位
            </Title>
            <List
              dataSource={keyIssues || []}
              renderItem={(item, index) => (
                <List.Item style={{ padding: '12px 0' }}>
                  <div style={{ display: 'flex', alignItems: 'flex-start' }}>
                    <div style={{ 
                      backgroundColor: '#ff4d4f', 
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
                    <Paragraph style={{ margin: 0, fontSize: '14px', lineHeight: '1.6' }}>
                      {item}
                    </Paragraph>
                  </div>
                </List.Item>
              )}
            />
          </div>
        </TabPane>
      </Tabs>

      {/* 总结 */}
      {/* 已移除重复总结模块 */}
    </Card>
  );
};

export default EnhancedFeedbackPanel; 