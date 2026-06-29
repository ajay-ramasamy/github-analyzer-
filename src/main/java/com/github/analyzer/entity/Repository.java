package com.github.analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "repositories")
@Data
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String owner;
    private String githubUrl;
    private String language;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    private Integer stars = 0;
    private Integer forks = 0;
    private Integer watchers = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    private List<Issue> issues;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    private List<PullRequest> pullRequests;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL)
    private List<Contribution> contributions;
}
