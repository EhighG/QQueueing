package com.qqueueing.main.log.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PrometheusData {

    private String resultType;

    private List<PrometheusResult> result;
}
