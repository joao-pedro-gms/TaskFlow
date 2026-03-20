package com.taskflow.messaging;

import com.taskflow.config.ActiveMQConfig;
import com.taskflow.entity.mongo.ActivityLog;
import com.taskflow.entity.mongo.Notification;
import com.taskflow.repository.mongo.ActivityLogRepository;
import com.taskflow.repository.mongo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadlineReminderConsumer {

    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = ActiveMQConfig.DEADLINE_REMINDERS_QUEUE,
                 containerFactory = "jmsListenerContainerFactory")
    public void handleDeadlineReminder(DeadlineReminderMessage message) {
        log.info("Processing deadline reminder for task {} (user {})",
                message.getTaskId(), message.getUserId());

        try {
            // 1. Create notification in MongoDB
            Notification notification = Notification.builder()
                    .userId(message.getUserId())
                    .message(buildMessage(message))
                    .type("DEADLINE_REMINDER")
                    .payload(Map.of(
                            "taskId", message.getTaskId(),
                            "deadline", message.getDeadline().toString()
                    ))
                    .build();
            notification = notificationRepository.save(notification);

            // 2. Log the activity
            activityLogRepository.save(ActivityLog.of(
                    message.getUserId(),
                    "REMINDER_SENT",
                    Map.of("taskId", message.getTaskId(), "notificationId", notification.getId())
            ));

            // 3. Broadcast to notifications topic (for future WebSocket/email consumers)
            jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATIONS_TOPIC, Map.of(
                    "userId", message.getUserId(),
                    "notificationId", notification.getId(),
                    "type", "DEADLINE_REMINDER"
            ));

            log.debug("Notification created: {}", notification.getId());
        } catch (Exception e) {
            log.error("Error processing reminder for task {}: {}", message.getTaskId(), e.getMessage(), e);
        }
    }

    private String buildMessage(DeadlineReminderMessage message) {
        long hours = Math.round(message.getHoursUntilDeadline());
        if (hours <= 1) {
            return String.format("'%s' is due in less than 1 hour!", message.getTaskTitle());
        }
        return String.format("'%s' is due in %d hours!", message.getTaskTitle(), hours);
    }
}
