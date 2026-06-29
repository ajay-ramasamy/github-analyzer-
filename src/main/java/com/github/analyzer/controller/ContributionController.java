package com.github.analyzer.controller;

import com.github.analyzer.dto.DataDto.ContributionDto;
import com.github.analyzer.service.ContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContributionController {

    private final ContributionService contributionService;

    @GetMapping("/repositories/{repoId}/contributions")
    public ResponseEntity<List<ContributionDto>> getByRepo(@PathVariable Long repoId) {
        return ResponseEntity.ok(contributionService.getByRepo(repoId));
    }

    @PostMapping("/contributions")
    public ResponseEntity<ContributionDto> create(@Valid @RequestBody ContributionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contributionService.create(dto));
    }

    @PutMapping("/contributions/{id}")
    public ResponseEntity<ContributionDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody ContributionDto dto) {
        return ResponseEntity.ok(contributionService.update(id, dto));
    }

    @DeleteMapping("/contributions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contributionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
