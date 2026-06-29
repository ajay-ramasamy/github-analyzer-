package com.github.analyzer.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String profilePicture;
    private String githubUsername;
    private Long totalRepositories;
    private Long totalContributions;
    private Long totalPoints;
    private Long rank;
}
