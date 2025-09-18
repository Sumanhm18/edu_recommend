package com.education.education.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "degree_type")
    private String degreeType; // UG, PG, Diploma, Certificate

    @Column(name = "stream")
    private String stream; // Arts, Science, Commerce, Vocational

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    private Integer intake;

    @Column(name = "medium_of_instruction")
    private String mediumOfInstruction;

    @Column(name = "duration_years")
    private Integer durationYears;

    @Column(name = "fees_per_year")
    private Integer feesPerYear;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CareerPath> careerPaths;

    // Constructors
    public Course() {
    }

    public Course(College college, String courseName, String degreeType, String stream) {
        this.college = college;
        this.courseName = courseName;
        this.degreeType = degreeType;
        this.stream = stream;
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public Integer getIntake() {
        return intake;
    }

    public void setIntake(Integer intake) {
        this.intake = intake;
    }

    public String getMediumOfInstruction() {
        return mediumOfInstruction;
    }

    public void setMediumOfInstruction(String mediumOfInstruction) {
        this.mediumOfInstruction = mediumOfInstruction;
    }

    public Integer getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(Integer durationYears) {
        this.durationYears = durationYears;
    }

    public Integer getFeesPerYear() {
        return feesPerYear;
    }

    public void setFeesPerYear(Integer feesPerYear) {
        this.feesPerYear = feesPerYear;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Set<CareerPath> getCareerPaths() {
        return careerPaths;
    }

    public void setCareerPaths(Set<CareerPath> careerPaths) {
        this.careerPaths = careerPaths;
    }
}