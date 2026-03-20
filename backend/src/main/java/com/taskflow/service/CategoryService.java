package com.taskflow.service;

import com.taskflow.entity.postgres.Category;
import com.taskflow.entity.postgres.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.postgres.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllForUser(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    public Category create(User user, Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new com.taskflow.exception.BadRequestException("Category name is required");
        }
        Category category = Category.builder()
                .user(user)
                .name(name)
                .colorHex(body.getOrDefault("colorHex", "#6366f1"))
                .iconName(body.getOrDefault("iconName", "tag"))
                .build();
        return categoryRepository.save(category);
    }

    public Category findOrCreateForUser(User user, String categoryName) {
        return categoryRepository
                .findByUserIdAndNameIgnoreCase(user.getId(), categoryName)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .user(user)
                                .name(categoryName)
                                .colorHex("#6366f1")
                                .iconName("tag")
                                .build()
                ));
    }

    public Category getByIdAndUser(Long categoryId, Long userId) {
        return categoryRepository.findById(categoryId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
