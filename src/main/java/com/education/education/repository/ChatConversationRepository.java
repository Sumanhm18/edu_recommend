package com.education.education.repository;

import com.education.education.entity.ChatConversation;
import com.education.education.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ChatConversation entity
 */
@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    /**
     * Find all active conversations for a user, ordered by most recent
     */
    @Query("SELECT c FROM ChatConversation c WHERE c.user = :user AND c.isActive = true ORDER BY c.updatedAt DESC")
    List<ChatConversation> findActiveConversationsByUser(@Param("user") User user);
    
    /**
     * Find all conversations for a user (including inactive)
     */
    @Query("SELECT c FROM ChatConversation c WHERE c.user = :user ORDER BY c.updatedAt DESC")
    List<ChatConversation> findAllConversationsByUser(@Param("user") User user);
    
    /**
     * Find the most recent active conversation for a user
     */
    @Query("SELECT c FROM ChatConversation c WHERE c.user = :user AND c.isActive = true ORDER BY c.updatedAt DESC LIMIT 1")
    Optional<ChatConversation> findMostRecentActiveConversation(@Param("user") User user);
    
    /**
     * Count active conversations for a user
     */
    @Query("SELECT COUNT(c) FROM ChatConversation c WHERE c.user = :user AND c.isActive = true")
    long countActiveConversationsByUser(@Param("user") User user);
}