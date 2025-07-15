import React, { useState, useEffect } from 'react';
import { Card, Typography, Input, Button, Progress, Space, message } from 'antd';
import { AudioOutlined, AudioMutedOutlined, SendOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

const AnswerRecorder = ({ 
  currentQuestion, 
  onAnswerSubmit, 
  answers, 
  mode = 'text', 
  totalQuestions = 0,
  currentQuestionIndex = 0,
  onNextQuestion,
  onPrevQuestion,
  onQuestionSelect,
  answerMode // 新增
}) => {
  const [currentAnswer, setCurrentAnswer] = useState('');
  
  // 安全的字符串处理函数
  const safeString = (value) => {
    if (typeof value === 'string') return value;
    if (value === null || value === undefined) return '';
    return String(value);
  };
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [audioChunks, setAudioChunks] = useState([]);
  const [audioUrl, setAudioUrl] = useState('');
  const [inputMode, setInputMode] = useState(mode); // 'text' or 'voice'

  useEffect(() => {
    // 当题目改变时，清空当前答案
    if (currentQuestion) {
      const existingAnswer = answers[currentQuestion.id];
      // 如果答案是对象格式，提取text字段；如果是字符串，直接使用；否则使用空字符串
      const answerText = existingAnswer 
        ? (typeof existingAnswer === 'string' ? existingAnswer : safeString(existingAnswer.text))
        : '';
      setCurrentAnswer(answerText);
      
      // 如果有音频数据，设置音频URL
      if (existingAnswer && typeof existingAnswer === 'object' && existingAnswer.audio) {
        setAudioUrl(existingAnswer.audio);
      } else {
        setAudioUrl('');
      }
      
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
    // 确保设置的值是字符串类型
    setCurrentAnswer(safeString(e.target.value));
  };

  const handleSubmit = () => {
    const safeAnswer = safeString(currentAnswer);
    if (!safeAnswer.trim() && !audioUrl) {
      message.warning('请输入答案或录制语音');
      return;
    }

    const answerData = {
      text: safeAnswer,
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
      <Title level={3}>
        {mode === 'voice' ? '语音录制' : '文字回答'}
      </Title>
      {answerMode === 'video' && (
        <Paragraph type="warning" style={{color:'#faad14',fontWeight:500}}>
          已提交视频，本题只能提交视频，文字和语音不可用。
        </Paragraph>
      )}
      {answerMode === 'audio' && (
        <Paragraph type="warning" style={{color:'#faad14',fontWeight:500}}>
          已提交语音，本题只能提交语音，文字输入不可用。
        </Paragraph>
      )}
      {answerMode === 'text' && (
        <Paragraph type="warning" style={{color:'#faad14',fontWeight:500}}>
          已提交文本，本题只能提交文本，语音录制不可用。
        </Paragraph>
      )}
      <Paragraph>
        {mode === 'voice' 
          ? '请针对当前题目录制您的语音答案：'
          : '请针对当前题目提供您的文字答案：'
        }
      </Paragraph>

      {/* 输入模式切换 - 只在基础模式下显示 */}
      {mode === 'text' && (
        <Space style={{ marginBottom: 16 }}>
          <Button 
            type={inputMode === 'text' ? 'primary' : 'default'}
            onClick={() => setInputMode('text')}
            disabled={answerMode === 'audio' || answerMode === 'video'}
          >文字输入</Button>
          <Button 
            type={inputMode === 'voice' ? 'primary' : 'default'}
            onClick={() => setInputMode('voice')}
            disabled={answerMode === 'text' || answerMode === 'video'}
          >语音录制</Button>
        </Space>
      )}

      {/* 文字输入模式 */}
      {(inputMode === 'text' || mode === 'text') && (
        <div>
          <TextArea
            rows={6}
            placeholder="请输入您的答案..."
            value={currentAnswer}
            onChange={handleTextChange}
            style={{ marginBottom: 16 }}
            disabled={answerMode === 'audio' || answerMode === 'video'}
          />
        </div>
      )}

      {/* 语音录制模式 */}
      {(inputMode === 'voice' || mode === 'voice') && (
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
                  disabled={answerMode === 'text' || answerMode === 'video'}
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
                  disabled={answerMode === 'text' || answerMode === 'video'}
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
      <div style={{ textAlign: 'center', marginTop: 16 }}>
        <Button 
          type="primary" 
          size="large"
          onClick={handleSubmit}
          disabled={(!currentAnswer.trim() && !audioUrl) || answerMode === 'video'}
        >
          提交答案
        </Button>
      </div>

      {/* 题目切换按钮 */}
      <div style={{ display: 'flex', justifyContent: 'center', marginTop: 24 }}>
        <Button
          onClick={onPrevQuestion}
          disabled={currentQuestionIndex === 0}
          style={{ marginRight: 16 }}
        >
          上一题
        </Button>
        <Button
          type="primary"
          onClick={onNextQuestion}
          disabled={currentQuestionIndex === totalQuestions - 1}
        >
          下一题
        </Button>
      </div>

      {/* 问题导航 */}
      {false && totalQuestions > 1 && (
        <div style={{ marginTop: 16, textAlign: 'center' }}>
          <div style={{ marginBottom: 8 }}>
            <span style={{ color: '#666' }}>
              题目 {currentQuestionIndex + 1} / {totalQuestions}
            </span>
          </div>
          <Space>
            <Button 
              onClick={onPrevQuestion}
              disabled={currentQuestionIndex === 0}
            >
              上一题
            </Button>
            <Button 
              onClick={onNextQuestion}
              disabled={currentQuestionIndex === totalQuestions - 1}
            >
              下一题
            </Button>
          </Space>
          {/* 问题快速导航 */}
          <div style={{ marginTop: 12 }}>
            <Space wrap>
              {Array.from({ length: totalQuestions }, (_, index) => (
                <Button
                  key={index}
                  size="small"
                  type={index === currentQuestionIndex ? 'primary' : 'default'}
                  onClick={() => onQuestionSelect(index)}
                  style={{
                    minWidth: '40px',
                    height: '32px',
                    borderRadius: '16px'
                  }}
                >
                  {index + 1}
                </Button>
              ))}
            </Space>
          </div>
        </div>
      )}

      {/* 答案统计 */}
      <div style={{ marginTop: 16, padding: '12px', backgroundColor: '#f6ffed', borderRadius: '4px' }}>
        <Paragraph style={{ margin: 0, color: '#52c41a' }}>
          已保存答案: {Object.keys(answers).length} / {totalQuestions} 
          {Object.keys(answers).length >= totalQuestions && totalQuestions > 0 && ' ✅'}
        </Paragraph>
        {totalQuestions > 0 && (
          <div style={{ marginTop: 8 }}>
            <Progress 
              percent={Math.round((Object.keys(answers).length / totalQuestions) * 100)} 
              size="small"
              status={Object.keys(answers).length >= totalQuestions ? 'success' : 'active'}
            />
          </div>
        )}
      </div>
    </Card>
  );
};

export default AnswerRecorder; 