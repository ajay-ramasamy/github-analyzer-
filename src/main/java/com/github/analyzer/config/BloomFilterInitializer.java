package com.github.analyzer.config;

import com.github.analyzer.repository.RepositoryRepository;
import com.github.analyzer.repository.UserRepository;
import com.github.analyzer.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BloomFilterInitializer implements ApplicationRunner {

    private final BloomFilterService bloomFilterService;
    private final UserRepository userRepository;
    private final RepositoryRepository repositoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing Bloom Filters from existing DB data...");

        userRepository.findAll().forEach(user -> {
            bloomFilterService.addUserEmail(user.getEmail());
        });

        repositoryRepository.findAll().forEach(repo -> {
            bloomFilterService.addRepository(repo.getId());
            if (repo.getGithubUrl() != null) {
                bloomFilterService.addGithubUrl(repo.getGithubUrl());
            }
        });

        log.info("Bloom Filters initialized.");
    }
}
