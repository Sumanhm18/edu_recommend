package com.education.education.service;

import com.education.education.entity.*;
import com.education.education.repository.ChatConversationRepository;
import com.education.education.repository.ChatMessageRepository;
import com.education.education.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for handling chatbot functionality
 * Integrates with Gemini AI for conversational responses
 */
@Service
public class ChatbotService {
    
    @Autowired
    private ChatConversationRepository conversationRepository;
    
    @Autowired
    private ChatMessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestTemplate chatbotRestTemplate;
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    
    private final String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    
    /**
     * Process a chat message and return AI response
     */
    public ChatResponse processMessage(Long userId, String message, Long conversationId) {
        try {
            // Get or create user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get or create conversation
            ChatConversation conversation = getOrCreateConversation(user, conversationId);
            
            // Save user message
            ChatMessage userMessage = new ChatMessage(conversation, message, ChatMessage.MessageType.USER);
            messageRepository.save(userMessage);
            conversation.addMessage(userMessage);
            
            // Get conversation context
            List<ChatMessage> recentMessages = messageRepository.findRecentMessagesByConversation(conversation, 10);
            Collections.reverse(recentMessages); // Order chronologically for context
            
            // Generate AI response
            String aiResponse = generateAIResponse(message, recentMessages, user);
            
            // Save AI response
            ChatMessage assistantMessage = new ChatMessage(conversation, aiResponse, ChatMessage.MessageType.ASSISTANT);
            messageRepository.save(assistantMessage);
            conversation.addMessage(assistantMessage);
            
            // Update conversation
            conversationRepository.save(conversation);
            
            return new ChatResponse(
                conversation.getConversationId(),
                aiResponse,
                assistantMessage.getTimestamp()
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse(
                conversationId,
                "I apologize, but I'm having trouble processing your message right now. Please try again.",
                null
            );
        }
    }
    
    /**
     * Get conversation history
     */
    public List<ChatMessage> getConversationHistory(Long conversationId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        return messageRepository.findMessagesByConversation(conversation);
    }
    
    /**
     * Get all conversations for a user
     */
    public List<ChatConversation> getUserConversations(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return conversationRepository.findActiveConversationsByUser(user);
    }
    
    /**
     * Create a new conversation
     */
    public ChatConversation createNewConversation(Long userId, String title) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (title == null || title.trim().isEmpty()) {
            title = "New Conversation";
        }
        
        ChatConversation conversation = new ChatConversation(user, title);
        return conversationRepository.save(conversation);
    }
    
    private ChatConversation getOrCreateConversation(User user, Long conversationId) {
        if (conversationId != null) {
            return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        } else {
            // Create new conversation with title based on first message
            return createNewConversation(user.getUserId(), "New Chat");
        }
    }
    
    @SuppressWarnings("unchecked")
    private String generateAIResponse(String userMessage, List<ChatMessage> context, User user) {
        try {
            // Build context for AI
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("You are an intelligent educational assistant for a college recommendation platform. ");
            contextBuilder.append("You help students with academic guidance, career counseling, college information, and study tips. ");
            contextBuilder.append("Be friendly, supportive, and focus on educational topics.\\n\\n");
            
            // Add user context
            contextBuilder.append("Student Information:\\n");
            contextBuilder.append("- Name: ").append(user.getName()).append("\\n");
            if (user.getClassLevel() != null) {
                contextBuilder.append("- Class: ").append(user.getClassLevel()).append("\\n");
            }
            if (user.getDistrict() != null) {
                contextBuilder.append("- Location: ").append(user.getDistrict()).append("\\n");
            }
            contextBuilder.append("\\n");
            
            // Add conversation context
            if (!context.isEmpty()) {
                contextBuilder.append("Previous conversation context:\\n");
                for (ChatMessage msg : context.subList(Math.max(0, context.size() - 6), context.size())) {
                    String role = msg.getMessageType() == ChatMessage.MessageType.USER ? "Student" : "Assistant";
                    contextBuilder.append(role).append(": ").append(msg.getContent()).append("\\n");
                }
                contextBuilder.append("\\n");
            }
            
            contextBuilder.append("Current question: ").append(userMessage);
            
            // Prepare request body
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of(
                        "parts", List.of(
                            Map.of("text", contextBuilder.toString())
                        )
                    )
                ),
                "generationConfig", Map.of(
                    "temperature", 0.7,
                    "maxOutputTokens", 1000,
                    "topP", 0.9
                )
            );
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", geminiApiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<Map<String, Object>> response = chatbotRestTemplate.exchange(
                geminiApiUrl,
                HttpMethod.POST,
                entity,
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            // Extract response
            return extractResponseText(response.getBody());
            
        } catch (Exception e) {
            e.printStackTrace();
            return "I apologize, but I'm having trouble generating a response right now. Could you please try rephrasing your question?";
        }
    }
    
    @SuppressWarnings("unchecked")
    private String extractResponseText(Map<String, Object> responseBody) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                if (content != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "I'm having trouble understanding. Could you please rephrase your question?";
    }
    
    /**
     * DTO for chat responses
     */
    public static class ChatResponse {
        private Long conversationId;
        private String message;
        private java.time.LocalDateTime timestamp;
        
        public ChatResponse(Long conversationId, String message, java.time.LocalDateTime timestamp) {
            this.conversationId = conversationId;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        // Getters and setters
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public java.time.LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}