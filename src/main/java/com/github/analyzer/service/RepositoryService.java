package com.github.analyzer.service;

import com.github.analyzer.dto.RepositoryDto;
import com.github.analyzer.entity.Repository;
import com.github.analyzer.entity.User;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final UserService userService;
    private final BloomFilterService bloomFilterService;

    @Cacheable(value = "repositories", key = "#email")
    public List<RepositoryDto> getAll(String email) {
        return repositoryRepository.findByUserId(userService.getUser(email).getId())
                .stream().map(this::toDto).toList();
    }

    @Cacheable(value = "repositories", key = "#id + ':' + #email")
    public RepositoryDto getById(Long id, String email) {
        // Bloom filter fast-path: if not in bloom filter, definitely doesn't exist
        if (!bloomFilterService.mightRepositoryExist(id)) {
            throw new AppException("Repository not found", HttpStatus.NOT_FOUND);
        }
        return toDto(getOwned(id, email));
    }

    @CacheEvict(value = "repositories", key = "#email")
    public RepositoryDto create(RepositoryDto dto, String email) {
        User user = userService.getUser(email);
        Repository repo = fromDto(dto, new Repository());
        repo.setUser(user);
        Repository saved = repositoryRepository.save(repo);
        bloomFilterService.addRepository(saved.getId());
        if (saved.getGithubUrl() != null) bloomFilterService.addGithubUrl(saved.getGithubUrl());
        return toDto(saved);
    }

    @CacheEvict(value = "repositories", allEntries = true)
    public RepositoryDto update(Long id, RepositoryDto dto, String email) {
        Repository repo = getOwned(id, email);
        return toDto(repositoryRepository.save(fromDto(dto, repo)));
    }

    @CacheEvict(value = "repositories", allEntries = true)
    public void delete(Long id, String email) {
        repositoryRepository.delete(getOwned(id, email));
    }

    public List<RepositoryDto> search(String email, String name, String language) {
        Long userId = userService.getUser(email).getId();
        return repositoryRepository.searchByUser(userId, name, language)
                .stream().map(this::toDto).toList();
    }

    private Repository getOwned(Long id, String email) {
        Repository repo = repositoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Repository not found", HttpStatus.NOT_FOUND));
        if (!repo.getUser().getEmail().equals(email))
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        return repo;
    }

    public Repository getEntity(Long id) {
        if (!bloomFilterService.mightRepositoryExist(id)) {
            throw new AppException("Repository not found", HttpStatus.NOT_FOUND);
        }
        return repositoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Repository not found", HttpStatus.NOT_FOUND));
    }

    private RepositoryDto toDto(Repository r) {
        RepositoryDto dto = new RepositoryDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setOwner(r.getOwner());
        dto.setGithubUrl(r.getGithubUrl());
        dto.setLanguage(r.getLanguage());
        dto.setDescription(r.getDescription());
        dto.setAiSummary(r.getAiSummary());
        dto.setStars(r.getStars());
        dto.setForks(r.getForks());
        dto.setWatchers(r.getWatchers());
        return dto;
    }

    private Repository fromDto(RepositoryDto dto, Repository repo) {
        repo.setName(dto.getName());
        repo.setOwner(dto.getOwner());
        repo.setGithubUrl(dto.getGithubUrl());
        repo.setLanguage(dto.getLanguage());
        repo.setDescription(dto.getDescription());
        if (dto.getStars() != null) repo.setStars(dto.getStars());
        if (dto.getForks() != null) repo.setForks(dto.getForks());
        if (dto.getWatchers() != null) repo.setWatchers(dto.getWatchers());
        return repo;
    }
}
