package com.taskflow.service;

import com.taskflow.dto.request.TaskRequest;
import com.taskflow.dto.response.TaskResponse;
import com.taskflow.entity.mongo.ActivityLog;
import com.taskflow.entity.postgres.Category;
import com.taskflow.entity.postgres.Task;
import com.taskflow.entity.postgres.User;
import com.taskflow.exception.BadRequestException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.mongo.ActivityLogRepository;
import com.taskflow.repository.postgres.CategoryRepository;
import com.taskflow.repository.postgres.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final AutoCategorizationService autoCategorizationService;
    private final CategoryService categoryService;
    private final ActivityLogRepository activityLogRepository;

    public List<TaskResponse> getAllTasks(Long userId, String statusFilter) {
        List<Task> tasks;
        if (statusFilter != null) {
            try {
                Task.TaskStatus status = Task.TaskStatus.valueOf(statusFilter.toUpperCase());
                tasks = taskRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + statusFilter);
            }
        } else {
            tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public TaskResponse getTask(Long taskId, Long userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, User user) {
        Category category = resolveCategory(request, user);

        Task task = Task.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .category(category)
                .priority(parsePriority(request.getPriority()))
                .build();

        task = taskRepository.save(task);
        activityLogRepository.save(ActivityLog.of(user.getId(), "TASK_CREATED",
                Map.of("taskId", task.getId(), "title", task.getTitle())));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, User user) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setPriority(parsePriority(request.getPriority()));
        task.setCategory(resolveCategory(request, user));

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse completeTask(Long taskId, Long userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setStatus(Task.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task = taskRepository.save(task);

        activityLogRepository.save(ActivityLog.of(userId, "TASK_COMPLETED",
                Map.of("taskId", task.getId(), "title", task.getTitle())));
        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    private Category resolveCategory(TaskRequest request, User user) {
        if (request.getCategoryId() != null) {
            return categoryService.getByIdAndUser(request.getCategoryId(), user.getId());
        }
        String categoryName = autoCategorizationService.categorize(request.getTitle());
        return categoryService.findOrCreateForUser(user, categoryName);
    }

    private Task.TaskPriority parsePriority(String priority) {
        if (priority == null) return Task.TaskPriority.MEDIUM;
        try {
            return Task.TaskPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Task.TaskPriority.MEDIUM;
        }
    }
}
