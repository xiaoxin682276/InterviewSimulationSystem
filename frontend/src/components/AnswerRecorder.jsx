import React, { useState, useEffect } from 'react';
import { Card, Typography, Input, Button, Progress, Space, message } from 'antd';
import { AudioOutlined, AudioMutedOutlined, SendOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

const AnswerRecorder = ({ currentQuestion, onAnswerSubmit, answers }) => {
  const [currentAnswer, setCurrentAnswer] = useState('');
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [audioChunks, setAudioChunks] = useState([]);
  const [audioUrl, setAudioUrl] = useState('');
  const [inputMode, setInputMode] = useState('text'); // 'text' or 'voice'

  useEffect(() => {
    // 当题目改变时，清空当前答案
    if (currentQuestion) {
      setCurrentAnswer(answers[currentQuestion.id] || '');
      setAudioUrl('');
      setAudioChunks([]);
      setRecordingTime(0);
    }
  }, [currentQuestion, answers]);

  useEffect(() => {
    let interval;
    if (isRecording) {
      interval = setInterval(() => {
        setRecordingTime(prev => prev + 1);
      }, 1000);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isRecording]);

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const recorder = new MediaRecorder(stream);
      const chunks = [];

      recorder.ondataavailable = (e) => {
        chunks.push(e.data);
      };

      recorder.onstop = () => {
        const blob = new Blob(chunks, { type: 'audio/wav' });
        const url = URL.createObjectURL(blob);
        setAudioUrl(url);
        setAudioChunks(chunks);
      };

      setMediaRecorder(recorder);
      recorder.start();
      setIsRecording(true);
      setRecordingTime(0);
      message.success('开始录音');
    } catch (error) {
      console.error('录音失败:', error);
      message.error('无法访问麦克风，请检查权限设置');
    }
  };

  const stopRecording = () => {
    if (mediaRecorder && isRecording) {
      mediaRecorder.stop();
      mediaRecorder.stream.getTracks().forEach(track => track.stop());
      setIsRecording(false);
      message.success('录音完成');
    }
  };

  const handleTextChange = (e) => {
    setCurrentAnswer(e.target.value);
  };

  const handleSubmit = () => {
    if (!currentAnswer.trim() && !audioUrl) {
      message.warning('请输入答案或录制语音');
      return;
    }

    const answerData = {
      text: currentAnswer,
      audio: audioUrl,
      timestamp: new Date().toISOString()
    };

    onAnswerSubmit(currentQuestion.id, answerData);
    message.success('答案已保存');
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  if (!currentQuestion) {
    return null;
  }

  return (
    <Card className="card">
      <Title level={3}>记录答案</Title>
      <Paragraph>
        请针对当前题目提供您的答案，可以选择文字输入或语音录制：
      </Paragraph>

      {/* 输入模式切换 */}
      <Space style={{ marginBottom: 16 }}>
        <Button 
          type={inputMode === 'text' ? 'primary' : 'default'}
          onClick={() => setInputMode('text')}
        >
          文字输入
        </Button>
        <Button 
          type={inputMode === 'voice' ? 'primary' : 'default'}
          onClick={() => setInputMode('voice')}
        >
          语音录制
        </Button>
      </Space>

      {/* 文字输入模式 */}
      {inputMode === 'text' && (
        <div>
          <TextArea
            rows={6}
            placeholder="请输入您的答案..."
            value={currentAnswer}
            onChange={handleTextChange}
            style={{ marginBottom: 16 }}
          />
        </div>
      )}

      {/* 语音录制模式 */}
      {inputMode === 'voice' && (
        <div>
          <div style={{ 
            padding: '16px', 
            border: '1px solid #d9d9d9', 
            borderRadius: '4px',
            backgroundColor: '#fafafa',
            marginBottom: 16
          }}>
            {isRecording ? (
              <div style={{ textAlign: 'center' }}>
                <AudioOutlined style={{ fontSize: '24px', color: '#ff4d4f' }} />
                <div style={{ marginTop: 8 }}>
                  正在录音: {formatTime(recordingTime)}
                </div>
                <Progress 
                  percent={(recordingTime / 300) * 100} 
                  status="active"
                  style={{ marginTop: 8 }}
                />
                <Button 
                  type="primary" 
                  danger 
                  onClick={stopRecording}
                  style={{ marginTop: 8 }}
                >
                  停止录音
                </Button>
              </div>
            ) : (
              <div style={{ textAlign: 'center' }}>
                <AudioMutedOutlined style={{ fontSize: '24px', color: '#666' }} />
                <div style={{ marginTop: 8 }}>
                  {audioUrl ? '录音完成' : '点击开始录音'}
                </div>
                <Button 
                  type="primary" 
                  onClick={startRecording}
                  style={{ marginTop: 8 }}
                >
                  开始录音
                </Button>
              </div>
            )}
          </div>

          {/* 播放录音 */}
          {audioUrl && (
            <div style={{ marginBottom: 16 }}>
              <audio controls style={{ width: '100%' }}>
                <source src={audioUrl} type="audio/wav" />
                您的浏览器不支持音频播放
              </audio>
            </div>
          )}
        </div>
      )}

      {/* 提交按钮 */}
      <div style={{ textAlign: 'center' }}>
        <Button 
          type="primary" 
          size="large"
          icon={<SendOutlined />}
          onClick={handleSubmit}
          disabled={!currentAnswer.trim() && !audioUrl}
        >
          提交答案
        </Button>
      </div>

      {/* 答案统计 */}
      <div style={{ marginTop: 16, padding: '12px', backgroundColor: '#f6ffed', borderRadius: '4px' }}>
        <Paragraph style={{ margin: 0, color: '#52c41a' }}>
          已保存答案: {Object.keys(answers).length} / {currentQuestion.totalQuestions || 0}
        </Paragraph>
      </div>
    </Card>
  );
};

export default AnswerRecorder; 