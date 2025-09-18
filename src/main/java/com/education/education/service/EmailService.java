package com.education.education.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.development.mode:true}")
    private boolean developmentMode;

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            System.out.println("📧 Attempting to send OTP email to: " + toEmail);
            System.out.println("🔐 OTP Code: " + otp);
            System.out.println("📨 Email Content:");
            System.out.println(buildOtpEmailContent(otp));
            System.out.println("=".repeat(60));

            if (developmentMode) {
                System.out.println("🔧 DEVELOPMENT MODE: Email sending simulated");
                System.out.println("✅ In production, this OTP would be sent to: " + toEmail);
                return true;
            } else {
                // Production mode - send actual email
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Education Platform - Email Verification OTP");
                message.setText(buildOtpEmailContent(otp));
                message.setFrom("sumanhm1234@gmail.com");

                mailSender.send(message);
                System.out.println("✅ OTP email sent successfully to: " + toEmail);
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP email to: " + toEmail);
            System.err.println("Error details: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Root cause: " + e.getCause().getMessage());
            }

            // In case of email failure, still return true if OTP is logged for testing
            if (developmentMode) {
                System.out.println("🔧 Development mode: OTP operation continues despite email failure");
                return true;
            }
            return false;
        }
    }

    private String buildOtpEmailContent(String otp) {
        return String.format(
                "Dear User,\n\n" +
                        "Your verification code for Education Platform is: %s\n\n" +
                        "This OTP is valid for 5 minutes. Please do not share this code with anyone.\n\n" +
                        "If you didn't request this verification, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Education Platform Team",
                otp);
    }

    public boolean sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to Education Platform!");
            message.setText(buildWelcomeEmailContent(userName));
            message.setFrom("sumanhm1234@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Welcome email sent successfully to: " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email to: " + toEmail);
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    private String buildWelcomeEmailContent(String userName) {
        return String.format(
                "Dear %s,\n\n" +
                        "Welcome to Education Platform! 🎓\n\n" +
                        "Your account has been successfully created. You can now:\n" +
                        "• Take aptitude quizzes\n" +
                        "• Get personalized college recommendations\n" +
                        "• Receive career guidance\n" +
                        "• Access government college information\n\n" +
                        "Start your educational journey today!\n\n" +
                        "Best regards,\n" +
                        "Education Platform Team",
                userName != null ? userName : "Student");
    }
}