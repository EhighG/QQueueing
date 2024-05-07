package com.qqueueing.main.registration.model;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegistrationUpdateRequest {

    private String targetUrl;
    private Integer maxCapacity; // 최대 수용 인원
    private Integer processingPerMinute; // 1분당 처리 인원
    private String serviceName; // 서비스명
    private String queueImageUrl; // 대기열 이미지
}
