package com.taskflow.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AutoCategorizationService {

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = Map.of(
            "Work",     List.of("meeting", "report", "deadline", "client", "project", "email",
                                "review", "presentation", "sprint", "standup", "call", "office",
                                "boss", "manager", "colleague", "task", "jira", "slack"),
            "Health",   List.of("gym", "workout", "doctor", "medicine", "run", "exercise",
                                "diet", "sleep", "yoga", "appointment", "dentist", "hospital",
                                "health", "fitness", "therapy", "meditate"),
            "Learning", List.of("course", "study", "read", "book", "tutorial", "practice",
                                "learn", "research", "udemy", "lecture", "exam", "class",
                                "homework", "assignment", "university", "college"),
            "Finance",  List.of("bill", "pay", "invoice", "budget", "bank", "tax", "expense",
                                "salary", "receipt", "transfer", "money", "rent", "insurance",
                                "investment", "credit"),
            "Home",     List.of("clean", "cook", "grocery", "repair", "shop", "laundry",
                                "organize", "buy", "fix", "install", "house", "apartment",
                                "furniture", "dishes", "trash")
    );

    public String categorize(String taskTitle) {
        String lower = taskTitle.toLowerCase();
        return CATEGORY_KEYWORDS.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(lower::contains))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse("Personal");
    }
}
