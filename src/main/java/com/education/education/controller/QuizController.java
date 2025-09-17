package com.education.education.controller;

import com.education.education.config.JwtTokenUtil;
import com.education.education.dto.QuizResultDto;
import com.education.education.dto.QuizSubmissionDto;
import com.education.education.entity.Quiz;
import com.education.education.entity.User;
import com.education.education.service.QuizService;
import com.education.education.service.GeminiAIService;
import com.education.education.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private GeminiAIService geminiAIService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Get available quizzes for current user based on their class level
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableQuizzes(HttpServletRequest request) {
        try {
            // Allow both authenticated users and guests
            if (!isAuthenticated(request)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Authentication required"));
            }
            
            Long userId = getUserIdFromToken(request); // Can be null for guests
            
            // For now, default to "12th" - this should come from user profile
            String classLevel = "12th"; // TODO: Get from user profile
            List<Quiz> quizzes = quizService.getAvailableQuizzes(classLevel);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Available quizzes retrieved successfully",
                "data", quizzes
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving quizzes: " + e.getMessage()));
        }
    }
    
    /**
     * Get specific quiz by ID
     */
    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizById(@PathVariable Long quizId, HttpServletRequest request) {
        try {
            // Allow both authenticated users and guests
            if (!isAuthenticated(request)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Authentication required"));
            }
            
            Quiz quiz = quizService.getQuizById(quizId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Quiz retrieved successfully",
                "data", quiz
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving quiz: " + e.getMessage()));
        }
    }
    
    /**
     * Submit quiz and get stream recommendations
     * This is the core SIH feature for stream guidance
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@Valid @RequestBody QuizSubmissionDto submission, 
                                       HttpServletRequest request) {
        try {
            // Allow both authenticated users and guests to submit quizzes
            if (!isAuthenticated(request)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Authentication required"));
            }
            
            Long userId = getUserIdFromToken(request); // Can be null for guests
            
            // Submit quiz and get results with stream recommendations
            QuizResultDto result = quizService.submitQuiz(userId, submission);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Quiz submitted successfully. Stream recommendations generated!",
                "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error submitting quiz: " + e.getMessage()));
        }
    }

    /**
     * Enhanced quiz submission with AI recommendations
     */
    @PostMapping("/submit-with-ai")
    public ResponseEntity<Map<String, Object>> submitQuizWithAI(
            @RequestBody QuizSubmissionDto submission, 
            HttpServletRequest request) {
        try {
            // Get user ID from token if available (can be null for guests)
            Long userId = getUserIdFromToken(request);
            
            // Step 1: Submit quiz and get basic results
            System.out.println("Submitting quiz for userId: " + userId + ", quizId: " + submission.getQuizId());
            QuizResultDto result = quizService.submitQuiz(userId, submission);
            System.out.println("Quiz submission successful, result: " + result);
            
            // Step 2: Get user information for personalized recommendations
            String userInfo = "Student Profile: ";
            if (userId != null) {
                try {
                    User user = userService.findById(userId);
                    if (user != null) {
                        userInfo += String.format("Name: %s, Phone: %s, District: %s, Class: %s", 
                            user.getName() != null ? user.getName() : "Not provided",
                            user.getPhone(),
                            user.getDistrict() != null ? user.getDistrict() : "Not provided",
                            user.getClassLevel() != null ? user.getClassLevel() : "12th");
                    }
                } catch (Exception e) {
                    System.out.println("Could not fetch user info: " + e.getMessage());
                    userInfo += "Guest user taking quiz";
                }
            } else {
                userInfo += "Guest user taking quiz";
            }
            
            // Step 3: Prepare quiz results data for Gemini AI
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("quizTitle", result.getQuizTitle());
            userProfile.put("score", result.getTotalScore());
            userProfile.put("maxScore", result.getMaxScore());
            userProfile.put("percentage", result.getPercentage());
            userProfile.put("performanceLevel", result.getPerformanceLevel());
            userProfile.put("recommendedStreams", result.getRecommendedStreams());
            userProfile.put("scoreBreakdown", result.getScoreBreakdown());
            userProfile.put("userInfo", userInfo);
            userProfile.put("classLevel", "12th");
            userProfile.put("district", "Unknown");
            
            // Step 4: Get AI recommendations from Gemini
            System.out.println("Calling Gemini AI service...");
            Map<String, Object> aiRecommendations = geminiAIService.getCollegeRecommendations(userProfile);
            System.out.println("AI recommendations received");
            
            // Step 5: Combine quiz results with AI recommendations
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quiz submitted successfully with AI recommendations");
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            response.put("quizResult", result);
            response.put("aiRecommendations", aiRecommendations);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error submitting quiz with AI: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }    /**
     * Get user's quiz history and past recommendations
     */
    @GetMapping("/history")
    public ResponseEntity<?> getQuizHistory(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Authentication required"));
            }
            
            List<QuizResultDto> history = quizService.getUserQuizHistory(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Quiz history retrieved successfully",
                "data", history
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving quiz history: " + e.getMessage()));
        }
    }
    
    /**
     * Get stream recommendations summary for user
     * Shows consolidated recommendations across all quizzes
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> getStreamRecommendations(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Authentication required"));
            }
            
            List<QuizResultDto> history = quizService.getUserQuizHistory(userId);
            
            if (history.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "No quiz attempts found. Please take a quiz first.",
                    "data", Map.of(
                        "hasAttempts", false,
                        "recommendedAction", "Take an aptitude quiz to get stream recommendations"
                    )
                ));
            }
            
            // Get latest quiz result for recommendations
            QuizResultDto latestResult = history.get(0);
            
            Map<String, Object> recommendations = new HashMap<>();
            recommendations.put("hasAttempts", true);
            recommendations.put("latestQuiz", latestResult.getQuizTitle());
            recommendations.put("score", latestResult.getTotalScore());
            recommendations.put("percentage", latestResult.getPercentage());
            recommendations.put("performanceLevel", latestResult.getPerformanceLevel());
            recommendations.put("collegeTier", latestResult.getCollegeTier());
            recommendations.put("recommendedStreams", latestResult.getRecommendedStreams());
            recommendations.put("recommendedColleges", latestResult.getRecommendedColleges());
            recommendations.put("scoreBreakdown", latestResult.getScoreBreakdown());
            recommendations.put("completedAt", latestResult.getCompletedAt());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Stream recommendations retrieved successfully",
                "data", recommendations
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving recommendations: " + e.getMessage()));
        }
    }
    
    /**
     * Get quiz statistics for analytics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getQuizStats(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Authentication required"));
            }
            
            List<QuizResultDto> history = quizService.getUserQuizHistory(userId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalQuizzes", history.size());
            
            if (!history.isEmpty()) {
                // Calculate average score
                double avgScore = history.stream()
                    .mapToDouble(QuizResultDto::getPercentage)
                    .average()
                    .orElse(0.0);
                
                // Find best performance
                QuizResultDto bestResult = history.stream()
                    .max((a, b) -> Double.compare(a.getPercentage(), b.getPercentage()))
                    .orElse(null);
                
                stats.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
                stats.put("bestScore", bestResult != null ? bestResult.getPercentage() : 0);
                stats.put("latestScore", history.get(0).getPercentage());
                stats.put("improvement", history.size() > 1 ? 
                    history.get(0).getPercentage() - history.get(history.size()-1).getPercentage() : 0);
            } else {
                stats.put("averageScore", 0);
                stats.put("bestScore", 0);
                stats.put("latestScore", 0);
                stats.put("improvement", 0);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Quiz statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving stats: " + e.getMessage()));
        }
    }
    
    /**
     * Get quiz categories and their explanations
     * Public endpoint - no authentication required
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getQuizCategories() {
        Map<String, Object> categories = new HashMap<>();
        
        Map<String, String> aptitudeTypes = new HashMap<>();
        aptitudeTypes.put("Mathematical", "Problem-solving, arithmetic, numerical reasoning");
        aptitudeTypes.put("Verbal", "Reading comprehension, vocabulary, language skills");
        aptitudeTypes.put("Analytical", "Pattern recognition, logical reasoning, critical thinking");
        aptitudeTypes.put("Technical", "Science concepts, technical aptitude, applied knowledge");
        
        Map<String, String> streamInfo = new HashMap<>();
        streamInfo.put("Science", "Mathematics, Physics, Chemistry, Biology - leads to Engineering, Medical, Research");
        streamInfo.put("Commerce", "Mathematics, Economics, Accounting, Business Studies - leads to CA, MBA, Finance");
        streamInfo.put("Arts", "Languages, Social Sciences, History, Psychology - leads to Literature, Law, Social Work");
        
        categories.put("aptitudeTypes", aptitudeTypes);
        categories.put("streamInformation", streamInfo);
        categories.put("scoringInfo", Map.of(
            "Science", "40% Mathematical + 40% Technical + 20% Analytical",
            "Commerce", "30% Mathematical + 40% Analytical + 30% Verbal", 
            "Arts", "60% Verbal + 40% Analytical"
        ));
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Quiz categories and information retrieved successfully",
            "data", categories
        ));
    }
    
    /**
     * Extract user ID from JWT token
     * Returns null for guest users (they can still take quizzes)
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Check if it's a guest token
                if (jwtTokenUtil.isGuestToken(token)) {
                    return null; // Guest users can take quizzes, results stored with guest session
                }
                
                // Get user ID from token
                return jwtTokenUtil.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            // Log error but don't expose details
        }
        return null;
    }
    
    /**
     * Check if user is authenticated (including guest)
     */
    private boolean isAuthenticated(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtTokenUtil.isGuestToken(token) || jwtTokenUtil.getUserIdFromToken(token) != null;
            }
        } catch (Exception e) {
            // Log error but don't expose details
        }
        return false;
    }
}