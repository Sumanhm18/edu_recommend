package com.education.education.dto;

public class OtpRequestDto {
    private String phoneNumber;

    public OtpRequestDto() {
    }

    public OtpRequestDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}