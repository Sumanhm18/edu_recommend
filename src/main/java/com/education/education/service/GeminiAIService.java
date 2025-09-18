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
            System.out.println("🎯 Starting Gemini AI recommendation generation...");
            System.out.println("📊 User Profile: " + userProfile);

            String prompt = buildCollegeRecommendationPrompt(userProfile);
            System.out.println("📝 Generated prompt length: " + prompt.length() + " characters");

            String response = callGeminiAPI(prompt);
            System.out.println("✅ Received Gemini response length: " + response.length() + " characters");
            System.out.println(
                    "🤖 Raw Gemini response preview: " + response.substring(0, Math.min(200, response.length())));

            Map<String, Object> parsedRecommendations = parseCollegeRecommendations(response);
            System.out.println("✅ Successfully parsed AI recommendations!");

            return parsedRecommendations;
        } catch (Exception e) {
            System.err.println("❌ Error getting college recommendations from Gemini: " + e.getMessage());
            e.printStackTrace();
            System.out.println("🔄 Falling back to default recommendations...");
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
        prompt.append("provide comprehensive GOVERNMENT college recommendations ONLY in India. ");
        prompt.append("FOCUS EXCLUSIVELY on government institutions, public universities, IITs, NITs, IIITs, central universities, state universities, and government-funded colleges. ");
        prompt.append("DO NOT recommend any private or deemed universities. ");
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
        prompt.append("      \"type\": \"Government\",\n");
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
        prompt.append("      \"type\": \"Government\",\n");
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

        // Enhanced location-based prioritization
        prompt.append("IMPORTANT LOCATION-BASED REQUIREMENTS:\n");
        prompt.append("1. PRIORITIZE colleges in ").append(userProfile.get("district")).append(", ")
                .append(userProfile.get("state")).append(" FIRST\n");
        prompt.append("2. Next, include colleges in neighboring districts of ").append(userProfile.get("state"))
                .append("\n");
        prompt.append("3. Then include other top colleges in ").append(userProfile.get("state")).append("\n");
        prompt.append("4. Finally, include 1-2 premier institutions from neighboring states if highly relevant\n\n");

        prompt.append("RANKING CRITERIA (in order of importance):\n");
        prompt.append("1. Geographic proximity to ").append(userProfile.get("district")).append(", ")
                .append(userProfile.get("state")).append(" (HIGHEST PRIORITY)\n");
        prompt.append("2. Student's quiz score compatibility (").append(userProfile.get("score")).append("/")
                .append(userProfile.get("maxScore")).append(")\n");
        prompt.append("3. Course availability in recommended stream: ").append(userProfile.get("recommendedStreams"))
                .append("\n");
        prompt.append("4. College reputation and placement record\n");
        prompt.append("5. Affordability and accessibility from student's location\n\n");

        prompt.append("GOVERNMENT COLLEGE REQUIREMENTS:\n");
        prompt.append("- RECOMMEND ONLY government colleges: IITs, NITs, IIITs, central universities, state universities, local government colleges, district colleges, government polytechnics\n");
        prompt.append("- Include LOCAL government colleges, municipal colleges, state board colleges, and district-level government institutions\n");
        prompt.append("- NO private colleges, deemed universities, or autonomous institutions\n");
        prompt.append("- Focus on affordability and government funding benefits\n");
        prompt.append("- Prioritize institutions with government job placement opportunities\n");
        prompt.append("- Mention government scholarships and financial aid available\n");
        prompt.append("- Give preference to nearby local government colleges for accessibility and cost-effectiveness\n\n");
        
        prompt.append("LOCATION REQUIREMENTS:\n");
        prompt.append("- Ensure that AT LEAST 70% of recommended colleges are within ").append(userProfile.get("state"))
                .append("\n");
        prompt.append("- Consider travel distance, local accommodation costs, and regional opportunities\n");
        prompt.append(
                "- Mention specific advantages of studying close to home (family support, local networks, cost savings)\n");
        prompt.append("- Include information about local transportation and connectivity\n");
        prompt.append("- Prioritize colleges with good local industry connections in ")
                .append(userProfile.get("district")).append(" area\n\n");

        prompt.append(
                "FOCUS EXCLUSIVELY on government institutions. Provide practical and actionable advice with specific focus on government college benefits and location advantages.");

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
        System.out.println("🚀 Calling Gemini API...");

        if ("your-gemini-api-key-here".equals(apiKey) || apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException(
                    "Gemini API key not configured. Please set GEMINI_API_KEY environment variable.");
        }

        System.out.println("🔑 API Key configured: " + apiKey.substring(0, 10) + "...");
        System.out.println("🌐 API URL: " + apiUrl);

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

        System.out.println("📤 Sending request to Gemini...");

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        System.out.println("📥 Received response status: " + response.getStatusCode());

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode responseNode = objectMapper.readTree(response.getBody());
            JsonNode candidates = responseNode.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content1 = candidates.get(0).get("content");
                if (content1 != null) {
                    JsonNode parts = content1.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        String aiResponse = parts.get(0).get("text").asText();
                        System.out.println("✅ Successfully extracted AI response");
                        return aiResponse;
                    }
                }
            }
        }

        System.err.println("❌ Failed to get valid response from Gemini API");
        System.err.println("Response body: " + response.getBody());
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
        System.out.println("🔄 Using location-aware fallback recommendations...");
        Map<String, Object> fallback = new HashMap<>();

        List<Map<String, Object>> topColleges = new ArrayList<>();

        // Location-aware fallback colleges for Karnataka
        Map<String, Object> college1 = new HashMap<>();
        college1.put("name", "University of Mysore");
        college1.put("location", "Mysore, Karnataka");
        college1.put("type", "Government");
        college1.put("tier", "Excellent");
        college1.put("coursesRecommended", List.of("B.Sc Physics", "B.Com", "B.A"));
        college1.put("admissionProcess", "Merit-based admission");
        college1.put("estimatedFees", "₹8,000 - ₹30,000");
        college1.put("placementHighlights", "Good placement opportunities in local industry");
        college1.put("whyRecommended", "Close to home in Karnataka, excellent reputation and affordable fees");
        topColleges.add(college1);

        Map<String, Object> college2 = new HashMap<>();
        college2.put("name", "Bangalore University");
        college2.put("location", "Bangalore, Karnataka");
        college2.put("type", "Government");
        college2.put("tier", "Excellent");
        college2.put("coursesRecommended", List.of("B.Sc Computer Science", "B.Com", "BBA"));
        college2.put("admissionProcess", "Merit-based admission");
        college2.put("estimatedFees", "₹10,000 - ₹40,000");
        college2.put("placementHighlights", "Excellent placement opportunities in IT capital");
        college2.put("whyRecommended", "Located in IT hub of Karnataka, great career opportunities");
        topColleges.add(college2);

        // Add local government colleges
        Map<String, Object> college3 = new HashMap<>();
        college3.put("name", "Government Science College");
        college3.put("location", "Bangalore, Karnataka");
        college3.put("type", "Government");
        college3.put("tier", "Good");
        college3.put("coursesRecommended", List.of("B.Sc Physics", "B.Sc Chemistry", "B.Sc Mathematics"));
        college3.put("admissionProcess", "Merit-based admission");
        college3.put("estimatedFees", "₹5,000 - ₹15,000");
        college3.put("placementHighlights", "Good opportunities in research and government jobs");
        college3.put("whyRecommended", "Local government college with excellent science programs and very affordable fees");
        topColleges.add(college3);

        Map<String, Object> college4 = new HashMap<>();
        college4.put("name", "Government First Grade College");
        college4.put("location", "Mysore, Karnataka");
        college4.put("type", "Government");
        college4.put("tier", "Good");
        college4.put("coursesRecommended", List.of("B.Com", "B.A", "B.Sc"));
        college4.put("admissionProcess", "Merit-based admission");
        college4.put("estimatedFees", "₹3,000 - ₹12,000");
        college4.put("placementHighlights", "Local placement opportunities and government job preparation");
        college4.put("whyRecommended", "Very affordable local government college with strong academic foundation");
        topColleges.add(college4);

        fallback.put("topColleges", topColleges);

        // Add Karnataka-focused alternative options
        List<Map<String, Object>> alternativeOptions = new ArrayList<>();
        Map<String, Object> alt1 = new HashMap<>();
        alt1.put("name", "Mangalore University");
        alt1.put("location", "Mangalore, Karnataka");
        alt1.put("type", "Government");
        alt1.put("specialization", "Strong in Commerce and Arts");
        alt1.put("whyConsider", "Coastal location in Karnataka with good academic reputation");
        alternativeOptions.add(alt1);

        Map<String, Object> alt2 = new HashMap<>();
        alt2.put("name", "Gulbarga University");
        alt2.put("location", "Gulbarga, Karnataka");
        alt2.put("type", "Government");
        alt2.put("specialization", "Wide range of undergraduate programs");
        alt2.put("whyConsider", "Affordable education within Karnataka state");
        alternativeOptions.add(alt2);

        // Add more local government colleges
        Map<String, Object> alt3 = new HashMap<>();
        alt3.put("name", "Government Engineering College, Hassan");
        alt3.put("location", "Hassan, Karnataka");
        alt3.put("type", "Government");
        alt3.put("specialization", "Engineering and technical education");
        alt3.put("whyConsider", "Local government engineering college with industry connections");
        alternativeOptions.add(alt3);

        Map<String, Object> alt4 = new HashMap<>();
        alt4.put("name", "Government Polytechnic College");
        alt4.put("location", "Hubli, Karnataka");
        alt4.put("type", "Government");
        alt4.put("specialization", "Technical and diploma courses");
        alt4.put("whyConsider", "Practical technical education at very affordable government rates");
        alternativeOptions.add(alt4);

        Map<String, Object> alt5 = new HashMap<>();
        alt5.put("name", "District Government College");
        alt5.put("location", "Mangalore, Karnataka");
        alt5.put("type", "Government");
        alt5.put("specialization", "Commerce and liberal arts");
        alt5.put("whyConsider", "Local district college with coastal region advantages");
        alternativeOptions.add(alt5);

        fallback.put("alternativeOptions", alternativeOptions);

        // Add relevant entrance exams
        List<Map<String, Object>> entranceExams = new ArrayList<>();
        Map<String, Object> exam1 = new HashMap<>();
        exam1.put("examName", "Karnataka CET");
        exam1.put("eligibility", "12th grade completion");
        exam1.put("preparationTips", "Focus on state syllabus and previous year papers");
        entranceExams.add(exam1);

        fallback.put("entranceExams", entranceExams);

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