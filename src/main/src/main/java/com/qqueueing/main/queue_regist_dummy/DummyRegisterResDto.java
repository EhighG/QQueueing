package com.qqueueing.main.queue_regist_dummy;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DummyRegisterResDto {
    private String id;
    private String topicName;
    private String targetUrl;
    private int maxCapacity;
    private int processingPerMinute;
    private String serviceName;
    private String queueImageUrl;
    private boolean isActive;
}
