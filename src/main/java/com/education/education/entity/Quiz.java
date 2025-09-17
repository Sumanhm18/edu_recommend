package com.education.education.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "quizzes")
public class Quiz {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "questions_json", columnDefinition = "TEXT")
    private String questionsJson;
    
    @Column(name = "target_class")
    private String targetClass; // 10th, 12th, all
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<QuizAttempt> attempts;
    
    // Constructors
    public Quiz() {}
    
    public Quiz(String title, String description, String questionsJson, String targetClass) {
        this.title = title;
        this.description = description;
        this.questionsJson = questionsJson;
        this.targetClass = targetClass;
    }
    
    // Getters and Setters
    public Long getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getQuestionsJson() {
        return questionsJson;
    }
    
    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
    
    public String getTargetClass() {
        return targetClass;
    }
    
    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Set<QuizAttempt> getAttempts() {
        return attempts;
    }
    
    public void setAttempts(Set<QuizAttempt> attempts) {
        this.attempts = attempts;
    }
}