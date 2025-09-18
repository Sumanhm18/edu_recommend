package com.education.education.service;

import com.education.education.dto.ProfileRequestDto;
import com.education.education.dto.ProfileResponseDto;
import com.education.education.entity.User;
import com.education.education.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get user profile by user ID
     */
    public ProfileResponseDto getProfile(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        return convertToProfileResponseDto(user);
    }

    /**
     * Get user profile by phone number
     */
    public ProfileResponseDto getProfileByPhone(String phone) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with phone: " + phone);
        }

        User user = userOptional.get();
        return convertToProfileResponseDto(user);
    }

    /**
     * Create or update user profile
     */
    public ProfileResponseDto createOrUpdateProfile(Long userId, ProfileRequestDto profileRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Update profile fields
        user.setName(profileRequest.getName());
        user.setClassLevel(profileRequest.getClassLevel());
        user.setDistrict(profileRequest.getDistrict());
        user.setPreferredLanguage(profileRequest.getPreferredLanguage());

        // Save updated user
        User savedUser = userRepository.save(user);

        return convertToProfileResponseDto(savedUser);
    }

    /**
     * Update specific profile fields
     */
    public ProfileResponseDto updateProfile(Long userId, ProfileRequestDto profileRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Update only non-null fields
        if (profileRequest.getName() != null && !profileRequest.getName().trim().isEmpty()) {
            user.setName(profileRequest.getName());
        }
        if (profileRequest.getClassLevel() != null && !profileRequest.getClassLevel().trim().isEmpty()) {
            user.setClassLevel(profileRequest.getClassLevel());
        }
        if (profileRequest.getDistrict() != null && !profileRequest.getDistrict().trim().isEmpty()) {
            user.setDistrict(profileRequest.getDistrict());
        }
        if (profileRequest.getPreferredLanguage() != null && !profileRequest.getPreferredLanguage().trim().isEmpty()) {
            user.setPreferredLanguage(profileRequest.getPreferredLanguage());
        }

        // Save updated user
        User savedUser = userRepository.save(user);

        return convertToProfileResponseDto(savedUser);
    }

    /**
     * Check if user profile is complete
     */
    public Boolean isProfileComplete(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        return user.getName() != null && !user.getName().trim().isEmpty() &&
                user.getClassLevel() != null && !user.getClassLevel().trim().isEmpty() &&
                user.getDistrict() != null && !user.getDistrict().trim().isEmpty() &&
                user.getPreferredLanguage() != null && !user.getPreferredLanguage().trim().isEmpty();
    }

    /**
     * Delete user profile (soft delete by marking as inactive)
     */
    public void deleteProfile(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // For now, we'll actually delete. In production, you might want soft delete
        userRepository.deleteById(userId);
    }

    /**
     * Convert User entity to ProfileResponseDto
     */
    private ProfileResponseDto convertToProfileResponseDto(User user) {
        return new ProfileResponseDto(
                user.getUserId(),
                user.getName(),
                user.getPhone(),
                user.getClassLevel(),
                user.getDistrict(),
                user.getPreferredLanguage(),
                user.getIsGuest(),
                user.getRole().toString(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}