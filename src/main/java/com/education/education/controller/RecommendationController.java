package com.education.education.controller;

import com.education.education.entity.QuizAttempt;
import com.education.education.entity.User;
import com.education.education.repository.QuizAttemptRepository;
import com.education.education.service.GeminiAIService;
import com.education.education.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private GeminiAIService geminiAIService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    /**
     * Get AI-powered college recommendations based on latest quiz attempt
     */
    @GetMapping("/colleges")
    public ResponseEntity<Map<String, Object>> getCollegeRecommendations(Authentication authentication) {
        try {
            // Get authenticated user
            String phone = authentication.getName();
            User user = userService.findByPhone(phone);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User not found"));
            }

            // Get user's latest quiz attempt
            List<QuizAttempt> attempts = quizAttemptRepository
                    .findByUserUserIdOrderByTimestampDesc(user.getUserId());

            if (attempts.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "No quiz attempts found. Please take a quiz first."));
            }

            QuizAttempt attempt = attempts.get(0); // Get the latest attempt

            // Build user profile for Gemini AI
            Map<String, Object> userProfile = buildUserProfile(user, attempt);

            // Get recommendations from Gemini AI
            Map<String, Object> recommendations = geminiAIService.getCollegeRecommendations(userProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", recommendations,
                    "userProfile", Map.of(
                            "name", user.getName(),
                            "location", user.getDistrict(),
                            "quizScore", attempt.getScore(),
                            "recommendedStreams", attempt.getRecommendedStreams())));

        } catch (Exception e) {
            System.err.println("Error getting college recommendations: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to get recommendations: " + e.getMessage()));
        }
    }

    /**
     * Get AI-powered career guidance
     */
    @GetMapping("/career-guidance")
    public ResponseEntity<Map<String, Object>> getCareerGuidance(Authentication authentication) {
        try {
            String phone = authentication.getName();
            User user = userService.findByPhone(phone);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User not found"));
            }

            // Get user's latest quiz attempt
            List<QuizAttempt> attempts = quizAttemptRepository
                    .findByUserUserIdOrderByTimestampDesc(user.getUserId());

            if (attempts.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "No quiz attempts found. Please take a quiz first."));
            }

            QuizAttempt attempt = attempts.get(0);

            // Build user profile for career guidance
            Map<String, Object> userProfile = buildUserProfile(user, attempt);

            // Get career guidance from Gemini AI
            Map<String, Object> guidance = geminiAIService.getCareerGuidance(userProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", guidance,
                    "userProfile", Map.of(
                            "name", user.getName(),
                            "location", user.getDistrict(),
                            "quizScore", attempt.getScore())));

        } catch (Exception e) {
            System.err.println("Error getting career guidance: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to get career guidance: " + e.getMessage()));
        }
    }

    /**
     * Get personalized recommendations with custom preferences
     */
    @PostMapping("/personalized")
    public ResponseEntity<Map<String, Object>> getPersonalizedRecommendations(
            @RequestBody Map<String, Object> preferences,
            Authentication authentication) {
        try {
            String phone = authentication.getName();
            User user = userService.findByPhone(phone);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User not found"));
            }

            // Get user's latest quiz attempt
            List<QuizAttempt> attempts = quizAttemptRepository
                    .findByUserUserIdOrderByTimestampDesc(user.getUserId());

            if (attempts.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "No quiz attempts found. Please take a quiz first."));
            }

            QuizAttempt attempt = attempts.get(0);

            // Build enhanced user profile with preferences
            Map<String, Object> userProfile = buildUserProfile(user, attempt);

            // Add user preferences
            if (preferences.containsKey("preferredCollegeType")) {
                userProfile.put("preferredCollegeType", preferences.get("preferredCollegeType"));
            }
            if (preferences.containsKey("budgetRange")) {
                userProfile.put("budgetRange", preferences.get("budgetRange"));
            }
            if (preferences.containsKey("interests")) {
                userProfile.put("interests", preferences.get("interests"));
            }
            if (preferences.containsKey("careerGoals")) {
                userProfile.put("careerGoals", preferences.get("careerGoals"));
            }

            // Get enhanced recommendations
            Map<String, Object> recommendations = geminiAIService.getCollegeRecommendations(userProfile);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", recommendations,
                    "preferences", preferences));

        } catch (Exception e) {
            System.err.println("Error getting personalized recommendations: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to get personalized recommendations: " + e.getMessage()));
        }
    }

    /**
     * Check API status and configuration
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Test basic functionality (without making actual API call)
            status.put("geminiService", "available");
            status.put("configuration", "loaded");
            status.put("endpoints", Map.of(
                    "colleges", "/api/recommendations/colleges",
                    "careerGuidance", "/api/recommendations/career-guidance",
                    "personalized", "/api/recommendations/personalized"));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", status,
                    "message", "Recommendation service is ready"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Service unavailable: " + e.getMessage()));
        }
    }

    private Map<String, Object> buildUserProfile(User user, QuizAttempt attempt) {
        Map<String, Object> profile = new HashMap<>();

        profile.put("totalScore", attempt.getScore());
        profile.put("recommendedStream", extractPrimaryStream(attempt.getRecommendedStreams()));
        profile.put("district", user.getDistrict());
        profile.put("state", user.getDistrict()); // Using district as state since User doesn't have getState()
        profile.put("name", user.getName());
        profile.put("quizTimestamp", attempt.getTimestamp());

        // Default interests based on quiz performance
        profile.put("interests", "Academic excellence and career growth");

        return profile;
    }

    private String extractPrimaryStream(String recommendedStreams) {
        if (recommendedStreams == null || recommendedStreams.isEmpty()) {
            return "Science"; // Default fallback
        }

        try {
            // Parse JSON and extract first stream
            if (recommendedStreams.contains("Science")) {
                return "Science";
            } else if (recommendedStreams.contains("Commerce")) {
                return "Commerce";
            } else if (recommendedStreams.contains("Arts")) {
                return "Arts";
            }
        } catch (Exception e) {
            System.err.println("Error parsing recommended streams: " + e.getMessage());
        }

        return "Science"; // Default fallback
    }
}