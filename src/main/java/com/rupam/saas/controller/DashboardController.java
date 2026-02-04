package com.rupam.saas.controller;

import com.rupam.saas.dto.DashboardResponse;
import com.rupam.saas.repository.TaskRepository;
import com.rupam.saas.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserRepository userRepo;
    private final TaskRepository taskRepo;

    public DashboardController(UserRepository userRepo, TaskRepository taskRepo) {
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
    }

    @GetMapping
    public DashboardResponse getDashboard(HttpServletRequest request) {

        // This is injected by JwtFilter
        Long companyId = (Long) request.getAttribute("companyId");

        DashboardResponse response = new DashboardResponse();
        response.setTotalUsers(userRepo.countByCompanyId(companyId));
        response.setTotalTasks(taskRepo.countByCompanyId(companyId));
        response.setTodo(taskRepo.countByCompanyIdAndStatus(companyId, "TODO"));
        response.setInProgress(taskRepo.countByCompanyIdAndStatus(companyId, "IN_PROGRESS"));
        response.setDone(taskRepo.countByCompanyIdAndStatus(companyId, "DONE"));

        return response;
    }
}
