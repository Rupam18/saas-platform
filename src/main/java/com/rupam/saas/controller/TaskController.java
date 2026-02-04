package com.rupam.saas.controller;

import com.rupam.saas.entity.Company;
import com.rupam.saas.entity.Task;
import com.rupam.saas.repository.CompanyRepository;
import com.rupam.saas.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepo;
    private final CompanyRepository companyRepo;

    public TaskController(TaskRepository taskRepo, CompanyRepository companyRepo) {
        this.taskRepo = taskRepo;
        this.companyRepo = companyRepo;
    }

    // Create task
    @PostMapping
    public Task create(@RequestBody Task task, HttpServletRequest request) {
        Long companyId = (Long) request.getAttribute("companyId");
        Company company = companyRepo.findById(companyId).orElseThrow();

        task.setCompany(company);
        return taskRepo.save(task);
    }

    // Get all tasks of this company
    @GetMapping
    public List<Task> getMyTasks(HttpServletRequest request) {
        Long companyId = (Long) request.getAttribute("companyId");
        return taskRepo.findByCompanyId(companyId);
    }
}
