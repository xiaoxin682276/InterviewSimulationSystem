import React, { useState, useRef, useEffect } from 'react';
import { Card, Typography, Button, Progress, Space, message } from 'antd';
import { VideoCameraOutlined, VideoCameraAddOutlined, StopOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

const VideoRecorder = ({ currentQuestion, onVideoSubmit, videos }) => {
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [videoUrl, setVideoUrl] = useState('');
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [videoChunks, setVideoChunks] = useState([]);
  const [stream, setStream] = useState(null);
  const videoRef = useRef(null);

  useEffect(() => {
    // 当题目改变时，清空当前视频
    if (currentQuestion) {
      setVideoUrl('');
      setVideoChunks([]);
      setRecordingTime(0);
    }
  }, [currentQuestion]);

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
      const mediaStream = await navigator.mediaDevices.getUserMedia({ 
        video: true, 
        audio: true 
      });
      
      setStream(mediaStream);
      
      if (videoRef.current) {
        videoRef.current.srcObject = mediaStream;
      }
      
      const recorder = new MediaRecorder(mediaStream, {
        mimeType: 'video/webm;codecs=vp9'
      });
      
      const chunks = [];
      
      recorder.ondataavailable = (e) => {
        chunks.push(e.data);
      };
      
      recorder.onstop = () => {
        const blob = new Blob(chunks, { type: 'video/webm' });
        const url = URL.createObjectURL(blob);
        setVideoUrl(url);
        setVideoChunks(chunks);
        message.success('视频录制完成');
      };
      
      setMediaRecorder(recorder);
      recorder.start();
      setIsRecording(true);
      setRecordingTime(0);
      message.success('开始录制视频');
      
    } catch (error) {
      console.error('视频录制失败:', error);
      message.error('无法访问摄像头，请检查权限设置');
    }
  };

  const stopRecording = () => {
    if (mediaRecorder && isRecording) {
      mediaRecorder.stop();
      mediaRecorder.stream.getTracks().forEach(track => track.stop());
      setIsRecording(false);
      message.success('视频录制完成');
    }
  };

  const handleSubmit = () => {
    if (!videoUrl) {
      message.warning('请先录制视频');
      return;
    }

    const videoData = {
      video: videoUrl,
      timestamp: new Date().toISOString(),
      duration: recordingTime
    };

    onVideoSubmit(currentQuestion.id, videoData);
    message.success('视频已保存');
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
      <Title level={3}>视频录制</Title>
      <Paragraph>
        请录制您的面试回答视频，系统将分析您的微表情、肢体语言和眼神交流：
      </Paragraph>

      {/* 视频预览区域 */}
      <div style={{ 
        marginBottom: 16,
        border: '1px solid #d9d9d9',
        borderRadius: '8px',
        overflow: 'hidden',
        backgroundColor: '#000'
      }}>
        <video
          ref={videoRef}
          autoPlay
          muted
          style={{ 
            width: '100%', 
            height: '300px',
            objectFit: 'cover'
          }}
        />
      </div>

      {/* 录制控制 */}
      <div style={{ textAlign: 'center', marginBottom: 16 }}>
        {isRecording ? (
          <div>
            <div style={{ marginBottom: 8 }}>
              <VideoCameraOutlined style={{ fontSize: '24px', color: '#ff4d4f' }} />
              <span style={{ marginLeft: 8, color: '#ff4d4f' }}>
                正在录制: {formatTime(recordingTime)}
              </span>
            </div>
            <Progress 
              percent={(recordingTime / 300) * 100} 
              status="active"
              style={{ marginBottom: 8 }}
            />
            <Button 
              type="primary" 
              danger 
              icon={<StopOutlined />}
              onClick={stopRecording}
            >
              停止录制
            </Button>
          </div>
        ) : (
          <div>
            <div style={{ marginBottom: 8 }}>
              <VideoCameraAddOutlined style={{ fontSize: '24px', color: '#666' }} />
              <span style={{ marginLeft: 8 }}>
                {videoUrl ? '视频录制完成' : '点击开始录制'}
              </span>
            </div>
            <Button 
              type="primary" 
              icon={<VideoCameraOutlined />}
              onClick={startRecording}
            >
              开始录制
            </Button>
          </div>
        )}
      </div>

      {/* 录制的视频播放 */}
      {videoUrl && (
        <div style={{ marginBottom: 16 }}>
          <Title level={4}>录制的视频</Title>
          <video
            controls
            style={{ width: '100%', borderRadius: '4px' }}
          >
            <source src={videoUrl} type="video/webm" />
            您的浏览器不支持视频播放
          </video>
        </div>
      )}

      {/* 提交按钮 */}
      <div style={{ textAlign: 'center' }}>
        <Button 
          type="primary" 
          size="large"
          onClick={handleSubmit}
          disabled={!videoUrl}
        >
          提交视频
        </Button>
      </div>

      {/* 视频统计 */}
      <div style={{ marginTop: 16, padding: '12px', backgroundColor: '#f6ffed', borderRadius: '4px' }}>
        <Paragraph style={{ margin: 0, color: '#52c41a' }}>
          已录制视频: {Object.keys(videos).length} / {currentQuestion.totalQuestions || 0}
        </Paragraph>
      </div>

      {/* 录制提示 */}
      <div style={{ marginTop: 16, padding: '12px', backgroundColor: '#f0f8ff', borderRadius: '4px' }}>
        <Title level={5} style={{ margin: '0 0 8px 0', color: '#1890ff' }}>
          录制建议
        </Title>
        <Paragraph style={{ margin: 0, fontSize: '12px' }}>
          • 保持眼神交流，展现自信<br/>
          • 注意肢体语言，保持自然姿态<br/>
          • 语速适中，表达清晰<br/>
          • 建议录制时长2-3分钟
        </Paragraph>
      </div>
    </Card>
  );
};

export default VideoRecorder; 