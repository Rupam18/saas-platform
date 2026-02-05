package com.rupam.saas.service;

import com.rupam.saas.dto.TaskRequest;
import com.rupam.saas.entity.Company;
import com.rupam.saas.entity.Task;
import com.rupam.saas.exception.ResourceNotFoundException;
import com.rupam.saas.repository.CompanyRepository;
import com.rupam.saas.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final CompanyRepository companyRepo; // Using repo directly for lookup

    public Task createTask(TaskRequest req, Long companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setStatus(req.getStatus() != null ? req.getStatus() : "PENDING");
        task.setCompany(company);

        return taskRepo.save(task);
    }

    public List<Task> getTasksByCompany(Long companyId) {
        return taskRepo.findByCompanyId(companyId);
    }

    public Task updateTask(Long taskId, TaskRequest req, Long companyId) {
        Task task = taskRepo.findById(taskId)
                .filter(t -> t.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));

        if (req.getTitle() != null)
            task.setTitle(req.getTitle());
        if (req.getDescription() != null)
            task.setDescription(req.getDescription());
        if (req.getStatus() != null)
            task.setStatus(req.getStatus());

        return taskRepo.save(task);
    }

    public void deleteTask(Long taskId, Long companyId) {
        Task task = taskRepo.findById(taskId)
                .filter(t -> t.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));

        taskRepo.delete(task);
    }
}
