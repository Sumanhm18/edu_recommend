# 🧪 Education MVP - Postman Testing Guide

## 📦 How to Import and Test

### 1. Import Collection & Environment

1. Open Postman
2. Click **Import** button
3. Drag and drop both files:
   - `Education_MVP_Postman_Collection.json`
   - `Education_MVP_Environment.json`
4. Select **"Education MVP - Local Development"** environment (top right)

### 2. Test Sequence (Recommended Order)

#### 🔐 **Authentication Flow**

1. **Health Check** - `GET /api/auth/test`

   - ✅ Should return: `{"success":true,"message":"Auth service is working!"}`

2. **Guest Login** - `POST /api/auth/guest-login`

   - ✅ Should return JWT token for anonymous users
   - 🔄 Token automatically saved to `{{guestToken}}` variable

3. **Send OTP** - `POST /api/auth/send-otp`

   - 📱 Body: `{"phoneNumber": "8197257997"}`
   - ✅ Should return: `{"success":true,"message":"OTP sent successfully"}`
   - 📋 Check application logs for OTP: `tail -f app.log | grep "OTP for"`

4. **Verify OTP** - `POST /api/auth/verify-otp`
   - 📱 Body: `{"phoneNumber": "8197257997", "otp": "YOUR_OTP_HERE"}`
   - ✅ Creates user account and returns JWT token
   - 🔄 Token automatically saved to `{{authToken}}` variable

#### 👤 **Profile Management Flow**

5. **Create Profile** - `POST /api/user/profile`

   - 🔑 Requires authentication token
   - ✅ Should create user profile with district, class, interests

6. **Get Profile** - `GET /api/user/profile`

   - 🔑 Requires authentication token
   - ✅ Should return complete user profile

7. **Update Profile** - `PUT /api/user/profile`
   - 🔑 Requires authentication token
   - ✅ Should update user profile fields

#### 📝 **Quiz Engine Flow (Core SIH Feature)**

8. **Get Quiz Categories** - `GET /api/quiz/categories`

   - 🌐 Public endpoint (no auth required)
   - ✅ Should return aptitude types and stream information

9. **Get Available Quizzes** - `GET /api/quiz/available`

   - 🔑 Requires authentication (guest or user token)
   - ✅ Should return 2 sample quizzes with questions

10. **Get Quiz Details** - `GET /api/quiz/1`

    - 🔑 Requires authentication (guest or user token)
    - ✅ Should return full quiz with questions JSON

11. **Submit Quiz** - `POST /api/quiz/submit`

    - 🔑 Requires authentication (guest or user token)
    - ✅ Should return stream recommendations and scores
    - 🎯 **CORE SIH FEATURE**: Stream guidance based on aptitude

12. **Get Quiz History** - `GET /api/quiz/history`

    - 🔑 Requires user authentication (not guest)
    - ✅ Should return user's past quiz attempts

13. **Get Stream Recommendations** - `GET /api/quiz/recommendations`
    - 🔑 Requires user authentication (not guest)
    - ✅ Should return consolidated stream recommendations

#### 🛡️ **Security Testing**

5. **Test Protected Endpoint** - Try any endpoint under "Admin APIs"
   - ❌ Without token: Should return `401 Unauthorized`
   - ✅ With token: Should work properly

## 🎯 **Current Working Endpoints**

### ✅ **FULLY IMPLEMENTED & TESTED:**

#### Authentication APIs:

- `GET /api/auth/test` - Health check
- `POST /api/auth/guest-login` - Anonymous login
- `POST /api/auth/send-otp` - Send OTP to phone
- `POST /api/auth/verify-otp` - Verify OTP and login

#### Profile Management APIs:

- `POST /api/user/profile` - Create user profile
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile

#### Quiz Engine APIs (CORE SIH FEATURE):

- `GET /api/quiz/categories` - Get quiz categories (Public)
- `GET /api/quiz/available` - Get available quizzes
- `GET /api/quiz/{id}` - Get quiz by ID
- `POST /api/quiz/submit` - Submit quiz answers & get stream recommendations
- `GET /api/quiz/history` - Get user's quiz history
- `GET /api/quiz/recommendations` - Get stream recommendations
- `GET /api/quiz/stats` - Get quiz statistics

