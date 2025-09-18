package com.education.education.service;

import com.education.education.dto.college.CollegeSearchDto;
import com.education.education.dto.college.CollegeDetailsDto;
import com.education.education.entity.College;
import com.education.education.entity.QuizAttempt;
import com.education.education.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private CollegeService collegeService;

    public Map<String, Object> getPersonalizedRecommendations(QuizAttempt quizAttempt,
            String userDistrict,
            String userState) {
        Map<String, Object> recommendations = new HashMap<>();

        // Get quiz-based stream recommendation from stored JSON
        String recommendedStream = determineRecommendedStream(quizAttempt);
        recommendations.put("recommendedStream", recommendedStream);

        // Get colleges based on stream and location
        List<CollegeSearchDto> recommendedColleges = getCollegeRecommendations(
                recommendedStream, userDistrict, userState, quizAttempt.getScore());
        recommendations.put("recommendedColleges", recommendedColleges);

        // Get career paths for the recommended stream
        List<Map<String, Object>> careerPaths = getCareerPaths(recommendedStream);
        recommendations.put("careerPaths", careerPaths);

        // Get study tips based on quiz performance
        Map<String, Object> studyGuidance = getStudyGuidance(quizAttempt);
        recommendations.put("studyGuidance", studyGuidance);

        return recommendations;
    }

    private String determineRecommendedStream(QuizAttempt quizAttempt) {
        // Parse the recommended streams JSON from quiz attempt
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode streamsNode = mapper.readTree(quizAttempt.getRecommendedStreams());

            if (streamsNode.isArray() && streamsNode.size() > 0) {
                return streamsNode.get(0).asText(); // Return the top recommended stream
            }
        } catch (Exception e) {
            // If parsing fails, fallback to default logic based on score
            System.err.println("Error parsing recommended streams: " + e.getMessage());
        }

        // Fallback: Determine stream based on total score distribution
        // This is a simplified approach - in reality, we'd need access to individual
        // scores
        int totalScore = quizAttempt.getScore();
        if (totalScore >= 70) {
            return "Science"; // Assume high scorers might prefer Science
        } else if (totalScore >= 50) {
            return "Commerce"; // Mid-range scorers might prefer Commerce
        } else {
            return "Arts"; // Lower scorers might prefer Arts
        }
    }

    private List<CollegeSearchDto> getCollegeRecommendations(String stream, String district,
            String state, Integer totalScore) {
        List<College> allColleges = new ArrayList<>();

        // First, try to find colleges in user's district
        List<College> localColleges = collegeRepository.findByDistrictAndStreamOffered(district, stream);
        allColleges.addAll(localColleges);

        // If not enough local colleges, expand to state level
        if (allColleges.size() < 5) {
            List<College> stateColleges = collegeRepository.findByStateAndStreamOffered(state, stream);
            allColleges.addAll(stateColleges.stream()
                    .filter(college -> !allColleges.contains(college))
                    .collect(Collectors.toList()));
        }

        // Sort colleges based on quiz score and college tier
        List<College> sortedColleges = sortCollegesByRelevance(allColleges, totalScore);

        // Limit to top 10 recommendations
        return sortedColleges.stream()
                .limit(10)
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }

    private List<College> sortCollegesByRelevance(List<College> colleges, Integer totalScore) {
        // Define score thresholds for college tiers
        Map<String, Integer> tierThresholds = Map.of(
                "Premier", 80,
                "Excellent", 60,
                "Good", 40,
                "Average", 0);

        return colleges.stream()
                .sorted((c1, c2) -> {
                    // Get tier priority based on user's score
                    int c1Priority = getTierPriority(c1.getCollegeTier(), totalScore, tierThresholds);
                    int c2Priority = getTierPriority(c2.getCollegeTier(), totalScore, tierThresholds);

                    if (c1Priority != c2Priority) {
                        return Integer.compare(c2Priority, c1Priority); // Higher priority first
                    }

                    // If same priority, sort by placement rate
                    return Double.compare(c2.getPlacementRate(), c1.getPlacementRate());
                })
                .collect(Collectors.toList());
    }

    private int getTierPriority(String collegeTier, Integer userScore, Map<String, Integer> thresholds) {
        if (userScore >= thresholds.get("Premier") && "Premier".equals(collegeTier)) {
            return 4;
        } else if (userScore >= thresholds.get("Excellent")
                && ("Premier".equals(collegeTier) || "Excellent".equals(collegeTier))) {
            return 3;
        } else if (userScore >= thresholds.get("Good") && !"Average".equals(collegeTier)) {
            return 2;
        } else {
            return 1;
        }
    }

    private List<Map<String, Object>> getCareerPaths(String stream) {
        List<Map<String, Object>> careers = new ArrayList<>();

        switch (stream) {
            case "Science":
                careers.add(createCareerPath("Engineering",
                        "Design and build technology solutions",
                        List.of("Software Engineer", "Mechanical Engineer", "Civil Engineer"),
                        "₹5,00,000 - ₹25,00,000"));

                careers.add(createCareerPath("Medical",
                        "Healthcare and medical research",
                        List.of("Doctor", "Pharmacist", "Medical Researcher"),
                        "₹6,00,000 - ₹30,00,000"));

                careers.add(createCareerPath("Research",
                        "Scientific research and development",
                        List.of("Research Scientist", "Lab Technician", "Data Scientist"),
                        "₹4,00,000 - ₹20,00,000"));
                break;

            case "Commerce":
                careers.add(createCareerPath("Finance",
                        "Banking, investment, and financial services",
                        List.of("Investment Banker", "Financial Analyst", "Chartered Accountant"),
                        "₹4,00,000 - ₹25,00,000"));

                careers.add(createCareerPath("Business",
                        "Management and entrepreneurship",
                        List.of("Business Manager", "Entrepreneur", "Marketing Manager"),
                        "₹3,50,000 - ₹20,00,000"));

                careers.add(createCareerPath("Digital Marketing",
                        "Online marketing and e-commerce",
                        List.of("Digital Marketer", "SEO Specialist", "Social Media Manager"),
                        "₹3,00,000 - ₹15,00,000"));
                break;

            case "Arts":
                careers.add(createCareerPath("Media & Communication",
                        "Journalism, content creation, and media",
                        List.of("Journalist", "Content Writer", "Video Producer"),
                        "₹2,50,000 - ₹12,00,000"));

                careers.add(createCareerPath("Public Service",
                        "Government jobs and civil services",
                        List.of("IAS Officer", "Teacher", "Police Officer"),
                        "₹3,00,000 - ₹15,00,000"));

                careers.add(createCareerPath("Creative Arts",
                        "Design, arts, and creative industries",
                        List.of("Graphic Designer", "Artist", "Photographer"),
                        "₹2,00,000 - ₹10,00,000"));
                break;
        }

        return careers;
    }

    private Map<String, Object> createCareerPath(String field, String description,
            List<String> jobRoles, String salaryRange) {
        Map<String, Object> career = new HashMap<>();
        career.put("field", field);
        career.put("description", description);
        career.put("jobRoles", jobRoles);
        career.put("salaryRange", salaryRange);
        return career;
    }

    private Map<String, Object> getStudyGuidance(QuizAttempt quizAttempt) {
        Map<String, Object> guidance = new HashMap<>();

        int totalScore = quizAttempt.getScore(); // Use getScore() instead of getTotalScore()

        // Overall performance analysis
        String overallPerformance;
        List<String> recommendations = new ArrayList<>();

        if (totalScore >= 80) {
            overallPerformance = "Excellent";
            recommendations.add("You have strong academic foundation. Focus on competitive exam preparation.");
            recommendations.add("Consider applying to premier institutions and scholarship programs.");
        } else if (totalScore >= 60) {
            overallPerformance = "Good";
            recommendations.add("Build upon your strengths while improving weaker areas.");
            recommendations.add("Focus on consistent study habits and time management.");
        } else if (totalScore >= 40) {
            overallPerformance = "Average";
            recommendations.add("Identify your strong subjects and work on improving weak areas.");
            recommendations.add("Consider additional coaching or study groups for better understanding.");
        } else {
            overallPerformance = "Needs Improvement";
            recommendations.add("Focus on building fundamental concepts in all subjects.");
            recommendations.add("Seek help from teachers and consider remedial classes.");
        }

        guidance.put("overallPerformance", overallPerformance);
        guidance.put("recommendations", recommendations);

        // Since we don't have individual subject scores in QuizAttempt, provide general
        // guidance
        Map<String, String> subjectAnalysis = new HashMap<>();
        String generalFeedback = getSubjectFeedback(totalScore);
        subjectAnalysis.put("Science", generalFeedback);
        subjectAnalysis.put("Commerce", generalFeedback);
        subjectAnalysis.put("Arts", generalFeedback);

        guidance.put("subjectAnalysis", subjectAnalysis);

        // Study resources
        List<String> studyResources = List.of(
                "NCERT textbooks for conceptual clarity",
                "Previous year question papers for practice",
                "Online educational platforms (Khan Academy, BYJU'S)",
                "Group study sessions with peers",
                "Regular mock tests for assessment");

        guidance.put("studyResources", studyResources);

        return guidance;
    }

    private String getSubjectFeedback(int score) {
        if (score >= 27) { // 90% of 30
            return "Excellent understanding. Focus on advanced topics.";
        } else if (score >= 21) { // 70% of 30
            return "Good grasp of concepts. Practice more challenging problems.";
        } else if (score >= 15) { // 50% of 30
            return "Basic understanding present. Strengthen fundamentals.";
        } else {
            return "Needs significant improvement. Focus on basic concepts.";
        }
    }

    public Map<String, Object> getCollegeComparisonData(List<Long> collegeIds) {
        List<College> colleges = collegeRepository.findAllById(collegeIds);

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("colleges", colleges.stream()
                .map(this::convertToDetailsDto)
                .collect(Collectors.toList()));

        // Add comparison metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("averagePlacementRate", colleges.stream()
                .mapToDouble(College::getPlacementRate)
                .average().orElse(0.0));
        metrics.put("feeRanges", colleges.stream()
                .map(College::getFeesRange)
                .distinct()
                .collect(Collectors.toList()));
        metrics.put("establishmentYears", colleges.stream()
                .map(College::getEstablishmentYear)
                .sorted()
                .collect(Collectors.toList()));

        comparison.put("metrics", metrics);

        return comparison;
    }

    // Helper methods for DTO conversion
    private CollegeSearchDto convertToSearchDto(College college) {
        CollegeSearchDto dto = new CollegeSearchDto();
        dto.setCollegeId(college.getCollegeId());
        dto.setName(college.getName());
        dto.setDistrict(college.getDistrict());
        dto.setState(college.getState());
        dto.setCollegeType(college.getCollegeType());
        dto.setCollegeTier(college.getCollegeTier());
        dto.setStreamsOffered(college.getStreamsOffered());
        dto.setEstablishmentYear(college.getEstablishmentYear());
        dto.setWebsite(college.getWebsite());
        dto.setPlacementRate(college.getPlacementRate());
        return dto;
    }

    private CollegeDetailsDto convertToDetailsDto(College college) {
        CollegeDetailsDto dto = new CollegeDetailsDto();
        dto.setCollegeId(college.getCollegeId());
        dto.setName(college.getName());
        dto.setAddress(college.getAddress());
        dto.setDistrict(college.getDistrict());
        dto.setState(college.getState());
        dto.setPincode(college.getPincode());
        dto.setCollegeType(college.getCollegeType());
        dto.setCollegeTier(college.getCollegeTier());
        dto.setStreamsOffered(college.getStreamsOffered());
        dto.setEstablishmentYear(college.getEstablishmentYear());
        dto.setWebsite(college.getWebsite());
        dto.setPhone(college.getPhone());
        dto.setEmail(college.getEmail());
        dto.setFeesRange(college.getFeesRange());
        dto.setPlacementRate(college.getPlacementRate());
        dto.setCoursesOffered(college.getCoursesOffered());
        dto.setFacilities(college.getFacilitiesJson()); // Map to facilities field
        dto.setContactInfo(college.getContactInfo());
        return dto;
    }
}