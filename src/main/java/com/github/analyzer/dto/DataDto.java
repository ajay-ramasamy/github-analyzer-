package com.github.analyzer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

public class DataDto {

    @Data
    public static class IssueDto {
        private Long id;
        private Long repositoryId;
        private Integer issueNumber;
        @NotBlank
        private String title;
        private String status;
        private String label;
        private String assignee;
    }

    @Data
    public static class PullRequestDto {
        private Long id;
        private Long repositoryId;
        private Integer prNumber;
        @NotBlank
        private String title;
        private String status;
        private Boolean merged;
        private String label;
    }

    @Data
    public static class ContributionDto {
        private Long id;
        private Long repositoryId;
        private String type;
        private String title;
        private Integer points;
        private LocalDate contributionDate;
        private String contributor;
    }

    @Data
    public static class NotificationDto {
        private Long id;
        private String type;
        private String message;
        private boolean read;
        private String createdAt;
    }
}
