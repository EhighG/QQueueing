package com.qqueueing.main.registration.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.qqueueing.main.common.StrictIntegerDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegistrationUpdateRequest {

    private String targetUrl;
    private Integer maxCapacity; // 최대 수용 인원
    @JsonDeserialize(using = StrictIntegerDeserializer.class)
    private Integer processingPerMinute; // 입장 속도(분당 입장 인원). 보내지 않으면 지금 값을 그대로 둔다.
    private String serviceName; // 서비스명
    private String queueImageUrl; // 대기열 이미지
}
