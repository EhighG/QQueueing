package com.example.tes24.qqueue_module.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrometheusResult {

//    private PrometheusMetric metric;

    private List<String> value;
}
