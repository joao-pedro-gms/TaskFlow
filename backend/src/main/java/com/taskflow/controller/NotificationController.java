package com.taskflow.controller;

import com.taskflow.entity.mongo.Notification;
import com.taskflow.security.SecurityUtils;
import com.taskflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ResponseEntity.ok(notificationService.getNotifications(
                securityUtils.getCurrentUserId(), unreadOnly));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("unread",
                notificationService.getUnreadCount(securityUtils.getCurrentUserId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.markRead(id, securityUtils.getCurrentUserId()));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllRead(securityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
