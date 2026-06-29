package com.github.analyzer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RepositoryDto {
    private Long id;
    @NotBlank
    private String name;
    private String owner;
    private String githubUrl;
    private String language;
    private String description;
    private String aiSummary;
    private Integer stars;
    private Integer forks;
    private Integer watchers;
}
