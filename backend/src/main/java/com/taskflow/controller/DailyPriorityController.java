package com.taskflow.controller;

import com.taskflow.dto.request.DailyPriorityRequest;
import com.taskflow.dto.response.DailyPriorityResponse;
import com.taskflow.security.SecurityUtils;
import com.taskflow.service.DailyPriorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
@RequiredArgsConstructor
public class DailyPriorityController {

    private final DailyPriorityService dailyPriorityService;
    private final SecurityUtils securityUtils;

    @GetMapping("/today")
    public ResponseEntity<List<DailyPriorityResponse>> getTodayPriorities() {
        return ResponseEntity.ok(dailyPriorityService.getTodayPriorities(securityUtils.getCurrentUserId()));
    }

    @PostMapping
    public ResponseEntity<List<DailyPriorityResponse>> setPriorities(
            @Valid @RequestBody DailyPriorityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dailyPriorityService.setPriorities(request, securityUtils.getCurrentUser()));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<DailyPriorityResponse> completePriority(@PathVariable Long id) {
        return ResponseEntity.ok(dailyPriorityService.completePriority(id, securityUtils.getCurrentUserId()));
    }
}
