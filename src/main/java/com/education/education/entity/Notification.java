package com.education.education.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "notification_type")
    private String notificationType; // ADMISSION_DEADLINE, SCHOLARSHIP, EXAM_ALERT, GENERAL
    
    @Column(name = "target_user_id")
    private Long targetUserId; // null for broadcast notifications
    
    @Column(name = "target_class")
    private String targetClass; // 10th, 12th, all
    
    @Column(name = "target_district")
    private String targetDistrict; // null for all districts
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "is_sent")
    private Boolean isSent = false;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Notification() {}
    
    public Notification(String title, String body, String notificationType, LocalDateTime scheduledTime) {
        this.title = title;
        this.body = body;
        this.notificationType = notificationType;
        this.scheduledTime = scheduledTime;
    }
    
    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    
    public Long getTargetUserId() {
        return targetUserId;
    }
    
    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
    
    public String getTargetClass() {
        return targetClass;
    }
    
    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }
    
    public String getTargetDistrict() {
        return targetDistrict;
    }
    
    public void setTargetDistrict(String targetDistrict) {
        this.targetDistrict = targetDistrict;
    }
    
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public Boolean getIsSent() {
        return isSent;
    }
    
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
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
}