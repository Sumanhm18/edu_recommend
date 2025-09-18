package com.education.education.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_paths")
public class CareerPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "career_title", nullable = false)
    private String careerTitle;

    @Column(name = "jobs_json", columnDefinition = "TEXT")
    private String jobsJson;

    @Column(name = "higher_studies_json", columnDefinition = "TEXT")
    private String higherStudiesJson;

    @Column(name = "exams_json", columnDefinition = "TEXT")
    private String examsJson;

    @Column(name = "salary_range")
    private String salaryRange;

    @Column(name = "job_market_demand")
    private String jobMarketDemand; // High, Medium, Low

    @Column(name = "skills_required", columnDefinition = "TEXT")
    private String skillsRequired;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public CareerPath() {
    }

    public CareerPath(Course course, String careerTitle, String jobsJson, String higherStudiesJson, String examsJson) {
        this.course = course;
        this.careerTitle = careerTitle;
        this.jobsJson = jobsJson;
        this.higherStudiesJson = higherStudiesJson;
        this.examsJson = examsJson;
    }

    // Getters and Setters
    public Long getCareerId() {
        return careerId;
    }

    public void setCareerId(Long careerId) {
        this.careerId = careerId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCareerTitle() {
        return careerTitle;
    }

    public void setCareerTitle(String careerTitle) {
        this.careerTitle = careerTitle;
    }

    public String getJobsJson() {
        return jobsJson;
    }

    public void setJobsJson(String jobsJson) {
        this.jobsJson = jobsJson;
    }

    public String getHigherStudiesJson() {
        return higherStudiesJson;
    }

    public void setHigherStudiesJson(String higherStudiesJson) {
        this.higherStudiesJson = higherStudiesJson;
    }

    public String getExamsJson() {
        return examsJson;
    }

    public void setExamsJson(String examsJson) {
        this.examsJson = examsJson;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getJobMarketDemand() {
        return jobMarketDemand;
    }

    public void setJobMarketDemand(String jobMarketDemand) {
        this.jobMarketDemand = jobMarketDemand;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}