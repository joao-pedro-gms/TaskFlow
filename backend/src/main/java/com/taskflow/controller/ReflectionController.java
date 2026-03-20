package com.taskflow.controller;

import com.taskflow.dto.response.ReflectionResponse;
import com.taskflow.security.SecurityUtils;
import com.taskflow.service.ReflectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reflection")
@RequiredArgsConstructor
public class ReflectionController {

    private final ReflectionService reflectionService;
    private final SecurityUtils securityUtils;

    @GetMapping("/today")
    public ResponseEntity<ReflectionResponse> getTodayReflection() {
        return ResponseEntity.ok(reflectionService.getReflection(
                securityUtils.getCurrentUserId(), LocalDate.now()));
    }

    @GetMapping
    public ResponseEntity<ReflectionResponse> getReflection(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reflectionService.getReflection(
                securityUtils.getCurrentUserId(), date));
    }
}
