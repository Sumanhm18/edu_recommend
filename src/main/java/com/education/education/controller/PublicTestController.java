package com.education.education.controller;

import com.education.education.entity.QuizAttempt;
import com.education.education.entity.User;
import com.education.education.repository.QuizAttemptRepository;
import com.education.education.service.GeminiAIService;
import com.education.education.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicTestController {

    @Autowired
    private GeminiAIService geminiAIService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    /**
     * Process quiz results and provide personalized AI recommendations
     */
    @PostMapping("/quiz-results-recommendations")
    public ResponseEntity<Map<String, Object>> getQuizBasedRecommendations(@RequestBody Map<String, Object> quizData) {
        try {
            // Extract quiz results and user info
            String studentName = (String) quizData.getOrDefault("studentName", "Student");
            String location = (String) quizData.getOrDefault("location", "India");
            String classLevel = (String) quizData.getOrDefault("classLevel", "12th");
            
            @SuppressWarnings("unchecked")
            List<String> quizAnswers = (List<String>) quizData.getOrDefault("quizAnswers", 
                List.of("A", "A", "A", "B", "A", "C", "A", "D", "B", "A", "B", "A"));
            
            String phone = (String) quizData.get("phone");
            String password = (String) quizData.get("password");
            
            // Calculate quiz score and determine recommended streams
            QuizAnalysis analysis = analyzeQuizResults(quizAnswers);
            
            // Build comprehensive user profile for AI
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("name", studentName);
            userProfile.put("totalScore", analysis.score);
            userProfile.put("maxScore", quizAnswers.size());
            userProfile.put("percentage", analysis.percentage);
            userProfile.put("recommendedStream", analysis.primaryStream);
            userProfile.put("alternativeStreams", analysis.alternativeStreams);
            userProfile.put("district", location);
            userProfile.put("state", location);
            userProfile.put("classLevel", classLevel);
            userProfile.put("strengths", analysis.strengths);
            userProfile.put("weaknesses", analysis.weaknesses);
            userProfile.put("interests", "Academic excellence and career growth");
            
            System.out.println("🎯 Processing quiz results for: " + studentName);
            System.out.println("📊 Quiz Analysis: " + analysis.toString());
            System.out.println("👤 User Profile: " + userProfile);
            
            // Get AI recommendations from Gemini
            Map<String, Object> geminiResponse = geminiAIService.getCollegeRecommendations(userProfile);
            
            // If user credentials provided, save quiz attempt
            String authMessage = "Guest analysis - results not saved";
            if (phone != null && password != null) {
                try {
                    User user = userService.findByPhone(phone);
                    if (user != null && userService.verifyPassword(user, password)) {
                        // Save quiz attempt
                        QuizAttempt attempt = new QuizAttempt();
                        attempt.setUser(user);
                        attempt.setAnswersJson(String.join(",", quizAnswers));
                        attempt.setScore(analysis.score);
                        attempt.setRecommendedStreams("[\"" + analysis.primaryStream + "\"]");
                        quizAttemptRepository.save(attempt);
                        authMessage = "Quiz results saved for user: " + user.getName();
                    }
                } catch (Exception e) {
                    System.err.println("Could not save quiz attempt: " + e.getMessage());
                }
            }
            
            // Build comprehensive response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Quiz results analyzed and AI recommendations generated successfully");
            
            response.put("quizAnalysis", Map.of(
                    "totalQuestions", quizAnswers.size(),
                    "correctAnswers", analysis.score,
                    "percentage", analysis.percentage + "%",
                    "grade", analysis.grade,
                    "primaryStream", analysis.primaryStream,
                    "alternativeStreams", analysis.alternativeStreams,
                    "strengths", analysis.strengths,
                    "areasForImprovement", analysis.weaknesses
            ));
            
            response.put("studentProfile", Map.of(
                    "name", studentName,
                    "location", location,
                    "classLevel", classLevel,
                    "academicPerformance", analysis.grade,
                    "recommendedField", analysis.primaryStream
            ));
            
            response.put("aiRecommendations", geminiResponse);
            
            response.put("systemInfo", Map.of(
                    "authStatus", authMessage,
                    "analysisTimestamp", System.currentTimeMillis(),
                    "aiModel", "Google Gemini 1.5 Flash",
                    "recommendationType", "Personalized based on quiz performance"
            ));
            
            response.put("dataFlow", Map.of(
                    "step1", "Quiz answers analyzed: " + quizAnswers.size() + " questions",
                    "step2", "Academic strengths identified: " + analysis.strengths,
                    "step3", "Stream recommendations generated: " + analysis.primaryStream,
                    "step4", "User profile built with " + analysis.percentage + "% score",
                    "step5", "AI recommendations generated by Gemini",
                    "step6", "Personalized guidance provided"
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Quiz analysis failed: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Quiz analysis failed: " + e.getMessage(),
                    "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Public endpoint to test Gemini AI integration with sample quiz data
     */
    @PostMapping("/test-gemini")
    public ResponseEntity<Map<String, Object>> testGeminiIntegration(@RequestBody Map<String, Object> testData) {
        try {
            // Extract test data or use defaults
            String name = (String) testData.getOrDefault("name", "Suman H M");
            Integer quizScore = (Integer) testData.getOrDefault("quizScore", 6);
            String location = (String) testData.getOrDefault("location", "Bangalore Urban");
            
            @SuppressWarnings("unchecked")
            List<String> streams = (List<String>) testData.getOrDefault("recommendedStreams", 
                List.of("Science (Based on Mathematical strength)"));

            // Build user profile for Gemini API
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("name", name);
            userProfile.put("totalScore", quizScore);
            userProfile.put("recommendedStream", streams.get(0));
            userProfile.put("district", location);
            userProfile.put("state", location);
            userProfile.put("interests", "Academic excellence and career growth");

            System.out.println("🔍 Testing Gemini AI with profile: " + userProfile);

            // Call Gemini AI service
            Map<String, Object> geminiResponse = geminiAIService.getCollegeRecommendations(userProfile);

            // Return successful response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gemini AI integration test successful");
            response.put("inputData", Map.of(
                    "name", name,
                    "quizScore", quizScore,
                    "location", location,
                    "recommendedStreams", streams
            ));
            response.put("geminiResponse", geminiResponse);
            response.put("dataFlow", Map.of(
                    "step1", "Quiz data received: " + testData,
                    "step2", "User profile built: " + userProfile,
                    "step3", "Gemini API called successfully",
                    "step4", "AI recommendations generated successfully"
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Gemini API test failed: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Gemini AI test failed: " + e.getMessage(),
                    "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Simple health check for public endpoints
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Public test endpoints are working",
                "endpoints", Map.of(
                        "quizResults", "/api/public/quiz-results-recommendations (POST)",
                        "testGemini", "/api/public/test-gemini (POST)",
                        "health", "/api/public/health (GET)"
                )
        ));
    }
    
    /**
     * Analyze quiz results to determine scores, streams, and academic profile
     */
    private QuizAnalysis analyzeQuizResults(List<String> answers) {
        if (answers.isEmpty()) {
            // Default for demo
            answers = List.of("A", "A", "A", "B", "A", "C", "A", "D", "B", "A", "B", "A");
        }
        
        // Sample correct answers for analysis (this would come from quiz configuration)
        List<String> correctAnswers = List.of("A", "B", "A", "B", "A", "C", "D", "D", "B", "C", "B", "A");
        
        int score = 0;
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        
        // Calculate score and analyze patterns
        for (int i = 0; i < Math.min(answers.size(), correctAnswers.size()); i++) {
            if (answers.get(i).equals(correctAnswers.get(i))) {
                score++;
                // Determine subject areas based on question patterns
                if (i < 4) strengths.add("Mathematical reasoning");
                else if (i < 8) strengths.add("Scientific thinking");
                else strengths.add("Analytical skills");
            } else {
                if (i < 4) weaknesses.add("Mathematical concepts");
                else if (i < 8) weaknesses.add("Scientific understanding");
                else weaknesses.add("Logical reasoning");
            }
        }
        
        // Remove duplicates
        strengths = strengths.stream().distinct().collect(java.util.stream.Collectors.toList());
        weaknesses = weaknesses.stream().distinct().collect(java.util.stream.Collectors.toList());
        
        double percentage = (score * 100.0) / answers.size();
        
        // Determine grade and recommendations
        String grade;
        String primaryStream;
        List<String> alternativeStreams = new ArrayList<>();
        
        if (percentage >= 85) {
            grade = "Excellent";
            primaryStream = "Science with Engineering focus";
            alternativeStreams = List.of("Medicine", "Research", "Technology");
        } else if (percentage >= 70) {
            grade = "Very Good";
            primaryStream = "Science with multiple options";
            alternativeStreams = List.of("Engineering", "Pure Sciences", "Applied Sciences");
        } else if (percentage >= 60) {
            grade = "Good";
            primaryStream = determineStreamBasedOnStrengths(strengths);
            alternativeStreams = List.of("Science", "Commerce", "Applied Arts");
        } else if (percentage >= 40) {
            grade = "Average";
            primaryStream = "Commerce or Arts based on interest";
            alternativeStreams = List.of("Commerce", "Arts", "Vocational courses");
        } else {
            grade = "Needs Improvement";
            primaryStream = "Foundation strengthening recommended";
            alternativeStreams = List.of("Arts", "Vocational training", "Skill development");
        }
        
        return new QuizAnalysis(score, percentage, grade, primaryStream, alternativeStreams, strengths, weaknesses);
    }
    
    private String determineStreamBasedOnStrengths(List<String> strengths) {
        if (strengths.contains("Mathematical reasoning")) {
            return "Science (Mathematics focus)";
        } else if (strengths.contains("Scientific thinking")) {
            return "Science (Applied Sciences)";
        } else if (strengths.contains("Analytical skills")) {
            return "Commerce (Business Analytics)";
        } else {
            return "Arts (Creative and Critical thinking)";
        }
    }
    
    // Helper class for quiz analysis
    private static class QuizAnalysis {
        final int score;
        final double percentage;
        final String grade;
        final String primaryStream;
        final List<String> alternativeStreams;
        final List<String> strengths;
        final List<String> weaknesses;
        
        QuizAnalysis(int score, double percentage, String grade, String primaryStream, 
                    List<String> alternativeStreams, List<String> strengths, List<String> weaknesses) {
            this.score = score;
            this.percentage = percentage;
            this.grade = grade;
            this.primaryStream = primaryStream;
            this.alternativeStreams = alternativeStreams;
            this.strengths = strengths;
            this.weaknesses = weaknesses;
        }
        
        @Override
        public String toString() {
            return String.format("Score: %d (%.1f%%), Grade: %s, Stream: %s", 
                               score, percentage, grade, primaryStream);
        }
    }
}