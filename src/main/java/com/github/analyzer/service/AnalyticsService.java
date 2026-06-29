package com.github.analyzer.service;

import com.github.analyzer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ContributionRepository contributionRepository;
    private final IssueRepository issueRepository;
    private final PullRequestRepository prRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserService userService;

    @Cacheable(value = "analytics", key = "#email")
    public Map<String, Object> getDashboard(String email) {
        Long userId = userService.getUser(email).getId();
        int currentYear = Year.now().getValue();
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("contributionsPerMonth", contributionsPerMonth(userId, currentYear));
        result.put("prsVsIssues", prsVsIssues(userId));
        result.put("mostActiveRepositories", mostActiveRepos(userId));
        result.put("languageWiseContributions", languageWise(userId));
        result.put("yearlyGrowth", yearlyGrowth(userId));
        result.put("dailyActivityHeatmap", dailyActivity(userId, currentYear));

        return result;
    }

    @CacheEvict(value = "analytics", key = "#email")
    public void evictDashboardCache(String email) {
        // Called after contributions/issues/PRs are created or synced
    }

    private List<Map<String, Object>> contributionsPerMonth(Long userId, int year) {
        List<Object[]> raw = contributionRepository.countPerMonthByYear(userId, year);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month", row[0]);
            m.put("count", row[1]);
            list.add(m);
        }
        return list;
    }

    private Map<String, Object> prsVsIssues(Long userId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalPRs", prRepository.countByUserId(userId));
        m.put("totalIssues", issueRepository.countByUserId(userId));
        return m;
    }

    private List<Map<String, Object>> mostActiveRepos(Long userId) {
        List<Object[]> raw = repositoryRepository.findMostActiveRepositories(userId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            com.github.analyzer.entity.Repository repo = (com.github.analyzer.entity.Repository) row[0];
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("repositoryId", repo.getId());
            m.put("name", repo.getName());
            m.put("contributions", row[1]);
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> languageWise(Long userId) {
        List<Object[]> raw = repositoryRepository.countContributionsByLanguage(userId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("language", row[0]);
            m.put("contributions", row[1]);
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> yearlyGrowth(Long userId) {
        List<Object[]> raw = contributionRepository.countPerYear(userId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("year", row[0]);
            m.put("count", row[1]);
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> dailyActivity(Long userId, int year) {
        List<Object[]> raw = contributionRepository.dailyActivityByYear(userId, year);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", row[0].toString());
            m.put("count", row[1]);
            list.add(m);
        }
        return list;
    }
}
