package com.education.education.dto.college;

public class CollegeDetailsDto {
    private Long collegeId;
    private String name;
    private String district;
    private String state;
    private String pincode;
    private String address;
    private String collegeType; // Government, Private, Aided
    private String collegeTier; // Premier, Excellent, Good, Foundation
    private String streamsOffered; // Science,Commerce,Arts
    private String coursesOffered; // JSON string of courses
    private String facilities; // JSON string of facilities
    private Integer establishmentYear;
    private String affiliation;
    private String website;
    private String phone;
    private String email;
    private String feesRange;
    private Double placementRate;
    private String contactInfo;
    
    // Constructors
    public CollegeDetailsDto() {}
    
    // Getters and Setters
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCollegeType() { return collegeType; }
    public void setCollegeType(String collegeType) { this.collegeType = collegeType; }
    
    public String getCollegeTier() { return collegeTier; }
    public void setCollegeTier(String collegeTier) { this.collegeTier = collegeTier; }
    
    public String getStreamsOffered() { return streamsOffered; }
    public void setStreamsOffered(String streamsOffered) { this.streamsOffered = streamsOffered; }
    
    public String getCoursesOffered() { return coursesOffered; }
    public void setCoursesOffered(String coursesOffered) { this.coursesOffered = coursesOffered; }
    
    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    
    public Integer getEstablishmentYear() { return establishmentYear; }
    public void setEstablishmentYear(Integer establishmentYear) { this.establishmentYear = establishmentYear; }
    
    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFeesRange() { return feesRange; }
    public void setFeesRange(String feesRange) { this.feesRange = feesRange; }
    
    public Double getPlacementRate() { return placementRate; }
    public void setPlacementRate(Double placementRate) { this.placementRate = placementRate; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}