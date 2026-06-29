package com.github.analyzer.controller;

import com.github.analyzer.dto.DataDto.PullRequestDto;
import com.github.analyzer.service.PullRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PullRequestController {

    private final PullRequestService prService;

    @GetMapping("/repositories/{repoId}/pull-requests")
    public ResponseEntity<List<PullRequestDto>> getByRepo(@PathVariable Long repoId) {
        return ResponseEntity.ok(prService.getByRepo(repoId));
    }

    @PostMapping("/pull-requests")
    public ResponseEntity<PullRequestDto> create(@Valid @RequestBody PullRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prService.create(dto));
    }

    @PutMapping("/pull-requests/{id}")
    public ResponseEntity<PullRequestDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody PullRequestDto dto) {
        return ResponseEntity.ok(prService.update(id, dto));
    }

    @DeleteMapping("/pull-requests/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/repositories/{repoId}/pull-requests/search")
    public ResponseEntity<List<PullRequestDto>> search(@PathVariable Long repoId,
                                                        @RequestParam(required = false) String title,
                                                        @RequestParam(required = false) String status,
                                                        @RequestParam(required = false) Boolean merged) {
        return ResponseEntity.ok(prService.search(repoId, title, status, merged));
    }
}
