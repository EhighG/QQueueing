package com.example.tes24.qqueue_module.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchLogsResDto {

    private String nodeMemoryMemAvailableBytes;

    private String diskAvailableBytes;

    private String cpuUsageRate;
}
