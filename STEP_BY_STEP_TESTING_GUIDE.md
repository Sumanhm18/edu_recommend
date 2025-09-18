# 📋 Education MVP - Step-by-Step Testing Guide

## 🎯 Complete Testing Workflow (Follow in Order)

**Purpose**: Test all implemented features in the correct sequence  
**Time Required**: ~15 minutes  
**Prerequisites**: Application running on `http://localhost:8080`

---

## 🚀 **STEP 1: Start the Application**

### 1.1 Start Spring Boot Application

```bash
# Navigate to project directory
cd /Users/sumanhm/Downloads/education

# Start the application
java -jar target/education-0.0.1-SNAPSHOT.jar &

# Wait 10 seconds for startup
sleep 10
```

### 1.2 Verify Application Status

```bash
# Test if application is running
curl -X GET "http://localhost:8080/api/auth/test"
```

**Expected Response:**

```json
{
  "success": true,
  "message": "Auth service is working!",
  "timestamp": 1758113214469
}
```

✅ **If successful**: Continue to Step 2  
❌ **If failed**: Check application logs and restart

---

## 🔐 **STEP 2: Authentication Testing**

### 2.1 Get Guest Access Token

```bash
curl -X POST "http://localhost:8080/api/auth/guest-login" \
  -H "Content-Type: application/json"
```

**Expected Response:**

```json
{
  "message": "Guest login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "userId": null,
    "name": "Guest User",
    "phone": null,
    "guest": true
  },
  "success": true
}
```

📝 **Action**: **Copy the token value** - you'll need it for next steps!

### 2.2 Test Phone OTP Flow (Optional)

```bash
# Send OTP to phone
curl -X POST "http://localhost:8080/api/auth/send-otp" \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "9876543210"}'
```

**Expected Response:**

```json
{
  "success": true,
  "message": "OTP sent successfully"
}
```

### 2.3 Verify OTP (Optional)

```bash
# Verify with default OTP: 123456
curl -X POST "http://localhost:8080/api/auth/verify-otp" \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "9876543210", "otp": "123456"}'
```

**Expected Response:**

```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "name": "User",
    "phone": "9876543210",
    "guest": false
  }
}
```

---

## 📚 **STEP 3: Quiz Categories (Public Access)**

### 3.1 Get Quiz Information

```bash
curl -X GET "http://localhost:8080/api/quiz/categories" \
  -H "Content-Type: application/json"
```

**Expected Response:**

```json
{
  "message": "Quiz categories and information retrieved successfully",
  "data": {
    "aptitudeTypes": {
      "Mathematical": "Problem-solving, arithmetic, numerical reasoning",
      "Verbal": "Reading comprehension, vocabulary, language skills",
      "Analytical": "Pattern recognition, logical reasoning, critical thinking",
      "Technical": "Science concepts, technical aptitude, applied knowledge"
    },
    "streamInformation": {
      "Science": "Mathematics, Physics, Chemistry, Biology - leads to Engineering, Medical, Research",
      "Commerce": "Mathematics, Economics, Accounting, Business Studies - leads to CA, MBA, Finance",
      "Arts": "Languages, Social Sciences, History, Psychology - leads to Literature, Law, Social Work"
    },
    "scoringInfo": {
      "Science": "40% Mathematical + 40% Technical + 20% Analytical",
      "Commerce": "30% Mathematical + 40% Analytical + 30% Verbal",
      "Arts": "60% Verbal + 40% Analytical"
    }
  },
  "success": true
}
```

---

## 📝 **STEP 4: Quiz Discovery & Taking**

### 4.1 Get Available Quizzes

**HTTP Method**: `GET`  
**URL**: `http://localhost:8080/api/quiz/available`  
**Authorization**: Bearer Token (from Step 2.1)

```bash
# Replace YOUR_GUEST_TOKEN with token from Step 2.1
curl -X GET "http://localhost:8080/api/quiz/available" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_GUEST_TOKEN"
```

**Expected Response:**

```json
{
  "message": "Available quizzes retrieved successfully",
  "data": [
    {
      "quizId": 1,
      "title": "General Aptitude Assessment",
      "description": "This quiz assesses your mathematical, verbal, analytical, and technical aptitude to recommend suitable academic streams.",
      "targetClass": "12th",
      "isActive": true
    },
    {
      "quizId": 2,
      "title": "Career Interest Assessment",
      "description": "This quiz helps identify your interests and preferences to suggest the most suitable academic stream and career path.",
      "targetClass": "12th",
      "isActive": true
    }
  ],
  "success": true
}
```

### 4.2 Get Quiz Details

**HTTP Method**: `GET`  
**URL**: `http://localhost:8080/api/quiz/1`  
**Authorization**: Bearer Token

```bash
# Get details for Quiz 1 (General Aptitude)
curl -X GET "http://localhost:8080/api/quiz/1" \
  -H "Authorization: Bearer YOUR_GUEST_TOKEN"
```

**Expected Response:** Full quiz with 12 questions and answer options

### 4.3 Get Quiz Details for Quiz 2

```bash
# Get details for Quiz 2 (Career Interest)
curl -X GET "http://localhost:8080/api/quiz/2" \
  -H "Authorization: Bearer YOUR_GUEST_TOKEN"
```

**Expected Response:** Full quiz with 8 career-focused questions

---

## 🎯 **STEP 5: Submit Quizzes & Get Stream Recommendations**

### 5.1 Submit General Aptitude Assessment (Perfect Score)

