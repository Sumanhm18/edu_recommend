package com.education.education.controller;

import com.education.education.config.JwtTokenUtil;
import com.education.education.dto.ProfileRequestDto;
import com.education.education.dto.ProfileResponseDto;
import com.education.education.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    /**
     * Get current user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid token or user ID not found"));
            }
            
            ProfileResponseDto profile = profileService.getProfile(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile retrieved successfully",
                "data", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving profile: " + e.getMessage()));
        }
    }
    
    /**
     * Create or update current user's profile
     */
    @PostMapping("/me")
    public ResponseEntity<?> createOrUpdateProfile(@Valid @RequestBody ProfileRequestDto profileRequest, 
                                                   HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid token or user ID not found"));
            }
            
            ProfileResponseDto profile = profileService.createOrUpdateProfile(userId, profileRequest);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "data", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error updating profile: " + e.getMessage()));
        }
    }
    
    /**
     * Update specific fields in current user's profile
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileRequestDto profileRequest, 
                                          HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid token or user ID not found"));
            }
            
            ProfileResponseDto profile = profileService.updateProfile(userId, profileRequest);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "data", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error updating profile: " + e.getMessage()));
        }
    }
    
    /**
     * Check if current user's profile is complete
     */
    @GetMapping("/me/complete")
    public ResponseEntity<?> isProfileComplete(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid token or user ID not found"));
            }
            
            Boolean isComplete = profileService.isProfileComplete(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("isComplete", isComplete);
            data.put("userId", userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", isComplete ? "Profile is complete" : "Profile is incomplete",
                "data", data
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error checking profile: " + e.getMessage()));
        }
    }
    
    /**
     * Delete current user's profile
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid token or user ID not found"));
            }
            
            profileService.deleteProfile(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error deleting profile: " + e.getMessage()));
        }
    }
    
    /**
     * Admin endpoint: Get user profile by ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            ProfileResponseDto profile = profileService.getProfile(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile retrieved successfully",
                "data", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error retrieving profile: " + e.getMessage()));
        }
    }
    
    /**
     * Get available options for profile fields
     */
    @GetMapping("/options")
    public ResponseEntity<?> getProfileOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("classLevels", new String[]{"10th", "12th", "Degree", "Diploma"});
        options.put("languages", new String[]{"English", "Hindi", "Kannada", "Tamil", "Telugu"});
        options.put("districts", new String[]{
            "Bangalore Urban", "Bangalore Rural", "Mysore", "Tumkur", "Mandya", 
            "Hassan", "Shimoga", "Chitradurga", "Davangere", "Bellary", 
            "Bagalkot", "Bijapur", "Gulbarga", "Raichur", "Bidar",
            "Belgaum", "Dharwad", "Gadag", "Haveri", "Uttara Kannada",
            "Udupi", "Dakshina Kannada", "Kodagu", "Chikmagalur", "Chamarajanagar",
            "Kolar", "Chikballapur", "Ramanagara", "Yadgir", "Koppal"
        });
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Profile options retrieved successfully",
            "data", options
        ));
    }
    
    /**
     * Extract user ID from JWT token
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Check if it's a guest token
                if (jwtTokenUtil.isGuestToken(token)) {
                    return null; // Guest users don't have profiles
                }
                
                // Get user ID from token
                return jwtTokenUtil.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            // Log error but don't expose details
        }
        return null;
    }
}