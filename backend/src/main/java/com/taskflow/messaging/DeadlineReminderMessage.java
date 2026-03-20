package com.taskflow.messaging;

import com.taskflow.entity.postgres.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadlineReminderMessage {

    private Long taskId;
    private Long userId;
    private String taskTitle;
    private LocalDateTime deadline;
    private double hoursUntilDeadline;

    public static DeadlineReminderMessage from(Task task) {
        double hours = ChronoUnit.MINUTES.between(LocalDateTime.now(), task.getDeadline()) / 60.0;
        return DeadlineReminderMessage.builder()
                .taskId(task.getId())
                .userId(task.getUser().getId())
                .taskTitle(task.getTitle())
                .deadline(task.getDeadline())
                .hoursUntilDeadline(hours)
                .build();
    }
}
