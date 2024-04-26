package com.example.tes24.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "dequeue_logs")
public class DequeueLog {
    @Id
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "dequeue_time")
    private Timestamp dequeueTime;

    @Column(name = "sequence_number")
    private Long sequenceNumber;
}
