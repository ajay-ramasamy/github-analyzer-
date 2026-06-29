package com.github.analyzer.service;

import com.github.analyzer.dto.AuthDto.*;
import com.github.analyzer.entity.User;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.UserRepository;
import com.github.analyzer.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new AppException("Email already in use", HttpStatus.BAD_REQUEST);
        if (userRepository.existsByUsername(req.getUsername()))
            throw new AppException("Username already taken", HttpStatus.BAD_REQUEST);

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new AppException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        if (!user.isEmailVerified())
            throw new AppException("Email not verified", HttpStatus.FORBIDDEN);

        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponse(token, user.getUsername(), user.getEmail());
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new AppException("Invalid token", HttpStatus.BAD_REQUEST));
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    public void forgotPassword(ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException("Email not found", HttpStatus.NOT_FOUND));
        user.setPasswordResetToken(UUID.randomUUID().toString());
        user.setPasswordResetExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getPasswordResetToken());
    }

    public void resetPassword(ResetPasswordRequest req) {
        User user = userRepository.findByPasswordResetToken(req.getToken())
                .orElseThrow(() -> new AppException("Invalid token", HttpStatus.BAD_REQUEST));
        if (user.getPasswordResetExpiry().isBefore(LocalDateTime.now()))
            throw new AppException("Token expired", HttpStatus.BAD_REQUEST);
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    public void changePassword(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword()))
            throw new AppException("Current password incorrect", HttpStatus.BAD_REQUEST);
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }
}
