import React from 'react';
import { Card, Typography } from 'antd';
import { Radar, RadarChart as RechartsRadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer } from 'recharts';

const { Title } = Typography;

const RadarChart = ({ categoryScores }) => {
  if (!categoryScores || Object.keys(categoryScores).length === 0) {
    return (
      <Card className="card">
        <Title level={3}>能力雷达图</Title>
        <p>暂无评估数据，请先完成面试评估。</p>
      </Card>
    );
  }

  // 将数据转换为雷达图格式
  const data = Object.keys(categoryScores).map(category => ({
    subject: category,
    A: categoryScores[category],
    fullMark: 100,
  }));

  // 计算平均分
  const averageScore = Object.values(categoryScores).reduce((sum, score) => sum + score, 0) / Object.values(categoryScores).length;

  // 根据分数确定颜色
  const getScoreColor = (score) => {
    if (score >= 80) return '#52c41a'; // 绿色 - 优秀
    if (score >= 60) return '#faad14'; // 黄色 - 良好
    return '#ff4d4f'; // 红色 - 需要提升
  };

  return (
    <Card className="card">
      <Title level={3}>能力雷达图</Title>
      <p>展示您在各个维度的能力评估结果：</p>
      
      <div style={{ height: '400px', marginTop: '20px' }}>
        <ResponsiveContainer width="100%" height="100%">
          <RechartsRadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
            <PolarGrid />
            <PolarAngleAxis dataKey="subject" />
            <PolarRadiusAxis angle={30} domain={[0, 100]} />
            <Radar
              name="能力评分"
              dataKey="A"
              stroke={getScoreColor(averageScore)}
              fill={getScoreColor(averageScore)}
              fillOpacity={0.3}
            />
          </RechartsRadarChart>
        </ResponsiveContainer>
      </div>

      {/* 分数详情 */}
      <div style={{ marginTop: '20px' }}>
        <Title level={4}>详细评分</Title>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
          {Object.entries(categoryScores).map(([category, score]) => (
            <div
              key={category}
              style={{
                padding: '12px',
                border: '1px solid #e8e8e8',
                borderRadius: '4px',
                backgroundColor: '#fafafa'
              }}
            >
              <div style={{ fontWeight: 'bold', marginBottom: '4px' }}>
                {category}
              </div>
              <div style={{ 
                fontSize: '18px', 
                fontWeight: 'bold',
                color: getScoreColor(score)
              }}>
                {score.toFixed(1)} 分
              </div>
              <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                {score >= 80 ? '优秀' : score >= 60 ? '良好' : '需要提升'}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 平均分 */}
      <div style={{ 
        marginTop: '20px', 
        padding: '16px', 
        backgroundColor: '#f6ffed', 
        borderRadius: '4px',
        textAlign: 'center'
      }}>
        <Title level={4} style={{ margin: 0, color: getScoreColor(averageScore) }}>
          综合评分: {averageScore.toFixed(1)} 分
        </Title>
        <p style={{ margin: '8px 0 0 0', color: '#52c41a' }}>
          {averageScore >= 80 ? '优秀！您展现了很强的综合能力。' : 
           averageScore >= 60 ? '良好！您具备该岗位的基本要求。' : 
           '需要提升！建议在多个方面加强学习和实践。'}
        </p>
      </div>
    </Card>
  );
};

export default RadarChart; 