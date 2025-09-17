package com.education.education.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @Column(columnDefinition = "TEXT")
    private String answersJson;
    
    private Integer score;
    
    @Column(name = "recommended_streams", columnDefinition = "TEXT")
    private String recommendedStreams;
    
    @CreationTimestamp
    private LocalDateTime timestamp;
    
    // Constructors
    public QuizAttempt() {}
    
    public QuizAttempt(User user, Quiz quiz, String answersJson, Integer score, String recommendedStreams) {
        this.user = user;
        this.quiz = quiz;
        this.answersJson = answersJson;
        this.score = score;
        this.recommendedStreams = recommendedStreams;
    }
    
    // Getters and Setters
    public Long getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    public String getAnswersJson() {
        return answersJson;
    }
    
    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getRecommendedStreams() {
        return recommendedStreams;
    }
    
    public void setRecommendedStreams(String recommendedStreams) {
        this.recommendedStreams = recommendedStreams;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}