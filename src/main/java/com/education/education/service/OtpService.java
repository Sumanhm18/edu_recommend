package com.education.education.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class OtpService {

    @Value("${otp.expiration}")
    private long otpExpirationMinutes;

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generateOtp(String phoneNumber) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        OtpData otpData = new OtpData(otp, LocalDateTime.now().plusMinutes(otpExpirationMinutes / 60000));
        otpStorage.put(phoneNumber, otpData);

        // In production, integrate with SMS service like Textlocal, AWS SNS, etc.
        // For now, we'll just log it (remove in production)
        System.out.println("OTP for " + phoneNumber + ": " + otp);

        return otp;
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        OtpData otpData = otpStorage.get(phoneNumber);

        if (otpData == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpData.getExpirationTime())) {
            otpStorage.remove(phoneNumber);
            return false;
        }

        boolean isValid = otpData.getOtp().equals(otp);
        if (isValid) {
            otpStorage.remove(phoneNumber); // OTP can only be used once
        }

        return isValid;
    }

    public boolean sendOtp(String phoneNumber) {
        // Clean up expired OTPs first
        cleanupExpiredOtps();

        generateOtp(phoneNumber);

        // In production, integrate with SMS gateway
        // For now, just return true to simulate successful SMS sending
        return true;
    }

    private void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpStorage.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpirationTime()));
    }

    // Inner class to hold OTP data
    private static class OtpData {
        private final String otp;
        private final LocalDateTime expirationTime;

        public OtpData(String otp, LocalDateTime expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }
    }
}