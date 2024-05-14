package com.qqueueing.main.log.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchLogsResDto {

    private String memoryAllBytes;

    private String nodeMemoryMemAvailableBytes;

    private String diskAllBytes;

    private String diskAvailableBytes;

    private String cpuUsageRate;

    private String tomcatRequestCount;
}
