package com.taskflow.repository.postgres;

import com.taskflow.entity.postgres.DailyPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPriorityRepository extends JpaRepository<DailyPriority, Long> {

    List<DailyPriority> findByUserIdAndPriorityDateOrderByRank(Long userId, LocalDate date);

    int countByUserIdAndPriorityDate(Long userId, LocalDate date);

    Optional<DailyPriority> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DailyPriority dp WHERE dp.user.id = :userId AND dp.priorityDate = :date")
    void deleteByUserIdAndPriorityDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
