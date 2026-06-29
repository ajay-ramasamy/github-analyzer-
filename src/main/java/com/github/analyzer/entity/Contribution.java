package com.github.analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "contributions")
@Data
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    private String type;   // commit, issue, pull_request
    private String title;
    private Integer points;
    private LocalDate contributionDate;
    private String contributor;
}
