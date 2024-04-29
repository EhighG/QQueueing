package com.qqueueing.main.waiting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QueueInfoDto {

    private String topicName;
    private boolean isActive;

    public QueueInfoDto(String topicName) {
        this.topicName = topicName;
        this.isActive = true;
    }
}
