import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemButton,
  IconButton,
  Divider,
  Chip,
  Button,
  CircularProgress
} from '@mui/material';
import {
  Add as AddIcon,
  Chat as ChatIcon,
  School as SchoolIcon
} from '@mui/icons-material';
import Chatbot from './Chatbot';
import { chatbotService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { ChatConversation } from '../types';

const ChatbotPage: React.FC = () => {
  const { user } = useAuth();
  const [conversations, setConversations] = useState<ChatConversation[]>([]);
  const [selectedConversationId, setSelectedConversationId] = useState<number | undefined>();
  const [isChatOpen, setIsChatOpen] = useState(true);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (user?.id) {
      loadConversations();
    } else {
      setIsLoading(false);
      setConversations([]);
    }
  }, [user]);

  const loadConversations = async () => {
    if (!user?.id) return;

    try {
      setIsLoading(true);
      const userConversations = await chatbotService.getUserConversations(user.id);
      
      // Ensure userConversations is an array
      const conversationsArray = Array.isArray(userConversations) ? userConversations : [];
      setConversations(conversationsArray);
      
      // If no conversations exist, start a new one
      if (conversationsArray.length === 0) {
        setSelectedConversationId(undefined);
      } else {
        // Select the most recent conversation by default
        setSelectedConversationId(conversationsArray[0].conversationId);
      }
    } catch (error) {
      console.error('Failed to load conversations:', error);
      // Set empty array on error
      setConversations([]);
      setSelectedConversationId(undefined);
    } finally {
      setIsLoading(false);
    }
  };

  const createNewConversation = async () => {
    if (!user?.id) return;

    try {
      const newConversation = await chatbotService.createNewConversation({
        userId: user.id,
        title: 'New Conversation'
      });
      
      setConversations(prev => Array.isArray(prev) ? [newConversation, ...prev] : [newConversation]);
      setSelectedConversationId(newConversation.conversationId);
    } catch (error) {
      console.error('Failed to create new conversation:', error);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) return 'Today';
    if (diffDays === 2) return 'Yesterday';
    if (diffDays <= 7) return `${diffDays - 1} days ago`;
    return date.toLocaleDateString();
  };

  return (
    <Container 
      maxWidth="xl" 
      sx={{ 
        py: 3,
        background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
        minHeight: '100vh',
        backgroundImage: `
          radial-gradient(circle at 20% 20%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
          radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.1) 0%, transparent 50%)
        `,
      }}
    >
      <Box sx={{ 
        mb: 4, 
        textAlign: 'center',
        p: 3,
        background: 'rgba(255, 255, 255, 0.8)',
        borderRadius: '20px',
        backdropFilter: 'blur(10px)',
        border: '1px solid rgba(255, 255, 255, 0.3)',
        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
      }}>
        <Typography 
          variant="h3" 
          gutterBottom 
          sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: 2, 
            justifyContent: 'center',
            fontWeight: 700,
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            backgroundClip: 'text',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
          }}
        >
          <Box
            sx={{
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              borderRadius: '50%',
              p: 1.5,
              boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
            }}
          >
            <SchoolIcon sx={{ color: 'white', fontSize: 32 }} />
          </Box>
          Educational Assistant
        </Typography>
        
        <Typography 
          variant="h6" 
          color="text.secondary" 
          sx={{ 
            fontWeight: 400,
            opacity: 0.8,
            lineHeight: 1.6,
          }}
        >
          Get personalized guidance on college recommendations, career planning, and academic advice.
        </Typography>
      </Box>

      <Box 
        sx={{ 
          display: 'flex', 
          gap: 3, 
          height: 'calc(100vh - 200px)',
          flexDirection: { xs: 'column', md: 'row' }
        }}
      >
        {/* Conversation History Sidebar */}
        <Box sx={{ 
          width: { xs: '100%', md: '320px', lg: '380px' },
          minWidth: '300px'
        }}>
          <Paper sx={{ 
            height: '100%', 
            display: 'flex', 
            flexDirection: 'column',
            borderRadius: '20px',
            background: 'rgba(255, 255, 255, 0.9)',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255, 255, 255, 0.3)',
            boxShadow: '0 20px 60px rgba(0, 0, 0, 0.1)',
            overflow: 'hidden',
          }}>
            <Box sx={{ 
              p: 3, 
              borderBottom: 1, 
              borderColor: 'rgba(0, 0, 0, 0.08)',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
            }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  Conversations
                </Typography>
                <IconButton
                  onClick={createNewConversation}
                  title="New Conversation"
                  sx={{
                    color: 'white',
                    bgcolor: 'rgba(255, 255, 255, 0.1)',
                    '&:hover': {
                      bgcolor: 'rgba(255, 255, 255, 0.2)',
                      transform: 'scale(1.1)',
                    },
                    transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                  }}
                >
                  <AddIcon />
                </IconButton>
              </Box>
            </Box>
            
            <Box sx={{ 
              flex: 1, 
              overflow: 'auto',
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
            }}>
              {isLoading ? (
                <Box sx={{ p: 3, textAlign: 'center' }}>
                  <CircularProgress 
                    size={32} 
                    sx={{ 
                      color: '#667eea',
                      mb: 2,
                    }} 
                  />
                  <Typography variant="body2" color="text.secondary">
                    Loading conversations...
                  </Typography>
                </Box>
              ) : !Array.isArray(conversations) || conversations.length === 0 ? (
                <Box sx={{ 
                  p: 4, 
                  textAlign: 'center',
                  background: 'rgba(255, 255, 255, 0.5)',
                  margin: 2,
                  borderRadius: '16px',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                }}>
                  <Box
                    sx={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      borderRadius: '50%',
                      p: 2,
                      mb: 2,
                      display: 'inline-flex',
                      boxShadow: '0 8px 32px rgba(102, 126, 234, 0.3)',
                    }}
                  >
                    <ChatIcon sx={{ fontSize: 32, color: 'white' }} />
                  </Box>
                  <Typography variant="body1" color="text.secondary" gutterBottom sx={{ fontWeight: 500 }}>
                    No conversations yet
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 3, opacity: 0.8 }}>
                    Start your first conversation with our AI assistant
                  </Typography>
                  <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={createNewConversation}
                    sx={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      borderRadius: '25px',
                      px: 3,
                      py: 1,
                      textTransform: 'none',
                      fontWeight: 600,
                      boxShadow: '0 4px 16px rgba(102, 126, 234, 0.3)',
                      '&:hover': {
                        background: 'linear-gradient(135deg, #764ba2 0%, #667eea 100%)',
                        boxShadow: '0 6px 20px rgba(102, 126, 234, 0.4)',
                        transform: 'translateY(-2px)',
                      },
                      transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                    }}
                  >
                    Start Chatting
                  </Button>
                </Box>
              ) : (
                <List sx={{ py: 1 }}>
                  {Array.isArray(conversations) && conversations.map((conversation) => (
                    <React.Fragment key={conversation.conversationId}>
                      <ListItemButton
                        selected={selectedConversationId === conversation.conversationId}
                        onClick={() => setSelectedConversationId(conversation.conversationId)}
                        sx={{ 
                          py: 2.5,
                          px: 2,
                          mx: 1,
                          my: 0.5,
                          borderRadius: '12px',
                          transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
                          '&:hover': {
                            background: 'rgba(102, 126, 234, 0.08)',
                            transform: 'translateX(4px)',
                          },
                          '&.Mui-selected': {
                            background: 'linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%)',
                            border: '1px solid rgba(102, 126, 234, 0.2)',
                            '&:hover': {
                              background: 'linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%)',
                            },
                          },
                        }}
                      >
                        <ListItemText
                          primary={
                            <Typography variant="subtitle2" noWrap>
                              {conversation.title || `Conversation ${conversation.conversationId}`}
                            </Typography>
                          }
                          secondary={
                            <Box>
                              <Typography variant="caption" color="text.secondary">
                                {formatDate(conversation.updatedAt)}
                              </Typography>
                              {conversation.isActive && (
                                <Chip
                                  label="Active"
                                  size="small"
                                  color="success"
                                  sx={{ ml: 1, height: 16, fontSize: '0.6rem' }}
                                />
                              )}
                            </Box>
                          }
                        />
                      </ListItemButton>
                      <Divider />
                    </React.Fragment>
                  ))}
                </List>
              )}
            </Box>
          </Paper>
        </Box>

        {/* Chat Area */}
        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Box sx={{ height: '100%', position: 'relative' }}>
            {selectedConversationId || !Array.isArray(conversations) || conversations.length === 0 ? (
              <Chatbot
                isOpen={isChatOpen}
                onToggle={() => setIsChatOpen(!isChatOpen)}
                conversationId={selectedConversationId}
              />
            ) : (
              <Paper sx={{ 
                height: '100%', 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                flexDirection: 'column',
                gap: 2
              }}>
                <ChatIcon sx={{ fontSize: 64, color: 'text.secondary' }} />
                <Typography variant="h6" color="text.secondary">
                  Select a conversation to continue
                </Typography>
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={createNewConversation}
                >
                  Start New Conversation
                </Button>
              </Paper>
            )}
          </Box>
        </Box>
      </Box>
    </Container>
  );
};

export default ChatbotPage;