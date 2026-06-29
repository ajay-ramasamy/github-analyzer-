package com.github.analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pull_requests")
@Data
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    private Integer prNumber;

    @Column(nullable = false)
    private String title;

    private String status;    // open, closed
    private Boolean merged = false;
    private String label;
}
