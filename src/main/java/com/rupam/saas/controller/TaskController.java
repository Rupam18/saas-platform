package com.rupam.saas.controller;

import com.rupam.saas.dto.TaskRequest;
import com.rupam.saas.entity.Task;
import com.rupam.saas.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private Long getCompanyId(HttpServletRequest request) {
        return (Long) request.getAttribute("companyId");
    }

    @PostMapping
    public Task create(@RequestBody TaskRequest req, HttpServletRequest request) {
        return taskService.createTask(req, getCompanyId(request));
    }

    @GetMapping
    public List<Task> getMyTasks(HttpServletRequest request) {
        return taskService.getTasksByCompany(getCompanyId(request));
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody TaskRequest req, HttpServletRequest request) {
        return taskService.updateTask(id, req, getCompanyId(request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        taskService.deleteTask(id, getCompanyId(request));
    }
}
