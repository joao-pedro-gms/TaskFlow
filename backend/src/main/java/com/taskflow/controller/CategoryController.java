package com.taskflow.controller;

import com.taskflow.entity.postgres.Category;
import com.taskflow.security.SecurityUtils;
import com.taskflow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllForUser(securityUtils.getCurrentUserId()));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(securityUtils.getCurrentUser(), body));
    }
}
