import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    console.log('发送请求:', config);
    return config;
  },
  (error) => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    console.log('收到响应:', response);
    return response;
  },
  (error) => {
    console.error('响应错误:', error);
    return Promise.reject(error);
  }
);

// 认证API函数
export const authAPI = {
  // 用户登录
  login: async (loginData) => {
    try {
      const response = await api.post('/auth/login', loginData);
      return response.data;
    } catch (error) {
      console.error('登录失败:', error);
      throw error;
    }
  },

  // 用户注册
  register: async (registerData) => {
    try {
      const response = await api.post('/auth/register', registerData);
      return response.data;
    } catch (error) {
      console.error('注册失败:', error);
      throw error;
    }
  },

  // 检查认证状态
  checkAuth: async (username) => {
    try {
      const response = await api.get('/auth/check-auth', {
        params: { username }
      });
      return response.data;
    } catch (error) {
      console.error('检查认证状态失败:', error);
      throw error;
    }
  },
};

// 面试API函数
export const interviewAPI = {
  // 获取可用岗位列表
  getPositions: async () => {
    try {
      const response = await api.get('/interview/positions');
      return response.data;
    } catch (error) {
      console.error('获取岗位列表失败:', error);
      throw error;
    }
  },

  // 根据岗位获取面试题目
  getQuestions: async (position) => {
    try {
      const response = await api.get('/interview/questions', {
        params: { position }
      });
      return response.data;
    } catch (error) {
      console.error('获取面试题目失败:', error);
      throw error;
    }
  },

  // 提交面试评估
  evaluateInterview: async (interviewData) => {
    try {
      const response = await api.post('/interview/evaluate', interviewData);
      return response.data;
    } catch (error) {
      console.error('面试评估失败:', error);
      throw error;
    }
  },

  // 提交多模态面试评估
  evaluateInterviewEnhanced: async (multimodalData) => {
    try {
      const response = await api.post('/interview/evaluate-enhanced', multimodalData);
      return response.data;
    } catch (error) {
      console.error('多模态面试评估失败:', error);
      throw error;
    }
  },
  
  // 增强多模态评估（包含面部情感和肢体语言分析）
  evaluateInterviewEnhancedWithVideo: async (multimodalData, videoFile) => {
    try {
      const formData = new FormData();
      formData.append('multimodalData', JSON.stringify(multimodalData));
      formData.append('videoFile', videoFile);
      
      const response = await api.post('/interview/evaluate-enhanced-with-video', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('增强多模态评估失败:', error);
      throw error;
    }
  },

  // 上传音频文件
  uploadAudio: async (file, questionId) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('questionId', questionId);
      
      const response = await api.post('/interview/upload-audio', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('音频上传失败:', error);
      throw error;
    }
  },

  // 上传视频文件
  uploadVideo: async (file, questionId) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('questionId', questionId);
      
      const response = await api.post('/interview/upload-video', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('视频上传失败:', error);
      throw error;
    }
  },

  // 获取学习路径
  getLearningPaths: async (position, competencies) => {
    try {
      const response = await api.get('/interview/learning-paths', {
        params: { position, competencies }
      });
      return response.data;
    } catch (error) {
      console.error('获取学习路径失败:', error);
      throw error;
    }
  },
};

export default api; 