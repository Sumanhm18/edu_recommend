package com.education.education.dto;

public class AuthResponseDto {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private boolean isGuest;

    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, Long userId, String name, String email, boolean isGuest) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isGuest = isGuest;
    }

    // Overloaded constructor for backward compatibility
    public AuthResponseDto(String token, Long userId, String name, String phone, String email, boolean isGuest) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isGuest = isGuest;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }
}