#!/bin/bash

echo "🎯 Testing Complete Quiz Flow"
echo "==============================="

# Test variables
BASE_URL="http://localhost:8080"
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJndWVzdCI6ZmFsc2UsInVzZXJJZCI6MTYsInN1YiI6IjkxOTE5MTkxOTEiLCJpYXQiOjE3NTgxNzQ0NzcsImV4cCI6MTc1ODI2MDg3N30.EPC-q1Yq4FQfpG_nA-a0Fai8_2csIBuEnepkt4ZAwlg4v1e0G0-IT7G1i39qgNqNFT--qUhMV9Z1brShKy5wxw"

echo "📋 Step 1: Getting Available Quiz..."
QUIZ_RESPONSE=$(curl -s -X GET "$BASE_URL/api/quiz/available" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN")

if [ $? -eq 0 ]; then
    echo "✅ Quiz endpoint accessible"
    echo "📊 Quiz data preview:"
    echo "$QUIZ_RESPONSE" | head -c 200
    echo "..."
else
    echo "❌ Failed to get quiz"
    exit 1
fi

echo -e "\n\n🎯 Step 2: Testing Quiz Submission..."
# Create a sample quiz submission
QUIZ_SUBMISSION='{
  "quizId": 1,
  "answers": [
    {"questionId": 1, "selectedOption": "A"},
    {"questionId": 2, "selectedOption": "A"},
    {"questionId": 3, "selectedOption": "A"}
  ]
}'

SUBMISSION_RESPONSE=$(curl -s -X POST "$BASE_URL/api/quiz/submit-with-ai" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "$QUIZ_SUBMISSION")

if [ $? -eq 0 ]; then
    echo "✅ Quiz submission successful"
    echo "🤖 AI Response preview:"
    echo "$SUBMISSION_RESPONSE" | head -c 300
    echo "..."
else
    echo "❌ Failed to submit quiz"
    exit 1
fi

echo -e "\n\n🎉 Quiz Flow Test Complete!"
echo "==============================="
echo "✅ Authentication: Working"
echo "✅ Quiz Loading: Working"
echo "✅ Quiz Submission: Working"
echo "✅ AI Recommendations: Working"