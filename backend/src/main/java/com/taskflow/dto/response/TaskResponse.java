package com.taskflow.dto.response;

import com.taskflow.entity.postgres.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String status;
    private String priority;
    private CategoryInfo category;
    private boolean reminderSent;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String colorHex;
        private String iconName;
    }

    public static TaskResponse from(Task task) {
        CategoryInfo categoryInfo = null;
        if (task.getCategory() != null) {
            categoryInfo = CategoryInfo.builder()
                    .id(task.getCategory().getId())
                    .name(task.getCategory().getName())
                    .colorHex(task.getCategory().getColorHex())
                    .iconName(task.getCategory().getIconName())
                    .build();
        }
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .category(categoryInfo)
                .reminderSent(task.isReminderSent())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}
