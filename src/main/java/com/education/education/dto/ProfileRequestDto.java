package com.education.education.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ProfileRequestDto {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Class level is required")
    @Pattern(regexp = "^(10th|12th|Degree|Diploma)$", message = "Class level must be 10th, 12th, Degree, or Diploma")
    private String classLevel;
    
    @NotBlank(message = "District is required")
    private String district;
    
    @NotBlank(message = "Preferred language is required")
    @Pattern(regexp = "^(English|Hindi|Kannada|Tamil|Telugu)$", message = "Language must be English, Hindi, Kannada, Tamil, or Telugu")
    private String preferredLanguage;
    
    // Constructors
    public ProfileRequestDto() {}
    
    public ProfileRequestDto(String name, String classLevel, String district, String preferredLanguage) {
        this.name = name;
        this.classLevel = classLevel;
        this.district = district;
        this.preferredLanguage = preferredLanguage;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getClassLevel() {
        return classLevel;
    }
    
    public void setClassLevel(String classLevel) {
        this.classLevel = classLevel;
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getPreferredLanguage() {
        return preferredLanguage;
    }
    
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}