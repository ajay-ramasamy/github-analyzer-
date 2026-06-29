package com.github.analyzer.controller;

import com.github.analyzer.service.GitHubSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubSyncService gitHubSyncService;

    @PostMapping("/import-repositories")
    public ResponseEntity<?> importRepositories(@AuthenticationPrincipal UserDetails u) throws Exception {
        return ResponseEntity.ok(gitHubSyncService.importRepositories(u.getUsername()));
    }

    @PostMapping("/repositories/{repoId}/sync-issues")
    public ResponseEntity<String> syncIssues(@PathVariable Long repoId,
                                              @AuthenticationPrincipal UserDetails u) throws Exception {
        gitHubSyncService.syncIssues(repoId, u.getUsername());
        return ResponseEntity.ok("Issues synced.");
    }

    @PostMapping("/repositories/{repoId}/sync-pull-requests")
    public ResponseEntity<String> syncPRs(@PathVariable Long repoId,
                                           @AuthenticationPrincipal UserDetails u) throws Exception {
        gitHubSyncService.syncPullRequests(repoId, u.getUsername());
        return ResponseEntity.ok("Pull requests synced.");
    }

    @PostMapping("/repositories/{repoId}/sync-commits")
    public ResponseEntity<String> syncCommits(@PathVariable Long repoId,
                                               @AuthenticationPrincipal UserDetails u) throws Exception {
        gitHubSyncService.syncCommits(repoId, u.getUsername());
        return ResponseEntity.ok("Commits synced.");
    }

    @PostMapping("/repositories/{repoId}/sync-stats")
    public ResponseEntity<String> syncStats(@PathVariable Long repoId,
                                             @AuthenticationPrincipal UserDetails u) throws Exception {
        gitHubSyncService.syncStarsForks(repoId, u.getUsername());
        return ResponseEntity.ok("Stars, forks, watchers synced.");
    }
}
