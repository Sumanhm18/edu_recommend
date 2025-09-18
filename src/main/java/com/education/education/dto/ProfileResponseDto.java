package com.education.education.dto;

import java.time.LocalDateTime;

public class ProfileResponseDto {

    private Long userId;
    private String name;
    private String phone;
    private String classLevel;
    private String district;
    private String preferredLanguage;
    private Boolean isGuest;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isProfileComplete;

    // Constructors
    public ProfileResponseDto() {
    }

    public ProfileResponseDto(Long userId, String name, String phone, String classLevel,
            String district, String preferredLanguage, Boolean isGuest,
            String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.classLevel = classLevel;
        this.district = district;
        this.preferredLanguage = preferredLanguage;
        this.isGuest = isGuest;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isProfileComplete = isProfileComplete();
    }

    // Check if profile is complete
    private Boolean isProfileComplete() {
        return name != null && !name.trim().isEmpty() &&
                classLevel != null && !classLevel.trim().isEmpty() &&
                district != null && !district.trim().isEmpty() &&
                preferredLanguage != null && !preferredLanguage.trim().isEmpty();
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.isProfileComplete = isProfileComplete();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(String classLevel) {
        this.classLevel = classLevel;
        this.isProfileComplete = isProfileComplete();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
        this.isProfileComplete = isProfileComplete();
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        this.isProfileComplete = isProfileComplete();
    }

    public Boolean getIsGuest() {
        return isGuest;
    }

    public void setIsGuest(Boolean isGuest) {
        this.isGuest = isGuest;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsProfileComplete() {
        return isProfileComplete;
    }

    public void setIsProfileComplete(Boolean isProfileComplete) {
        this.isProfileComplete = isProfileComplete;
    }
}