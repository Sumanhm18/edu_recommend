package com.education.education.service;

import com.education.education.entity.College;
import com.education.education.repository.CollegeRepository;
import com.education.education.dto.college.CollegeDetailsDto;
import com.education.education.dto.college.CollegeSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollegeService {
    
    @Autowired
    private CollegeRepository collegeRepository;
    
    /**
     * Search colleges by district
     */
    public List<CollegeSearchDto> findCollegesByDistrict(String district) {
        List<College> colleges = collegeRepository.findByDistrictIgnoreCaseAndIsActiveTrue(district);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search colleges by state
     */
    public List<CollegeSearchDto> findCollegesByState(String state) {
        List<College> colleges = collegeRepository.findByStateIgnoreCaseAndIsActiveTrue(state);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search colleges by district and state
     */
    public List<CollegeSearchDto> findCollegesByLocation(String district, String state) {
        List<College> colleges = collegeRepository.findByDistrictIgnoreCaseAndStateIgnoreCaseAndIsActiveTrue(district, state);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search colleges by type (Government, Private, etc.)
     */
    public List<CollegeSearchDto> findCollegesByType(String collegeType) {
        List<College> colleges = collegeRepository.findByCollegeTypeIgnoreCaseAndIsActiveTrue(collegeType);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search government colleges by district
     */
    public List<CollegeSearchDto> findGovernmentCollegesByDistrict(String district) {
        List<College> colleges = collegeRepository.findGovernmentCollegesByDistrict(district);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search government colleges by state
     */
    public List<CollegeSearchDto> findGovernmentCollegesByState(String state) {
        List<College> colleges = collegeRepository.findGovernmentCollegesByState(state);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search colleges by stream (Science, Commerce, Arts)
     */
    public List<CollegeSearchDto> findCollegesByStream(String stream) {
        List<College> colleges = collegeRepository.findByStreamOffered(stream);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search colleges by district and stream
     */
    public List<CollegeSearchDto> findCollegesByDistrictAndStream(String district, String stream) {
        List<College> colleges = collegeRepository.findByDistrictAndStreamOffered(district, stream);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Search colleges by state and stream
     */
    public List<CollegeSearchDto> findCollegesByStateAndStream(String state, String stream) {
        List<College> colleges = collegeRepository.findByStateAndStreamOffered(state, stream);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Advanced search with multiple criteria
     */
    public List<CollegeSearchDto> searchColleges(String district, String state, String collegeType, String stream) {
        List<College> colleges = collegeRepository.findCollegesByCriteria(district, state, collegeType, stream);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Find colleges by name (partial match)
     */
    public List<CollegeSearchDto> findCollegesByName(String name) {
        List<College> colleges = collegeRepository.findByNameContainingIgnoreCase(name);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get detailed college information by ID
     */
    public Optional<CollegeDetailsDto> getCollegeDetails(Long collegeId) {
        Optional<College> college = collegeRepository.findById(collegeId);
        return college.map(this::convertToDetailsDto);
    }
    
    /**
     * Get top colleges by district (Premier and Excellent tier)
     */
    public List<CollegeSearchDto> getTopCollegesByDistrict(String district) {
        List<College> colleges = collegeRepository.findTopCollegesByDistrict(district);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get colleges with high placement rates
     */
    public List<CollegeSearchDto> getHighPlacementColleges(Double minPlacementRate) {
        List<College> colleges = collegeRepository.findCollegesWithHighPlacementRate(minPlacementRate);
        return colleges.stream()
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get college recommendations based on stream and location
     * This method will be used by the quiz recommendation engine
     */
    public List<CollegeSearchDto> getRecommendedColleges(String recommendedStream, String userDistrict, String performanceLevel) {
        // Start with colleges in user's district that offer the recommended stream
        List<College> colleges = collegeRepository.findByDistrictAndStreamOffered(userDistrict, recommendedStream);
        
        // If no colleges found in district, expand to state
        if (colleges.isEmpty() && userDistrict != null) {
            // We need to get state from district - for now use generic search
            colleges = collegeRepository.findByStreamOffered(recommendedStream);
        }
        
        // Filter by performance level if specified
        if ("Excellent".equals(performanceLevel)) {
            colleges = colleges.stream()
                    .filter(c -> "Premier".equals(c.getCollegeTier()) || "Excellent".equals(c.getCollegeTier()))
                    .collect(Collectors.toList());
        } else if ("Good".equals(performanceLevel)) {
            colleges = colleges.stream()
                    .filter(c -> !"Foundation".equals(c.getCollegeTier()))
                    .collect(Collectors.toList());
        }
        
        // Limit to top 10 recommendations
        return colleges.stream()
                .limit(10)
                .map(this::convertToSearchDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert College entity to CollegeSearchDto
     */
    private CollegeSearchDto convertToSearchDto(College college) {
        CollegeSearchDto dto = new CollegeSearchDto();
        dto.setCollegeId(college.getCollegeId());
        dto.setName(college.getName());
        dto.setDistrict(college.getDistrict());
        dto.setState(college.getState());
        dto.setCollegeType(college.getCollegeType());
        dto.setCollegeTier(college.getCollegeTier());
        dto.setStreamsOffered(college.getStreamsOffered());
        dto.setEstablishmentYear(college.getEstablishmentYear());
        dto.setWebsite(college.getWebsite());
        dto.setPlacementRate(college.getPlacementRate());
        return dto;
    }
    
    /**
     * Convert College entity to CollegeDetailsDto
     */
    private CollegeDetailsDto convertToDetailsDto(College college) {
        CollegeDetailsDto dto = new CollegeDetailsDto();
        dto.setCollegeId(college.getCollegeId());
        dto.setName(college.getName());
        dto.setDistrict(college.getDistrict());
        dto.setState(college.getState());
        dto.setPincode(college.getPincode());
        dto.setAddress(college.getAddress());
        dto.setCollegeType(college.getCollegeType());
        dto.setCollegeTier(college.getCollegeTier());
        dto.setStreamsOffered(college.getStreamsOffered());
        dto.setCoursesOffered(college.getCoursesOffered());
        dto.setFacilities(college.getFacilitiesJson());
        dto.setEstablishmentYear(college.getEstablishmentYear());
        dto.setAffiliation(college.getAffiliation());
        dto.setWebsite(college.getWebsite());
        dto.setPhone(college.getPhone());
        dto.setEmail(college.getEmail());
        dto.setFeesRange(college.getFeesRange());
        dto.setPlacementRate(college.getPlacementRate());
        dto.setContactInfo(college.getContactInfo());
        return dto;
    }
}