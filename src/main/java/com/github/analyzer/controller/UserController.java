package com.github.analyzer.controller;

import com.github.analyzer.dto.UserProfileDto;
import com.github.analyzer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(userService.updateProfile(
                userDetails.getUsername(),
                body.get("username"),
                body.get("profilePicture"),
                body.get("githubUsername")));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserProfileDto>> leaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }

    @PutMapping("/me/github-token")
    public ResponseEntity<String> connectGitHub(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody Map<String, String> body) {
        userService.connectGitHub(userDetails.getUsername(), body.get("githubToken"), body.get("githubUsername"));
        return ResponseEntity.ok("GitHub account connected.");
    }
}
