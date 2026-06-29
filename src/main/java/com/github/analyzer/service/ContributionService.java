package com.github.analyzer.service;

import com.github.analyzer.dto.DataDto.ContributionDto;
import com.github.analyzer.entity.Contribution;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.ContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final RepositoryService repositoryService;

    public List<ContributionDto> getByRepo(Long repoId) {
        return contributionRepository.findByRepositoryId(repoId).stream().map(this::toDto).toList();
    }

    public ContributionDto create(ContributionDto dto) {
        Contribution c = new Contribution();
        c.setRepository(repositoryService.getEntity(dto.getRepositoryId()));
        c.setType(dto.getType());
        c.setTitle(dto.getTitle());
        c.setPoints(dto.getPoints());
        c.setContributionDate(dto.getContributionDate());
        c.setContributor(dto.getContributor());
        return toDto(contributionRepository.save(c));
    }

    public ContributionDto update(Long id, ContributionDto dto) {
        Contribution c = contributionRepository.findById(id)
                .orElseThrow(() -> new AppException("Contribution not found", HttpStatus.NOT_FOUND));
        c.setType(dto.getType());
        c.setTitle(dto.getTitle());
        c.setPoints(dto.getPoints());
        c.setContributionDate(dto.getContributionDate());
        c.setContributor(dto.getContributor());
        return toDto(contributionRepository.save(c));
    }

    public void delete(Long id) {
        contributionRepository.deleteById(id);
    }

    public Map<String, Object> getStats(Long userId) {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Total contributions and points
        stats.put("totalContributions", contributionRepository.countByUserId(userId));
        stats.put("totalPoints", contributionRepository.sumPointsByUserId(userId));

        // Breakdown by type (commit, issue, pull_request)
        Map<String, Long> byType = new LinkedHashMap<>();
        for (Object[] row : contributionRepository.countByType(userId))
            byType.put((String) row[0], (Long) row[1]);
        stats.put("byType", byType);

        // Top contributors by points
        List<Map<String, Object>> topContributors = new java.util.ArrayList<>();
        for (Object[] row : contributionRepository.topContributors(userId)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("contributor", row[0]);
            entry.put("points", row[1]);
            topContributors.add(entry);
        }
        stats.put("topContributors", topContributors);

        // Contributions per repository
        List<Map<String, Object>> byRepo = new java.util.ArrayList<>();
        for (Object[] row : contributionRepository.countByRepository(userId)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("repository", row[0]);
            entry.put("contributions", row[1]);
            byRepo.add(entry);
        }
        stats.put("byRepository", byRepo);

        return stats;
    }

    private ContributionDto toDto(Contribution c) {
        ContributionDto dto = new ContributionDto();
        dto.setId(c.getId());
        dto.setRepositoryId(c.getRepository().getId());
        dto.setType(c.getType());
        dto.setTitle(c.getTitle());
        dto.setPoints(c.getPoints());
        dto.setContributionDate(c.getContributionDate());
        dto.setContributor(c.getContributor());
        return dto;
    }
}
