// Auth Types
export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string; // Optional for backward compatibility
  district: string;
  className: string;
  guest: boolean;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data?: {
    token: string;
    type: string;
    userId: number;
    name: string;
    email: string;
    phone?: string; // Optional for backward compatibility
    guest: boolean;
  };
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  district: string;
  className: string;
  otp: string; // OTP is now required for registration
}

// OTP Types
export interface OtpRequest {
  email: string;
}

export interface OtpVerifyRequest {
  email: string;
  otp: string;
}

export interface OtpResponse {
  success: boolean;
  message: string;
}

// Quiz Types
export interface Question {
  id: number;
  questionText: string;
  options: {
    A: string;
    B: string;
    C: string;
    D: string;
  };
  correctAnswer: string;
  points: number;
  category: string;
}

export interface Quiz {
  id: number;
  title: string;
  description: string;
  questions: Question[];
  timeLimit: number;
  passingScore: number;
  categories: string[];
}

export interface QuizAnswer {
  questionId: number;
  selectedOption: string;
}

export interface QuizSubmission {
  quizId: number;
  answers: QuizAnswer[];
}

// Quiz Result Types
export interface ScoreBreakdown {
  mathematicalScore: number;
  verbalScore: number;
  analyticalScore: number;
  technicalScore: number;
  dominantAptitude: string;
}

export interface QuizResult {
  attemptId: number;
  quizId: number;
  quizTitle: string;
  totalScore: number;
  maxScore: number;
  percentage: number;
  completedAt: string;
  scoreBreakdown: ScoreBreakdown;
  recommendedStreams: string[];
  recommendedColleges: string[];
  collegeTier: string;
  performanceLevel: string;
}

// AI Recommendation Types
export interface College {
  name: string;
  location: string;
  type: string;
  tier: string;
  coursesRecommended: string[];
  admissionProcess: string;
  estimatedFees: string;
  placementHighlights: string;
  whyRecommended: string;
}

export interface AlternativeOption {
  name: string;
  location: string;
  type: string;
  specialization: string;
  whyConsider: string;
}

export interface EntranceExam {
  examName: string;
  eligibility: string;
  preparationTips: string;
}

export interface ActionPlan {
  immediate: string[];
  next6Months: string[];
  nextYear: string[];
}

export interface AIRecommendations {
  topColleges: College[];
  alternativeOptions: AlternativeOption[];
  entranceExams: EntranceExam[];
  actionPlan: ActionPlan;
}

export interface QuizSubmissionResponse {
  success: boolean;
  aiRecommendations: AIRecommendations;
  quizResult: QuizResult;
  message: string;
  timestamp: number;
}

// Chatbot Types
export interface ChatMessage {
  messageId: number;
  content: string;
  messageType: 'USER' | 'ASSISTANT' | 'SYSTEM';
  timestamp: string;
  metadata?: string;
}

export interface ChatConversation {
  conversationId: number;
  title: string;
  createdAt: string;
  updatedAt: string;
  isActive: boolean;
  messages?: ChatMessage[];
}

export interface ChatRequest {
  userId: number;
  message: string;
  conversationId?: number;
}

export interface ChatResponse {
  conversationId: number;
  message: string;
  timestamp: string;
}

export interface NewConversationRequest {
  userId: number;
  title: string;
}