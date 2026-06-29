package com.github.analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "issues")
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    private Integer issueNumber;

    @Column(nullable = false)
    private String title;

    private String status;   // open, closed
    private String label;    // bug, feature, documentation, enhancement, security
    private String assignee;
}
