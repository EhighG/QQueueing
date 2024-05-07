package com.example.tes24.qqueue_module.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PrometheusData {

    private String resultType;

    private List<PrometheusResult> result;
}
