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
  Chat as ChatIcon,
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

  const handleChatbot = () => {
    navigate('/chatbot');
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <Container 
      maxWidth="lg" 
      sx={{
        background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
        minHeight: '100vh',
        backgroundImage: `
          radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
          radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.1) 0%, transparent 50%)
        `,
        py: 4,
      }}
    >
      <Box>
        {/* Header */}
        <Box 
          display="flex" 
          justifyContent="space-between" 
          alignItems="center" 
          mb={4}
          sx={{
            background: 'rgba(255, 255, 255, 0.9)',
            borderRadius: '20px',
            p: 3,
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255, 255, 255, 0.3)',
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.1)',
          }}
        >
          <Box display="flex" alignItems="center" gap={3}>
            <Avatar sx={{ 
              bgcolor: 'transparent',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              width: 64,
              height: 64,
              boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
            }}>
              <AccountIcon sx={{ fontSize: 32, color: 'white' }} />
            </Avatar>
            <Box>
              <Typography 
                variant="h3" 
                sx={{
                  fontWeight: 700,
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  backgroundClip: 'text',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  mb: 1,
                }}
              >
                Welcome, {user?.name}! 🎓
              </Typography>
              <Typography variant="h6" color="text.secondary" sx={{ fontWeight: 400 }}>
                {user?.district} • Class {user?.className}
              </Typography>
            </Box>
          </Box>
          <Button 
            variant="outlined" 
            onClick={handleLogout}
            sx={{
              borderRadius: '25px',
              px: 3,
              py: 1.5,
              borderColor: '#667eea',
              color: '#667eea',
              fontWeight: 600,
              textTransform: 'none',
              '&:hover': {
                borderColor: '#764ba2',
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                transform: 'translateY(-2px)',
                boxShadow: '0 4px 16px rgba(102, 126, 234, 0.3)',
              },
              transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            }}
          >
            Logout
          </Button>
        </Box>

        {/* Status Cards */}
        <Box display="flex" gap={3} mb={4} sx={{ flexWrap: 'wrap' }}>
          <Box flex={1} minWidth={300}>
            <Card sx={{
              borderRadius: '20px',
              background: 'rgba(255, 255, 255, 0.9)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              '&:hover': {
                transform: 'translateY(-8px)',
                boxShadow: '0 20px 60px rgba(0, 0, 0, 0.15)',
              },
            }}>
              <CardContent sx={{ p: 3 }}>
                <Box display="flex" alignItems="center" gap={2}>
                  <Avatar sx={{ 
                    background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                    width: 56,
                    height: 56,
                    boxShadow: '0 4px 16px rgba(16, 185, 129, 0.3)',
                  }}>
                    <QuizIcon sx={{ fontSize: 28 }} />
                  </Avatar>
                  <Box>
                    <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>Quiz Status</Typography>
                    <Chip 
                      label="Ready to Take" 
                      sx={{
                        background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
                        color: 'white',
                        fontWeight: 500,
                      }}
                      size="small" 
                    />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>
          <Box flex={1} minWidth={300}>
            <Card sx={{
              borderRadius: '20px',
              background: 'rgba(255, 255, 255, 0.9)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              '&:hover': {
                transform: 'translateY(-8px)',
                boxShadow: '0 20px 60px rgba(0, 0, 0, 0.15)',
              },
            }}>
              <CardContent sx={{ p: 3 }}>
                <Box display="flex" alignItems="center" gap={2}>
                  <Avatar sx={{ 
                    background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
                    width: 56,
                    height: 56,
                    boxShadow: '0 4px 16px rgba(59, 130, 246, 0.3)',
                  }}>
                    <AssessmentIcon sx={{ fontSize: 28 }} />
                  </Avatar>
                  <Box>
                    <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>AI Analysis</Typography>
                    <Chip 
                      label="Pending Quiz" 
                      sx={{
                        background: 'rgba(107, 114, 128, 0.8)',
                        color: 'white',
                        fontWeight: 500,
                      }}
                      size="small" 
                    />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>
          <Box flex={1} minWidth={300}>
            <Card sx={{
              borderRadius: '20px',
              background: 'rgba(255, 255, 255, 0.9)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              '&:hover': {
                transform: 'translateY(-8px)',
                boxShadow: '0 20px 60px rgba(0, 0, 0, 0.15)',
              },
            }}>
              <CardContent sx={{ p: 3 }}>
                <Box display="flex" alignItems="center" gap={2}>
                  <Avatar sx={{ 
                    background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
                    width: 56,
                    height: 56,
                    boxShadow: '0 4px 16px rgba(245, 158, 11, 0.3)',
                  }}>
                    <SchoolIcon sx={{ fontSize: 28 }} />
                  </Avatar>
                  <Box>
                    <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>Recommendations</Typography>
                    <Chip 
                      label="Available Soon" 
                      sx={{
                        background: 'rgba(107, 114, 128, 0.8)',
                        color: 'white',
                        fontWeight: 500,
                      }}
                      size="small" 
                    />
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* Main Actions */}
        <Box display="flex" gap={4} mb={4} sx={{ flexWrap: 'wrap' }}>
          <Box flex={1} minWidth={350}>
            <Card sx={{ 
              height: '100%',
              borderRadius: '24px',
              background: 'rgba(255, 255, 255, 0.95)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 20px 60px rgba(0, 0, 0, 0.1)',
              transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
              overflow: 'hidden',
              '&:hover': {
                transform: 'translateY(-12px)',
                boxShadow: '0 30px 80px rgba(0, 0, 0, 0.15)',
                '& .action-avatar': {
                  transform: 'scale(1.1) rotate(5deg)',
                },
                '& .action-button': {
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 8px 32px rgba(102, 126, 234, 0.4)',
                },
              },
            }}>
              <CardContent sx={{ p: 4, position: 'relative' }}>
                <Box 
                  sx={{
                    position: 'absolute',
                    top: 0,
                    right: 0,
                    width: '100px',
                    height: '100px',
                    background: 'linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%)',
                    borderRadius: '0 0 0 100px',
                  }}
                />
                <Box display="flex" flexDirection="column" alignItems="center" textAlign="center" sx={{ position: 'relative', zIndex: 1 }}>
                  <Avatar 
                    className="action-avatar"
                    sx={{ 
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      width: 80, 
                      height: 80, 
                      mb: 3,
                      boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
                      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    }}
                  >
                    <QuizIcon sx={{ fontSize: 40, color: 'white' }} />
                  </Avatar>
                  <Typography 
                    variant="h4" 
                    gutterBottom 
                    sx={{ 
                      fontWeight: 700,
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      backgroundClip: 'text',
                      WebkitBackgroundClip: 'text',
                      WebkitTextFillColor: 'transparent',
                      mb: 2,
                    }}
                  >
                    Take Assessment Quiz
                  </Typography>
                  <Typography variant="body1" color="text.secondary" mb={4} sx={{ lineHeight: 1.7 }}>
                    Complete our comprehensive aptitude assessment to get personalized college recommendations
                    based on your strengths and location.
                  </Typography>
                  <Button
                    className="action-button"
                    variant="contained"
                    size="large"
                    onClick={handleStartQuiz}
                    sx={{ 
                      minWidth: 220,
                      py: 1.5,
                      borderRadius: '25px',
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      fontSize: '1.1rem',
                      fontWeight: 600,
                      textTransform: 'none',
                      boxShadow: '0 4px 16px rgba(102, 126, 234, 0.3)',
                      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    }}
                  >
                    Start Quiz ✨
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box flex={1} minWidth={350}>
            <Card sx={{ 
              height: '100%',
              borderRadius: '24px',
              background: 'rgba(255, 255, 255, 0.95)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 20px 60px rgba(0, 0, 0, 0.1)',
              transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
              overflow: 'hidden',
              '&:hover': {
                transform: 'translateY(-12px)',
                boxShadow: '0 30px 80px rgba(0, 0, 0, 0.15)',
                '& .action-avatar': {
                  transform: 'scale(1.1) rotate(-5deg)',
                },
                '& .action-button': {
                  background: 'linear-gradient(135deg, #e11d48 0%, #be123c 100%)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 8px 32px rgba(225, 29, 72, 0.4)',
                  color: 'white',
                },
              },
            }}>
              <CardContent sx={{ p: 4, position: 'relative' }}>
                <Box 
                  sx={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100px',
                    height: '100px',
                    background: 'linear-gradient(135deg, rgba(225, 29, 72, 0.1) 0%, rgba(190, 18, 60, 0.1) 100%)',
                    borderRadius: '0 0 100px 0',
                  }}
                />
                <Box display="flex" flexDirection="column" alignItems="center" textAlign="center" sx={{ position: 'relative', zIndex: 1 }}>
                  <Avatar 
                    className="action-avatar"
                    sx={{ 
                      background: 'linear-gradient(135deg, #e11d48 0%, #be123c 100%)',
                      width: 80, 
                      height: 80, 
                      mb: 3,
                      boxShadow: '0 8px 32px rgba(225, 29, 72, 0.3)',
                      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    }}
                  >
                    <SchoolIcon sx={{ fontSize: 40, color: 'white' }} />
                  </Avatar>
                  <Typography 
                    variant="h4" 
                    gutterBottom 
                    sx={{ 
                      fontWeight: 700,
                      background: 'linear-gradient(135deg, #e11d48 0%, #be123c 100%)',
                      backgroundClip: 'text',
                      WebkitBackgroundClip: 'text',
                      WebkitTextFillColor: 'transparent',
                      mb: 2,
                    }}
                  >
                    View Recommendations
                  </Typography>
                  <Typography variant="body1" color="text.secondary" mb={4} sx={{ lineHeight: 1.7 }}>
                    Access your AI-powered college recommendations, career guidance, and action plans
                    after completing the assessment.
                  </Typography>
                  <Button
                    className="action-button"
                    variant="outlined"
                    size="large"
                    onClick={handleViewRecommendations}
                    sx={{ 
                      minWidth: 220,
                      py: 1.5,
                      borderRadius: '25px',
                      borderColor: '#e11d48',
                      color: '#e11d48',
                      fontSize: '1.1rem',
                      fontWeight: 600,
                      textTransform: 'none',
                      borderWidth: '2px',
                      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    }}
                  >
                    View Recommendations 🎯
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box flex={1} minWidth={300}>
            <Card sx={{ 
              height: '100%',
              background: 'linear-gradient(135deg, rgba(46, 125, 50, 0.05) 0%, rgba(76, 175, 80, 0.05) 100%)',
              backdropFilter: 'blur(10px)',
              border: '1px solid rgba(46, 125, 50, 0.2)',
              borderRadius: 3,
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              position: 'relative',
              overflow: 'hidden',
              '&::before': {
                content: '""',
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background: 'linear-gradient(135deg, rgba(46, 125, 50, 0.02) 0%, rgba(76, 175, 80, 0.02) 100%)',
                zIndex: 0,
              },
              '&:hover': {
                transform: 'translateY(-5px)',
                boxShadow: '0 20px 40px rgba(46, 125, 50, 0.2)',
                border: '1px solid rgba(46, 125, 50, 0.3)',
              }
            }}>
              <CardContent sx={{ p: 4, position: 'relative', zIndex: 1 }}>
                <Box display="flex" flexDirection="column" alignItems="center" textAlign="center">
                  <Avatar sx={{ 
                    background: 'linear-gradient(135deg, #2e7d32 0%, #4caf50 100%)',
                    width: 64, 
                    height: 64, 
                    mb: 2,
                    boxShadow: '0 8px 20px rgba(46, 125, 50, 0.3)',
                    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    '&:hover': {
                      transform: 'scale(1.1) rotate(5deg)',
                      boxShadow: '0 12px 30px rgba(46, 125, 50, 0.4)',
                    }
                  }}>
                    <ChatIcon sx={{ fontSize: 32, color: 'white' }} />
                  </Avatar>
                  <Typography variant="h5" gutterBottom sx={{
                    background: 'linear-gradient(135deg, #2e7d32 0%, #4caf50 100%)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    fontWeight: 700,
                    mb: 1,
                  }}>
                    AI Chatbot
                  </Typography>
                  <Typography variant="body1" color="text.secondary" mb={3} sx={{
                    lineHeight: 1.6,
                    maxWidth: '280px',
                  }}>
                    Get instant answers to your education questions and personalized guidance
                    from our AI assistant.
                  </Typography>
                  <Button
                    variant="contained"
                    size="large"
                    sx={{
                      background: 'linear-gradient(135deg, #2e7d32 0%, #4caf50 100%)',
                      color: 'white',
                      px: 4,
                      py: 1.5,
                      borderRadius: 2,
                      fontWeight: 600,
                      textTransform: 'none',
                      borderWidth: '2px',
                      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                      minWidth: 200,
                      '&:hover': {
                        background: 'linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%)',
                        transform: 'translateY(-2px)',
                        boxShadow: '0 10px 25px rgba(46, 125, 50, 0.4)',
                      }
                    }}
                    onClick={handleChatbot}
                  >
                    Start Chat 💬
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* Info Section */}
        <Box mt={4}>
          <Card sx={{
            background: 'linear-gradient(135deg, rgba(63, 81, 181, 0.05) 0%, rgba(103, 58, 183, 0.05) 100%)',
            backdropFilter: 'blur(10px)',
            border: '1px solid rgba(63, 81, 181, 0.2)',
            borderRadius: 3,
            position: 'relative',
            overflow: 'hidden',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              background: 'linear-gradient(135deg, rgba(63, 81, 181, 0.02) 0%, rgba(103, 58, 183, 0.02) 100%)',
              zIndex: 0,
            }
          }}>
            <CardContent sx={{ p: 4, position: 'relative', zIndex: 1 }}>
              <Typography variant="h6" gutterBottom sx={{
                background: 'linear-gradient(135deg, #3f51b5 0%, #673ab7 100%)',
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                fontWeight: 700,
                fontSize: '1.5rem',
                textAlign: 'center',
                mb: 4,
              }}>
                How It Works ✨
              </Typography>
              <Box display="flex" gap={4} sx={{ flexWrap: 'wrap' }}>
                <Box flex={1} minWidth={200} textAlign="center" sx={{
                  p: 3,
                  borderRadius: 2,
                  background: 'linear-gradient(135deg, rgba(63, 81, 181, 0.08) 0%, rgba(103, 58, 183, 0.08) 100%)',
                  border: '1px solid rgba(63, 81, 181, 0.15)',
                  backdropFilter: 'blur(5px)',
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: '0 15px 30px rgba(63, 81, 181, 0.2)',
                  }
                }}>
                  <Typography variant="h6" sx={{
                    background: 'linear-gradient(135deg, #3f51b5 0%, #673ab7 100%)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    fontWeight: 700,
                    fontSize: '2rem',
                    mb: 2,
                  }}>1</Typography>
                  <Typography variant="body2" sx={{
                    lineHeight: 1.6,
                    color: 'text.secondary',
                    fontWeight: 500,
                  }}>
                    Take the comprehensive aptitude assessment covering multiple domains
                  </Typography>
                </Box>
                <Box flex={1} minWidth={200} textAlign="center" sx={{
                  p: 3,
                  borderRadius: 2,
                  background: 'linear-gradient(135deg, rgba(63, 81, 181, 0.08) 0%, rgba(103, 58, 183, 0.08) 100%)',
                  border: '1px solid rgba(63, 81, 181, 0.15)',
                  backdropFilter: 'blur(5px)',
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: '0 15px 30px rgba(63, 81, 181, 0.2)',
                  }
                }}>
                  <Typography variant="h6" sx={{
                    background: 'linear-gradient(135deg, #3f51b5 0%, #673ab7 100%)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    fontWeight: 700,
                    fontSize: '2rem',
                    mb: 2,
                  }}>2</Typography>
                  <Typography variant="body2" sx={{
                    lineHeight: 1.6,
                    color: 'text.secondary',
                    fontWeight: 500,
                  }}>
                    AI analyzes your performance and considers your location preferences
                  </Typography>
                </Box>
                <Box flex={1} minWidth={200} textAlign="center" sx={{
                  p: 3,
                  borderRadius: 2,
                  background: 'linear-gradient(135deg, rgba(63, 81, 181, 0.08) 0%, rgba(103, 58, 183, 0.08) 100%)',
                  border: '1px solid rgba(63, 81, 181, 0.15)',
                  backdropFilter: 'blur(5px)',
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: '0 15px 30px rgba(63, 81, 181, 0.2)',
                  }
                }}>
                  <Typography variant="h6" sx={{
                    background: 'linear-gradient(135deg, #3f51b5 0%, #673ab7 100%)',
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    fontWeight: 700,
                    fontSize: '2rem',
                    mb: 2,
                  }}>3</Typography>
                  <Typography variant="body2" sx={{
                    lineHeight: 1.6,
                    color: 'text.secondary',
                    fontWeight: 500,
                  }}>
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