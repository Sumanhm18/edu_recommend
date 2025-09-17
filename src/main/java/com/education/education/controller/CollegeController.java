package com.education.education.controller;

import com.education.education.dto.college.CollegeSearchDto;
import com.education.education.dto.college.CollegeDetailsDto;
import com.education.education.service.CollegeService;
import com.education.education.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/college")
@CrossOrigin(origins = "*")
public class CollegeController {
    
    @Autowired
    private CollegeService collegeService;
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * Search colleges by district
     * GET /api/college/search/district/{district}
     */
    @GetMapping("/search/district/{district}")
    public ResponseEntity<?> searchByDistrict(@PathVariable String district) {
        try {
            List<CollegeSearchDto> colleges = collegeService.findCollegesByDistrict(district);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Colleges found successfully for district: " + district,
                "data", colleges
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error searching colleges by district: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Search colleges by state
     * GET /api/college/search/state/{state}
     */
    @GetMapping("/search/state/{state}")
    public ResponseEntity<?> searchByState(@PathVariable String state) {
        try {
            List<CollegeSearchDto> colleges = collegeService.findCollegesByState(state);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Colleges found successfully for state: " + state,
                "data", colleges
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error searching colleges by state: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Search government colleges by district
     * GET /api/college/government/district/{district}
     */
    @GetMapping("/government/district/{district}")
    public ResponseEntity<?> searchGovernmentByDistrict(@PathVariable String district) {
        try {
            List<CollegeSearchDto> colleges = collegeService.findGovernmentCollegesByDistrict(district);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Government colleges found successfully for district: " + district,
                "data", colleges
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error searching government colleges: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Search colleges by stream (Science, Commerce, Arts)
     * GET /api/college/search/stream/{stream}
     */
    @GetMapping("/search/stream/{stream}")
    public ResponseEntity<?> searchByStream(@PathVariable String stream) {
        try {
            List<CollegeSearchDto> colleges = collegeService.findCollegesByStream(stream);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Colleges found successfully for stream: " + stream,
                "data", colleges
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error searching colleges by stream: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Advanced search with multiple parameters
     * GET /api/college/search?district=X&state=Y&type=Z&stream=W
     */
    @GetMapping("/search")
    public ResponseEntity<?> advancedSearch(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String stream) {
        try {
            List<CollegeSearchDto> colleges = collegeService.searchColleges(district, state, type, stream);
            
            String message = "Colleges found successfully";
            if (district != null || state != null || type != null || stream != null) {
                message += " with filters applied";
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message,
                "data", colleges
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error in advanced college search: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get detailed college information by ID
     * GET /api/college/details/{id}
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getCollegeDetails(@PathVariable Long id) {
        try {
            Optional<CollegeDetailsDto> college = collegeService.getCollegeDetails(id);
            
            if (college.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "College details retrieved successfully",
                    "data", college.get()
                ));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "message", "College not found with ID: " + id
                    ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error retrieving college details: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get college recommendations based on stream and location
     * This will be used by the quiz recommendation engine
     * GET /api/college/recommendations?stream=X&district=Y&performance=Z
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> getCollegeRecommendations(
            @RequestParam String stream,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String performance) {
        try {
            List<CollegeSearchDto> colleges = collegeService.getRecommendedColleges(stream, district, performance);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "College recommendations generated successfully for " + stream + " stream",
                "data", colleges
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error generating college recommendations: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Compare colleges by IDs
     */
    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareColleges(@RequestBody List<Long> collegeIds) {
        try {
            Map<String, Object> comparison = recommendationService.getCollegeComparisonData(collegeIds);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Colleges comparison retrieved successfully",
                "data", comparison
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error comparing colleges: " + e.getMessage()
            ));
        }
    }
}