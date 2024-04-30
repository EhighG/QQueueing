package com.qqueueing.main.queue_regist_dummy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class DummyRegisterReqDto {
    private String targetUrl;
    private int maxCapacity;
    private int processingPerMinute;
    private String serviceName;
    private String queueImageUrl;
}
