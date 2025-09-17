package com.education.education.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public class QuizResultDto {
    
    private Long attemptId;
    private Long quizId;
    private String quizTitle;
    private Integer totalScore;
    private Integer maxScore;
    private Double percentage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;
    
    // Detailed scoring breakdown
    private ScoreBreakdown scoreBreakdown;
    
    // Recommendations based on score
    private List<String> recommendedStreams;
    private List<String> recommendedColleges;
    private String collegeTier;
    private String performanceLevel;
    
    public QuizResultDto() {}
    
    public QuizResultDto(Long attemptId, Long quizId, String quizTitle, Integer totalScore, 
                        Integer maxScore, LocalDateTime completedAt) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.totalScore = totalScore;
        this.maxScore = maxScore;
        this.percentage = maxScore > 0 ? (totalScore * 100.0 / maxScore) : 0.0;
        this.completedAt = completedAt;
        this.performanceLevel = calculatePerformanceLevel(this.percentage);
        this.collegeTier = calculateCollegeTier(this.percentage);
    }
    
    private String calculatePerformanceLevel(Double percentage) {
        if (percentage >= 85) return "Excellent";
        if (percentage >= 70) return "Very Good"; 
        if (percentage >= 55) return "Good";
        if (percentage >= 40) return "Average";
        return "Needs Improvement";
    }
    
    private String calculateCollegeTier(Double percentage) {
        if (percentage >= 85) return "Premier";
        if (percentage >= 70) return "Tier-1";
        if (percentage >= 55) return "Tier-2"; 
        if (percentage >= 40) return "Tier-3";
        return "Foundation";
    }
    
    // Getters and Setters
    public Long getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }
    
    public Long getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    
    public String getQuizTitle() {
        return quizTitle;
    }
    
    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }
    
    public Integer getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
        if (maxScore != null && maxScore > 0) {
            this.percentage = totalScore * 100.0 / maxScore;
            this.performanceLevel = calculatePerformanceLevel(this.percentage);
            this.collegeTier = calculateCollegeTier(this.percentage);
        }
    }
    
    public Integer getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
        if (totalScore != null && maxScore > 0) {
            this.percentage = totalScore * 100.0 / maxScore;
            this.performanceLevel = calculatePerformanceLevel(this.percentage);
            this.collegeTier = calculateCollegeTier(this.percentage);
        }
    }
    
    public Double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public ScoreBreakdown getScoreBreakdown() {
        return scoreBreakdown;
    }
    
    public void setScoreBreakdown(ScoreBreakdown scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }
    
    public List<String> getRecommendedStreams() {
        return recommendedStreams;
    }
    
    public void setRecommendedStreams(List<String> recommendedStreams) {
        this.recommendedStreams = recommendedStreams;
    }
    
    public List<String> getRecommendedColleges() {
        return recommendedColleges;
    }
    
    public void setRecommendedColleges(List<String> recommendedColleges) {
        this.recommendedColleges = recommendedColleges;
    }
    
    public String getCollegeTier() {
        return collegeTier;
    }
    
    public void setCollegeTier(String collegeTier) {
        this.collegeTier = collegeTier;
    }
    
    public String getPerformanceLevel() {
        return performanceLevel;
    }
    
    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
    }
    
    public static class ScoreBreakdown {
        private Integer mathematicalScore;
        private Integer verbalScore;
        private Integer analyticalScore;
        private Integer technicalScore;
        private String dominantAptitude;
        
        public ScoreBreakdown() {}
        
        public ScoreBreakdown(Integer mathematicalScore, Integer verbalScore, 
                             Integer analyticalScore, Integer technicalScore) {
            this.mathematicalScore = mathematicalScore;
            this.verbalScore = verbalScore;
            this.analyticalScore = analyticalScore;
            this.technicalScore = technicalScore;
            this.dominantAptitude = calculateDominantAptitude();
        }
        
        private String calculateDominantAptitude() {
            int max = Math.max(Math.max(mathematicalScore, verbalScore), 
                              Math.max(analyticalScore, technicalScore));
            
            if (max == mathematicalScore) return "Mathematical";
            if (max == verbalScore) return "Verbal";
            if (max == analyticalScore) return "Analytical";
            return "Technical";
        }
        
        // Getters and Setters
        public Integer getMathematicalScore() {
            return mathematicalScore;
        }
        
        public void setMathematicalScore(Integer mathematicalScore) {
            this.mathematicalScore = mathematicalScore;
        }
        
        public Integer getVerbalScore() {
            return verbalScore;
        }
        
        public void setVerbalScore(Integer verbalScore) {
            this.verbalScore = verbalScore;
        }
        
        public Integer getAnalyticalScore() {
            return analyticalScore;
        }
        
        public void setAnalyticalScore(Integer analyticalScore) {
            this.analyticalScore = analyticalScore;
        }
        
        public Integer getTechnicalScore() {
            return technicalScore;
        }
        
        public void setTechnicalScore(Integer technicalScore) {
            this.technicalScore = technicalScore;
        }
        
        public String getDominantAptitude() {
            return dominantAptitude;
        }
        
        public void setDominantAptitude(String dominantAptitude) {
            this.dominantAptitude = dominantAptitude;
        }
    }
}