import React from 'react';
import { Card, Typography, List, Tag, Progress, Divider } from 'antd';
import { TrophyOutlined, BulbOutlined, CheckCircleOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

const FeedbackPanel = ({ evaluationResult }) => {
  if (!evaluationResult) {
    return (
      <Card className="card">
        <Title level={3}>评估结果</Title>
        <p>请先完成面试评估以查看结果。</p>
      </Card>
    );
  }

  const { totalScore, categoryScores, recommendations, overallFeedback } = evaluationResult;

  // 根据总分确定等级
  const getScoreLevel = (score) => {
    if (score >= 80) return { level: '优秀', color: '#52c41a', icon: <TrophyOutlined /> };
    if (score >= 60) return { level: '良好', color: '#faad14', icon: <CheckCircleOutlined /> };
    return { level: '需要提升', color: '#ff4d4f', icon: <BulbOutlined /> };
  };

  const scoreLevel = getScoreLevel(totalScore);

  return (
    <Card className="card">
      <Title level={3}>评估结果</Title>
      
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

      {/* 各维度评分 */}
      <div style={{ marginBottom: '24px' }}>
        <Title level={4}>各维度评分</Title>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '16px' }}>
          {Object.entries(categoryScores).map(([category, score]) => {
            const level = getScoreLevel(score);
            return (
              <div
                key={category}
                style={{
                  padding: '16px',
                  border: '1px solid #e8e8e8',
                  borderRadius: '8px',
                  backgroundColor: '#fafafa'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <span style={{ fontWeight: 'bold' }}>{category}</span>
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

      {/* 改进建议 */}
      <div>
        <Title level={4}>
          <BulbOutlined style={{ marginRight: '8px' }} />
          改进建议
        </Title>
        <List
          dataSource={recommendations}
          renderItem={(item, index) => (
            <List.Item style={{ padding: '12px 0' }}>
              <div style={{ display: 'flex', alignItems: 'flex-start' }}>
                <div style={{ 
                  backgroundColor: '#1890ff', 
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

      {/* 总结 */}
      {/* 已移除重复总结模块 */}
    </Card>
  );
};

export default FeedbackPanel; 