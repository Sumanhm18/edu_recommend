package com.education.education.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing individual chat messages within a conversation
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // For storing additional context/data as JSON
    
    // Constructors
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(ChatConversation conversation, String content, MessageType messageType) {
        this();
        this.conversation = conversation;
        this.content = content;
        this.messageType = messageType;
    }
    
    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public ChatConversation getConversation() {
        return conversation;
    }
    
    public void setConversation(ChatConversation conversation) {
        this.conversation = conversation;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Enum for message types
     */
    public enum MessageType {
        USER("user"),
        ASSISTANT("assistant"),
        SYSTEM("system");
        
        private final String value;
        
        MessageType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}