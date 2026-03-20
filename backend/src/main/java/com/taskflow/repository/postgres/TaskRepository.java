package com.taskflow.repository.postgres;

import com.taskflow.entity.postgres.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Task> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Task.TaskStatus status);

    List<Task> findByUserIdAndCompletedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN :now AND :cutoff " +
           "AND t.reminderSent = false AND t.status != 'COMPLETED'")
    List<Task> findTasksDueWithin24Hours(
            @Param("now") LocalDateTime now,
            @Param("cutoff") LocalDateTime cutoff
    );
}
