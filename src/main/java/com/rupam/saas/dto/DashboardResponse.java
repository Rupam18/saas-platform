package com.rupam.saas.dto;

public class DashboardResponse {

    private String companyName;
    private Long totalUsers;
    private Long totalTasks;
    private Long todo;
    private Long inProgress;
    private Long done;

    // Required for JSON serialization
    public DashboardResponse() {
    }

    public DashboardResponse(String companyName,
                             Long totalUsers,
                             Long totalTasks,
                             Long todo,
                             Long inProgress,
                             Long done) {
        this.companyName = companyName;
        this.totalUsers = totalUsers;
        this.totalTasks = totalTasks;
        this.todo = todo;
        this.inProgress = inProgress;
        this.done = done;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(Long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public Long getTodo() {
        return todo;
    }

    public void setTodo(Long todo) {
        this.todo = todo;
    }

    public Long getInProgress() {
        return inProgress;
    }

    public void setInProgress(Long inProgress) {
        this.inProgress = inProgress;
    }

    public Long getDone() {
        return done;
    }

    public void setDone(Long done) {
        this.done = done;
    }
}
