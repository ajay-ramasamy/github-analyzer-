package com.github.analyzer.controller;

import com.github.analyzer.dto.DataDto.IssueDto;
import com.github.analyzer.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/repositories/{repoId}/issues")
    public ResponseEntity<List<IssueDto>> getByRepo(@PathVariable Long repoId) {
        return ResponseEntity.ok(issueService.getByRepo(repoId));
    }

    @PostMapping("/issues")
    public ResponseEntity<IssueDto> create(@Valid @RequestBody IssueDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(dto));
    }

    @PutMapping("/issues/{id}")
    public ResponseEntity<IssueDto> update(@PathVariable Long id, @Valid @RequestBody IssueDto dto) {
        return ResponseEntity.ok(issueService.update(id, dto));
    }

    @DeleteMapping("/issues/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/repositories/{repoId}/issues/search")
    public ResponseEntity<List<IssueDto>> search(@PathVariable Long repoId,
                                                  @RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String label) {
        return ResponseEntity.ok(issueService.search(repoId, title, status, label));
    }
}
