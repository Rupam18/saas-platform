package com.rupam.saas.controller;

import com.rupam.saas.dto.TaskRequest;
import com.rupam.saas.dto.TaskSearchRequest;
import com.rupam.saas.entity.Task;
import com.rupam.saas.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private Long getCompanyId(HttpServletRequest request) {
        return (Long) request.getAttribute("companyId");
    }

    @PostMapping
    public Task create(@Valid @RequestBody TaskRequest req, HttpServletRequest request) {
        return taskService.createTask(req, getCompanyId(request));
    }

    @GetMapping
    public Page<Task> getMyTasks(@ModelAttribute TaskSearchRequest searchReq, HttpServletRequest request) {
        return taskService.searchTasks(searchReq, getCompanyId(request));
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @Valid @RequestBody TaskRequest req, HttpServletRequest request) {
        return taskService.updateTask(id, req, getCompanyId(request));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        taskService.deleteTask(id, getCompanyId(request));
    }
}
