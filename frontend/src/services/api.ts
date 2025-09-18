import axios, { AxiosResponse } from 'axios';
import { 
  AuthResponse, 
  LoginRequest, 
  RegisterRequest, 
  OtpRequest,
  OtpVerifyRequest,
  OtpResponse,
  Quiz, 
  QuizSubmission, 
  QuizSubmissionResponse 
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth Services
export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response: AxiosResponse<AuthResponse> = await api.post('/auth/login', credentials);
    if (response.data.success && response.data.data?.token) {
      localStorage.setItem('authToken', response.data.data.token);
      localStorage.setItem('user', JSON.stringify({
        id: response.data.data.userId,
        name: response.data.data.name,
        email: response.data.data.email,
        guest: response.data.data.guest,
      }));
    }
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response: AxiosResponse<AuthResponse> = await api.post('/auth/register', userData);
    if (response.data.success && response.data.data?.token) {
      localStorage.setItem('authToken', response.data.data.token);
      localStorage.setItem('user', JSON.stringify({
        id: response.data.data.userId,
        name: response.data.data.name,
        email: response.data.data.email,
        guest: response.data.data.guest,
      }));
    }
    return response.data;
  },

  sendRegistrationOtp: async (otpRequest: OtpRequest): Promise<OtpResponse> => {
    const response: AxiosResponse<OtpResponse> = await api.post('/auth/send-registration-otp', otpRequest);
    return response.data;
  },

  sendOtp: async (otpRequest: OtpRequest): Promise<OtpResponse> => {
    const response: AxiosResponse<OtpResponse> = await api.post('/auth/send-otp', otpRequest);
    return response.data;
  },

  verifyOtp: async (verifyRequest: OtpVerifyRequest): Promise<AuthResponse> => {
    const response: AxiosResponse<AuthResponse> = await api.post('/auth/verify-otp', verifyRequest);
    if (response.data.success && response.data.data?.token) {
      localStorage.setItem('authToken', response.data.data.token);
      localStorage.setItem('user', JSON.stringify({
        id: response.data.data.userId,
        name: response.data.data.name,
        email: response.data.data.email,
        guest: response.data.data.guest,
      }));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  testConnection: async () => {
    const response = await api.get('/auth/test');
    return response.data;
  }
};

// Quiz Services
export const quizService = {
  getAvailableQuiz: async (): Promise<Quiz> => {
    const response = await api.get('/quiz/available');
    
    // The API returns: { data: [{ quizId, title, description, questionsJson }] }
    // We need to transform it to the expected Quiz format
    if (response.data && response.data.data && response.data.data.length > 0) {
      const quizData = response.data.data[0];
      
      // Parse the questionsJson string into an array
      let questions = [];
      try {
        questions = JSON.parse(quizData.questionsJson || '[]');
      } catch (error) {
        console.error('Failed to parse questions JSON:', error);
        questions = [];
      }
      
      // Transform to expected Quiz format
      const quiz: Quiz = {
        id: quizData.quizId,
        title: quizData.title,
        description: quizData.description,
        questions: questions.map((q: any) => ({
          id: q.id,
          questionText: q.question,
          options: q.options,
          correctAnswer: q.correctAnswer,
          points: q.points,
          category: q.category
        })),
        timeLimit: quizData.timeLimit || 30, // Default 30 minutes if not provided
        passingScore: quizData.passingScore || 50, // Default 50% if not provided
        categories: Array.from(new Set(questions.map((q: any) => q.category))) // Extract unique categories
      };
      
      return quiz;
    }
    
    throw new Error('No quiz data available');
  },

  submitQuiz: async (submission: QuizSubmission): Promise<any> => {
    const response = await api.post('/quiz/submit', submission);
    return response.data;
  },

  submitQuizWithAI: async (submission: QuizSubmission): Promise<QuizSubmissionResponse> => {
    const response: AxiosResponse<QuizSubmissionResponse> = await api.post('/quiz/submit-with-ai', submission);
    return response.data;
  },

  getRecommendationsSummary: async (): Promise<any> => {
    const response = await api.get('/quiz/recommendations-summary');
    return response.data;
  }
};

export default api;