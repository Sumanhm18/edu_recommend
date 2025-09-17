package com.education.education.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get personalized college recommendations from Gemini AI
     */
    public Map<String, Object> getCollegeRecommendations(Map<String, Object> userProfile) {
        try {
            String prompt = buildCollegeRecommendationPrompt(userProfile);
            String response = callGeminiAPI(prompt);
            return parseCollegeRecommendations(response);
        } catch (Exception e) {
            System.err.println("Error getting college recommendations from Gemini: " + e.getMessage());
            return getFallbackRecommendations();
        }
    }

    /**
     * Get career guidance from Gemini AI
     */
    public Map<String, Object> getCareerGuidance(Map<String, Object> userProfile) {
        try {
            String prompt = buildCareerGuidancePrompt(userProfile);
            String response = callGeminiAPI(prompt);
            return parseCareerGuidance(response);
        } catch (Exception e) {
            System.err.println("Error getting career guidance from Gemini: " + e.getMessage());
            return getFallbackCareerGuidance();
        }
    }

    private String buildCollegeRecommendationPrompt(Map<String, Object> userProfile) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert educational counselor. Based on the following student profile, ");
        prompt.append("provide comprehensive college recommendations in India. ");
        prompt.append("Respond in JSON format with the structure specified below.\n\n");

        prompt.append("Student Profile:\n");
        prompt.append("- Total Quiz Score: ").append(userProfile.get("totalScore")).append("/100\n");
        prompt.append("- Recommended Stream: ").append(userProfile.get("recommendedStream")).append("\n");
        prompt.append("- Location: ").append(userProfile.get("district")).append(", ").append(userProfile.get("state"))
                .append("\n");
        prompt.append("- Academic Interests: ").append(userProfile.get("interests")).append("\n");

        if (userProfile.containsKey("preferredCollegeType")) {
            prompt.append("- Preferred College Type: ").append(userProfile.get("preferredCollegeType")).append("\n");
        }

        prompt.append("\nPlease provide recommendations in this exact JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"topColleges\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"name\": \"College Name\",\n");
        prompt.append("      \"location\": \"City, State\",\n");
        prompt.append("      \"type\": \"Government/Private/Deemed\",\n");
        prompt.append("      \"tier\": \"Premier/Excellent/Good\",\n");
        prompt.append("      \"coursesRecommended\": [\"Course 1\", \"Course 2\"],\n");
        prompt.append("      \"admissionProcess\": \"Brief description\",\n");
        prompt.append("      \"estimatedFees\": \"Fee range\",\n");
        prompt.append("      \"placementHighlights\": \"Brief highlights\",\n");
        prompt.append("      \"whyRecommended\": \"Reason for recommendation\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"alternativeOptions\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"name\": \"Alternative College\",\n");
        prompt.append("      \"location\": \"City, State\",\n");
        prompt.append("      \"type\": \"Government/Private\",\n");
        prompt.append("      \"specialization\": \"Key strength\",\n");
        prompt.append("      \"whyConsider\": \"Reason to consider\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"entranceExams\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"examName\": \"Exam Name\",\n");
        prompt.append("      \"eligibility\": \"Eligibility criteria\",\n");
        prompt.append("      \"preparationTips\": \"Key preparation tips\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"actionPlan\": {\n");
        prompt.append("    \"immediate\": [\"Action 1\", \"Action 2\"],\n");
        prompt.append("    \"next6Months\": [\"Goal 1\", \"Goal 2\"],\n");
        prompt.append("    \"nextYear\": [\"Milestone 1\", \"Milestone 2\"]\n");
        prompt.append("  }\n");
        prompt.append("}\n\n");

        prompt.append("Focus on colleges in ").append(userProfile.get("state")).append(" and neighboring states. ");
        prompt.append(
                "Consider the student's quiz score and recommend colleges where they have a good chance of admission. ");
        prompt.append("Include a mix of government and private institutions. Provide practical and actionable advice.");

        return prompt.toString();
    }

    private String buildCareerGuidancePrompt(Map<String, Object> userProfile) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a career counselor. Based on the student's profile, provide career guidance ");
        prompt.append("in JSON format.\n\n");

        prompt.append("Student Profile:\n");
        prompt.append("- Stream: ").append(userProfile.get("recommendedStream")).append("\n");
        prompt.append("- Quiz Score: ").append(userProfile.get("totalScore")).append("/100\n");
        prompt.append("- Location: ").append(userProfile.get("state")).append("\n\n");

        prompt.append("Provide career guidance in this JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"topCareerPaths\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"field\": \"Career Field\",\n");
        prompt.append("      \"roles\": [\"Role 1\", \"Role 2\"],\n");
        prompt.append("      \"growthProspects\": \"Growth description\",\n");
        prompt.append("      \"salaryRange\": \"Salary range\",\n");
        prompt.append("      \"skillsRequired\": [\"Skill 1\", \"Skill 2\"]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"skillDevelopment\": {\n");
        prompt.append("    \"technical\": [\"Tech Skill 1\", \"Tech Skill 2\"],\n");
        prompt.append("    \"soft\": [\"Soft Skill 1\", \"Soft Skill 2\"]\n");
        prompt.append("  },\n");
        prompt.append("  \"certifications\": [\"Certification 1\", \"Certification 2\"]\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    private String callGeminiAPI(String prompt) throws Exception {
        if ("your-gemini-api-key-here".equals(apiKey) || apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException(
                    "Gemini API key not configured. Please set GEMINI_API_KEY environment variable.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build request body for Gemini API
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));

        // Add generation config for better JSON responses
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        requestBody.put("generationConfig", generationConfig);

        String requestJson = objectMapper.writeValueAsString(requestBody);
        String url = apiUrl + "?key=" + apiKey;

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode responseNode = objectMapper.readTree(response.getBody());
            JsonNode candidates = responseNode.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content1 = candidates.get(0).get("content");
                if (content1 != null) {
                    JsonNode parts = content1.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        return parts.get(0).get("text").asText();
                    }
                }
            }
        }

        throw new RuntimeException("Failed to get response from Gemini API");
    }

    private Map<String, Object> parseCollegeRecommendations(String response) {
        try {
            // Extract JSON from response (in case there's extra text)
            String jsonString = extractJSON(response);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(jsonString, Map.class);
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            return getFallbackRecommendations();
        }
    }

    private Map<String, Object> parseCareerGuidance(String response) {
        try {
            String jsonString = extractJSON(response);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(jsonString, Map.class);
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing career guidance response: " + e.getMessage());
            return getFallbackCareerGuidance();
        }
    }

    private String extractJSON(String response) {
        // Find JSON content between braces
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        return response; // Return as is if no braces found
    }

    private Map<String, Object> getFallbackRecommendations() {
        Map<String, Object> fallback = new HashMap<>();

        List<Map<String, Object>> topColleges = new ArrayList<>();
        Map<String, Object> college1 = new HashMap<>();
        college1.put("name", "Delhi University");
        college1.put("location", "Delhi");
        college1.put("type", "Government");
        college1.put("tier", "Premier");
        college1.put("coursesRecommended", List.of("B.Sc Physics", "B.Com"));
        college1.put("admissionProcess", "Merit-based admission");
        college1.put("estimatedFees", "₹10,000 - ₹50,000");
        college1.put("placementHighlights", "Good placement opportunities");
        college1.put("whyRecommended", "Excellent reputation and affordable fees");
        topColleges.add(college1);

        fallback.put("topColleges", topColleges);
        fallback.put("alternativeOptions", new ArrayList<>());
        fallback.put("entranceExams", new ArrayList<>());

        Map<String, Object> actionPlan = new HashMap<>();
        actionPlan.put("immediate", List.of("Research colleges", "Prepare for entrance exams"));
        actionPlan.put("next6Months", List.of("Apply to colleges", "Focus on studies"));
        actionPlan.put("nextYear", List.of("Start college", "Build network"));
        fallback.put("actionPlan", actionPlan);

        return fallback;
    }

    private Map<String, Object> getFallbackCareerGuidance() {
        Map<String, Object> fallback = new HashMap<>();

        List<Map<String, Object>> careers = new ArrayList<>();
        Map<String, Object> career1 = new HashMap<>();
        career1.put("field", "Technology");
        career1.put("roles", List.of("Software Developer", "Data Analyst"));
        career1.put("growthProspects", "High growth potential");
        career1.put("salaryRange", "₹3,00,000 - ₹15,00,000");
        career1.put("skillsRequired", List.of("Programming", "Problem Solving"));
        careers.add(career1);

        fallback.put("topCareerPaths", careers);

        Map<String, Object> skills = new HashMap<>();
        skills.put("technical", List.of("Programming", "Data Analysis"));
        skills.put("soft", List.of("Communication", "Teamwork"));
        fallback.put("skillDevelopment", skills);

        fallback.put("certifications", List.of("Google Certificates", "Coursera Courses"));

        return fallback;
    }
}