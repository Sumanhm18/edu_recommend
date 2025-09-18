package com.education.education.repository;

import com.education.education.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Find active quizzes
    List<Quiz> findByIsActiveTrue();

    // Find quizzes by target class
    List<Quiz> findByTargetClassAndIsActiveTrue(String targetClass);

    // Find quizzes for all classes or specific class
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true AND (q.targetClass = :targetClass OR q.targetClass = 'all')")
    List<Quiz> findQuizzesForClass(@Param("targetClass") String targetClass);

    // Find quiz by title
    Quiz findByTitleAndIsActiveTrue(String title);
}