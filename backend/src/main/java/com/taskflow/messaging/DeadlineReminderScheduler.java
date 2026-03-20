package com.taskflow.messaging;

import com.taskflow.config.ActiveMQConfig;
import com.taskflow.entity.postgres.Task;
import com.taskflow.repository.postgres.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadlineReminderScheduler {

    private final TaskRepository taskRepository;
    private final JmsTemplate jmsTemplate;

    @Scheduled(fixedRate = 30 * 60 * 1000) // every 30 minutes
    @Transactional
    public void checkDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusHours(24);

        List<Task> dueTasks = taskRepository.findTasksDueWithin24Hours(now, cutoff);

        if (!dueTasks.isEmpty()) {
            log.info("Found {} tasks due within 24 hours, sending reminders", dueTasks.size());
        }

        for (Task task : dueTasks) {
            try {
                DeadlineReminderMessage message = DeadlineReminderMessage.from(task);
                jmsTemplate.convertAndSend(ActiveMQConfig.DEADLINE_REMINDERS_QUEUE, message);
                task.setReminderSent(true);
                taskRepository.save(task);
                log.debug("Reminder queued for task {} (user {})", task.getId(), task.getUser().getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for task {}: {}", task.getId(), e.getMessage());
            }
        }
    }
}
