package com.example.tes24.qqueueing.api.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PrometheusResponse {

    private String status;

    private PrometheusData data;


}