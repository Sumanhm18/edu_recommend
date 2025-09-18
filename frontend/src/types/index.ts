// Auth Types
export interface User {
  id: number;
  name: string;
  phone: string;
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
    phone: string;
    guest: boolean;
  };
}

export interface LoginRequest {
  phone: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  phone: string;
  password: string;
  district: string;
  className: string;
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