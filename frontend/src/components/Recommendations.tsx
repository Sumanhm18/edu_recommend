import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Container,
  Chip,
  Button,
  Alert,
  CircularProgress,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Divider,
} from '@mui/material';
import {
  School as SchoolIcon,
  LocationOn as LocationIcon,
  AttachMoney as FeeIcon,
  Business as BusinessIcon,
  ExpandMore as ExpandMoreIcon,
  CheckCircle as CheckIcon,
  Schedule as ScheduleIcon,
  ArrowBack as BackIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { QuizSubmissionResponse } from '../types';

const Recommendations: React.FC = () => {
  const [recommendations, setRecommendations] = useState<QuizSubmissionResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // For now, we'll get the data from localStorage if it was stored during quiz submission
    // In a real app, you'd call an API to get the latest recommendations
    loadRecommendations();
  }, []);

  const loadRecommendations = async () => {
    try {
      setLoading(true);
      // Simulate API call - in real implementation, you'd call the recommendations API
      const storedRecommendations = localStorage.getItem('lastQuizResult');
      if (storedRecommendations) {
        setRecommendations(JSON.parse(storedRecommendations));
      } else {
        setError('No recommendations found. Please take the quiz first.');
      }
    } catch (err) {
      setError('Failed to load recommendations.');
      console.error('Recommendations load error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container maxWidth="lg">
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error || !recommendations) {
    return (
      <Container maxWidth="lg">
        <Box py={4}>
          <Alert 
            severity="info" 
            action={
              <Button color="inherit" size="small" onClick={() => navigate('/quiz')}>
                Take Quiz
              </Button>
            }
          >
            {error || 'No recommendations available. Please complete the assessment quiz first.'}
          </Alert>
        </Box>
      </Container>
    );
  }

  const { aiRecommendations, quizResult } = recommendations;

  return (
    <Container maxWidth="lg">
      <Box py={4}>
        {/* Header */}
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
          <Typography variant="h4">Your AI-Powered Recommendations</Typography>
          <Button
            startIcon={<BackIcon />}
            onClick={() => navigate('/dashboard')}
          >
            Back to Dashboard
          </Button>
        </Box>

        {/* Quiz Results Summary */}
        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Your Assessment Results
            </Typography>
            <Box display="flex" gap={2} flexWrap="wrap" mb={2}>
              <Chip 
                label={`Score: ${quizResult.percentage.toFixed(1)}%`}
                color="primary"
                icon={<CheckIcon />}
              />
              <Chip 
                label={`Performance: ${quizResult.performanceLevel}`}
                color={quizResult.performanceLevel === 'Excellent' ? 'success' : 'secondary'}
              />
              <Chip 
                label={`Dominant Aptitude: ${quizResult.scoreBreakdown.dominantAptitude}`}
                color="info"
              />
              <Chip 
                label={`College Tier: ${quizResult.collegeTier}`}
                color="warning"
              />
            </Box>
            <Typography variant="body2" color="text.secondary">
              Recommended Streams: {quizResult.recommendedStreams.join(', ')}
            </Typography>
          </CardContent>
        </Card>

        {/* Top Colleges */}
        <Card sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Top College Recommendations
            </Typography>
            {aiRecommendations.topColleges.map((college, index) => (
              <Card key={index} variant="outlined" sx={{ mb: 2 }}>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Box>
                      <Typography variant="h6" gutterBottom>
                        {college.name}
                      </Typography>
                      <Box display="flex" alignItems="center" gap={1} mb={1}>
                        <LocationIcon fontSize="small" color="action" />
                        <Typography variant="body2">{college.location}</Typography>
                      </Box>
                    </Box>
                    <Box textAlign="right">
                      <Chip label={college.tier} color="primary" size="small" sx={{ mb: 1 }} />
                      <br />
                      <Chip label={college.type} color="secondary" size="small" />
                    </Box>
                  </Box>

                  <Typography variant="body2" color="text.secondary" mb={2}>
                    {college.whyRecommended}
                  </Typography>

                  <Box display="flex" gap={2} flexWrap="wrap" mb={2}>
                    <Box display="flex" alignItems="center" gap={1}>
                      <FeeIcon fontSize="small" color="action" />
                      <Typography variant="body2">{college.estimatedFees}</Typography>
                    </Box>
                    <Box display="flex" alignItems="center" gap={1}>
                      <BusinessIcon fontSize="small" color="action" />
                      <Typography variant="body2">{college.placementHighlights}</Typography>
                    </Box>
                  </Box>

                  <Box>
                    <Typography variant="subtitle2" gutterBottom>
                      Recommended Courses:
                    </Typography>
                    <Box display="flex" gap={1} flexWrap="wrap" mb={2}>
                      {college.coursesRecommended.map((course, courseIndex) => (
                        <Chip key={courseIndex} label={course} size="small" variant="outlined" />
                      ))}
                    </Box>
                  </Box>

                  <Typography variant="body2" color="text.secondary">
                    <strong>Admission Process:</strong> {college.admissionProcess}
                  </Typography>
                </CardContent>
              </Card>
            ))}
          </CardContent>
        </Card>

        {/* Alternative Options */}
        {aiRecommendations.alternativeOptions && aiRecommendations.alternativeOptions.length > 0 && (
          <Card sx={{ mb: 4 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Alternative Options
              </Typography>
              {aiRecommendations.alternativeOptions.map((option, index) => (
                <Card key={index} variant="outlined" sx={{ mb: 2 }}>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      {option.name}
                    </Typography>
                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                      <LocationIcon fontSize="small" color="action" />
                      <Typography variant="body2">{option.location}</Typography>
                      <Chip label={option.type} size="small" color="secondary" sx={{ ml: 1 }} />
                    </Box>
                    <Typography variant="body2" color="text.secondary" mb={1}>
                      <strong>Specialization:</strong> {option.specialization}
                    </Typography>
                    <Typography variant="body2">
                      <strong>Why Consider:</strong> {option.whyConsider}
                    </Typography>
                  </CardContent>
                </Card>
              ))}
            </CardContent>
          </Card>
        )}

        {/* Entrance Exams */}
        {aiRecommendations.entranceExams && aiRecommendations.entranceExams.length > 0 && (
          <Card sx={{ mb: 4 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recommended Entrance Exams
              </Typography>
              {aiRecommendations.entranceExams.map((exam, index) => (
                <Accordion key={index}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="h6">{exam.examName}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Typography variant="body2" color="text.secondary" mb={2}>
                      <strong>Eligibility:</strong> {exam.eligibility}
                    </Typography>
                    <Typography variant="body2">
                      <strong>Preparation Tips:</strong> {exam.preparationTips}
                    </Typography>
                  </AccordionDetails>
                </Accordion>
              ))}
            </CardContent>
          </Card>
        )}

        {/* Action Plan */}
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Your Personalized Action Plan
            </Typography>
            
            <Box mb={3}>
              <Typography variant="subtitle1" gutterBottom color="error">
                Immediate Actions
              </Typography>
              {aiRecommendations.actionPlan.immediate.map((action, index) => (
                <Box key={index} display="flex" alignItems="center" gap={1} mb={1}>
                  <CheckIcon fontSize="small" color="error" />
                  <Typography variant="body2">{action}</Typography>
                </Box>
              ))}
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box mb={3}>
              <Typography variant="subtitle1" gutterBottom color="warning.main">
                Next 6 Months
              </Typography>
              {aiRecommendations.actionPlan.next6Months.map((action, index) => (
                <Box key={index} display="flex" alignItems="center" gap={1} mb={1}>
                  <ScheduleIcon fontSize="small" color="warning" />
                  <Typography variant="body2">{action}</Typography>
                </Box>
              ))}
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box>
              <Typography variant="subtitle1" gutterBottom color="success.main">
                Next Year
              </Typography>
              {aiRecommendations.actionPlan.nextYear.map((action, index) => (
                <Box key={index} display="flex" alignItems="center" gap={1} mb={1}>
                  <SchoolIcon fontSize="small" color="success" />
                  <Typography variant="body2">{action}</Typography>
                </Box>
              ))}
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default Recommendations;