```bash
curl -X POST "http://localhost:8080/api/quiz/submit" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_GUEST_TOKEN" \
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
  }'
```

**Expected Results:**

```json
{
  "message": "Quiz submitted successfully. Stream recommendations generated!",
  "data": {
    "quizTitle": "General Aptitude Assessment",
    "totalScore": 26,
    "maxScore": 26,
    "percentage": 100.0,
    "scoreBreakdown": {
      "mathematicalScore": 6,
      "verbalScore": 6,
      "analyticalScore": 8,
      "technicalScore": 6,
      "dominantAptitude": "Analytical"
    },
    "recommendedStreams": ["Science (100% match)", "Commerce (100% match)"],
    "performanceLevel": "Excellent",
    "collegeTier": "Premier"
  }
}
```

### 5.2 Submit Career Interest Assessment (Arts Stream)

```bash
curl -X POST "http://localhost:8080/api/quiz/submit" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_GUEST_TOKEN" \
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
  }'
```

**Expected Results:**

```json
{
  "message": "Quiz submitted successfully. Stream recommendations generated!",
  "data": {
    "quizTitle": "Career Interest Assessment",
    "totalScore": 5,
    "maxScore": 23,
    "percentage": 21.7,
    "scoreBreakdown": {
      "dominantAptitude": "Verbal"
    },
    "recommendedStreams": ["Arts (60% match)"],
    "performanceLevel": "Needs Improvement"
  }
}
```

---

## 👤 **STEP 6: Profile Management (If Using Authenticated User)**

### 6.1 Create User Profile

```bash
# Only works with authenticated user token (not guest)
curl -X POST "http://localhost:8080/api/user/profile" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN" \
  -d '{
    "fullName": "John Doe",
    "dateOfBirth": "2005-01-15",
    "gender": "MALE",
    "currentClass": "12th",
    "district": "Bangalore Urban",
    "state": "Karnataka",
    "interests": ["Engineering", "Technology"]
  }'
```

### 6.2 Get User Profile

```bash
curl -X GET "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"
```

### 6.3 Update User Profile

```bash
curl -X PUT "http://localhost:8080/api/user/profile" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN" \
  -d '{
    "fullName": "John Doe Updated",
    "currentClass": "12th",
    "district": "Mysore"
  }'
```

---

## 📊 **STEP 7: Quiz History & Analytics (Authenticated Users Only)**

### 7.1 Get Quiz History

```bash
curl -X GET "http://localhost:8080/api/quiz/history" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"
```

### 7.2 Get Stream Recommendations Summary

```bash
curl -X GET "http://localhost:8080/api/quiz/recommendations" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"
```

### 7.3 Get Quiz Statistics

```bash
curl -X GET "http://localhost:8080/api/quiz/stats" \
  -H "Authorization: Bearer YOUR_AUTH_TOKEN"
```

---

## ✅ **STEP 8: Validation Checklist**

After completing all steps, verify these outcomes:

### Core SIH Requirements:

- ✅ **Aptitude Assessment**: Mathematical, Verbal, Analytical, Technical scores calculated
- ✅ **Stream Recommendations**: Different answers produce different stream suggestions
- ✅ **Government College Awareness**: College tier recommendations provided
- ✅ **Guest Access**: Full functionality without registration
- ✅ **Real-time Results**: Instant scoring and stream guidance

### Technical Validation:

- ✅ **Authentication**: Both guest and phone-based auth working
- ✅ **Quiz Engine**: Both quizzes loading and submitting correctly
- ✅ **Stream Algorithm**: Science/Commerce/Arts recommendations based on scores
- ✅ **Database**: Sample data loaded and queries working
- ✅ **API Security**: Protected endpoints requiring proper tokens

---

## 🛠️ **TROUBLESHOOTING**

### Common Issues:

**❌ Connection Refused**

```bash
# Check if application is running
ps aux | grep "education-0.0.1-SNAPSHOT.jar"

# If not running, start it
java -jar target/education-0.0.1-SNAPSHOT.jar &
```

**❌ 401 Unauthorized**

- Check if you're using the correct token
- Ensure Authorization header format: `Bearer YOUR_TOKEN`
- **In Postman**: Use "Bearer Token" auth type and paste ONLY the token (no "Bearer " prefix)
- **In curl**: Use format `-H "Authorization: Bearer YOUR_TOKEN"`
- Guest tokens work for quiz APIs, auth tokens for profile APIs
- Token may have expired - get a new one from Step 2.1

**❌ 500 Internal Server Error**

```bash
# Check application logs
tail -f logs/spring.log

# Check database connection
curl -X GET "http://localhost:8080/api/auth/test"
```

**❌ Token Expired**

- Get a new guest token from Step 2.1
- Tokens are valid for 24 hours

---

## 🎯 **SUCCESS CRITERIA**

You've successfully tested the Education MVP if:

1. ✅ Health check returns success
2. ✅ Guest login provides valid token
3. ✅ Quiz categories load without authentication
4. ✅ Available quizzes load with guest token
5. ✅ Quiz submission returns stream recommendations
6. ✅ Different quiz answers produce different streams
7. ✅ Profile management works with authenticated users
8. ✅ All APIs return proper JSON responses

---

## 🚀 **AUTOMATION SCRIPT**

For quick testing of all APIs:

```bash
# Run automated demonstration
./demo_all_apis.sh
```

This script will automatically run through Steps 1-5 and show you all the working functionality!

---

**🎉 Congratulations! You've successfully tested the complete Education MVP that solves the SIH problem statement!**
