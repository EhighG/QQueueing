package com.qqueueing.consumer.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChangeConsumerPropertiesReqDto {

    private int maxPollRecordsConfig;
}
