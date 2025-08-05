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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchJobMetric extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_job_metric_id")
    private Long id;

    private Long adminContentJobId;

    @Enumerated(EnumType.STRING)
    private BatchJobType type;

    @Enumerated(EnumType.STRING)
    private BatchJobStatus status;

    private long totalRead;
    private long totalWrite;
    private long totalSkip;

    private LocalDateTime startTime;
    private LocalDateTime endTime;


    @Builder(access = PRIVATE)
    private BatchJobMetric(Long adminContentJobId, BatchJobType type, BatchJobStatus status,
            long totalRead, long totalWrite, long totalSkip, LocalDateTime startTime,
            LocalDateTime endTime) {
        this.adminContentJobId = adminContentJobId;
        this.type = type;
        this.status = status;
        this.totalRead = totalRead;
        this.totalWrite = totalWrite;
        this.totalSkip = totalSkip;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static BatchJobMetric of(Long adminContentJobId, BatchJobType type,
            BatchJobStatus status, long totalRead, long totalWrite, long totalSkip,
            LocalDateTime startTime, LocalDateTime endTime) {

        return BatchJobMetric.builder()
                .adminContentJobId(adminContentJobId)
                .type(type)
                .status(status)
                .totalRead(totalRead)
                .totalWrite(totalWrite)
                .totalSkip(totalSkip)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public void update(BatchJobStatus status, long totalRead, long totalWrite, long totalSkip,
            LocalDateTime startTime, LocalDateTime endTime) {
        this.status = status;
        this.totalRead += totalRead;
        this.totalWrite += totalWrite;
        this.totalSkip += totalSkip;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
