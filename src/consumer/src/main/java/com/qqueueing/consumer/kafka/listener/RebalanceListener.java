package com.qqueueing.consumer.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

@Slf4j
public class RebalanceListener implements ConsumerRebalanceListener {
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        log.info("Partitions are revoked : " + partitions.toString());
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        log.info("Partitions are assigned : " + partitions.toString());

    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        log.info("Partitions are losted : " + partitions.toString());
        ConsumerRebalanceListener.super.onPartitionsLost(partitions);
    }
}
