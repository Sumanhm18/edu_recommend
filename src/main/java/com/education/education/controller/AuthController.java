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
            String email = request.getEmail();

            // Validate email format using OtpService validation
            if (!otpService.isValidEmail(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid email format"));
            }

            boolean sent = otpService.sendOtp(email);

            if (sent) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP sent successfully to " + email));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("success", false, "message", "Failed to send OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @PostMapping("/send-registration-otp")
    public ResponseEntity<?> sendRegistrationOtp(@RequestBody OtpRequestDto request) {
        try {
            String email = request.getEmail();

            // Validate email format
            if (!otpService.isValidEmail(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid email format"));
            }

            // Check if user already exists
            User existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User already exists with this email"));
            }

            boolean sent = otpService.sendOtp(email);

            if (sent) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Registration OTP sent successfully to " + email));
            } else {
                return ResponseEntity.internalServerError()
                        .body(Map.of("success", false, "message", "Failed to send registration OTP"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyDto request) {
        try {
            String email = request.getEmail();
            String otp = request.getOtp();

            if (email == null || otp == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email and OTP are required"));
            }

            boolean isValid = otpService.validateOtp(email, otp);

            if (isValid) {
                // Create or get user by email
                User user = userService.createOrGetUserByEmail(email, null, null, null, null, null);

                // Generate JWT token with user ID
                String token = jwtTokenUtil.generateTokenForUser(email, user.getUserId());

                AuthResponseDto response = new AuthResponseDto(
                        token,
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
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
            String email = request.get("email");
            String password = request.get("password");
            String district = request.get("district");
            String className = request.get("className");
            String otp = request.get("otp");

            // Validate required fields
            if (name == null || email == null || password == null || otp == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Name, email, password, and OTP are required"));
            }

            // Validate email format
            if (!otpService.isValidEmail(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid email format"));
            }

            // Verify OTP first
            boolean isValidOtp = otpService.validateOtp(email, otp);
            if (!isValidOtp) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid or expired OTP. Please request a new OTP."));
            }

            // Check if user already exists
            User existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User already exists with this email"));
            }

            // Create new user after OTP verification
            User user = userService.createOrGetUserByEmail(email, name, null, district, className, password);

            // Generate JWT token
            String token = jwtTokenUtil.generateTokenForUser(email, user.getUserId());

            AuthResponseDto response = new AuthResponseDto(
                    token,
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    false);

            System.out.println("✅ User registered successfully with email verification: " + email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User registered successfully with verified email",
                    "data", response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Email and password are required"));
            }

            // Find user by email
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User not found"));
            }

            // Verify password
            boolean isValidPassword = userService.verifyPassword(user, password);
            if (!isValidPassword) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Invalid password"));
            }

            // Generate JWT token
            String token = jwtTokenUtil.generateTokenForUser(email, user.getUserId());

            AuthResponseDto response = new AuthResponseDto(
                    token,
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
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