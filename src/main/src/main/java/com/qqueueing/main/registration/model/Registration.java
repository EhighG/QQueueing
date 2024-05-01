package com.qqueueing.main.registration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "registration_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    private String id; // 자동 생성되는 식별자
    private String topicName;
    private String targetUrl;
    private Integer maxCapacity; // 최대 수용 인원
    private Integer processingPerMinute; // 1분당 처리 인원
    private String serviceName; // 서비스명
    private String queueImageUrl; // 대기열 이미지
    @Setter
    private Boolean isActive = false; // 활성화 여부
    @Setter
    private Integer partitionNo;


    public Registration(String topicName, String targetUrl) {
        this.topicName = topicName;
        this.targetUrl = targetUrl;
        this.isActive = false;
    }
}
