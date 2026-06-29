package com.github.analyzer.service;

import com.github.analyzer.dto.DataDto.IssueDto;
import com.github.analyzer.entity.Issue;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final RepositoryService repositoryService;
    private final NotificationService notificationService;

    public List<IssueDto> getByRepo(Long repoId) {
        return issueRepository.findByRepositoryId(repoId).stream().map(this::toDto).toList();
    }

    public IssueDto create(IssueDto dto) {
        Issue issue = new Issue();
        issue.setRepository(repositoryService.getEntity(dto.getRepositoryId()));
        issue.setIssueNumber(dto.getIssueNumber());
        issue.setTitle(dto.getTitle());
        issue.setStatus(dto.getStatus());
        issue.setLabel(dto.getLabel());
        issue.setAssignee(dto.getAssignee());
        Issue saved = issueRepository.save(issue);
        if (dto.getAssignee() != null) {
            notificationService.notifyRepoOwner(dto.getRepositoryId(),
                    "ISSUE_ASSIGNED", "Issue assigned: " + dto.getTitle());
        }
        return toDto(saved);
    }

    public IssueDto update(Long id, IssueDto dto) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new AppException("Issue not found", HttpStatus.NOT_FOUND));
        issue.setTitle(dto.getTitle());
        issue.setStatus(dto.getStatus());
        issue.setLabel(dto.getLabel());
        issue.setAssignee(dto.getAssignee());
        return toDto(issueRepository.save(issue));
    }

    public void delete(Long id) {
        issueRepository.deleteById(id);
    }

    public List<IssueDto> search(Long repoId, String title, String status, String label) {
        return issueRepository.search(repoId, title, status, label).stream().map(this::toDto).toList();
    }

    private IssueDto toDto(Issue i) {
        IssueDto dto = new IssueDto();
        dto.setId(i.getId());
        dto.setRepositoryId(i.getRepository().getId());
        dto.setIssueNumber(i.getIssueNumber());
        dto.setTitle(i.getTitle());
        dto.setStatus(i.getStatus());
        dto.setLabel(i.getLabel());
        dto.setAssignee(i.getAssignee());
        return dto;
    }
}
