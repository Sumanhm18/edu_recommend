package com.education.education.controller;

import com.education.education.config.JwtTokenUtil;
import com.education.education.dto.AuthResponseDto;
import com.education.education.dto.OtpRequestDto;
import com.education.education.dto.OtpVerifyDto;
import com.education.education.entity.User;
import com.education.education.service.OtpService;
import com.education.education.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequestDto request) {
        try {
            String phoneNumber = request.getPhoneNumber();

            // Validate phone number format
            if (phoneNumber == null || !phoneNumber.matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid phone number format"));
            }

            boolean sent = otpService.sendOtp(phoneNumber);

            if (sent) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP sent successfully to " + phoneNumber));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("success", false, "message", "Failed to send OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDto request) {
        try {
            String phoneNumber = request.getPhoneNumber();
            String otp = request.getOtp();

            if (phoneNumber == null || otp == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Phone number and OTP are required"));
            }

            boolean isValid = otpService.validateOtp(phoneNumber, otp);

            if (isValid) {
                // Create or get user
                User user = userService.createOrGetUser(phoneNumber, null, null, null, null);

                // Generate JWT token with user ID
                String token = jwtTokenUtil.generateTokenForUser(phoneNumber, user.getUserId());

                AuthResponseDto response = new AuthResponseDto(
                        token,
                        user.getUserId(),
                        user.getName(),
                        user.getPhone(),
                        false);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP verified successfully",
                        "data", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid or expired OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @PostMapping("/guest-login")
    public ResponseEntity<?> guestLogin() {
        try {
            String guestId = "guest_" + UUID.randomUUID().toString();
            String token = jwtTokenUtil.generateTokenForGuest(guestId);

            AuthResponseDto response = new AuthResponseDto(
                    token,
                    null,
                    "Guest User",
                    null,
                    true);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Guest login successful",
                    "data", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String phone = request.get("phone");
            String password = request.get("password");
            String location = request.get("location");

            // Validate required fields
            if (name == null || phone == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Name, phone, and password are required"));
            }

            // Validate phone format
            if (!phone.matches("^[6-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid phone number format"));
            }

            // Check if user already exists
            User existingUser = userService.findByPhone(phone);
            if (existingUser != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User already exists with this phone number"));
            }

            // Create new user
            User user = userService.createOrGetUser(phone, name, location, null, null, password);

            // Generate JWT token
            String token = jwtTokenUtil.generateTokenForUser(phone, user.getUserId());

            AuthResponseDto response = new AuthResponseDto(
                    token,
                    user.getUserId(),
                    user.getName(),
                    user.getPhone(),
                    false);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User registered successfully",
                    "data", response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String password = request.get("password");

            if (phone == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Phone and password are required"));
            }

            // Find user by phone
            User user = userService.findByPhone(phone);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User not found"));
            }

            // Verify password (you'll need to implement this in UserService)
            boolean isValidPassword = userService.verifyPassword(user, password);
            if (!isValidPassword) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid password"));
            }

            // Generate JWT token
            String token = jwtTokenUtil.generateTokenForUser(phone, user.getUserId());

            AuthResponseDto response = new AuthResponseDto(
                    token,
                    user.getUserId(),
                    user.getName(),
                    user.getPhone(),
                    false);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "data", response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Login failed: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Auth service is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}