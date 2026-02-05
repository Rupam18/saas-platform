package com.rupam.saas.dto;

import lombok.Data;

@Data
public class TaskSearchRequest {
    private String title;
    private String status;

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String direction = "DESC";
}
