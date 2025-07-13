import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Tabs, Divider } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, IdcardOutlined } from '@ant-design/icons';
import { authAPI } from '../api';
import './Login.css';

const { TabPane } = Tabs;

const Login = ({ onLoginSuccess }) => {
    const [loginForm] = Form.useForm();
    const [registerForm] = Form.useForm();
    const [loading, setLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('login');

    const handleLogin = async (values) => {
        setLoading(true);
        try {
            console.log('开始登录请求...');
            console.log('登录数据:', {
                username: values.username,
                password: values.password
            });

            const data = await authAPI.login({
                username: values.username,
                password: values.password
            });

            console.log('响应数据:', data);
            
            if (data.success) {
                message.success(data.message);
                localStorage.setItem('user', JSON.stringify(data.user));
                onLoginSuccess(data.user);
            } else {
                message.error(data.message || '登录失败');
            }
        } catch (error) {
            console.error('登录错误:', error);
            if (error.message.includes('Network Error')) {
                message.error('无法连接到服务器，请检查后端服务是否启动');
            } else if (error.response?.status === 500) {
                message.error('服务器内部错误，请检查后端日志');
            } else if (error.response?.status === 400) {
                message.error('请求参数错误，请检查输入信息');
            } else {
                message.error(`登录失败: ${error.message}`);
            }
        } finally {
            setLoading(false);
        }
    };

    const handleRegister = async (values) => {
        setLoading(true);
        try {
            console.log('开始注册请求...');
            console.log('注册数据:', {
                username: values.username,
                password: values.password,
                email: values.email,
                fullName: values.fullName
            });

            const data = await authAPI.register({
                username: values.username,
                password: values.password,
                email: values.email,
                fullName: values.fullName
            });

            console.log('响应数据:', data);
            
            if (data.success) {
                message.success(data.message);
                setActiveTab('login');
                loginForm.setFieldsValue({
                    username: values.username,
                    password: values.password
                });
            } else {
                message.error(data.message || '注册失败');
            }
        } catch (error) {
            console.error('注册错误:', error);
            if (error.message.includes('Network Error')) {
                message.error('无法连接到服务器，请检查后端服务是否启动');
            } else if (error.response?.status === 500) {
                message.error('服务器内部错误，请检查后端日志');
            } else if (error.response?.status === 400) {
                message.error('请求参数错误，请检查输入信息');
            } else {
                message.error(`注册失败: ${error.message}`);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-background">
                <div className="login-content">
                    <Card className="login-card">
                        <div className="login-header">
                            <h1>智能面试模拟系统</h1>
                            <p>提升面试技能，获得理想工作</p>
                        </div>
                        
                        <Tabs 
                            activeKey={activeTab} 
                            onChange={setActiveTab}
                            centered
                            className="login-tabs"
                        >
                            <TabPane tab="登录" key="login">
                                <Form
                                    form={loginForm}
                                    onFinish={handleLogin}
                                    layout="vertical"
                                    className="login-form"
                                >
                                    <Form.Item
                                        name="username"
                                        rules={[
                                            { required: true, message: '请输入用户名' }
                                        ]}
                                    >
                                        <Input 
                                            prefix={<UserOutlined />} 
                                            placeholder="用户名" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item
                                        name="password"
                                        rules={[
                                            { required: true, message: '请输入密码' }
                                        ]}
                                    >
                                        <Input.Password 
                                            prefix={<LockOutlined />} 
                                            placeholder="密码" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item>
                                        <Button 
                                            type="primary" 
                                            htmlType="submit" 
                                            loading={loading}
                                            size="large"
                                            block
                                        >
                                            登录
                                        </Button>
                                    </Form.Item>
                                </Form>
                            </TabPane>
                            
                            <TabPane tab="注册" key="register">
                                <Form
                                    form={registerForm}
                                    onFinish={handleRegister}
                                    layout="vertical"
                                    className="register-form"
                                >
                                    <Form.Item
                                        name="username"
                                        rules={[
                                            { required: true, message: '请输入用户名' },
                                            { min: 3, message: '用户名至少3个字符' }
                                        ]}
                                    >
                                        <Input 
                                            prefix={<UserOutlined />} 
                                            placeholder="用户名" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item
                                        name="fullName"
                                        rules={[
                                            { required: true, message: '请输入姓名' }
                                        ]}
                                    >
                                        <Input 
                                            prefix={<IdcardOutlined />} 
                                            placeholder="姓名" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item
                                        name="email"
                                        rules={[
                                            { required: true, message: '请输入邮箱' },
                                            { type: 'email', message: '请输入有效的邮箱地址' }
                                        ]}
                                    >
                                        <Input 
                                            prefix={<MailOutlined />} 
                                            placeholder="邮箱" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item
                                        name="password"
                                        rules={[
                                            { required: true, message: '请输入密码' },
                                            { min: 6, message: '密码至少6个字符' }
                                        ]}
                                    >
                                        <Input.Password 
                                            prefix={<LockOutlined />} 
                                            placeholder="密码" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item
                                        name="confirmPassword"
                                        dependencies={['password']}
                                        rules={[
                                            { required: true, message: '请确认密码' },
                                            ({ getFieldValue }) => ({
                                                validator(_, value) {
                                                    if (!value || getFieldValue('password') === value) {
                                                        return Promise.resolve();
                                                    }
                                                    return Promise.reject(new Error('两次输入的密码不一致'));
                                                },
                                            }),
                                        ]}
                                    >
                                        <Input.Password 
                                            prefix={<LockOutlined />} 
                                            placeholder="确认密码" 
                                            size="large"
                                        />
                                    </Form.Item>
                                    
                                    <Form.Item>
                                        <Button 
                                            type="primary" 
                                            htmlType="submit" 
                                            loading={loading}
                                            size="large"
                                            block
                                        >
                                            注册
                                        </Button>
                                    </Form.Item>
                                </Form>
                            </TabPane>
                        </Tabs>
                        
                        <Divider>系统特色</Divider>
                        <div className="features">
                            <div className="feature-item">
                                <h4>🎯 多模态评估</h4>
                                <p>语音、视频、文本全方位分析</p>
                            </div>
                            <div className="feature-item">
                                <h4>🤖 AI智能反馈</h4>
                                <p>基于大模型的个性化建议</p>
                            </div>
                            <div className="feature-item">
                                <h4>📈 学习路径</h4>
                                <p>个性化学习计划推荐</p>
                            </div>
                        </div>
                    </Card>
                </div>
            </div>
        </div>
    );
};

export default Login; 