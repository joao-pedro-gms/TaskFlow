package com.taskflow.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReflectionRequest {
    private String note;
    private String mood;
    private LocalDate date;
}
