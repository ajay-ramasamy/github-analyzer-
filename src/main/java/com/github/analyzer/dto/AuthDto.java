package com.github.analyzer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String username;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String username;
        private String email;

        public LoginResponse(String token, String username, String email) {
            this.token = token;
            this.username = username;
            this.email = email;
        }
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;
        @NotBlank @Size(min = 6)
        private String newPassword;
    }

    @Data
    public static class ForgotPasswordRequest {
        @NotBlank @Email
        private String email;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank
        private String token;
        @NotBlank @Size(min = 6)
        private String newPassword;
    }
}
