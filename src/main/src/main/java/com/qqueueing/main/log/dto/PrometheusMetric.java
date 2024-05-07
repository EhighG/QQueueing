package com.qqueueing.main.log.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PrometheusMetric {

    private String __name__;
    private String instance;
    private String job;
}
