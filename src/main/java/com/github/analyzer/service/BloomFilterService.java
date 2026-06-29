package com.github.analyzer.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class BloomFilterService {

    @Value("${app.bloom-filter.expected-insertions}")
    private int expectedInsertions;

    @Value("${app.bloom-filter.false-positive-probability}")
    private double falsePositiveProbability;

    private BloomFilter<String> repositoryBloomFilter;
    private BloomFilter<String> userEmailBloomFilter;
    private BloomFilter<String> githubUrlBloomFilter;

    @PostConstruct
    public void init() {
        repositoryBloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                expectedInsertions,
                falsePositiveProbability);

        userEmailBloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                expectedInsertions,
                falsePositiveProbability);

        githubUrlBloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                expectedInsertions,
                falsePositiveProbability);
    }

    // Repository bloom filter
    public void addRepository(Long repoId) {
        repositoryBloomFilter.put(repoId.toString());
    }

    public boolean mightRepositoryExist(Long repoId) {
        return repositoryBloomFilter.mightContain(repoId.toString());
    }

    // User email bloom filter
    public void addUserEmail(String email) {
        userEmailBloomFilter.put(email);
    }

    public boolean mightUserEmailExist(String email) {
        return userEmailBloomFilter.mightContain(email);
    }

    // GitHub URL bloom filter (prevent duplicate repo imports)
    public void addGithubUrl(String url) {
        githubUrlBloomFilter.put(url);
    }

    public boolean mightGithubUrlExist(String url) {
        return githubUrlBloomFilter.mightContain(url);
    }
}
