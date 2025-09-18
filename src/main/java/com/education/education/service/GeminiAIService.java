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
        prompt.append("You are an educational counselor. Recommend 5 specific colleges for this student. ");
        prompt.append("Include only government  from Karnataka, India.prioritize the location and give the results based ont the lcoation of the student.and prioritize ");
        prompt.append("You MUST provide exactly 5 colleges in the topColleges array.\n\n");

        prompt.append("Student Profile:\n");
        prompt.append("- Quiz Score: ").append(userProfile.get("score")).append("/").append(userProfile.get("maxScore"))
                .append("\n");
        prompt.append("- Percentage: ").append(userProfile.get("percentage")).append("%\n");
        prompt.append("- Performance Level: ").append(userProfile.get("performanceLevel")).append("\n");
        prompt.append("- Recommended Streams: ").append(userProfile.get("recommendedStreams")).append("\n");
        prompt.append("- Location: ").append(userProfile.get("district")).append(", ").append(userProfile.get("state"))
                .append("\n");
        prompt.append("- Class Level: ").append(userProfile.get("classLevel")).append("\n");

        prompt.append("\nRespond ONLY with this JSON format (no extra text):\n");
        prompt.append("{\n");
        prompt.append("  \"topColleges\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"name\": \"Bangalore University\",\n");
        prompt.append("      \"location\": \"Bangalore, Karnataka\",\n");
        prompt.append("      \"type\": \"Government\",\n");
        prompt.append("      \"tier\": \"Excellent\",\n");
        prompt.append("      \"coursesRecommended\": [\"B.Com\", \"B.Sc\"],\n");
        prompt.append("      \"admissionProcess\": \"Merit based\",\n");
        prompt.append("      \"estimatedFees\": \"₹20,000 per year\",\n");
        prompt.append("      \"placementHighlights\": \"Good placements\",\n");
        prompt.append("      \"whyRecommended\": \"Government college with good reputation\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"alternativeOptions\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"name\": \"Mount Carmel College\",\n");
        prompt.append("      \"location\": \"Bangalore, Karnataka\",\n");
        prompt.append("      \"type\": \"Private\",\n");
        prompt.append("      \"specialization\": \"Liberal Arts\",\n");
        prompt.append("      \"whyConsider\": \"Excellent academics\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"entranceExams\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"examName\": \"Karnataka CET\",\n");
        prompt.append("      \"eligibility\": \"12th grade\",\n");
        prompt.append("      \"preparationTips\": \"Focus on state syllabus\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"actionPlan\": {\n");
        prompt.append("    \"immediate\": [\"Research colleges\", \"Prepare documents\"],\n");
        prompt.append("    \"next6Months\": [\"Apply to colleges\"],\n");
        prompt.append("    \"nextYear\": [\"Start college\"]\n");
        prompt.append("  }\n");
        prompt.append("}\n\n");

        prompt.append("IMPORTANT: Provide exactly 5 colleges in topColleges array. ");
        prompt.append("Use real college names from Karnataka. ");
        prompt.append("Include both government and private options. ");
        prompt.append("Respond ONLY with valid JSON - no extra text before or after.");

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
            System.out.println("🔍 Parsing Gemini response...");
            System.out.println(
                    "📋 Raw response (first 500 chars): " + response.substring(0, Math.min(500, response.length())));

            // Extract JSON from response (in case there's extra text)
            String jsonString = extractJSON(response);
            System.out.println("📋 Extracted JSON (first 300 chars): "
                    + jsonString.substring(0, Math.min(300, jsonString.length())));

            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(jsonString, Map.class);

            // Check if topColleges exists and has content
            if (result.containsKey("topColleges")) {
                @SuppressWarnings("unchecked")
                List<Object> topColleges = (List<Object>) result.get("topColleges");
                System.out.println(
                        "📊 Number of colleges in AI response: " + (topColleges != null ? topColleges.size() : 0));

                if (topColleges == null || topColleges.isEmpty()) {
                    System.out.println("⚠️ AI returned empty or null topColleges array");
                    // Instead of returning fallback, let's add some colleges to the result
                    result.put("topColleges", createDefaultCollegeList());
                    System.out.println("✅ Added default colleges to AI response");
                }
            } else {
                System.out.println("⚠️ AI response missing topColleges field");
                result.put("topColleges", createDefaultCollegeList());
                System.out.println("✅ Added default colleges to AI response");
            }

            return result;
        } catch (Exception e) {
            System.err.println("❌ Error parsing Gemini response: " + e.getMessage());
            e.printStackTrace();
            System.out.println("🔄 Using fallback recommendations due to parsing error");
            return getFallbackRecommendations();
        }
    }

    private List<Map<String, Object>> createDefaultCollegeList() {
        List<Map<String, Object>> colleges = new ArrayList<>();

        Map<String, Object> college1 = new HashMap<>();
        college1.put("name", "Bangalore University");
        college1.put("location", "Bangalore, Karnataka");
        college1.put("type", "Government");
        college1.put("tier", "Excellent");
        college1.put("coursesRecommended", List.of("B.Com", "B.Sc", "B.A"));
        college1.put("admissionProcess", "Merit-based admission");
        college1.put("estimatedFees", "₹15,000 - ₹35,000 per year");
        college1.put("placementHighlights", "Good placement opportunities in Bangalore");
        college1.put("whyRecommended", "Government university with strong academic reputation");
        colleges.add(college1);

        Map<String, Object> college2 = new HashMap<>();
        college2.put("name", "University of Mysore");
        college2.put("location", "Mysore, Karnataka");
        college2.put("type", "Government");
        college2.put("tier", "Excellent");
        college2.put("coursesRecommended", List.of("B.Sc", "B.Com", "B.A"));
        college2.put("admissionProcess", "Karnataka CET based admission");
        college2.put("estimatedFees", "₹10,000 - ₹25,000 per year");
        college2.put("placementHighlights", "Historic university with good industry connections");
        college2.put("whyRecommended", "Well-established university with affordable education");
        colleges.add(college2);

        return colleges;
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
        System.out.println("🔄 Using enhanced fallback recommendations...");
        Map<String, Object> fallback = new HashMap<>();

        List<Map<String, Object>> topColleges = new ArrayList<>();

        // Add realistic Karnataka colleges
        Map<String, Object> college1 = new HashMap<>();
        college1.put("name", "University of Mysore");
        college1.put("location", "Mysore, Karnataka");
        college1.put("type", "Government");
        college1.put("tier", "Excellent");
        college1.put("coursesRecommended", List.of("B.Sc Physics", "B.Com", "B.A Economics", "B.Sc Computer Science"));
        college1.put("admissionProcess", "Merit-based admission through Karnataka CET");
        college1.put("estimatedFees", "₹8,000 - ₹30,000 per year");
        college1.put("placementHighlights", "Good placement opportunities in local industry and MNCs");
        college1.put("whyRecommended",
                "Historic university in Karnataka with excellent reputation and affordable fees");
        topColleges.add(college1);

        Map<String, Object> college2 = new HashMap<>();
        college2.put("name", "Bangalore University");
        college2.put("location", "Bangalore, Karnataka");
        college2.put("type", "Government");
        college2.put("tier", "Excellent");
        college2.put("coursesRecommended", List.of("B.Sc Computer Science", "B.Com", "BBA", "B.Sc Mathematics"));
        college2.put("admissionProcess", "Merit-based admission");
        college2.put("estimatedFees", "₹10,000 - ₹40,000 per year");
        college2.put("placementHighlights", "Excellent placement opportunities in IT capital of India");
        college2.put("whyRecommended", "Located in IT hub with great career opportunities and industry connections");
        topColleges.add(college2);

        Map<String, Object> college3 = new HashMap<>();
        college3.put("name", "Government Science College");
        college3.put("location", "Bangalore, Karnataka");
        college3.put("type", "Government");
        college3.put("tier", "Good");
        college3.put("coursesRecommended",
                List.of("B.Sc Physics", "B.Sc Chemistry", "B.Sc Mathematics", "B.Sc Biology"));
        college3.put("admissionProcess", "Merit-based admission");
        college3.put("estimatedFees", "₹5,000 - ₹15,000 per year");
        college3.put("placementHighlights", "Good opportunities in research and government jobs");
        college3.put("whyRecommended", "Government college with excellent science programs and very affordable fees");
        topColleges.add(college3);

        Map<String, Object> college4 = new HashMap<>();
        college4.put("name", "Mount Carmel College");
        college4.put("location", "Bangalore, Karnataka");
        college4.put("type", "Private");
        college4.put("tier", "Excellent");
        college4.put("coursesRecommended", List.of("B.Com", "B.A", "B.Sc", "BBA"));
        college4.put("admissionProcess", "Merit-based with entrance test");
        college4.put("estimatedFees", "₹50,000 - ₹1,20,000 per year");
        college4.put("placementHighlights", "Excellent placement record with top companies");
        college4.put("whyRecommended", "Premier women's college with outstanding academic reputation");
        topColleges.add(college4);

        Map<String, Object> college5 = new HashMap<>();
        college5.put("name", "St. Joseph's College");
        college5.put("location", "Bangalore, Karnataka");
        college5.put("type", "Private");
        college5.put("tier", "Premier");
        college5.put("coursesRecommended", List.of("B.Com", "B.A Economics", "B.Sc", "BBA"));
        college5.put("admissionProcess", "Entrance test and merit-based");
        college5.put("estimatedFees", "₹80,000 - ₹1,50,000 per year");
        college5.put("placementHighlights", "Top-tier placements with multinational companies");
        college5.put("whyRecommended", "Prestigious college with excellent academic standards and alumni network");
        topColleges.add(college5);

        fallback.put("topColleges", topColleges);

        // Add more alternative options
        List<Map<String, Object>> alternativeOptions = new ArrayList<>();

        Map<String, Object> alt1 = new HashMap<>();
        alt1.put("name", "Mangalore University");
        alt1.put("location", "Mangalore, Karnataka");
        alt1.put("type", "Government");
        alt1.put("specialization", "Strong in Commerce and Arts");
        alt1.put("whyConsider", "Coastal location with good academic reputation and affordable fees");
        alternativeOptions.add(alt1);

        Map<String, Object> alt2 = new HashMap<>();
        alt2.put("name", "Christ University");
        alt2.put("location", "Bangalore, Karnataka");
        alt2.put("type", "Private");
        alt2.put("specialization", "Wide range of undergraduate programs");
        alt2.put("whyConsider", "Excellent infrastructure and placement opportunities");
        alternativeOptions.add(alt2);

        Map<String, Object> alt3 = new HashMap<>();
        alt3.put("name", "PES University");
        alt3.put("location", "Bangalore, Karnataka");
        alt3.put("type", "Private");
        alt3.put("specialization", "Engineering and technology programs");
        alt3.put("whyConsider", "Strong industry connections and good placement record");
        alternativeOptions.add(alt3);

        fallback.put("alternativeOptions", alternativeOptions);

        // Add relevant entrance exams
        List<Map<String, Object>> entranceExams = new ArrayList<>();
        Map<String, Object> exam1 = new HashMap<>();
        exam1.put("examName", "Karnataka CET (KCET)");
        exam1.put("eligibility", "12th grade completion with relevant subjects");
        exam1.put("preparationTips", "Focus on state syllabus, practice previous year papers, and time management");
        entranceExams.add(exam1);

        Map<String, Object> exam2 = new HashMap<>();
        exam2.put("examName", "COMEDK UGET");
        exam2.put("eligibility", "12th grade with PCM/PCB subjects");
        exam2.put("preparationTips", "Focus on NCERT concepts and practice mock tests");
        entranceExams.add(exam2);

        fallback.put("entranceExams", entranceExams);

        Map<String, Object> actionPlan = new HashMap<>();
        actionPlan.put("immediate", List.of("Research college admission requirements", "Prepare for entrance exams",
                "Gather necessary documents"));
        actionPlan.put("next6Months",
                List.of("Apply to selected colleges", "Prepare for interviews", "Focus on academic performance"));
        actionPlan.put("nextYear",
                List.of("Begin college journey", "Join relevant clubs and activities", "Build professional network"));
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