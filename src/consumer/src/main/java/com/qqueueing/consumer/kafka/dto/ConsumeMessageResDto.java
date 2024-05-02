package com.qqueueing.consumer.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ConsumeMessageResDto {

    private List<String> curDoneList;

    private Long batchLastIdx;
    private Long totalQueueSize;
}
