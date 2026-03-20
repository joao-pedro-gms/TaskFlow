package com.taskflow.dto.response;

import com.taskflow.entity.postgres.DailyPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriorityResponse {
    private Long id;
    private TaskResponse task;
    private int rank;
    private boolean completed;
    private LocalDate priorityDate;

    public static DailyPriorityResponse from(DailyPriority priority) {
        return DailyPriorityResponse.builder()
                .id(priority.getId())
                .task(TaskResponse.from(priority.getTask()))
                .rank(priority.getRank())
                .completed(priority.isCompleted())
                .priorityDate(priority.getPriorityDate())
                .build();
    }
}
