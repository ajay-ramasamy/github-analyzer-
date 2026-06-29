package com.github.analyzer.service;

import com.github.analyzer.dto.DataDto.PullRequestDto;
import com.github.analyzer.entity.PullRequest;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.PullRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PullRequestService {

    private final PullRequestRepository prRepository;
    private final RepositoryService repositoryService;
    private final NotificationService notificationService;

    public List<PullRequestDto> getByRepo(Long repoId) {
        return prRepository.findByRepositoryId(repoId).stream().map(this::toDto).toList();
    }

    public PullRequestDto create(PullRequestDto dto) {
        PullRequest pr = new PullRequest();
        pr.setRepository(repositoryService.getEntity(dto.getRepositoryId()));
        pr.setPrNumber(dto.getPrNumber());
        pr.setTitle(dto.getTitle());
        pr.setStatus(dto.getStatus());
        pr.setMerged(dto.getMerged() != null ? dto.getMerged() : false);
        pr.setLabel(dto.getLabel());
        PullRequest saved = prRepository.save(pr);
        if (Boolean.TRUE.equals(dto.getMerged())) {
            notificationService.notifyRepoOwner(dto.getRepositoryId(),
                    "PR_MERGED", "PR merged: " + dto.getTitle());
        }
        return toDto(saved);
    }

    public PullRequestDto update(Long id, PullRequestDto dto) {
        PullRequest pr = prRepository.findById(id)
                .orElseThrow(() -> new AppException("PR not found", HttpStatus.NOT_FOUND));
        boolean wasMerged = Boolean.TRUE.equals(pr.getMerged());
        pr.setTitle(dto.getTitle());
        pr.setStatus(dto.getStatus());
        pr.setMerged(dto.getMerged());
        pr.setLabel(dto.getLabel());
        PullRequest saved = prRepository.save(pr);
        if (!wasMerged && Boolean.TRUE.equals(dto.getMerged())) {
            notificationService.notifyRepoOwner(pr.getRepository().getId(),
                    "PR_MERGED", "PR merged: " + dto.getTitle());
        }
        return toDto(saved);
    }

    public void delete(Long id) {
        prRepository.deleteById(id);
    }

    public List<PullRequestDto> search(Long repoId, String title, String status, Boolean merged) {
        return prRepository.search(repoId, title, status, merged).stream().map(this::toDto).toList();
    }

    private PullRequestDto toDto(PullRequest p) {
        PullRequestDto dto = new PullRequestDto();
        dto.setId(p.getId());
        dto.setRepositoryId(p.getRepository().getId());
        dto.setPrNumber(p.getPrNumber());
        dto.setTitle(p.getTitle());
        dto.setStatus(p.getStatus());
        dto.setMerged(p.getMerged());
        dto.setLabel(p.getLabel());
        return dto;
    }
}
