package com.education.education.service;

import com.education.education.dto.QuizResultDto;
import com.education.education.dto.QuizSubmissionDto;
import com.education.education.entity.Quiz;
import com.education.education.entity.QuizAttempt;
import com.education.education.entity.User;
import com.education.education.repository.QuizAttemptRepository;
import com.education.education.repository.QuizRepository;
import com.education.education.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get available quizzes for a student based on their class level
     */
    public List<Quiz> getAvailableQuizzes(String classLevel) {
        return quizRepository.findQuizzesForClass(classLevel);
    }

    /**
     * Get quiz by ID
     */
    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));
    }

    /**
     * Submit quiz and calculate results with stream recommendation
     */
    public QuizResultDto submitQuiz(Long userId, QuizSubmissionDto submission) {
        try {
            Quiz quiz = getQuizById(submission.getQuizId());

            // Parse quiz questions from JSON
            JsonNode questionsNode = objectMapper.readTree(quiz.getQuestionsJson());

            // Calculate scores
            ScoreCalculationResult scoreResult = calculateScores(submission, questionsNode);

            // Generate stream recommendations
            List<String> recommendedStreams = generateStreamRecommendations(scoreResult);

            // If user is authenticated, save to database and get user location for college
            // recommendations
            if (userId != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Generate college recommendations based on score and user location
                List<String> recommendedColleges = generateCollegeRecommendations(scoreResult.totalScore,
                        user.getDistrict());

                // Save quiz attempt
                String answersJson = objectMapper.writeValueAsString(submission.getAnswers());
                String streamsJson = objectMapper.writeValueAsString(recommendedStreams);

                QuizAttempt attempt = new QuizAttempt(user, quiz, answersJson, scoreResult.totalScore, streamsJson);
                attempt = quizAttemptRepository.save(attempt);

                // Create result DTO
                QuizResultDto result = createQuizResultDto(attempt, quiz, scoreResult, recommendedStreams);
                result.setRecommendedColleges(recommendedColleges);
                return result;
            } else {
                // For guest users, return results without saving to database
                List<String> recommendedColleges = generateCollegeRecommendations(scoreResult.totalScore, "Unknown");

                // Create result DTO for guest
                QuizResultDto result = new QuizResultDto(
                        null, // No attempt ID for guests
                        quiz.getQuizId(),
                        quiz.getTitle(),
                        scoreResult.totalScore,
                        scoreResult.maxScore,
                        java.time.LocalDateTime.now());

                // Set detailed breakdown
                QuizResultDto.ScoreBreakdown breakdown = new QuizResultDto.ScoreBreakdown(
                        scoreResult.mathematicalScore,
                        scoreResult.verbalScore,
                        scoreResult.analyticalScore,
                        scoreResult.technicalScore);
                result.setScoreBreakdown(breakdown);
                result.setRecommendedStreams(recommendedStreams);
                result.setRecommendedColleges(recommendedColleges);

                return result;
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing quiz data: " + e.getMessage());
        }
    }

    /**
     * Helper method to create QuizResultDto from QuizAttempt
     */
    private QuizResultDto createQuizResultDto(QuizAttempt attempt, Quiz quiz, ScoreCalculationResult scoreResult,
            List<String> recommendedStreams) {
        QuizResultDto result = new QuizResultDto(
                attempt.getAttemptId(),
                quiz.getQuizId(),
                quiz.getTitle(),
                scoreResult.totalScore,
                scoreResult.maxScore,
                attempt.getTimestamp());

        // Set detailed breakdown
        QuizResultDto.ScoreBreakdown breakdown = new QuizResultDto.ScoreBreakdown(
                scoreResult.mathematicalScore,
                scoreResult.verbalScore,
                scoreResult.analyticalScore,
                scoreResult.technicalScore);
        result.setScoreBreakdown(breakdown);
        result.setRecommendedStreams(recommendedStreams);

        return result;
    }

    /**
     * Calculate scores based on quiz answers
     */
    private ScoreCalculationResult calculateScores(QuizSubmissionDto submission, JsonNode questionsNode) {
        int totalScore = 0;
        int maxScore = 0;
        int mathematicalScore = 0, verbalScore = 0, analyticalScore = 0, technicalScore = 0;
        int mathMax = 0, verbalMax = 0, analyticalMax = 0, technicalMax = 0;

        // Create answer map for quick lookup
        Map<Integer, String> answerMap = new HashMap<>();
        for (QuizSubmissionDto.QuizAnswerDto answer : submission.getAnswers()) {
            answerMap.put(answer.getQuestionId(), answer.getSelectedOption());
        }

        // Process each question
        if (questionsNode.isArray()) {
            for (JsonNode questionNode : questionsNode) {
                int questionId = questionNode.get("id").asInt();
                String correctAnswer = questionNode.get("correctAnswer").asText();
                String category = questionNode.get("category").asText();
                int points = questionNode.has("points") ? questionNode.get("points").asInt() : 1;

                maxScore += points;

                // Categorize max scores
                switch (category.toLowerCase()) {
                    case "mathematical":
                    case "mathematics":
                    case "numerical":
                        mathMax += points;
                        break;
                    case "verbal":
                    case "language":
                    case "english":
                        verbalMax += points;
                        break;
                    case "analytical":
                    case "logical":
                    case "reasoning":
                        analyticalMax += points;
                        break;
                    case "technical":
                    case "science":
                    case "physics":
                    case "chemistry":
                        technicalMax += points;
                        break;
                }

                // Check if answer is correct
                String userAnswer = answerMap.get(questionId);
                if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                    totalScore += points;

                    // Add to category scores
                    switch (category.toLowerCase()) {
                        case "mathematical":
                        case "mathematics":
                        case "numerical":
                            mathematicalScore += points;
                            break;
                        case "verbal":
                        case "language":
                        case "english":
                            verbalScore += points;
                            break;
                        case "analytical":
                        case "logical":
                        case "reasoning":
                            analyticalScore += points;
                            break;
                        case "technical":
                        case "science":
                        case "physics":
                        case "chemistry":
                            technicalScore += points;
                            break;
                    }
                }
            }
        }

        return new ScoreCalculationResult(totalScore, maxScore, mathematicalScore, verbalScore,
                analyticalScore, technicalScore, mathMax, verbalMax,
                analyticalMax, technicalMax);
    }

    /**
     * Generate stream recommendations based on SIH requirements
     * Focus: Arts, Science, Commerce streams
     */
    private List<String> generateStreamRecommendations(ScoreCalculationResult scores) {
        List<StreamScore> streamScores = new ArrayList<>();

        // Calculate stream scores based on aptitude
        double scienceScore = calculateScienceAptitude(scores);
        double commerceScore = calculateCommerceAptitude(scores);
        double artsScore = calculateArtsAptitude(scores);

        streamScores.add(new StreamScore("Science", scienceScore));
        streamScores.add(new StreamScore("Commerce", commerceScore));
        streamScores.add(new StreamScore("Arts", artsScore));

        // Sort by score (highest first)
        streamScores.sort((a, b) -> Double.compare(b.score, a.score));

        List<String> recommendations = new ArrayList<>();

        // Add recommendations with detailed explanations
        for (int i = 0; i < Math.min(2, streamScores.size()); i++) {
            StreamScore stream = streamScores.get(i);
            if (stream.score > 0.4) { // Minimum threshold
                recommendations.add(stream.stream + " (" + Math.round(stream.score * 100) + "% match)");
            }
        }

        // If no strong match, recommend based on highest single aptitude
        if (recommendations.isEmpty()) {
            String strongestAptitude = findStrongestAptitude(scores);
            recommendations.add(getDefaultStreamForAptitude(strongestAptitude));
        }

        return recommendations;
    }

    /**
     * Calculate Science stream aptitude (Math + Technical)
     */
    private double calculateScienceAptitude(ScoreCalculationResult scores) {
        double mathPercent = scores.mathMax > 0 ? (double) scores.mathematicalScore / scores.mathMax : 0;
        double techPercent = scores.technicalMax > 0 ? (double) scores.technicalScore / scores.technicalMax : 0;
        double analyticalPercent = scores.analyticalMax > 0 ? (double) scores.analyticalScore / scores.analyticalMax
                : 0;

        // Science = 40% Math + 40% Technical + 20% Analytical
        return (mathPercent * 0.4) + (techPercent * 0.4) + (analyticalPercent * 0.2);
    }

    /**
     * Calculate Commerce stream aptitude (Math + Analytical + Verbal)
     */
    private double calculateCommerceAptitude(ScoreCalculationResult scores) {
        double mathPercent = scores.mathMax > 0 ? (double) scores.mathematicalScore / scores.mathMax : 0;
        double analyticalPercent = scores.analyticalMax > 0 ? (double) scores.analyticalScore / scores.analyticalMax
                : 0;
        double verbalPercent = scores.verbalMax > 0 ? (double) scores.verbalScore / scores.verbalMax : 0;

        // Commerce = 30% Math + 40% Analytical + 30% Verbal
        return (mathPercent * 0.3) + (analyticalPercent * 0.4) + (verbalPercent * 0.3);
    }

    /**
     * Calculate Arts stream aptitude (Verbal + Analytical)
     */
    private double calculateArtsAptitude(ScoreCalculationResult scores) {
        double verbalPercent = scores.verbalMax > 0 ? (double) scores.verbalScore / scores.verbalMax : 0;
        double analyticalPercent = scores.analyticalMax > 0 ? (double) scores.analyticalScore / scores.analyticalMax
                : 0;

        // Arts = 60% Verbal + 40% Analytical
        return (verbalPercent * 0.6) + (analyticalPercent * 0.4);
    }

    /**
     * Find strongest aptitude area
     */
    private String findStrongestAptitude(ScoreCalculationResult scores) {
        double mathPercent = scores.mathMax > 0 ? (double) scores.mathematicalScore / scores.mathMax : 0;
        double verbalPercent = scores.verbalMax > 0 ? (double) scores.verbalScore / scores.verbalMax : 0;
        double analyticalPercent = scores.analyticalMax > 0 ? (double) scores.analyticalScore / scores.analyticalMax
                : 0;
        double technicalPercent = scores.technicalMax > 0 ? (double) scores.technicalScore / scores.technicalMax : 0;

        double max = Math.max(Math.max(mathPercent, verbalPercent),
                Math.max(analyticalPercent, technicalPercent));

        if (max == mathPercent)
            return "Mathematical";
        if (max == verbalPercent)
            return "Verbal";
        if (max == analyticalPercent)
            return "Analytical";
        return "Technical";
    }

    /**
     * Get default stream recommendation for aptitude
     */
    private String getDefaultStreamForAptitude(String aptitude) {
        switch (aptitude) {
            case "Mathematical":
            case "Technical":
                return "Science (Based on " + aptitude + " strength)";
            case "Analytical":
                return "Commerce (Based on " + aptitude + " strength)";
            case "Verbal":
                return "Arts (Based on " + aptitude + " strength)";
            default:
                return "Arts (General recommendation)";
        }
    }

    /**
     * Generate college recommendations based on score and location
     */
    private List<String> generateCollegeRecommendations(int score, String district) {
        List<String> recommendations = new ArrayList<>();
        double percentage = score; // This should be calculated as percentage

        // Add district-specific colleges based on score
        if (percentage >= 85) {
            recommendations.add("Government Science College, " + (district != null ? district : "Bangalore"));
            recommendations.add("University College, " + (district != null ? district : "Bangalore"));
        } else if (percentage >= 70) {
            recommendations.add("District Government College, " + (district != null ? district : "Bangalore"));
            recommendations.add("Regional Engineering College, " + (district != null ? district : "Bangalore"));
        } else if (percentage >= 55) {
            recommendations.add("Government Degree College, " + (district != null ? district : "Bangalore"));
            recommendations.add("Government Arts & Science College, " + (district != null ? district : "Bangalore"));
        } else {
            recommendations.add("Government First Grade College, " + (district != null ? district : "Bangalore"));
            recommendations.add("Government Diploma Institute, " + (district != null ? district : "Bangalore"));
        }

        return recommendations;
    }

    /**
     * Get user's quiz history
     */
    public List<QuizResultDto> getUserQuizHistory(Long userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserUserIdOrderByTimestampDesc(userId);
        List<QuizResultDto> results = new ArrayList<>();

        for (QuizAttempt attempt : attempts) {
            QuizResultDto result = new QuizResultDto(
                    attempt.getAttemptId(),
                    attempt.getQuiz().getQuizId(),
                    attempt.getQuiz().getTitle(),
                    attempt.getScore(),
                    100, // Default max score - should be calculated
                    attempt.getTimestamp());
            results.add(result);
        }

        return results;
    }

    // Helper classes
    private static class ScoreCalculationResult {
        int totalScore, maxScore;
        int mathematicalScore, verbalScore, analyticalScore, technicalScore;
        int mathMax, verbalMax, analyticalMax, technicalMax;

        ScoreCalculationResult(int totalScore, int maxScore, int mathematicalScore,
                int verbalScore, int analyticalScore, int technicalScore,
                int mathMax, int verbalMax, int analyticalMax, int technicalMax) {
            this.totalScore = totalScore;
            this.maxScore = maxScore;
            this.mathematicalScore = mathematicalScore;
            this.verbalScore = verbalScore;
            this.analyticalScore = analyticalScore;
            this.technicalScore = technicalScore;
            this.mathMax = mathMax;
            this.verbalMax = verbalMax;
            this.analyticalMax = analyticalMax;
            this.technicalMax = technicalMax;
        }
    }

    private static class StreamScore {
        String stream;
        double score;

        StreamScore(String stream, double score) {
            this.stream = stream;
            this.score = score;
        }
    }
}