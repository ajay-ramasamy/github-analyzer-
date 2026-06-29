package com.github.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String to, String token) {
        send(to, "Verify your email",
             "Click to verify: http://localhost:8080/api/auth/verify-email?token=" + token);
    }

    public void sendPasswordResetEmail(String to, String token) {
        send(to, "Reset your password",
             "Click to reset: http://localhost:8080/api/auth/reset-password?token=" + token);
    }

    public void sendNotificationEmail(String to, String subject, String message) {
        send(to, subject, message);
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
