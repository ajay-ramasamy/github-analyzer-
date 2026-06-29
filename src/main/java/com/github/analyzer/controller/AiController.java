package com.github.analyzer.controller;

import com.github.analyzer.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/repositories/{repoId}/summarize")
    public ResponseEntity<Map<String, String>> summarize(@PathVariable Long repoId) {
        return ResponseEntity.ok(Map.of("summary", aiService.generateRepoSummary(repoId)));
    }

    @PostMapping("/issues/{issueId}/categorize")
    public ResponseEntity<Map<String, String>> categorize(@PathVariable Long issueId) {
        return ResponseEntity.ok(Map.of("category", aiService.categorizeIssue(issueId)));
    }
}
