import React, { useState, useEffect } from 'react';
import { Select, Card, Typography } from 'antd';
import { interviewAPI } from '../api';

const { Title } = Typography;
const { Option } = Select;

const PositionSelector = ({ onPositionSelect }) => {
  const [positions, setPositions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedPosition, setSelectedPosition] = useState('');

  useEffect(() => {
    loadPositions();
  }, []);

  const loadPositions = async () => {
    setLoading(true);
    try {
      const data = await interviewAPI.getPositions();
      setPositions(data);
    } catch (error) {
      console.error('加载岗位列表失败:', error);
      // 如果API失败，使用默认岗位列表
      setPositions(['前端开发', '后端开发', '全栈开发']);
    } finally {
      setLoading(false);
    }
  };

  const handlePositionChange = (value) => {
    setSelectedPosition(value);
    onPositionSelect(value);
  };

  return (
    <Card className="card">
      <Title level={3}>选择面试岗位</Title>
      <p>请选择您要面试的岗位类型：</p>
      
      <Select
        placeholder="请选择岗位"
        style={{ width: '100%', marginTop: 16 }}
        value={selectedPosition}
        onChange={handlePositionChange}
        loading={loading}
        size="large"
      >
        {positions.map((position) => (
          <Option key={position} value={position}>
            {position}
          </Option>
        ))}
      </Select>
      
      {selectedPosition && (
        <div style={{ marginTop: 16, padding: 12, backgroundColor: '#f6ffed', borderRadius: 4 }}>
          <p style={{ margin: 0, color: '#52c41a' }}>
            已选择: <strong>{selectedPosition}</strong>
          </p>
        </div>
      )}
    </Card>
  );
};

export default PositionSelector; 