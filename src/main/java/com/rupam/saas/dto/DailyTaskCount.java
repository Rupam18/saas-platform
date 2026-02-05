package com.rupam.saas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTaskCount {
    private String date; // Format: YYYY-MM-DD or simple Day Name
    private long count;
}
