package com.qqueueing.main.registration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Document(collection = "registration_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    private String id; // 자동 생성되는 식별자
    private String topicName;
    @Indexed(unique = true)
    private String targetUrl;
    private Integer maxCapacity; // 최대 수용 인원
    private Integer processingPerMinute; // 1분당 처리 인원
    private String serviceName; // 서비스명
    private String queueImageUrl; // 대기열 이미지
    @Setter
    private Boolean isActive = true; // 활성화 여부
    @Setter
    private Integer partitionNo;

    public Registration(String topicName, String targetUrl) {
        this.topicName = topicName;
        this.targetUrl = targetUrl;
        this.isActive = false;
    }

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

}
