import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  Paper,
  TextField,
  IconButton,
  Typography,
  List,
  ListItem,
  Avatar,
  Chip,
  Divider,
  CircularProgress,
  Fab,
  Collapse,
  Alert,
  useTheme,
  useMediaQuery
} from '@mui/material';
import {
  Send as SendIcon,
  Person as PersonIcon,
  SmartToy as BotIcon,
  Chat as ChatIcon,
  Close as CloseIcon,
  Refresh as RefreshIcon
} from '@mui/icons-material';
import { chatbotService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { ChatMessage, ChatResponse, ChatConversation } from '../types';

interface ChatbotProps {
  isOpen?: boolean;
  onToggle?: () => void;
  conversationId?: number;
}

const Chatbot: React.FC<ChatbotProps> = ({ 
  isOpen = false, 
  onToggle,
  conversationId: initialConversationId 
}) => {
  const { user } = useAuth();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId, setConversationId] = useState<number | undefined>(initialConversationId);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (isOpen && conversationId) {
      loadConversationHistory();
    }
  }, [isOpen, conversationId]);

  const loadConversationHistory = async () => {
    if (!conversationId) return;
    
    try {
      const history = await chatbotService.getConversationHistory(conversationId);
      // Ensure history is an array
      if (Array.isArray(history)) {
        setMessages(history);
      } else {
        console.error('Conversation history is not an array:', history);
        setMessages([]);
        setError('Invalid conversation history format');
      }
    } catch (error) {
      console.error('Failed to load conversation history:', error);
      setMessages([]); // Ensure messages is always an array
      setError('Failed to load conversation history');
    }
  };

  const sendMessage = async () => {
    if (!inputMessage.trim() || !user || isLoading) return;

    console.log('Sending message:', inputMessage.trim());
    console.log('Current user:', user);
    console.log('Current conversationId:', conversationId);

    const userMessage: ChatMessage = {
      messageId: Date.now(), // Temporary ID
      content: inputMessage.trim(),
      messageType: 'USER',
      timestamp: new Date().toISOString()
    };

    setMessages(prev => {
      // Ensure prev is always an array
      const currentMessages = Array.isArray(prev) ? prev : [];
      return [...currentMessages, userMessage];
    });
    setInputMessage('');
    setIsLoading(true);
    setError(null);

    try {
      console.log('Making API call to chatbot service...');
      const response: ChatResponse = await chatbotService.sendMessage({
        userId: user.id,
        message: inputMessage.trim(),
        conversationId
      });
      console.log('Received response:', response);

      // Update conversation ID if this is a new conversation
      if (!conversationId && response.conversationId) {
        setConversationId(response.conversationId);
      }

      const assistantMessage: ChatMessage = {
        messageId: Date.now() + 1,
        content: response.message,
        messageType: 'ASSISTANT',
        timestamp: response.timestamp
      };

      setMessages(prev => {
        // Ensure prev is always an array
        const currentMessages = Array.isArray(prev) ? prev : [];
        return [...currentMessages, assistantMessage];
      });
    } catch (error: any) {
      console.error('Failed to send message:', error);
      setError('Failed to send message. Please try again.');
      
      // Add error message to chat
      const errorMessage: ChatMessage = {
        messageId: Date.now() + 1,
        content: 'Sorry, I encountered an error. Please try again.',
        messageType: 'ASSISTANT',
        timestamp: new Date().toISOString()
      };
      setMessages(prev => {
        // Ensure prev is always an array
        const currentMessages = Array.isArray(prev) ? prev : [];
        return [...currentMessages, errorMessage];
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      sendMessage();
    }
  };

  const startNewConversation = () => {
    setMessages([]);
    setConversationId(undefined);
    setError(null);
    
    // Add welcome message
    const welcomeMessage: ChatMessage = {
      messageId: Date.now(),
      content: `Hello ${user?.name}! 👋 I'm your educational assistant. I can help you with college recommendations, career guidance, study tips, and academic planning. How can I assist you today?`,
      messageType: 'ASSISTANT',
      timestamp: new Date().toISOString()
    };
    setMessages([welcomeMessage]);
  };

  const formatTimestamp = (timestamp: string) => {
    return new Date(timestamp).toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  if (!isOpen) {
    return (
      <Fab
        color="primary"
        aria-label="chat"
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
          zIndex: 1000,
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
            boxShadow: '0 12px 40px rgba(102, 126, 234, 0.4)',
            transform: 'translateY(-2px) scale(1.05)',
          },
          '&:active': {
            transform: 'translateY(0) scale(0.98)',
          },
          animation: 'pulse 2s infinite',
          '@keyframes pulse': {
            '0%': {
              boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
            },
            '50%': {
              boxShadow: '0 8px 32px rgba(102, 126, 234, 0.6)',
            },
            '100%': {
              boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
            },
          },
        }}
        onClick={onToggle}
      >
        <ChatIcon sx={{ fontSize: 28 }} />
      </Fab>
    );
  }

  return (
    <Paper
      elevation={24}
      sx={{
        position: 'fixed',
        bottom: isMobile ? 0 : 24,
        right: isMobile ? 0 : 24,
        width: isMobile ? '100vw' : 420,
        height: isMobile ? '100vh' : 650,
        zIndex: 1000,
        display: 'flex',
        flexDirection: 'column',
        borderRadius: isMobile ? 0 : '24px',
        overflow: 'hidden',
        background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
        backdropFilter: 'blur(20px)',
        border: '1px solid rgba(255, 255, 255, 0.2)',
        boxShadow: '0 20px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.1)',
        animation: isMobile ? 'slideInFromBottom 0.4s cubic-bezier(0.4, 0, 0.2, 1)' : 'slideInUp 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
        '@keyframes slideInUp': {
          '0%': {
            transform: 'translateY(100%) scale(0.9)',
            opacity: 0,
          },
          '100%': {
            transform: 'translateY(0) scale(1)',
            opacity: 1,
          },
        },
        '@keyframes slideInFromBottom': {
          '0%': {
            transform: 'translateY(100%)',
            opacity: 0,
          },
          '100%': {
            transform: 'translateY(0)',
            opacity: 1,
          },
        },
      }}
    >
      {/* Header */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          p: 2.5,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          position: 'relative',
          '&::before': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: 'rgba(255, 255, 255, 0.1)',
            backdropFilter: 'blur(10px)',
          },
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, zIndex: 1 }}>
          <Avatar
            sx={{
              bgcolor: 'rgba(255, 255, 255, 0.2)',
              width: 44,
              height: 44,
              border: '2px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
            }}
          >
            <BotIcon sx={{ color: 'white', fontSize: 24 }} />
          </Avatar>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 600, fontSize: '1.1rem', mb: 0.2 }}>
              Educational Assistant
            </Typography>
            <Typography variant="caption" sx={{ opacity: 0.9, fontSize: '0.8rem' }}>
              {isLoading ? 'Thinking...' : 'Ready to help'}
            </Typography>
          </Box>
        </Box>
        <Box sx={{ zIndex: 1, display: 'flex', gap: 0.5 }}>
          <IconButton
            color="inherit"
            onClick={startNewConversation}
            title="New Conversation"
            size="small"
            sx={{
              bgcolor: 'rgba(255, 255, 255, 0.1)',
              '&:hover': {
                bgcolor: 'rgba(255, 255, 255, 0.2)',
                transform: 'scale(1.1)',
              },
              transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            }}
          >
            <RefreshIcon fontSize="small" />
          </IconButton>
          <IconButton
            color="inherit"
            onClick={onToggle}
            title="Close Chat"
            size="small"
            sx={{
              bgcolor: 'rgba(255, 255, 255, 0.1)',
              '&:hover': {
                bgcolor: 'rgba(255, 255, 255, 0.2)',
                transform: 'scale(1.1)',
              },
              transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            }}
          >
            <CloseIcon fontSize="small" />
          </IconButton>
        </Box>
      </Box>

      {/* Error Alert */}
      <Collapse in={!!error}>
        <Alert 
          severity="error" 
          onClose={() => setError(null)}
          sx={{ m: 1 }}
        >
          {error}
        </Alert>
      </Collapse>

      {/* Messages */}
      <Box
        sx={{
          flex: 1,
          overflow: 'auto',
          p: 2,
          background: 'linear-gradient(to bottom, #f8fafc 0%, #e2e8f0 100%)',
          backgroundImage: `
            radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.05) 0%, transparent 50%),
            radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.05) 0%, transparent 50%)
          `,
          '&::-webkit-scrollbar': {
            width: '6px',
          },
          '&::-webkit-scrollbar-track': {
            background: 'rgba(0, 0, 0, 0.05)',
            borderRadius: '10px',
          },
          '&::-webkit-scrollbar-thumb': {
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            borderRadius: '10px',
            '&:hover': {
              background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
            },
          },
        }}
      >
        {!Array.isArray(messages) || messages.length === 0 ? (
          <Box
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              height: '100%',
              textAlign: 'center',
              p: 3,
              background: 'rgba(255, 255, 255, 0.7)',
              borderRadius: '20px',
              margin: '20px',
              backdropFilter: 'blur(10px)',
              border: '1px solid rgba(255, 255, 255, 0.3)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
              animation: 'fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1)',
              '@keyframes fadeInUp': {
                '0%': {
                  opacity: 0,
                  transform: 'translateY(30px)',
                },
                '100%': {
                  opacity: 1,
                  transform: 'translateY(0)',
                },
              },
            }}
          >
            <Box
              sx={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                borderRadius: '50%',
                p: 2,
                mb: 3,
                boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
              }}
            >
              <BotIcon sx={{ fontSize: 48, color: 'white' }} />
            </Box>
            <Typography 
              variant="h5" 
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
              Welcome to Educational Assistant! 🎓
            </Typography>
            <Typography 
              variant="body1" 
              color="text.secondary" 
              sx={{ 
                mb: 3, 
                lineHeight: 1.6,
                opacity: 0.8,
              }}
            >
              Ask me about college recommendations, career guidance, study tips, or any educational questions you have.
            </Typography>
            <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1, justifyContent: 'center' }}>
              <Chip 
                label="College Advice" 
                size="small" 
                sx={{ 
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  color: 'white',
                  fontWeight: 500,
                  '&:hover': {
                    background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
                    transform: 'scale(1.05)',
                  },
                  transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                }} 
              />
              <Chip 
                label="Career Guidance" 
                size="small" 
                sx={{ 
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  color: 'white',
                  fontWeight: 500,
                  '&:hover': {
                    background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
                    transform: 'scale(1.05)',
                  },
                  transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                }} 
              />
              <Chip 
                label="Study Tips" 
                size="small" 
                sx={{ 
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  color: 'white',
                  fontWeight: 500,
                  '&:hover': {
                    background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
                    transform: 'scale(1.05)',
                  },
                  transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                }} 
              />
              <Chip 
                label="Admission Help" 
                size="small" 
                sx={{ 
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  color: 'white',
                  fontWeight: 500,
                  '&:hover': {
                    background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
                    transform: 'scale(1.05)',
                  },
                  transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                }} 
              />
            </Box>
          </Box>
        ) : (
          <List sx={{ py: 0 }}>
            {Array.isArray(messages) && messages.map((message, index) => (
              <ListItem
                key={message.messageId}
                sx={{
                  flexDirection: 'column',
                  alignItems: message.messageType === 'USER' ? 'flex-end' : 'flex-start',
                  py: 1
                }}
              >
                <Box
                  sx={{
                    display: 'flex',
                    alignItems: 'flex-start',
                    gap: 1,
                    maxWidth: '85%',
                    flexDirection: message.messageType === 'USER' ? 'row-reverse' : 'row'
                  }}
                >
                  <Avatar
                    sx={{
                      width: 36,
                      height: 36,
                      background: message.messageType === 'USER' 
                        ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                        : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      boxShadow: '0 4px 16px rgba(102, 126, 234, 0.3)',
                      border: '2px solid rgba(255, 255, 255, 0.2)',
                    }}
                  >
                    {message.messageType === 'USER' ? (
                      <PersonIcon sx={{ color: 'white', fontSize: 20 }} />
                    ) : (
                      <BotIcon sx={{ color: 'white', fontSize: 20 }} />
                    )}
                  </Avatar>
                  <Paper
                    elevation={0}
                    sx={{
                      p: 2.5,
                      background: message.messageType === 'USER' 
                        ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                        : 'rgba(255, 255, 255, 0.9)',
                      color: message.messageType === 'USER' ? 'white' : 'text.primary',
                      borderRadius: message.messageType === 'USER' ? '20px 20px 6px 20px' : '20px 20px 20px 6px',
                      backdropFilter: 'blur(10px)',
                      border: message.messageType === 'USER' 
                        ? 'none' 
                        : '1px solid rgba(255, 255, 255, 0.3)',
                      boxShadow: message.messageType === 'USER'
                        ? '0 8px 32px rgba(102, 126, 234, 0.3)'
                        : '0 4px 16px rgba(0, 0, 0, 0.1)',
                      transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                      wordBreak: 'break-word',
                      '&:hover': {
                        transform: 'translateY(-2px)',
                        boxShadow: message.messageType === 'USER'
                          ? '0 12px 40px rgba(102, 126, 234, 0.4)'
                          : '0 8px 24px rgba(0, 0, 0, 0.15)',
                      },
                    }}
                  >
                    <Typography 
                      variant="body2" 
                      sx={{ 
                        whiteSpace: 'pre-wrap',
                        lineHeight: 1.6,
                        fontSize: '0.9rem',
                      }}
                    >
                      {message.content}
                    </Typography>
                    <Typography
                      variant="caption"
                      sx={{
                        display: 'block',
                        mt: 1,
                        opacity: 0.7,
                        fontSize: '0.7rem',
                        textAlign: message.messageType === 'USER' ? 'right' : 'left',
                      }}
                    >
                      {formatTimestamp(message.timestamp)}
                    </Typography>
                  </Paper>
                </Box>
              </ListItem>
            ))}
          </List>
        )}
        
        {isLoading && (
          <Box sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: 1.5, 
            p: 2,
            animation: 'fadeIn 0.3s ease-in-out',
            '@keyframes fadeIn': {
              '0%': { opacity: 0 },
              '100%': { opacity: 1 },
            },
          }}>
            <Avatar sx={{ 
              width: 36, 
              height: 36, 
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              boxShadow: '0 4px 16px rgba(102, 126, 234, 0.3)',
            }}>
              <BotIcon sx={{ color: 'white', fontSize: 20 }} />
            </Avatar>
            <Paper 
              elevation={0} 
              sx={{ 
                p: 2.5, 
                borderRadius: '20px 20px 20px 6px',
                background: 'rgba(255, 255, 255, 0.9)',
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255, 255, 255, 0.3)',
                boxShadow: '0 4px 16px rgba(0, 0, 0, 0.1)',
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                <CircularProgress 
                  size={18} 
                  sx={{ 
                    color: '#667eea',
                    animation: 'spin 1s linear infinite',
                  }} 
                />
                <Typography 
                  variant="body2" 
                  sx={{ 
                    fontStyle: 'italic',
                    color: 'text.secondary',
                  }}
                >
                  AI is thinking...
                </Typography>
              </Box>
            </Paper>
          </Box>
        )}
        
        <div ref={messagesEndRef} />
      </Box>

      <Divider />

      {/* Input */}
      <Box sx={{ 
        p: 3, 
        background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
        borderTop: '1px solid rgba(0, 0, 0, 0.08)',
        backdropFilter: 'blur(10px)',
      }}>
        <Box sx={{ display: 'flex', gap: 1.5, alignItems: 'flex-end' }}>
          <TextField
            fullWidth
            multiline
            maxRows={4}
            placeholder="Ask me anything about education..."
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={isLoading}
            variant="outlined"
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: '20px',
                backgroundColor: 'rgba(255, 255, 255, 0.9)',
                backdropFilter: 'blur(10px)',
                border: '1px solid rgba(255, 255, 255, 0.3)',
                '&:hover': {
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#667eea',
                  },
                },
                '&.Mui-focused': {
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#667eea',
                    borderWidth: '2px',
                  },
                },
                '& .MuiOutlinedInput-notchedOutline': {
                  borderColor: 'rgba(102, 126, 234, 0.2)',
                  transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                },
              },
              '& .MuiInputBase-input': {
                fontSize: '0.9rem',
                lineHeight: 1.6,
                '&::placeholder': {
                  color: 'rgba(0, 0, 0, 0.5)',
                  opacity: 1,
                },
              },
            }}
          />
          <IconButton
            color="primary"
            onClick={sendMessage}
            disabled={!inputMessage.trim() || isLoading}
            sx={{ 
              alignSelf: 'flex-end',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              width: 48,
              height: 48,
              borderRadius: '50%',
              boxShadow: '0 4px 16px rgba(102, 126, 234, 0.3)',
              '&:hover': {
                background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
                boxShadow: '0 6px 20px rgba(102, 126, 234, 0.4)',
                transform: 'scale(1.05)',
              },
              '&:active': {
                transform: 'scale(0.95)',
              },
              '&:disabled': {
                background: 'rgba(0, 0, 0, 0.12)',
                color: 'rgba(0, 0, 0, 0.26)',
                boxShadow: 'none',
                transform: 'none',
              },
              transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            }}
          >
            <SendIcon sx={{ fontSize: 22 }} />
          </IconButton>
        </Box>
      </Box>
    </Paper>
  );
};

export default Chatbot;