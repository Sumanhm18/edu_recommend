package com.education.education.repository;

import com.education.education.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    // Find colleges by district
    List<College> findByDistrictIgnoreCaseAndIsActiveTrue(String district);

    // Find colleges by state
    List<College> findByStateIgnoreCaseAndIsActiveTrue(String state);

    // Find colleges by district and state
    List<College> findByDistrictIgnoreCaseAndStateIgnoreCaseAndIsActiveTrue(String district, String state);

    // Find colleges by type (Government, Private, etc.)
    List<College> findByCollegeTypeIgnoreCaseAndIsActiveTrue(String collegeType);

    // Find colleges by tier (Premier, Excellent, etc.)
    List<College> findByCollegeTierIgnoreCaseAndIsActiveTrue(String collegeTier);

    // Find colleges by district and type
    List<College> findByDistrictIgnoreCaseAndCollegeTypeIgnoreCaseAndIsActiveTrue(String district, String collegeType);

    // Find colleges by state and type
    List<College> findByStateIgnoreCaseAndCollegeTypeIgnoreCaseAndIsActiveTrue(String state, String collegeType);

    // Find colleges offering specific stream
    @Query("SELECT c FROM College c WHERE c.streamsOffered LIKE %:stream% AND c.isActive = true")
    List<College> findByStreamOffered(@Param("stream") String stream);

    // Find colleges by district and stream
    @Query("SELECT c FROM College c WHERE c.district LIKE %:district% AND c.streamsOffered LIKE %:stream% AND c.isActive = true")
    List<College> findByDistrictAndStreamOffered(@Param("district") String district, @Param("stream") String stream);

    // Find colleges by state and stream
    @Query("SELECT c FROM College c WHERE c.state LIKE %:state% AND c.streamsOffered LIKE %:stream% AND c.isActive = true")
    List<College> findByStateAndStreamOffered(@Param("state") String state, @Param("stream") String stream);

    // Search colleges by name
    @Query("SELECT c FROM College c WHERE c.name LIKE %:name% AND c.isActive = true")
    List<College> findByNameContainingIgnoreCase(@Param("name") String name);

    // Find government colleges by district
    @Query("SELECT c FROM College c WHERE c.district LIKE %:district% AND c.collegeType = 'Government' AND c.isActive = true")
    List<College> findGovernmentCollegesByDistrict(@Param("district") String district);

    // Find government colleges by state
    @Query("SELECT c FROM College c WHERE c.state LIKE %:state% AND c.collegeType = 'Government' AND c.isActive = true")
    List<College> findGovernmentCollegesByState(@Param("state") String state);

    // Find colleges with high placement rates
    @Query("SELECT c FROM College c WHERE c.placementRate >= :minPlacementRate AND c.isActive = true ORDER BY c.placementRate DESC")
    List<College> findCollegesWithHighPlacementRate(@Param("minPlacementRate") Double minPlacementRate);

    // Find top colleges by tier and district
    @Query("SELECT c FROM College c WHERE c.district LIKE %:district% AND c.collegeTier IN ('Premier', 'Excellent') AND c.isActive = true ORDER BY c.collegeTier ASC")
    List<College> findTopCollegesByDistrict(@Param("district") String district);

    // Advanced search combining multiple criteria
    @Query("SELECT c FROM College c WHERE " +
            "(:district IS NULL OR c.district LIKE %:district%) AND " +
            "(:state IS NULL OR c.state LIKE %:state%) AND " +
            "(:collegeType IS NULL OR c.collegeType LIKE %:collegeType%) AND " +
            "(:stream IS NULL OR c.streamsOffered LIKE %:stream%) AND " +
            "c.isActive = true " +
            "ORDER BY " +
            "CASE c.collegeTier " +
            "WHEN 'Premier' THEN 1 " +
            "WHEN 'Excellent' THEN 2 " +
            "WHEN 'Good' THEN 3 " +
            "WHEN 'Foundation' THEN 4 " +
            "ELSE 5 END")
    List<College> findCollegesByCriteria(
            @Param("district") String district,
            @Param("state") String state,
            @Param("collegeType") String collegeType,
            @Param("stream") String stream);
}