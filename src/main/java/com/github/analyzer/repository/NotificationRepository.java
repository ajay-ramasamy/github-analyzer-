package com.github.analyzer.repository;

import com.github.analyzer.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndRead(Long userId, boolean read);
    long countByUserIdAndRead(Long userId, boolean read);
}
