package com.taskflow.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class DailyPriorityRequest {
    @NotNull
    @Size(min = 3, max = 3, message = "Exactly 3 task IDs are required")
    private List<Long> taskIds;
}
