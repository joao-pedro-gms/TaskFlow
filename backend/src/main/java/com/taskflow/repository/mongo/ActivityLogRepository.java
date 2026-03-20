package com.taskflow.repository.mongo;

import com.taskflow.entity.mongo.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
}
