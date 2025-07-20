import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Typography, 
  Avatar, 
  Button, 
  Form, 
  Input, 
  message, 
  Row, 
  Col, 
  Statistic, 
  Divider,
  Descriptions,
  Space,
  Tag,
  Upload,
  Modal
} from 'antd';
import { 
  UserOutlined, 
  EditOutlined, 
  SaveOutlined, 
  CloseOutlined,
  TrophyOutlined,
  FileTextOutlined,
  VideoCameraOutlined,
  BarChartOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;
const { TextArea } = Input;

const Profile = ({ user, onBack }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [profileData, setProfileData] = useState({
    username: user?.username || '',
    fullName: user?.fullName || '',
    email: user?.email || '',
    phone: user?.phone || '',
    position: user?.position || '',
    experience: user?.experience || '',
    skills: user?.skills || [],
    bio: user?.bio || '',
    avatar: user?.avatar || null
  });

  // 模拟统计数据
  const [stats, setStats] = useState({
    totalInterviews: 12,
    completedInterviews: 8,
    averageScore: 85.6,
    improvementRate: 23.5
  });

  useEffect(() => {
    form.setFieldsValue(profileData);
  }, [profileData, form]);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async (values) => {
    setLoading(true);
    try {
      // 这里可以调用API保存用户信息
      setProfileData({ ...profileData, ...values });
      setIsEditing(false);
      message.success('个人信息更新成功！');
    } catch (error) {
      message.error('保存失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    form.setFieldsValue(profileData);
  };

  const handleAvatarChange = (info) => {
    if (info.file.status === 'done') {
      setProfileData(prev => ({
        ...prev,
        avatar: info.file.response?.url || info.file.thumbUrl
      }));
      message.success('头像上传成功！');
    }
  };

  const avatarUploadProps = {
    name: 'avatar',
    action: '/api/upload/avatar', // 这里需要后端提供上传接口
    showUploadList: false,
    onChange: handleAvatarChange,
  };

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '20px' }}>
      <div style={{ marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
        <Button icon={<CloseOutlined />} onClick={onBack}>
          返回
        </Button>
        <Title level={2} style={{ margin: 0 }}>个人信息</Title>
      </div>

      <Row gutter={[24, 24]}>
        {/* 左侧：个人信息卡片 */}
        <Col xs={24} lg={16}>
          <Card>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <Title level={3}>基本信息</Title>
              {!isEditing ? (
                <Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
                  编辑信息
                </Button>
              ) : (
                <Space>
                  <Button onClick={handleCancel}>取消</Button>
                  <Button type="primary" icon={<SaveOutlined />} onClick={() => form.submit()}>
                    保存
                  </Button>
                </Space>
              )}
            </div>

            <Form
              form={form}
              layout="vertical"
              onFinish={handleSave}
              disabled={!isEditing}
            >
              <Row gutter={16}>
                <Col span={24}>
                  <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                    <Upload {...avatarUploadProps}>
                      <div style={{ position: 'relative', display: 'inline-block' }}>
                        <Avatar 
                          size={100} 
                          src={profileData.avatar}
                          icon={<UserOutlined />}
                          style={{ cursor: isEditing ? 'pointer' : 'default' }}
                        />
                        {isEditing && (
                          <div style={{
                            position: 'absolute',
                            bottom: 0,
                            right: 0,
                            background: '#1890ff',
                            color: 'white',
                            borderRadius: '50%',
                            width: '24px',
                            height: '24px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: '12px'
                          }}>
                            <EditOutlined />
                          </div>
                        )}
                      </div>
                    </Upload>
                    {isEditing && <Text type="secondary">点击头像更换</Text>}
                  </div>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="用户名"
                    name="username"
                    rules={[{ required: true, message: '请输入用户名' }]}
                  >
                    <Input prefix={<UserOutlined />} />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="真实姓名"
                    name="fullName"
                  >
                    <Input />
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="邮箱"
                    name="email"
                    rules={[
                      { type: 'email', message: '请输入有效的邮箱地址' }
                    ]}
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="手机号"
                    name="phone"
                  >
                    <Input />
                  </Form.Item>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="目标岗位"
                    name="position"
                  >
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="工作经验"
                    name="experience"
                  >
                    <Input placeholder="如：3年" />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                label="技能标签"
                name="skills"
              >
                <Input placeholder="用逗号分隔，如：JavaScript, React, Node.js" />
              </Form.Item>

              <Form.Item
                label="个人简介"
                name="bio"
              >
                <TextArea rows={4} placeholder="请简要介绍自己的背景和目标..." />
              </Form.Item>
            </Form>
          </Card>
        </Col>

        {/* 右侧：统计信息 */}
        <Col xs={24} lg={8}>
          <Card>
            <Title level={4}>面试统计</Title>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Statistic
                  title="总面试次数"
                  value={stats.totalInterviews}
                  prefix={<FileTextOutlined />}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="已完成"
                  value={stats.completedInterviews}
                  prefix={<TrophyOutlined />}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="平均分数"
                  value={stats.averageScore}
                  suffix="分"
                  precision={1}
                  prefix={<BarChartOutlined />}
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="提升幅度"
                  value={stats.improvementRate}
                  suffix="%"
                  precision={1}
                  prefix={<VideoCameraOutlined />}
                />
              </Col>
            </Row>
          </Card>

          <Card style={{ marginTop: '16px' }}>
            <Title level={4}>最近活动</Title>
            <div style={{ fontSize: '14px' }}>
              <div style={{ marginBottom: '8px' }}>
                <Text type="secondary">2024-01-15</Text>
                <br />
                <Text>完成了前端开发岗位面试</Text>
                <Tag color="green" style={{ marginLeft: '8px' }}>85分</Tag>
              </div>
              <div style={{ marginBottom: '8px' }}>
                <Text type="secondary">2024-01-10</Text>
                <br />
                <Text>完成了后端开发岗位面试</Text>
                <Tag color="blue" style={{ marginLeft: '8px' }}>78分</Tag>
              </div>
              <div>
                <Text type="secondary">2024-01-05</Text>
                <br />
                <Text>完成了全栈开发岗位面试</Text>
                <Tag color="orange" style={{ marginLeft: '8px' }}>82分</Tag>
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Profile; 