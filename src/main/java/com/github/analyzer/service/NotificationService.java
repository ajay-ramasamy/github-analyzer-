package com.github.analyzer.service;

import com.github.analyzer.dto.DataDto.NotificationDto;
import com.github.analyzer.entity.Notification;
import com.github.analyzer.entity.Repository;
import com.github.analyzer.exception.AppException;
import com.github.analyzer.repository.NotificationRepository;
import com.github.analyzer.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RepositoryRepository repositoryRepository;

    public List<NotificationDto> getAll(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    public List<NotificationDto> getUnread(Long userId) {
        return notificationRepository.findByUserIdAndRead(userId, false)
                .stream().map(this::toDto).toList();
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }

    public void markRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException("Notification not found", HttpStatus.NOT_FOUND));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void notifyRepoOwner(Long repoId, String type, String message) {
        Repository repo = repositoryRepository.findById(repoId).orElse(null);
        if (repo == null || repo.getUser() == null) return;
        Notification n = new Notification();
        n.setUser(repo.getUser());
        n.setType(type);
        n.setMessage(message);
        notificationRepository.save(n);
    }

    public void createNotification(Long userId, String type, String message) {
        Notification n = new Notification();
        n.setType(type);
        n.setMessage(message);
        notificationRepository.save(n);
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setMessage(n.getMessage());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt().toString());
        return dto;
    }
}
