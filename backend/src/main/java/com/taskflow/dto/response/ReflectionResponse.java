package com.taskflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReflectionResponse {
    private LocalDate date;
    private List<TaskResponse> completedTasks;
    private int totalCompleted;
    private List<DailyPriorityResponse> priorities;
    private int prioritiesCompleted;
    private int prioritiesTotal;
}
