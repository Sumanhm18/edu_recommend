package com.education.education.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class OtpService {

    @Value("${otp.expiration}")
    private long otpExpirationMinutes;

    @Autowired
    private EmailService emailService;

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    public String generateOtp(String email) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        OtpData otpData = new OtpData(otp, LocalDateTime.now().plusMinutes(otpExpirationMinutes / 60000));
        otpStorage.put(email, otpData);

        // Log OTP for development (remove in production)
        System.out.println("📧 OTP for " + email + ": " + otp);

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            System.out.println("❌ No OTP found for email: " + email);
            return false;
        }

        if (LocalDateTime.now().isAfter(otpData.getExpirationTime())) {
            otpStorage.remove(email);
            System.out.println("⏰ OTP expired for email: " + email);
            return false;
        }

        boolean isValid = otpData.getOtp().equals(otp);
        if (isValid) {
            otpStorage.remove(email); // OTP can only be used once
            System.out.println("✅ OTP validated successfully for email: " + email);
        } else {
            System.out.println("❌ Invalid OTP for email: " + email);
        }

        return isValid;
    }

    public boolean sendOtp(String email) {
        // Clean up expired OTPs first
        cleanupExpiredOtps();

        // Validate email format
        if (!isValidEmail(email)) {
            System.out.println("❌ Invalid email format: " + email);
            return false;
        }

        // Generate OTP
        String otp = generateOtp(email);

        // Send OTP via email
        boolean emailSent = emailService.sendOtpEmail(email, otp);

        if (!emailSent) {
            // Remove OTP from storage if email failed to send
            otpStorage.remove(email);
            System.out.println("❌ Failed to send OTP email to: " + email);
            return false;
        }

        System.out.println("📧 OTP sent successfully to: " + email);
        return true;
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        int removedCount = 0;
        var iterator = otpStorage.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (now.isAfter(entry.getValue().getExpirationTime())) {
                iterator.remove();
                removedCount++;
            }
        }
        if (removedCount > 0) {
            System.out.println("🧹 Cleaned up " + removedCount + " expired OTPs");
        }
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