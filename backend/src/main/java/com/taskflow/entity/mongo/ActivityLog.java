package com.taskflow.entity.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private String action;

    private Map<String, Object> metadata;

    @Indexed
    private LocalDateTime timestamp;

    public static ActivityLog of(Long userId, String action, Map<String, Object> metadata) {
        return ActivityLog.builder()
                .userId(userId)
                .action(action)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
