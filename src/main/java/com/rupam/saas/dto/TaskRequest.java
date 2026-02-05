package com.rupam.saas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private String status; // PENDING, IN_PROGRESS, DONE
}
