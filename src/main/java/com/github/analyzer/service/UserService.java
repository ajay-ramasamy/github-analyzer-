package com.github.analyzer.service;

import com.github.analyzer.dto.UserProfileDto;
import com.github.analyzer.entity.User;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RepositoryRepository repositoryRepository;
    private final ContributionRepository contributionRepository;

    @Cacheable(value = "userProfiles", key = "#email")
    public UserProfileDto getProfile(String email) {
        return buildProfile(getUser(email));
    }

    @CacheEvict(value = "userProfiles", key = "#email")
    public UserProfileDto updateProfile(String email, String username, String profilePicture, String githubUsername) {
        User user = getUser(email);
        if (username != null) user.setUsername(username);
        if (profilePicture != null) user.setProfilePicture(profilePicture);
        if (githubUsername != null) user.setGithubUsername(githubUsername);
        userRepository.save(user);
        return buildProfile(user);
    }

    @Cacheable(value = "leaderboard", key = "'all'")
    public List<UserProfileDto> getLeaderboard() {
        return userRepository.findAll().stream()
                .map(this::buildProfile)
                .sorted((a, b) -> Long.compare(
                        b.getTotalPoints() == null ? 0 : b.getTotalPoints(),
                        a.getTotalPoints() == null ? 0 : a.getTotalPoints()))
                .toList();
    }

    @CacheEvict(value = "userProfiles", key = "#email")
    public void connectGitHub(String email, String githubToken, String githubUsername) {
        User user = getUser(email);
        if (githubToken != null) user.setGithubToken(githubToken);
        if (githubUsername != null) user.setGithubUsername(githubUsername);
        userRepository.save(user);
    }

    private UserProfileDto buildProfile(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setGithubUsername(user.getGithubUsername());
        dto.setTotalRepositories((long) repositoryRepository.findByUserId(user.getId()).size());
        dto.setTotalContributions(contributionRepository.countByUserId(user.getId()));
        Long points = contributionRepository.sumPointsByUserId(user.getId());
        dto.setTotalPoints(points == null ? 0L : points);
        return dto;
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }
}
