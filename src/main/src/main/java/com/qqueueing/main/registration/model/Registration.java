package com.qqueueing.main.registration.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.qqueueing.main.common.StrictIntegerDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "registration_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    /** 입장 속도를 입력하지 않았을 때 쓰는 분당 입장 인원. 1초에 100명과 같다. */
    public static final int DEFAULT_PROCESSING_PER_MINUTE = 6000;

    @Id
    private String id; // 자동 생성되는 식별자. 대기열 id로 쓴다.
    @Indexed(unique = true)
    private String targetUrl;
    private Integer maxCapacity; // 최대 수용 인원
    @JsonDeserialize(using = StrictIntegerDeserializer.class)
    private Integer processingPerMinute; // 입장 속도(분당 입장 인원)
    private String serviceName; // 서비스명
    private String queueImageUrl; // 대기열 이미지
    @Setter
    private Boolean isActive = true; // 활성화 여부

    public void update(String targetUrl, Integer maxCapacity, Integer processingPerMinute, String serviceName, String queueImageUrl) {
        if (targetUrl != null) {
            this.targetUrl = targetUrl;
        }
        if (maxCapacity != null) {
            this.maxCapacity = maxCapacity;
        }
        if (processingPerMinute != null) {
            this.processingPerMinute = processingPerMinute;
        }
        if (serviceName != null) {
            this.serviceName = serviceName;
        }
        if (queueImageUrl != null) {
            this.queueImageUrl = queueImageUrl;
        }
    }

    /** 등록할 때 입장 속도를 입력하지 않았으면 기본값을 넣는다. */
    public void fillDefaultProcessingPerMinute() {
        if (processingPerMinute == null) {
            processingPerMinute = DEFAULT_PROCESSING_PER_MINUTE;
        }
    }

    /**
     * 입장 처리에 쓰는 입장 속도(분당 입장 인원).
     * 입장 속도 검증이 생기기 전에 등록해 값이 없거나 0 이하로 저장된 대기열은 기본값을 쓴다. 아무도 입장하지 못하는 대기열이 생기지 않게 하기 위해서다.
     */
    public int admissionRatePerMinute() {
        if (processingPerMinute == null || processingPerMinute < 1) {
            return DEFAULT_PROCESSING_PER_MINUTE;
        }
        return processingPerMinute;
    }

}
