import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Container,
  RadioGroup,
  FormControlLabel,
  Radio,
  LinearProgress,
  Chip,
  Alert,
  CircularProgress,
} from '@mui/material';
import {
  Timer as TimerIcon,
  CheckCircle as CheckIcon,
  ArrowBack as BackIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { Quiz, QuizAnswer, QuizSubmissionResponse } from '../types';
import { quizService } from '../services/api';

const QuizComponent: React.FC = () => {
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<QuizAnswer[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [timeRemaining, setTimeRemaining] = useState(0);
  const [quizResult, setQuizResult] = useState<QuizSubmissionResponse | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadQuiz();
  }, []);

  useEffect(() => {
    if (timeRemaining > 0) {
      const timer = setTimeout(() => {
        setTimeRemaining(time => time - 1);
      }, 1000);
      return () => clearTimeout(timer);
    } else if (timeRemaining === 0 && quiz) {
      // Auto-submit when time runs out
      handleSubmitQuiz();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [timeRemaining, quiz]);

  const loadQuiz = async () => {
    try {
      setLoading(true);
      console.log('Loading quiz...');
      const quizData = await quizService.getAvailableQuiz();
      console.log('Quiz data received:', quizData);
      
      if (!quizData) {
        throw new Error('No quiz data received');
      }
      
      if (!quizData.questions || !Array.isArray(quizData.questions)) {
        throw new Error('Invalid quiz structure: questions array is missing or invalid');
      }
      
      setQuiz(quizData);
      setTimeRemaining((quizData.timeLimit || 30) * 60); // Convert minutes to seconds
      // Initialize answers array
      const initialAnswers = quizData.questions.map(q => ({
        questionId: q.id,
        selectedOption: ''
      }));
      setAnswers(initialAnswers);
      console.log('Quiz loaded successfully');
    } catch (err) {
      console.error('Quiz load error:', err);
      setError(`Failed to load quiz: ${err instanceof Error ? err.message : 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  };

  const handleAnswerChange = (value: string) => {
    if (!quiz) return;
    
    const currentQuestion = quiz.questions[currentQuestionIndex];
    setAnswers(prev => 
      prev.map(answer => 
        answer.questionId === currentQuestion.id 
          ? { ...answer, selectedOption: value }
          : answer
      )
    );
  };

  const handleNextQuestion = () => {
    if (quiz && currentQuestionIndex < quiz.questions.length - 1) {
      setCurrentQuestionIndex(prev => prev + 1);
    }
  };

  const handlePreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(prev => prev - 1);
    }
  };

  const handleSubmitQuiz = async () => {
    if (!quiz) return;

    try {
      setSubmitting(true);
      // Filter out empty answers and submit
      const validAnswers = answers.filter(answer => answer.selectedOption !== '');
      
      const result = await quizService.submitQuizWithAI({
        quizId: quiz.id,
        answers: validAnswers
      });
      
      // Store result in localStorage for recommendations component
      localStorage.setItem('lastQuizResult', JSON.stringify(result));
      
      setQuizResult(result);
    } catch (err) {
      setError('Failed to submit quiz. Please try again.');
      console.error('Quiz submission error:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const formatTime = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  const getProgress = (): number => {
    if (!quiz) return 0;
    return ((currentQuestionIndex + 1) / quiz.questions.length) * 100;
  };

  const getCurrentAnswer = (): string => {
    if (!quiz) return '';
    const currentQuestion = quiz.questions[currentQuestionIndex];
    const answer = answers.find(a => a.questionId === currentQuestion.id);
    return answer?.selectedOption || '';
  };

  const getAnsweredCount = (): number => {
    return answers.filter(answer => answer.selectedOption !== '').length;
  };

  if (loading) {
    return (
      <Container maxWidth="md">
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md">
        <Box py={4}>
          <Alert severity="error" action={
            <Button color="inherit" size="small" onClick={() => window.location.reload()}>
              Retry
            </Button>
          }>
            {error}
          </Alert>
        </Box>
      </Container>
    );
  }

  if (quizResult) {
    return (
      <Container maxWidth="md">
        <Box py={4}>
          <Card>
            <CardContent sx={{ p: 4, textAlign: 'center' }}>
              <CheckIcon sx={{ fontSize: 64, color: 'success.main', mb: 2 }} />
              <Typography variant="h4" gutterBottom>
                Quiz Completed!
              </Typography>
              <Typography variant="h6" color="text.secondary" mb={3}>
                Your Score: {quizResult.quizResult.percentage.toFixed(1)}%
              </Typography>
              
              <Box mb={3}>
                <Chip 
                  label={`Performance: ${quizResult.quizResult.performanceLevel}`}
                  color={quizResult.quizResult.performanceLevel === 'Excellent' ? 'success' : 'primary'}
                  sx={{ mr: 1, mb: 1 }}
                />
                <Chip 
                  label={`Dominant: ${quizResult.quizResult.scoreBreakdown.dominantAptitude}`}
                  color="secondary"
                  sx={{ mr: 1, mb: 1 }}
                />
              </Box>

              <Typography variant="body1" color="text.secondary" mb={4}>
                AI has analyzed your performance and generated personalized recommendations!
              </Typography>

              <Box display="flex" gap={2} justifyContent="center" flexWrap="wrap">
                <Button
                  variant="contained"
                  onClick={() => navigate('/recommendations')}
                  size="large"
                >
                  View AI Recommendations
                </Button>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/dashboard')}
                  size="large"
                >
                  Back to Dashboard
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Box>
      </Container>
    );
  }

  if (!quiz) {
    return (
      <Container maxWidth="md">
        <Box py={4}>
          <Alert severity="info">
            No quiz available at the moment.
          </Alert>
        </Box>
      </Container>
    );
  }

  const currentQuestion = quiz.questions[currentQuestionIndex];
  const isLastQuestion = currentQuestionIndex === quiz.questions.length - 1;

  return (
    <Container maxWidth="md">
      <Box py={4}>
        {/* Header */}
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Button
            startIcon={<BackIcon />}
            onClick={() => navigate('/dashboard')}
          >
            Back to Dashboard
          </Button>
          <Box display="flex" alignItems="center" gap={2}>
            <TimerIcon color="primary" />
            <Typography variant="h6" color={timeRemaining < 300 ? 'error' : 'text.primary'}>
              {formatTime(timeRemaining)}
            </Typography>
          </Box>
        </Box>

        {/* Progress */}
        <Box mb={3}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
            <Typography variant="body2">
              Question {currentQuestionIndex + 1} of {quiz.questions.length}
            </Typography>
            <Typography variant="body2">
              Answered: {getAnsweredCount()}/{quiz.questions.length}
            </Typography>
          </Box>
          <LinearProgress variant="determinate" value={getProgress()} />
        </Box>

        {/* Question Card */}
        <Card>
          <CardContent sx={{ p: 4 }}>
            <Box mb={2}>
              <Chip 
                label={currentQuestion.category} 
                color="primary" 
                size="small" 
                sx={{ mb: 2 }}
              />
              <Typography variant="h6" gutterBottom>
                {currentQuestion.questionText}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Points: {currentQuestion.points}
              </Typography>
            </Box>

            <RadioGroup
              value={getCurrentAnswer()}
              onChange={(e) => handleAnswerChange(e.target.value)}
            >
              {Object.entries(currentQuestion.options).map(([key, value]) => (
                <FormControlLabel
                  key={key}
                  value={key}
                  control={<Radio />}
                  label={`${key}. ${value}`}
                  sx={{ mb: 1 }}
                />
              ))}
            </RadioGroup>
          </CardContent>
        </Card>

        {/* Navigation */}
        <Box display="flex" justifyContent="space-between" mt={3}>
          <Button
            variant="outlined"
            onClick={handlePreviousQuestion}
            disabled={currentQuestionIndex === 0}
          >
            Previous
          </Button>
          
          <Box display="flex" gap={2}>
            {!isLastQuestion ? (
              <Button
                variant="contained"
                onClick={handleNextQuestion}
              >
                Next
              </Button>
            ) : (
              <Button
                variant="contained"
                onClick={handleSubmitQuiz}
                disabled={submitting}
                color="success"
              >
                {submitting ? 'Submitting...' : 'Submit Quiz'}
              </Button>
            )}
          </Box>
        </Box>
      </Box>
    </Container>
  );
};

export default QuizComponent;