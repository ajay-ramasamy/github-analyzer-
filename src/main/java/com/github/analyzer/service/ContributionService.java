package com.github.analyzer.service;

import com.github.analyzer.dto.DataDto.ContributionDto;
import com.github.analyzer.entity.Contribution;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.ContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

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
