package com.github.analyzer.controller;

import com.github.analyzer.dto.DataDto.NotificationDto;
import com.github.analyzer.service.NotificationService;
import com.github.analyzer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAll(@AuthenticationPrincipal UserDetails u) {
        Long userId = userService.getUser(u.getUsername()).getId();
        return ResponseEntity.ok(notificationService.getAll(userId));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnread(@AuthenticationPrincipal UserDetails u) {
        Long userId = userService.getUser(u.getUsername()).getId();
        return ResponseEntity.ok(notificationService.getUnread(userId));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> countUnread(@AuthenticationPrincipal UserDetails u) {
        Long userId = userService.getUser(u.getUsername()).getId();
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(userId)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok("Marked as read.");
    }
}
