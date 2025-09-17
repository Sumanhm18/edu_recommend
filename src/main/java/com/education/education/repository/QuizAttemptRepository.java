package com.education.education.repository;

import com.education.education.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    // Find all attempts by user
    List<QuizAttempt> findByUserUserIdOrderByTimestampDesc(Long userId);
    
    // Find attempts by user and quiz
    List<QuizAttempt> findByUserUserIdAndQuizQuizIdOrderByTimestampDesc(Long userId, Long quizId);
    
    // Find latest attempt by user and quiz
    Optional<QuizAttempt> findFirstByUserUserIdAndQuizQuizIdOrderByTimestampDesc(Long userId, Long quizId);
    
    // Find user's best score for a quiz
    @Query("SELECT MAX(qa.score) FROM QuizAttempt qa WHERE qa.user.userId = :userId AND qa.quiz.quizId = :quizId")
    Optional<Integer> findBestScoreByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);
    
    // Get user's average score across all quizzes
    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.user.userId = :userId")
    Optional<Double> findAverageScoreByUser(@Param("userId") Long userId);
    
    // Check if user has attempted a specific quiz
    boolean existsByUserUserIdAndQuizQuizId(Long userId, Long quizId);
}