package com.taskflow.service;

import com.taskflow.entity.mongo.Notification;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.mongo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getNotifications(Long userId, boolean unreadOnly) {
        if (unreadOnly) {
            return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 50));
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public Notification markRead(String notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
    }

    public Notification create(Long userId, String message, String type, Map<String, Object> payload) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .payload(payload)
                .build();
        return notificationRepository.save(notification);
    }
}
