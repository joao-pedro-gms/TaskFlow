package com.taskflow.entity.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long userId;

    @Builder.Default
    private String morningReviewTime = "08:00";

    @Builder.Default
    private String nightlyReflectionTime = "21:00";

    @Builder.Default
    private String timezone = "America/Sao_Paulo";

    @Builder.Default
    private boolean emailNotifications = true;

    @Builder.Default
    private boolean pushNotifications = false;

    @Builder.Default
    private List<String> defaultCategories = List.of("Work", "Personal", "Health");

    private Map<String, Object> uiPreferences;

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
