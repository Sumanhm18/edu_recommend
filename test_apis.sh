#!/bin/bash

echo "Testing Education MVP Backend APIs"
echo "=================================="

# Wait for application to start
echo "Waiting for application to start..."
sleep 5

# Test 1: Basic health check
echo "1. Testing auth test endpoint..."
response=$(curl -s -X GET "http://localhost:8080/api/auth/test")
echo "Response: $response"
echo ""

# Test 2: Guest login
echo "2. Testing guest login..."
guest_response=$(curl -s -X POST "http://localhost:8080/api/auth/guest-login" \
  -H "Content-Type: application/json")
echo "Guest login response: $guest_response"
echo ""

# Test 3: Send OTP (with a valid Indian phone number)
echo "3. Testing send OTP..."
otp_response=$(curl -s -X POST "http://localhost:8080/api/auth/send-otp" \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"9876543210"}')
echo "Send OTP response: $otp_response"
echo ""

# Test 4: Database check - try to access a protected endpoint
echo "4. Testing protected endpoint (should return 401)..."
protected_response=$(curl -s -X GET "http://localhost:8080/api/admin/test" -w "HTTP_CODE:%{http_code}")
echo "Protected endpoint response: $protected_response"
echo ""

echo "Testing completed!"