package com.education.education.repository;

import com.education.education.entity.ChatMessage;
import com.education.education.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ChatMessage entity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Find all messages for a conversation, ordered by timestamp
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation = :conversation ORDER BY m.timestamp ASC")
    List<ChatMessage> findMessagesByConversation(@Param("conversation") ChatConversation conversation);
    
    /**
     * Find recent messages for a conversation (for context)
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation = :conversation ORDER BY m.timestamp DESC LIMIT :limit")
    List<ChatMessage> findRecentMessagesByConversation(@Param("conversation") ChatConversation conversation, @Param("limit") int limit);
    
    /**
     * Count messages in a conversation
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversation = :conversation")
    long countMessagesByConversation(@Param("conversation") ChatConversation conversation);
}