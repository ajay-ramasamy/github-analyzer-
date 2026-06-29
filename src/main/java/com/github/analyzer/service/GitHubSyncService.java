package com.github.analyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.analyzer.entity.*;
import com.github.analyzer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubSyncService {

    private final RepositoryRepository repositoryRepository;
    private final IssueRepository issueRepository;
    private final PullRequestRepository prRepository;
    private final ContributionRepository contributionRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final BloomFilterService bloomFilterService;
    private final AnalyticsService analyticsService;

    @Value("${github.api.base-url}")
    private String githubBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Repository> importRepositories(String email) throws Exception {
        User user = userService.getUser(email);
        String token = user.getGithubToken();
        String githubUsername = user.getGithubUsername();
        if (token == null || githubUsername == null)
            throw new IllegalStateException("GitHub account not connected");

        JsonNode repos = fetch(githubBaseUrl + "/users/" + githubUsername + "/repos?per_page=100", token);
        List<Repository> saved = new ArrayList<>();
        for (JsonNode node : repos) {
            String githubUrl = node.path("html_url").asText();
            // Bloom filter: skip if this GitHub URL was already imported
            if (bloomFilterService.mightGithubUrlExist(githubUrl)) continue;
            Repository repo = new Repository();
            repo.setName(node.path("name").asText());
            repo.setOwner(node.path("owner").path("login").asText());
            repo.setGithubUrl(githubUrl);
            repo.setLanguage(node.path("language").asText(null));
            repo.setDescription(node.path("description").asText(null));
            repo.setStars(node.path("stargazers_count").asInt(0));
            repo.setForks(node.path("forks_count").asInt(0));
            repo.setWatchers(node.path("watchers_count").asInt(0));
            repo.setUser(user);
            Repository savedRepo = repositoryRepository.save(repo);
            bloomFilterService.addRepository(savedRepo.getId());
            bloomFilterService.addGithubUrl(githubUrl);
            saved.add(savedRepo);
        }
        if (!saved.isEmpty()) {
            notificationService.notifyRepoOwner(saved.get(0).getId(),
                    "REPO_UPDATED", "Repositories imported from GitHub");
            analyticsService.evictDashboardCache(email);
        }
        return saved;
    }

    public void syncIssues(Long repoId, String email) throws Exception {
        Repository repo = getOwnedRepo(repoId, email);
        String token = repo.getUser().getGithubToken();
        String path = "/repos/" + repo.getOwner() + "/" + repo.getName() + "/issues?state=all&per_page=100";
        JsonNode issues = fetch(githubBaseUrl + path, token);
        for (JsonNode node : issues) {
            if (node.has("pull_request")) continue; // skip PRs listed as issues
            Issue issue = new Issue();
            issue.setRepository(repo);
            issue.setIssueNumber(node.path("number").asInt());
            issue.setTitle(node.path("title").asText());
            issue.setStatus(node.path("state").asText());
            issue.setAssignee(node.path("assignee").path("login").asText(null));
            issueRepository.save(issue);
        }
    }

    public void syncPullRequests(Long repoId, String email) throws Exception {
        Repository repo = getOwnedRepo(repoId, email);
        String token = repo.getUser().getGithubToken();
        String path = "/repos/" + repo.getOwner() + "/" + repo.getName() + "/pulls?state=all&per_page=100";
        JsonNode prs = fetch(githubBaseUrl + path, token);
        for (JsonNode node : prs) {
            PullRequest pr = new PullRequest();
            pr.setRepository(repo);
            pr.setPrNumber(node.path("number").asInt());
            pr.setTitle(node.path("title").asText());
            pr.setStatus(node.path("state").asText());
            pr.setMerged(!node.path("merged_at").isNull());
            prRepository.save(pr);
        }
    }

    public void syncCommits(Long repoId, String email) throws Exception {
        Repository repo = getOwnedRepo(repoId, email);
        String token = repo.getUser().getGithubToken();
        String path = "/repos/" + repo.getOwner() + "/" + repo.getName() + "/commits?per_page=100";
        JsonNode commits = fetch(githubBaseUrl + path, token);
        for (JsonNode node : commits) {
            Contribution c = new Contribution();
            c.setRepository(repo);
            c.setType("commit");
            c.setTitle(node.path("commit").path("message").asText());
            c.setContributor(node.path("commit").path("author").path("name").asText(null));
            String dateStr = node.path("commit").path("author").path("date").asText(null);
            if (dateStr != null) c.setContributionDate(LocalDate.parse(dateStr.substring(0, 10)));
            c.setPoints(1);
            contributionRepository.save(c);
        }
    }

    public void syncStarsForks(Long repoId, String email) throws Exception {
        Repository repo = getOwnedRepo(repoId, email);
        String token = repo.getUser().getGithubToken();
        String path = "/repos/" + repo.getOwner() + "/" + repo.getName();
        JsonNode node = fetch(githubBaseUrl + path, token);
        repo.setStars(node.path("stargazers_count").asInt(0));
        repo.setForks(node.path("forks_count").asInt(0));
        repo.setWatchers(node.path("watchers_count").asInt(0));
        repositoryRepository.save(repo);
    }

    private Repository getOwnedRepo(Long repoId, String email) {
        Repository repo = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new RuntimeException("Repository not found"));
        if (!repo.getUser().getEmail().equals(email))
            throw new RuntimeException("Access denied");
        return repo;
    }

    private JsonNode fetch(String url, String token) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/vnd.github+json");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        return objectMapper.readTree(response.getBody());
    }
}
