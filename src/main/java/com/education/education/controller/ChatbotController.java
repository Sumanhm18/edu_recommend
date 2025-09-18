package com.education.education.controller;

import com.education.education.entity.ChatConversation;
import com.education.education.entity.ChatMessage;
import com.education.education.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for chatbot functionality
 * Provides endpoints for chat interactions, conversation management
 */
@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ChatbotController {
    
    @Autowired
    private ChatbotService chatbotService;
    
    /**
     * Send a message to the chatbot
     * 
     * @param request Chat request containing user message and optional conversation ID
     * @return AI response with conversation details
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatbotService.ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        try {
            ChatbotService.ChatResponse response = chatbotService.processMessage(
                request.getUserId(),
                request.getMessage(),
                request.getConversationId()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            ChatbotService.ChatResponse errorResponse = new ChatbotService.ChatResponse(
                request.getConversationId(),
                "I apologize, but I'm experiencing technical difficulties. Please try again later.",
                java.time.LocalDateTime.now()
            );
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get conversation history
     * 
     * @param conversationId ID of the conversation
     * @return List of messages in the conversation
     */
    @GetMapping("/conversation/{conversationId}/history")
    public ResponseEntity<List<ChatMessage>> getConversationHistory(@PathVariable Long conversationId) {
        try {
            List<ChatMessage> messages = chatbotService.getConversationHistory(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all conversations for a user
     * 
     * @param userId ID of the user
     * @return List of user's conversations
     */
    @GetMapping("/user/{userId}/conversations")
    public ResponseEntity<List<ChatConversation>> getUserConversations(@PathVariable Long userId) {
        try {
            List<ChatConversation> conversations = chatbotService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Create a new conversation
     * 
     * @param request New conversation request
     * @return Created conversation details
     */
    @PostMapping("/conversation/new")
    public ResponseEntity<ChatConversation> createNewConversation(@RequestBody NewConversationRequest request) {
        try {
            ChatConversation conversation = chatbotService.createNewConversation(
                request.getUserId(),
                request.getTitle()
            );
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Health check endpoint for chatbot service
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Chatbot service is running");
    }
    
    /**
     * DTO for chat requests
     */
    public static class ChatRequest {
        private Long userId;
        private String message;
        private Long conversationId; // Optional - null for new conversation
        
        public ChatRequest() {}
        
        public ChatRequest(Long userId, String message, Long conversationId) {
            this.userId = userId;
            this.message = message;
            this.conversationId = conversationId;
        }
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    }
    
    /**
     * DTO for new conversation requests
     */
    public static class NewConversationRequest {
        private Long userId;
        private String title;
        
        public NewConversationRequest() {}
        
        public NewConversationRequest(Long userId, String title) {
            this.userId = userId;
            this.title = title;
        }
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}