### 🚧 **PLANNED (Next in Development):**

- College Directory APIs
- Course & Career Mapping APIs
- Notification APIs
- Admin Management APIs

## 📱 **Sample Test Data**

### Phone Numbers (for OTP testing):

- `8197257997` - Your personal number (primary)
- `9876543210` - Valid test number
- `8123456789` - Alternative test number
- `7987654321` - Another valid number

### Sample User Profile:

```json
{
  "name": "John Doe",
  "classLevel": "12th",
  "district": "Bangalore Urban",
  "preferredLanguage": "English"
}
```

### Sample Quiz Submission (General Aptitude):

```json
{
  "quizId": 1,
  "answers": [
    { "questionId": 1, "selectedOption": "A" },
    { "questionId": 2, "selectedOption": "A" },
    { "questionId": 3, "selectedOption": "A" },
    { "questionId": 4, "selectedOption": "B" },
    { "questionId": 5, "selectedOption": "A" },
    { "questionId": 6, "selectedOption": "C" },
    { "questionId": 7, "selectedOption": "A" },
    { "questionId": 8, "selectedOption": "D" },
    { "questionId": 9, "selectedOption": "B" },
    { "questionId": 10, "selectedOption": "A" },
    { "questionId": 11, "selectedOption": "B" },
    { "questionId": 12, "selectedOption": "A" }
  ]
}
```

### Sample Quiz Submission (Career Interest):

```json
{
  "quizId": 2,
  "answers": [
    { "questionId": 1, "selectedOption": "B" },
    { "questionId": 2, "selectedOption": "A" },
    { "questionId": 3, "selectedOption": "B" },
    { "questionId": 4, "selectedOption": "B" },
    { "questionId": 5, "selectedOption": "B" },
    { "questionId": 6, "selectedOption": "B" },
    { "questionId": 7, "selectedOption": "B" },
    { "questionId": 8, "selectedOption": "B" }
  ]
}
```

## 🔧 **Troubleshooting**

### ❌ **Common Issues:**

1. **Connection Refused**

   - ✅ Make sure application is running: `ps aux | grep "education-0.0.1-SNAPSHOT.jar"`
   - ✅ Check port 8080 is free: `lsof -i :8080`

2. **401 Unauthorized**

   - ✅ Make sure you're using the correct token
   - ✅ Check if token is properly set in Authorization header

3. **Internal Server Error (500)**

   - ✅ Check application logs: `tail -f app.log`
   - ✅ Verify database connection

4. **OTP Not Working**
   - ✅ Check logs for OTP: `grep "OTP for" app.log`
   - ✅ Use the exact phone number you sent OTP to

## 📊 **Expected Response Formats**

### Success Response:

```json
{
    "success": true,
    "message": "Operation successful",
    "data": { ... }
}
```

### Error Response:

```json
{
  "success": false,
  "message": "Error description"
}
```

### Quiz Result Response:

```json
{
  "success": true,
  "message": "Quiz submitted successfully. Stream recommendations generated!",
  "data": {
    "attemptId": null,
    "quizId": 1,
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
    "recommendedColleges": [
      "Government First Grade College",
      "Government Diploma Institute"
    ],
    "collegeTier": "Premier",
    "performanceLevel": "Excellent"
  }
}
```

## 🎯 **Next Steps After Testing**

1. ✅ **Authentication flow** - COMPLETED & TESTED
2. ✅ **Profile management** - COMPLETED & TESTED
3. ✅ **Quiz Engine with Stream Recommendations** - COMPLETED & TESTED
4. 🔄 **College Directory implementation** - NEXT PRIORITY
5. 🔄 **Course-Career Mapping** - UPCOMING
6. 🔄 **Notification System** - UPCOMING

### 🏆 **Major Achievements:**

- ✅ Core SIH problem statement solved with working aptitude assessment
- ✅ Stream recommendations (Science/Commerce/Arts) with AI algorithms
- ✅ Guest mode for immediate access without registration
- ✅ Complete authentication and profile system
- ✅ Real-time quiz scoring and college tier recommendations

---

**🚀 Happy Testing!**

The MVP core features are fully implemented and ready for comprehensive testing!
