package com.education.education.dto.college;

public class CollegeSearchDto {
    private Long collegeId;
    private String name;
    private String district;
    private String state;
    private String collegeType; // Government, Private, Aided
    private String collegeTier; // Premier, Excellent, Good, Foundation
    private String streamsOffered; // Science,Commerce,Arts
    private Integer establishmentYear;
    private String website;
    private Double placementRate;
    
    // Constructors
    public CollegeSearchDto() {}
    
    public CollegeSearchDto(Long collegeId, String name, String district, String state, 
                          String collegeType, String collegeTier, String streamsOffered) {
        this.collegeId = collegeId;
        this.name = name;
        this.district = district;
        this.state = state;
        this.collegeType = collegeType;
        this.collegeTier = collegeTier;
        this.streamsOffered = streamsOffered;
    }
    
    // Getters and Setters
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCollegeType() { return collegeType; }
    public void setCollegeType(String collegeType) { this.collegeType = collegeType; }
    
    public String getCollegeTier() { return collegeTier; }
    public void setCollegeTier(String collegeTier) { this.collegeTier = collegeTier; }
    
    public String getStreamsOffered() { return streamsOffered; }
    public void setStreamsOffered(String streamsOffered) { this.streamsOffered = streamsOffered; }
    
    public Integer getEstablishmentYear() { return establishmentYear; }
    public void setEstablishmentYear(Integer establishmentYear) { this.establishmentYear = establishmentYear; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public Double getPlacementRate() { return placementRate; }
    public void setPlacementRate(Double placementRate) { this.placementRate = placementRate; }
}