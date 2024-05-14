package com.qqueueing.main.log.service;

import com.qqueueing.main.log.dto.PrometheusData;
import com.qqueueing.main.log.dto.PrometheusResult;
import com.qqueueing.main.log.dto.SearchLogsResDto;
import com.qqueueing.main.log.dto.SearchRequestCountResDto;
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

    private final String MEMORY_ALL_QUERY = "node_memory_MemTotal_bytes";
    private final String MEMORY_AVAILABLE_QUERY = "node_memory_MemAvailable_bytes";

    private final String DISK_ALL_QUERY = "node_filesystem_size_bytes";

    private final String DISK_AVAILABLE_QUERY = "node_filesystem_avail_bytes";
//    private final String DISK_AVAILABLE_QUERY = "node_filesystem_avail_bytes{device=\"/dev/root\",fstype=\"ext4\",mountpoint=\"/etc/hosts\"}";

    //    private final String CPU_USAGE_QUERY = "sum(rate(node_cpu_seconds_total{mode=\"user\"}[5m]))*100";
    private final String CPU_USAGE_QUERY = "sum(rate(node_cpu_seconds_total{CPU_USAGE_MODE}";
    private final String CPU_USAGE_MODE = "{mode=\"user\"}[5s]))*100";

    private final String TOMCAT_REQUEST_COUNT = "rate(tomcat_servlet_request_seconds_count{TOMCAT_REQUEST_MODE}";
    private final String TOMCAT_REQUEST_MODE = "{name=\"dispatcherServlet\"}[5s])*100";

    public SearchLogsResDto searchLogs() {

        // 메모리 전체 용량
        String memoryAllBytes = getMemoryOrDiskQueryResult(MEMORY_ALL_QUERY);
        log.info("memoryAllBytes : " + memoryAllBytes);

        // 메모리 사용 가능량
        String nodeMemoryMemAvailableBytes = getMemoryOrDiskQueryResult(MEMORY_AVAILABLE_QUERY);
        log.info("nodeMemoryMemAvailableBytes : " + nodeMemoryMemAvailableBytes);

        // 디스크 전체 용량
        String diskAllBytes = getMemoryOrDiskQueryResult(DISK_ALL_QUERY);
        log.info("diskAllBytes : " + diskAllBytes);

        // 디스크 사용 가능량
        String diskAvailableBytes = getMemoryOrDiskQueryResult(DISK_AVAILABLE_QUERY);
        log.info("diskAvailableBytes : " + diskAvailableBytes);

        // 현재 cpu 사용량
        String cpuUsageRate = getRateWithMode(CPU_USAGE_QUERY, CPU_USAGE_MODE);
        log.info("cpuUsageRate : " + cpuUsageRate);

        SearchLogsResDto searchLogsResDto = SearchLogsResDto.builder()
                .memoryAllBytes(memoryAllBytes)
                .nodeMemoryMemAvailableBytes(nodeMemoryMemAvailableBytes)
                .diskAllBytes(diskAllBytes)
                .diskAvailableBytes(diskAvailableBytes)
                .cpuUsageRate(cpuUsageRate)
                .build();

        return searchLogsResDto;
    }

    public String getMemoryOrDiskQueryResult(String query) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(PROMETHEUS_URL + query, Map.class);

        Map<String, PrometheusData> data = (Map<String, PrometheusData>) response.getBody().get("data");
        List<Map<String, PrometheusResult>> prometheusResult = (List<Map<String, PrometheusResult>>) data.get("result");
        List<String> result = (List<String>) prometheusResult.get(0).get("value");

        return result.get(1);
    }

    public String getRateWithMode(String query, String mode) {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.getForEntity(PROMETHEUS_URL + query, Map.class, mode);

        Map<String, PrometheusData> data = (Map<String, PrometheusData>) response.getBody().get("data");
        List<Map<String, PrometheusResult>> prometheusResult = (List<Map<String, PrometheusResult>>) data.get("result");
        List<String> result = (List<String>) prometheusResult.get(0).get("value");

        Double avgCpuUsage = Double.parseDouble(result.get(1));
        avgCpuUsage /= 4;

        return String.valueOf(avgCpuUsage);
    }

    public SearchRequestCountResDto searchRequestCount() {

        // 톰캣 http request 횟수 (5초 이내)
        String tomcatRequestCount = getRateWithMode(TOMCAT_REQUEST_COUNT, TOMCAT_REQUEST_MODE);
        log.info("tomcatRequestCount : " + tomcatRequestCount);

        SearchRequestCountResDto result = SearchRequestCountResDto.builder()
                .tomcatRequestCount(tomcatRequestCount).build();

        return result;
    }
}
