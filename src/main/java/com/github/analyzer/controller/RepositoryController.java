package com.github.analyzer.controller;

import com.github.analyzer.dto.RepositoryDto;
import com.github.analyzer.service.RepositoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;

    @GetMapping
    public ResponseEntity<List<RepositoryDto>> getAll(@AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(repositoryService.getAll(u.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepositoryDto> getById(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(repositoryService.getById(id, u.getUsername()));
    }

    @PostMapping
    public ResponseEntity<RepositoryDto> create(@Valid @RequestBody RepositoryDto dto,
                                                 @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repositoryService.create(dto, u.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepositoryDto> update(@PathVariable Long id,
                                                 @Valid @RequestBody RepositoryDto dto,
                                                 @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(repositoryService.update(id, dto, u.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails u) {
        repositoryService.delete(id, u.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RepositoryDto>> search(@AuthenticationPrincipal UserDetails u,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String language) {
        return ResponseEntity.ok(repositoryService.search(u.getUsername(), name, language));
    }
}
