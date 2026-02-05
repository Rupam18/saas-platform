package com.rupam.saas.service;

import com.rupam.saas.dto.TaskRequest;
import com.rupam.saas.dto.TaskSearchRequest;
import com.rupam.saas.entity.Company;
import com.rupam.saas.entity.Task;
import com.rupam.saas.exception.ResourceNotFoundException;
import com.rupam.saas.repository.CompanyRepository;
import com.rupam.saas.repository.TaskRepository;
import com.rupam.saas.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final CompanyRepository companyRepo;

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

    public Page<Task> searchTasks(TaskSearchRequest searchReq, Long companyId) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchReq.getDirection()), searchReq.getSortBy());
        Pageable pageable = PageRequest.of(searchReq.getPage(), searchReq.getSize(), sort);

        Specification<Task> spec = TaskSpecification.getTasks(searchReq, companyId);
        return taskRepo.findAll(spec, pageable);
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

    public com.rupam.saas.dto.TaskStatistics getTaskStatistics(Long companyId) {
        long total = taskRepo.countByCompanyId(companyId);
        long completed = taskRepo.countByCompanyIdAndStatus(companyId, "DONE");
        long pending = taskRepo.countByCompanyIdAndStatus(companyId, "PENDING");
        long inProgress = taskRepo.countByCompanyIdAndStatus(companyId, "IN_PROGRESS");

        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(6).withHour(0).withMinute(0)
                .withSecond(0);
        List<Object[]> rawCounts = taskRepo.findTaskCountsByDate(companyId, sevenDaysAgo);

        java.util.Map<String, Long> countMap = rawCounts.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).longValue()));

        java.util.List<com.rupam.saas.dto.DailyTaskCount> activity = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Fill in last 7 days (including today)
        for (int i = 6; i >= 0; i--) {
            String date = java.time.LocalDateTime.now().minusDays(i).format(formatter);
            activity.add(new com.rupam.saas.dto.DailyTaskCount(date, countMap.getOrDefault(date, 0L)));
        }

        return new com.rupam.saas.dto.TaskStatistics(total, completed, pending, inProgress, activity);
    }
}
