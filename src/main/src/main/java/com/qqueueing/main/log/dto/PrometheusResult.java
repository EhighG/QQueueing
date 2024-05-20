package com.qqueueing.main.log.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrometheusResult {

//    private PrometheusMetric metric;

    private List<String> value;
}
