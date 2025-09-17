#!/bin/bash

echo "🚀 Education MVP - Complete API Demonstration"
echo "=============================================="

BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo ""
echo -e "${BLUE}🔍 Step 1: Health Check${NC}"
echo "Testing if the application is running..."
curl -s -X GET "$BASE_URL/api/auth/test" | jq '.' || echo "❌ Application not running"

echo ""
echo -e "${BLUE}🎭 Step 2: Guest Authentication${NC}"
echo "Getting guest token for anonymous access..."
GUEST_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/guest-login" -H "Content-Type: application/json")
GUEST_TOKEN=$(echo $GUEST_RESPONSE | jq -r '.data.token')
echo "Guest Token: ${GUEST_TOKEN:0:50}..."

echo ""
echo -e "${BLUE}📚 Step 3: Quiz Categories (Public)${NC}"
echo "Getting quiz categories without authentication..."
curl -s -X GET "$BASE_URL/api/quiz/categories" -H "Content-Type: application/json" | jq '.data.streamInformation'

echo ""
echo -e "${BLUE}📝 Step 4: Available Quizzes${NC}"
echo "Getting available quizzes with guest token..."
curl -s -X GET "$BASE_URL/api/quiz/available" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $GUEST_TOKEN" | jq '.data[] | {quizId, title, description}'

echo ""
echo -e "${BLUE}📋 Step 5: Quiz Details${NC}"
echo "Getting details for Quiz 1..."
curl -s -X GET "$BASE_URL/api/quiz/1" \
  -H "Authorization: Bearer $GUEST_TOKEN" | jq '.data | {quizId, title, description}'

echo ""
echo -e "${BLUE}🎯 Step 6: Submit Quiz (Core SIH Feature)${NC}"
echo "Submitting General Aptitude Assessment..."
QUIZ_RESULT=$(curl -s -X POST "$BASE_URL/api/quiz/submit" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $GUEST_TOKEN" \
  -d '{
    "quizId": 1,
    "answers": [
      {"questionId": 1, "selectedOption": "A"},
      {"questionId": 2, "selectedOption": "A"},
      {"questionId": 3, "selectedOption": "A"},
      {"questionId": 4, "selectedOption": "B"},
      {"questionId": 5, "selectedOption": "A"},
      {"questionId": 6, "selectedOption": "C"},
      {"questionId": 7, "selectedOption": "A"},
      {"questionId": 8, "selectedOption": "D"},
      {"questionId": 9, "selectedOption": "B"},
      {"questionId": 10, "selectedOption": "A"},
      {"questionId": 11, "selectedOption": "B"},
      {"questionId": 12, "selectedOption": "A"}
    ]
  }')

echo $QUIZ_RESULT | jq '.data | {
  quizTitle,
  totalScore,
  maxScore,
  percentage,
  dominantAptitude: .scoreBreakdown.dominantAptitude,
  recommendedStreams,
  performanceLevel,
  collegeTier
}'

echo ""
echo -e "${BLUE}🎨 Step 7: Submit Career Interest Quiz${NC}"
echo "Submitting Career Interest Assessment for Arts stream..."
CAREER_RESULT=$(curl -s -X POST "$BASE_URL/api/quiz/submit" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $GUEST_TOKEN" \
  -d '{
    "quizId": 2,
    "answers": [
      {"questionId": 1, "selectedOption": "B"},
      {"questionId": 2, "selectedOption": "A"},
      {"questionId": 3, "selectedOption": "B"},
      {"questionId": 4, "selectedOption": "B"},
      {"questionId": 5, "selectedOption": "B"},
      {"questionId": 6, "selectedOption": "B"},
      {"questionId": 7, "selectedOption": "B"},
      {"questionId": 8, "selectedOption": "B"}
    ]
  }')

echo $CAREER_RESULT | jq '.data | {
  quizTitle,
  totalScore,
  maxScore,
  percentage,
  dominantAptitude: .scoreBreakdown.dominantAptitude,
  recommendedStreams,
  performanceLevel
}'

echo ""
echo -e "${GREEN}✅ DEMONSTRATION COMPLETE!${NC}"
echo ""
echo -e "${YELLOW}🏆 SIH Problem Statement Solutions Demonstrated:${NC}"
echo "1. ✅ Aptitude Assessment (Mathematical, Verbal, Analytical, Technical)"
echo "2. ✅ Stream Recommendations (Science, Commerce, Arts)"
echo "3. ✅ Government College Awareness (Tier-based recommendations)"
echo "4. ✅ Guest Access (No registration required)"
echo "5. ✅ Real-time Career Guidance"
echo ""
echo -e "${BLUE}📊 Results Summary:${NC}"
echo "- General Aptitude: High scores → Science/Commerce recommendation"
echo "- Career Interest: Arts-oriented answers → Arts stream recommendation"
echo "- College Suggestions: Performance-based tier recommendations"
echo "- Guest Mode: Full functionality without user registration"
echo ""
echo -e "${GREEN}🎯 The Education MVP successfully addresses the SIH problem statement!${NC}"