package com.github.analyzer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String type;    // PR_MERGED, ISSUE_ASSIGNED, REPO_UPDATED, MILESTONE, WEEKLY_REPORT
    private String message;
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
