package com.example.udtbe.domain.batch.entity;


import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.batch.entity.enums.BatchJobStatus;
import com.example.udtbe.domain.batch.entity.enums.BatchJobType;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name = "batch_job_metric")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchJobMetric extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_job_metric_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BatchJobType type;

    @Enumerated(EnumType.STRING)
    private BatchJobStatus status;

    private long totalRead = 0;
    private long totalComplete = 0;
    private long totalInvalid = 0;
    private long totalFailed = 0;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder(access = PRIVATE)
    private BatchJobMetric(BatchJobType type, BatchJobStatus status, LocalDateTime startTime,
            LocalDateTime endTime) {
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static BatchJobMetric of(BatchJobType type, BatchJobStatus status,
            LocalDateTime startTime, LocalDateTime endTime) {

        return BatchJobMetric.builder()
                .type(type)
                .status(status)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public void update(BatchJobStatus status, long totalRead, long totalComplete, long totalInvalid,
            long totalFailed, LocalDateTime startTime, LocalDateTime endTime) {
        this.status = status;
        this.totalRead = totalRead;
        this.totalComplete = totalComplete;
        this.totalInvalid = totalInvalid;
        this.totalFailed = totalFailed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
