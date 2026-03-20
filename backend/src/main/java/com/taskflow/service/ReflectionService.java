package com.taskflow.service;

import com.taskflow.dto.response.DailyPriorityResponse;
import com.taskflow.dto.response.ReflectionResponse;
import com.taskflow.dto.response.TaskResponse;
import com.taskflow.entity.postgres.Task;
import com.taskflow.repository.postgres.DailyPriorityRepository;
import com.taskflow.repository.postgres.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReflectionService {

    private final TaskRepository taskRepository;
    private final DailyPriorityRepository dailyPriorityRepository;

    public ReflectionResponse getReflection(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Task> completedToday = taskRepository
                .findByUserIdAndCompletedAtBetween(userId, startOfDay, endOfDay);

        List<TaskResponse> completedTaskResponses = completedToday.stream()
                .map(TaskResponse::from)
                .collect(Collectors.toList());

        List<DailyPriorityResponse> priorities = dailyPriorityRepository
                .findByUserIdAndPriorityDateOrderByRank(userId, date)
                .stream()
                .map(DailyPriorityResponse::from)
                .collect(Collectors.toList());

        long prioritiesCompleted = priorities.stream().filter(DailyPriorityResponse::isCompleted).count();

        return ReflectionResponse.builder()
                .date(date)
                .completedTasks(completedTaskResponses)
                .totalCompleted(completedTaskResponses.size())
                .priorities(priorities)
                .prioritiesCompleted((int) prioritiesCompleted)
                .prioritiesTotal(priorities.size())
                .build();
    }
}
