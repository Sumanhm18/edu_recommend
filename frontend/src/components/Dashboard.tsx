import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Container,
  Avatar,
  Chip,
} from '@mui/material';
import {
  Quiz as QuizIcon,
  School as SchoolIcon,
  Assessment as AssessmentIcon,
  AccountCircle as AccountIcon,
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleStartQuiz = () => {
    navigate('/quiz');
  };

  const handleViewRecommendations = () => {
    navigate('/recommendations');
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <Container maxWidth="lg">
      <Box py={4}>
        {/* Header */}
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
          <Box display="flex" alignItems="center" gap={2}>
            <Avatar sx={{ bgcolor: 'primary.main' }}>
              <AccountIcon />
            </Avatar>
            <Box>
              <Typography variant="h4">Welcome, {user?.name}!</Typography>
              <Typography variant="body2" color="text.secondary">
                {user?.district} • Class {user?.className}
              </Typography>
            </Box>
          </Box>
          <Button variant="outlined" onClick={handleLogout}>
            Logout
          </Button>
        </Box>

        {/* Status Cards */}
        <Box display="flex" gap={3} mb={4} sx={{ flexWrap: 'wrap' }}>
          <Box flex={1} minWidth={300}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={2}>
                  <Avatar sx={{ bgcolor: 'success.main' }}>
                    <QuizIcon />
                  </Avatar>
                  <Box>
                    <Typography variant="h6">Quiz Status</Typography>
                    <Chip label="Ready to Take" color="success" size="small" />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>
          <Box flex={1} minWidth={300}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={2}>
                  <Avatar sx={{ bgcolor: 'info.main' }}>
                    <AssessmentIcon />
                  </Avatar>
                  <Box>
                    <Typography variant="h6">AI Analysis</Typography>
                    <Chip label="Pending Quiz" color="default" size="small" />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>
          <Box flex={1} minWidth={300}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center" gap={2}>
                  <Avatar sx={{ bgcolor: 'warning.main' }}>
                    <SchoolIcon />
                  </Avatar>
                  <Box>
                    <Typography variant="h6">Recommendations</Typography>
                    <Chip label="Available Soon" color="default" size="small" />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* Main Actions */}
        <Box display="flex" gap={3} mb={4} sx={{ flexWrap: 'wrap' }}>
          <Box flex={1} minWidth={300}>
            <Card sx={{ height: '100%' }}>
              <CardContent sx={{ p: 4 }}>
                <Box display="flex" flexDirection="column" alignItems="center" textAlign="center">
                  <Avatar sx={{ bgcolor: 'primary.main', width: 64, height: 64, mb: 2 }}>
                    <QuizIcon sx={{ fontSize: 32 }} />
                  </Avatar>
                  <Typography variant="h5" gutterBottom>
                    Take Assessment Quiz
                  </Typography>
                  <Typography variant="body1" color="text.secondary" mb={3}>
                    Complete our comprehensive aptitude assessment to get personalized college recommendations
                    based on your strengths and location.
                  </Typography>
                  <Button
                    variant="contained"
                    size="large"
                    onClick={handleStartQuiz}
                    sx={{ minWidth: 200 }}
                  >
                    Start Quiz
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box flex={1} minWidth={300}>
            <Card sx={{ height: '100%' }}>
              <CardContent sx={{ p: 4 }}>
                <Box display="flex" flexDirection="column" alignItems="center" textAlign="center">
                  <Avatar sx={{ bgcolor: 'secondary.main', width: 64, height: 64, mb: 2 }}>
                    <SchoolIcon sx={{ fontSize: 32 }} />
                  </Avatar>
                  <Typography variant="h5" gutterBottom>
                    View Recommendations
                  </Typography>
                  <Typography variant="body1" color="text.secondary" mb={3}>
                    Access your AI-powered college recommendations, career guidance, and action plans
                    after completing the assessment.
                  </Typography>
                  <Button
                    variant="outlined"
                    size="large"
                    onClick={handleViewRecommendations}
                    sx={{ minWidth: 200 }}
                  >
                    View Recommendations
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* Info Section */}
        <Box mt={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                How It Works
              </Typography>
              <Box display="flex" gap={2} sx={{ flexWrap: 'wrap' }}>
                <Box flex={1} minWidth={200} textAlign="center">
                  <Typography variant="h6" color="primary">1</Typography>
                  <Typography variant="body2">
                    Take the comprehensive aptitude assessment covering multiple domains
                  </Typography>
                </Box>
                <Box flex={1} minWidth={200} textAlign="center">
                  <Typography variant="h6" color="primary">2</Typography>
                  <Typography variant="body2">
                    AI analyzes your performance and considers your location preferences
                  </Typography>
                </Box>
                <Box flex={1} minWidth={200} textAlign="center">
                  <Typography variant="h6" color="primary">3</Typography>
                  <Typography variant="body2">
                    Get personalized college recommendations and career guidance
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Box>
      </Box>
    </Container>
  );
};

export default Dashboard;