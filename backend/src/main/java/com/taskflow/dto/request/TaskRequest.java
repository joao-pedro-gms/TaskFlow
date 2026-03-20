package com.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    @NotBlank @Size(max = 500)
    private String title;

    private String description;

    private LocalDateTime deadline;

    private String priority;

    private Long categoryId;
}
