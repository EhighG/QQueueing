package com.qqueueing.main.waiting.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "queues")
public class TargetInfo {
    @Id
    private String id;
    private String topicName;
    private String targetUrl;
    @Setter
    private boolean isActive;

    public TargetInfo(String topicName, String targetUrl, boolean isActive) {
        this.topicName = topicName;
        this.targetUrl = targetUrl;
        this.isActive = isActive;
    }
}
