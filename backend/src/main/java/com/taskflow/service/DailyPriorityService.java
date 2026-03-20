package com.taskflow.service;

import com.taskflow.dto.request.DailyPriorityRequest;
import com.taskflow.dto.response.DailyPriorityResponse;
import com.taskflow.entity.postgres.DailyPriority;
import com.taskflow.entity.postgres.Task;
import com.taskflow.entity.postgres.User;
import com.taskflow.exception.BadRequestException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.postgres.DailyPriorityRepository;
import com.taskflow.repository.postgres.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyPriorityService {

    private final DailyPriorityRepository dailyPriorityRepository;
    private final TaskRepository taskRepository;

    public List<DailyPriorityResponse> getTodayPriorities(Long userId) {
        return dailyPriorityRepository
                .findByUserIdAndPriorityDateOrderByRank(userId, LocalDate.now())
                .stream()
                .map(DailyPriorityResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DailyPriorityResponse> setPriorities(DailyPriorityRequest request, User user) {
        if (request.getTaskIds().size() != 3) {
            throw new BadRequestException("Exactly 3 tasks must be selected as priorities");
        }

        LocalDate today = LocalDate.now();
        dailyPriorityRepository.deleteByUserIdAndPriorityDate(user.getId(), today);

        List<DailyPriority> priorities = new ArrayList<>();
        List<Long> taskIds = request.getTaskIds();

        for (int i = 0; i < taskIds.size(); i++) {
            Long taskId = taskIds.get(i);
            Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

            if (task.getStatus() == Task.TaskStatus.COMPLETED) {
                throw new BadRequestException("Cannot prioritize a completed task");
            }

            DailyPriority priority = DailyPriority.builder()
                    .user(user)
                    .task(task)
                    .priorityDate(today)
                    .rank(i + 1)
                    .build();
            priorities.add(dailyPriorityRepository.save(priority));
        }

        return priorities.stream().map(DailyPriorityResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public DailyPriorityResponse completePriority(Long priorityId, Long userId) {
        DailyPriority priority = dailyPriorityRepository.findByIdAndUserId(priorityId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority not found"));
        priority.setCompleted(true);
        return DailyPriorityResponse.from(dailyPriorityRepository.save(priority));
    }
}
