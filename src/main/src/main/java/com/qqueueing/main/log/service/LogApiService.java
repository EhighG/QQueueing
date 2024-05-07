package com.qqueueing.main.log.service;

import com.qqueueing.main.log.dto.PrometheusData;
import com.qqueueing.main.log.dto.PrometheusResult;
import com.qqueueing.main.log.dto.SearchLogsResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LogApiService {

    private final String PROMETHEUS_URL = "http://k10a401.p.ssafy.io:3003/api/v1/query?query=";
    private final String MEMORY_AVAILABLE_QUERY = "node_memory_MemAvailable_bytes";
    private final String DISK_AVAILABLE_QUERY = "node_filesystem_avail_bytes{device=\"/dev/root\",fstype=\"ext4\",mountpoint=\"/etc/hosts\"}";

    //    private final String CPU_USAGE_QUERY = "sum(rate(node_cpu_seconds_total{mode=\"user\"}[5m]))*100";
    private final String CPU_USAGE_QUERY = "sum(rate(node_cpu_seconds_total{CPU_USAGE_MODE}";
    private final String CPU_USAGE_MODE = "{mode=\"user\"}[5m]))*100";

    public SearchLogsResDto searchLogs() {

        // 메모리 사용 가능량
        String nodeMemoryMemAvailableBytes = getMemoryAvailableBytes();
        log.info("nodeMemoryMemAvailableBytes = " + nodeMemoryMemAvailableBytes);

        // 디스크 사용 가능량
        String diskAvailableBytes = getDiskAvailableBytes();
        log.info("diskAvailableBytes = " + diskAvailableBytes);

        // 현재 cpu 사용량
        String cpuUsageRate = getCpuUsageRate();
        log.info("cpuUsageRate = " + cpuUsageRate);

        SearchLogsResDto searchLogsResDto = SearchLogsResDto.builder()
                .nodeMemoryMemAvailableBytes(nodeMemoryMemAvailableBytes)
                .diskAvailableBytes(diskAvailableBytes)
                .cpuUsageRate(cpuUsageRate)
                .build();

        return searchLogsResDto;
    }

    public String getMemoryAvailableBytes() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(PROMETHEUS_URL + MEMORY_AVAILABLE_QUERY, Map.class);

        Map<String, PrometheusData> data = (Map<String, PrometheusData>) response.getBody().get("data");
        List<Map<String, PrometheusResult>> prometheusResult = (List<Map<String, PrometheusResult>>) data.get("result");
        List<String> result = (List<String>) prometheusResult.get(0).get("value");

        return result.get(1);
    }

    public String getDiskAvailableBytes() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(PROMETHEUS_URL + DISK_AVAILABLE_QUERY, Map.class);

        Map<String, PrometheusData> data = (Map<String, PrometheusData>) response.getBody().get("data");
        List<Map<String, PrometheusResult>> prometheusResult = (List<Map<String, PrometheusResult>>) data.get("result");
        List<String> result = (List<String>) prometheusResult.get(0).get("value");

        return result.get(1);
    }

    public String getCpuUsageRate() {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.getForEntity(PROMETHEUS_URL + CPU_USAGE_QUERY, Map.class, CPU_USAGE_MODE);

        Map<String, PrometheusData> data = (Map<String, PrometheusData>) response.getBody().get("data");
        List<Map<String, PrometheusResult>> prometheusResult = (List<Map<String, PrometheusResult>>) data.get("result");
        List<String> result = (List<String>) prometheusResult.get(0).get("value");

        return result.get(1);
    }
}
