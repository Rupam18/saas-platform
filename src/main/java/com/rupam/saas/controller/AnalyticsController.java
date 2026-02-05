package com.rupam.saas.controller;

import com.rupam.saas.dto.TaskStatistics;
import com.rupam.saas.entity.User;
import com.rupam.saas.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public TaskStatistics getTaskStatistics(@AuthenticationPrincipal User user) {
        return taskService.getTaskStatistics(user.getCompany().getId());
    }
}
